package com.example.uxn_common.global.domain_new.doctor;

import javax.persistence.Column;
import javax.persistence.Id;

public class Doctor {
    @Id
    @Column(nullable = false)
    private int personId;

    private int hospitalId;

    private int patientCount;

    private String approvalCode;
}
