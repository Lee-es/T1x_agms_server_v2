package com.example.uxn_common.global.domain.staff;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.example.uxn_common.global.entity.BaseTimeEntity;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.utils.annotation.Password;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Staff extends Login {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_idx")
    private Long idx;

//    @Column(unique = true)
//    private String staffId;

    private String staffName;

    @Email
    private String email;

    private String password;

//    private String checkPwd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String birth;

    private String phoneNumber;

    private int userCount;

    private String emailVerifyCode;

    private boolean emailVerifiedSuccess;

    private LocalDateTime emailVerifyStartTime;

    private LocalDateTime willConfirmDate;

    private String approvalCode;

    @Embedded
    @AttributeOverride(name = "hospitalName", column = @Column(name = "hospital_name"))
    @AttributeOverride(name = "hospitalAddress", column = @Column(name = "hospital_address"))
    @AttributeOverride(name = "hospitalCall", column = @Column(name = "hospital_call"))
    private Hospital hospital;

    /*
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "staff")
    private final List<User> userList = new ArrayList<>();
    */

    private boolean enabled;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_idx", foreignKey = @ForeignKey(name = "staff_idx"))
    private Set<StaffAuthority> authorities;

    @Override
    public String getUsername() {
        return staffName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void emailCheck(boolean emailVerifiedSuccess){
        this.emailVerifiedSuccess = emailVerifiedSuccess;
        this.willConfirmDate = LocalDateTime.now().plusYears(1);
    }

    public void passwordUpdate(String password){
        this.password = password;
    }
}
