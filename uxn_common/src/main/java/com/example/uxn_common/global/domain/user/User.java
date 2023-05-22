package com.example.uxn_common.global.domain.user;

import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.entity.BaseTimeEntity;

import com.example.uxn_common.global.utils.annotation.Password;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "user_idx")
    private Long idx;

//    @Column(unique = true)
//    private String userId;

    private String userName;

    @Email
    private String email;

    private String emailVerifyCode;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String birth;

    private boolean eventCheck;

    private boolean emailVerifiedSuccess;

//    private Long median;
//
//    private Long average;

    private boolean enabled; // 권한 관련

    private int errorCount;

    private boolean accountLock = false;

    private int scanCount;

//    private boolean recognize;

    private LocalDateTime emailVerifyStartTime;

    private LocalDateTime willConfirmDate;

    @Column(columnDefinition = "integer default 70")
    private int minGlucose = 70;
    @Column(columnDefinition = "integer default 180")
    private int maxGlucose = 180;



    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_idx", foreignKey = @ForeignKey(name = "user_idx"))
    private Set<UserAuthority> authorities;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user") // 자식의 엔티티 삭제 처리를 부모가 관리.
    @OrderBy("createDataTime asc")
    private Set<Device> devices;

    /*@ManyToOne
    private Staff staff;*/

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Note> noteList;


    @Override
    public String getUsername() {
        return userName;
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

    /*
    public void update(Staff staff){
        this.staff = staff;
    }

    public void deRegistration(){
        this.staff = null;
    }*/

    public void eventCheckUpdate(boolean eventCheck){
        this.eventCheck = eventCheck;
    }

//    public void updateMedian(Long median){
//        this.median = median;
//    }

    public void emailVerifyCode(String emailVerifyCode){
        this.emailVerifyCode = emailVerifyCode;
    }

    public void emailCheck(boolean emailVerifiedSuccess){
        this.emailVerifiedSuccess = emailVerifiedSuccess;
        this.willConfirmDate = LocalDateTime.now().plusYears(1);
    }

    public void passwordUpdate(String password){
        this.password = password;
    }

    public void accountLockUpdate (boolean accountLock) {
        this.accountLock = accountLock;
    }
    public void errorCountUpdate(int errorCount) {
        this.errorCount = errorCount;
    }

    public void updateScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    /*public void updateRecognize(boolean recognize) {
        this.recognize = recognize;
    }*/
}
