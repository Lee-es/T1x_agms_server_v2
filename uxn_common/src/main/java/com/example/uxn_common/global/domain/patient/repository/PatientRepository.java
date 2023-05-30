package com.example.uxn_common.global.domain.patient.repository;

import com.example.uxn_common.global.domain.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByUserId(Integer userId);

    @Modifying
    @Query("UPDATE Patient p SET p.errorCount = p.errorCount + 1 WHERE p.email = :email")
    void updateErrorCount(String email);
}
