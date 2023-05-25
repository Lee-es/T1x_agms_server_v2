package com.example.uxn_common.global.domain_new.patient.repository;

import com.example.uxn_common.global.domain_new.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByPersonId(int personId);
}
