package menuservice.menu.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import menuservice.global.exception.BusinessException;
import menuservice.global.exception.ErrorCode;
import menuservice.menu.domain.Menu;
import menuservice.menu.domain.enums.MenuType;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.dto.MenuResponse;
import menuservice.menu.repository.MenuRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public List<MenuResponse> getAllMenus() {
        List<Menu> allMenus = menuRepository.findAllOrderedByParentAndOrderSn();
        validateMenuListNotEmpty(allMenus);

        Map<Long, List<Menu>> childrenMap = allMenus.stream()
                .filter(m -> m.getParent() != null)
                .collect(Collectors.groupingBy(m -> m.getParent().getId()));

        return allMenus.stream()
                .filter(m -> m.getParent() == null)
                .map(m -> buildMenuTreeResponse(m, childrenMap))
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuResponse createMenu(MenuRequest requestDto) {
        if (pathExistsCheck(requestDto.path())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PATH);
        }

        Long parentId = requestDto.parentId();
        Menu parentMenu = null;

        if (Objects.nonNull(parentId)) {
            validateParentFolder(requestDto.parentId(), requestDto.type());

             parentMenu = menuRepository.findById(parentId)
                     .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_FOLDER_ID));
        }

        Integer finalOrderSn = resolveOrderSnForRegister(requestDto.parentId(), requestDto.orderSn());

        Menu menu = menuRepository.save(Menu.of(requestDto, parentMenu, finalOrderSn));

        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest requestDto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        validatePathForUpdate(menu, requestDto.path());
        validateTypeChangeConstraint(menu, requestDto.type());

        Integer oldOrderSn = menu.getOrderSn();
        Integer newOrderSn = requestDto.orderSn();

        if (newOrderSn != null && !newOrderSn.equals(oldOrderSn)) {
            Long parentId = menu.getParent() != null ? menu.getParent().getId() : null;
            shiftOrderSnByTargetOrderSn(parentId, newOrderSn);
        }

        menu.update(requestDto, menu.getParent());
        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }

    /**
     * 조회된 메뉴 목록이 비어있는지 검증합니다.
     * <p>
     * 등록된 메뉴가 하나도 없는 경우 비즈니스 예외를 발생시킵니다.
     * </p>
     *
     * @param menus 검증할 메뉴 엔티티 목록
     * @throws BusinessException 메뉴 목록이 비어있는 경우 발생하는 예외
     */
    private void validateMenuListNotEmpty(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            throw new BusinessException(ErrorCode.MENU_LIST_NOT_FOUND);
        }
    }

    /**
     * 메뉴 경로(Path)가 이미 존재하는지 중복 여부를 확인합니다.
     *
     * @param path 중복 여부를 확인할 메뉴 경로
     * @return 이미 사용 중인 경로일 경우 true, 그렇지 않으면 false
     */
    private boolean pathExistsCheck(String path) {
        return menuRepository.existsByPath(path);
    }

    /**
     * 지정된 상위 메뉴가 하위 항목을 가질 수 있는 유효한 폴더인지 검증합니다.
     * <p>
     * 이 메서드는 다음과 같은 규칙을 검증합니다:
     * 1. 폴더({@code MenuType.FOLDER}) 타입의 메뉴는 상위 메뉴 하위에 중첩되어 생성될 수 없습니다.
     * 2. 상위 메뉴로 지정된 ID({@code parentId})는 데이터베이스에 반드시 존재해야 합니다.
     * 3. 조회된 상위 메뉴의 타입은 반드시 폴더({@code MenuType.FOLDER})여야 합니다.
     * 검증에 실패할 경우 비즈니스 예외를 발생시킵니다.
     * </p>
     *
     * @param parentId 검증할 상위 메뉴 ID
     * @param menuType 현재 등록 또는 수정하려는 대상 메뉴의 타입
     * @throws BusinessException 대상 메뉴가 폴더 타입이거나, 상위 메뉴가 존재하지 않거나 폴더 타입이 아닌 경우 발생하는 예외
     */
    private void validateParentFolder(Long parentId, MenuType menuType) {
        if (menuType == MenuType.FOLDER) {
            throw new BusinessException(ErrorCode.INVALID_MENU_TYPE);
        }

        Menu parentFolder = menuRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_FOLDER_ID));

        if (parentFolder.getType() != MenuType.FOLDER) {
            throw new BusinessException(ErrorCode.INVALID_FOLDER_ID);
        }
    }

    /**
     * 메뉴 수정 시 변경하려는 경로(Path)의 유효성을 검증합니다.
     * <p>
     * 기존 경로와 다른 새로운 경로로 변경을 시도할 때, 해당 새 경로가 이미 다른 메뉴에서
     * 사용 중인 경로라면 중복 예외를 발생시킵니다.
     * </p>
     *
     * @param menu 기존 메뉴 엔티티
     * @param newPath 변경하고자 하는 새로운 메뉴 경로
     * @throws BusinessException 변경하려는 경로가 이미 데이터베이스에 존재하는 경우 발생하는 예외
     */
    private void validatePathForUpdate(Menu menu, String newPath) {
        if (!menu.getPath().equals(newPath) && pathExistsCheck(newPath)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PATH);
        }
    }

    /**
     * 메뉴 타입 변경 시 위배되는 제약 조건이 없는지 검증합니다.
     * <p>
     * 타입 변경이 일어나는 경우 다음과 같은 제약 조건을 확인합니다:
     * 1. 기존 타입이 폴더({@code FOLDER})인 상태에서 다른 타입으로 변경하려 할 때, 해당 폴더 하위에 등록된 메뉴가 존재하면 안 됩니다.
     * 2. 기존 타입이 폴더가 아닌 상태에서 폴더({@code FOLDER}) 타입으로 변경하려 할 때, 현재 상위 메뉴에 속해있지 않은 최상위(Root) 레벨이어야 합니다.
     * </p>
     *
     * @param menu 타입 변경을 수행할 대상 메뉴 엔티티
     * @param newType 변경하고자 하는 새로운 메뉴 타입
     * @throws BusinessException 하위 항목이 있는 폴더의 타입을 변경하려 하거나, 상위 메뉴에 속한 항목을 폴더로 변경하려 할 때 발생하는 예외
     */
    private void validateTypeChangeConstraint(Menu menu, MenuType newType) {
        MenuType oldType = menu.getType();

        if (oldType == newType) return;

        if (oldType == MenuType.FOLDER) {
            if (menuRepository.existsByParentId(menu.getId())) {
                throw new BusinessException(ErrorCode.FOLDER_HAS_CHILDREN_CANNOT_CHANGE);
            }
        } else {
            Long parentId = menu.getParent().getId();

            if (Objects.nonNull(parentId) && newType == MenuType.FOLDER) {
                throw new BusinessException(ErrorCode.ITEM_CANNOT_CHANGE_TO_FOLDER);
            }
        }
    }

    /**
     * 신규 등록될 메뉴의 최종 순번(orderSn)을 결정합니다.
     * <p>
     * 요청된 기준 순번({@code requestOrderSn})이 존재하는 경우, 해당 순번 이상의 기존 항목들을 1씩 뒤로 밀어내어(Shift) 공간을 확보한 후 요청된 순번을 반환합니다.
     * 요청된 순번이 없는({@code null}) 경우, 지정된 상위 메뉴({@code parentId}) 하위에 있는 기존 메뉴들의 마지막 순번을 조회하여 그 다음 순번(+1)을 자동으로 생성해 반환합니다.
     * </p>
     *
     * @param parentId 상위 메뉴 ID (최상위 루트 메뉴의 경우 null)
     * @param requestOrderSn 지정하고자 하는 대상 메뉴 순번 (null일 경우 마지막 순번 다음 값으로 자동 배정)
     * @return 신규 메뉴 등록에 사용될 최종 순번
     */
    private Integer resolveOrderSnForRegister(Long parentId, Integer requestOrderSn) {
        if (requestOrderSn != null) {
            shiftOrderSnByTargetOrderSn(parentId, requestOrderSn);
            return requestOrderSn;
        }

        return generateNextOrderSn(parentId);
    }

    /**
     * 지정된 순번과 같거나 큰 기존 메뉴 항목들의 순번을 1씩 증가시켜 등록을 위한 공간을 확보합니다.
     * <p>
     * 상위 메뉴 ID({@code parentId})가 null인 경우 최상위(Root) 메뉴들을 대상으로 순번 업데이트를 수행하며,
     * 존재하는 경우 해당 상위 메뉴에 속한 하위 메뉴들을 대상으로 수행합니다.
     * </p>
     *
     * @param parentId 상위 메뉴 ID (최상위 루트 메뉴인 경우 null)
     * @param targetOrderSn 밀어내기를 시작할 기준 순번
     */
    private void shiftOrderSnByTargetOrderSn(Long parentId, Integer targetOrderSn) {
        if (parentId == null) {
            menuRepository.shiftUpOrderSnAfterRoot(targetOrderSn);
            return;
        }
        menuRepository.shiftUpOrderSnAfter(parentId, targetOrderSn);
    }

    /**
     * 특정 상위 메뉴 하위에서 새롭게 할당할 다음 메뉴 순번을 생성하여 반환합니다.
     * <p>
     * 해당 계층(루트 또는 특정 상위 메뉴 하위)에 등록된 메뉴 중 가장 큰 순번(Max OrderSn)을 조회하여
     * 1을 더한 값을 반환합니다. 등록된 기존 메뉴가 전혀 없는 경우에는 1을 반환합니다.
     * </p>
     *
     * @param parentId 상위 메뉴 ID (최상위 루트 메뉴인 경우 null)
     * @return 신규 메뉴에 할당할 다음 순번
     */
    private Integer generateNextOrderSn(Long parentId) {
        if (parentId == null) {
            return menuRepository.findMaxOrderSnByParentIdIsNull().orElse(0) + 1;
        }
        return menuRepository.findMaxOrderSnByParentId(parentId).orElse(0) + 1;
    }

    /**
     * 메뉴 엔티티와 자식 맵을 기반으로 트리 구조의 {@link MenuResponse}를 재귀적으로 생성합니다.
     * <p>
     * {@code childrenMap}에서 현재 메뉴의 ID에 해당하는 자식 목록을 조회하고,
     * {@code orderSn} 기준 오름차순으로 정렬한 뒤 동일한 방식으로 재귀 호출하여
     * 전체 계층 구조를 {@link MenuResponse}로 변환합니다.
     * 자식이 존재하지 않는 경우 빈 리스트로 처리됩니다.
     * </p>
     *
     * @param menu        변환 대상 메뉴 엔티티
     * @param childrenMap 부모 메뉴 ID를 키로, 자식 메뉴 목록을 값으로 가지는 맵
     * @return 자식 메뉴를 포함한 계층 구조의 {@link MenuResponse}
     */
    private MenuResponse buildMenuTreeResponse(Menu menu, Map<Long, List<Menu>> childrenMap) {
        List<MenuResponse> children = childrenMap
                .getOrDefault(menu.getId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparingInt(m -> m.getOrderSn() != null ? m.getOrderSn() : 0))
                .map(m -> buildMenuTreeResponse(m, childrenMap))   // 재귀
                .collect(Collectors.toList());

        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPath(),
                menu.isUsed(),
                menu.getType(),
                menu.getRole(),
                menu.getParent() != null ? menu.getParent().getId() : null,
                menu.getOrderSn(),
                children
        );
    }
}