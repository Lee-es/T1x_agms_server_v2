package com.example.uxn_common.global.domain.user;

import com.example.uxn_common.global.domain.Login;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String userName;

    @Email
    @Column(length = 20)
    private String email;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private Boolean isMale;

    private String password;

    private LocalDate birth;

    private Boolean isVerified;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private Boolean isEnabled;

    private String phoneNumber;

    private LocalDate expiredAt;

    private String authority;

    private String emailVerifyCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    ///////////////////////////////////////////////////////////////////////////////////////

//    @OneToOne
//    @JoinColumn(name = "id")
//    private Patient patient;

//    public void setPatient(Patient patient) {
//        this.patient = patient;
//        this.patient.setPerson(this);
//    }

    ///////////////////////////////////////////////////////////////////////////////////////


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authority;
            }
        });
        return null;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isEnabled() {
        // 사이트 내에서 1년동안 로그인을 안하면 휴먼계정을 전환을 하도록 하겠다.
        // -> loginDate 타입을 모아놨다가 이 값을 false로 return 해버리면 된다.
        return isEnabled;
    }

    public void emailCheck(boolean emailVerifiedSuccess){
        this.isVerified = emailVerifiedSuccess;
        this.expiredAt = LocalDate.now().plusYears(1);
    }
}
