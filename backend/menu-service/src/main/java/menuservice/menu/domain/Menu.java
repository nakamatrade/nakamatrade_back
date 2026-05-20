package menuservice.menu.domain;

import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;
import org.hibernate.annotations.Comment;
import jakarta.persistence.*;
import lombok.*;
import menuservice.menu.dto.MenuRequest;

@Entity
@Table(name = "tb_com_menu")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    
    @Id @Comment(value = "메뉴 PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(name = "menu_nm", nullable = false)
    @Comment(value = "메뉴명")
    private String name;
    
    @Column(nullable = false, unique = true)
    @Comment(value = "경로")
    private String path;
	
    @Builder.Default
    @Column(name = "use_at", nullable = false)
    @Comment(value = "사용여부")
    private boolean isUsed = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment(value = "메뉴 타입")
    private MenuType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    @Comment(value = "메뉴 권한")
    private MenuRole role;

    @Column(name = "parent_id")
    @Comment(value = "상위 메뉴 ID")
    private Long parentId;
    
    @Column(name = "folder_sn")
    @Comment(value = "폴더 순번")
    private Integer folderSn;
    
    @Column(name = "item_sn")
    @Comment(value = "항목 순번")
    private Integer itemSn;
    
    public static Menu of (MenuRequest requestDto, Long parentId, Integer folderSn, Integer itemSn) {
        return Menu.builder()
                .name(requestDto.name())
                .path(requestDto.path())
                .isUsed(requestDto.isUsed())
                .type(requestDto.type())
                .role(requestDto.role())
                .parentId(parentId)
                .folderSn(folderSn)
                .itemSn(itemSn)
                .build();
    }
    
    public void update(MenuRequest request, Integer folderSn, Integer itemSn) {
        this.name = request.name();
        this.path = request.path();
        this.type = request.type();
        this.parentId = request.parentId();
        this.folderSn = folderSn;
        this.itemSn = itemSn;
    }
}