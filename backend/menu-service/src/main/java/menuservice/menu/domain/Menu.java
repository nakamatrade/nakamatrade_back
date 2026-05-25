package menuservice.menu.domain;

import menuservice.menu.domain.enums.MenuRole;
import menuservice.menu.domain.enums.MenuType;
import org.hibernate.annotations.Comment;
import jakarta.persistence.*;
import lombok.*;
import menuservice.menu.dto.MenuRequest;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment(value = "상위 메뉴 ID")
    private Menu parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> children = new ArrayList<>();

    @Column(name = "order_sn")
    @Comment(value = "정렬 순번")
    private Integer orderSn;

    public static Menu of (MenuRequest requestDto, Menu parentMenu, Integer orderSn) {
        return Menu.builder()
                .name(requestDto.name())
                .path(requestDto.path())
                .isUsed(requestDto.isUsed())
                .type(requestDto.type())
                .role(requestDto.role())
                .parent(parentMenu)
                .orderSn(orderSn)
                .build();
    }
    
    public void update(MenuRequest request, Menu parentMenu) {
        this.name = request.name();
        this.path = request.path();
        this.type = request.type();
        this.parent = parentMenu;
        this.orderSn = request.orderSn();
    }

    public void addChild(Menu child) {
        this.children.add(child);
        child.updateParent(this);
    }

    private void updateParent(Menu parent) {
        this.parent = parent;
    }
}