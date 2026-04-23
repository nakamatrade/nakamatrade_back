package authservice.user.domain;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_com_autorithy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @Column(name = "auth_id") 
    @Comment(value = "권한 PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_type") 
    @Comment(value = "권한 타입")
    private String type;

    @Column(name = "rm") 
    @Comment(value = "권한에 관한 설명")
    private String description;

    public Role(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
