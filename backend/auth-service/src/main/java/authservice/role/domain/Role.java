package authservice.role.domain;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_com_authorithy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @Column(name = "auth_id")
    @Comment(value = "권한 PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type") 
    @Comment(value = "권한 타입")
    private RoleType type;

    @Column(name = "rm") 
    @Comment(value = "권한에 관한 설명")
    private String description;

    public Role(RoleType type) {
        this.type = type;
        this.description = type.getDescription();
    }
}
