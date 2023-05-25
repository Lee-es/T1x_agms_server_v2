package com.example.uxn_common.global.domain_new.person;

import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.example.uxn_common.global.domain_new.patient.Patient;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(length = 20)
    private String email;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean isMale;

    private String password;

    private LocalDate birth;

    private boolean isVerified;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isEnabled;

    private String phoneNumber;

    private LocalDate expiredAt;

    private short role1;

    ///////////////////////////////////////////////////////////////////////////////////////

//    @OneToOne
//    @JoinColumn(name = "id")
//    private Patient patient;

//    public void setPatient(Patient patient) {
//        this.patient = patient;
//        this.patient.setPerson(this);
//    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void emailCheck(boolean emailVerifiedSuccess){
        this.isVerified = emailVerifiedSuccess;
        this.expiredAt = LocalDate.now().plusYears(1);
    }
}
