package authservice.user.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_com_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    private static final int MAX_FAIL_COUNT = 5;

    @Id
    @Column(name = "user_id")
    @Comment(value = "사용자 PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_nm", unique = true, nullable = false)
    @Comment(value = "사용자 ID")
    private String username;

    @Column(name = "pswd", nullable = false)
    @Comment(value = "비밀번호")
    private String password;

    @Column(name = "brdt", nullable = false)
    @Comment(value = "생년월일")
    private String birthDate;

    @Column(name = "gender")
    @Comment(value = "성별")
    private String gender;

    @UpdateTimestamp
    @Column(name = "last_lgn_dt")
    @Comment(value = "마지막 로그인 시도 시간")
    private LocalDateTime lastLoginDate;

    @Column(name = "lgn_fail_cnt")
    @Comment(value = "로그인 실패 횟수")
    private int failCount;

    @Column(name = "lck_yn")
    @Comment(value = "로그인 잠금 여부")
    private boolean locked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    @Comment(value = "권한 FK")
    private Role role;

    @Builder
    public User(String username, String password, String birthDate, String gender, Role role) {
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.role = role;
    }

    public void recordLoginFailure() {
        this.failCount++;
        if (this.failCount >= MAX_FAIL_COUNT) {
            this.locked = true;
        }
    }

    public void resetFailCount() {
        this.failCount = 0;
        this.locked = false;
    }

    public String getRoleType() {
        return role.getType();
    }
}
