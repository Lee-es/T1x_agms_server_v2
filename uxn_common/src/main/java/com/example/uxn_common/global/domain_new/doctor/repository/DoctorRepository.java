package com.example.uxn_common.global.domain_new.doctor.repository;

import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain_new.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DoctorRepository extends JpaRepository<Staff, Long> {
    Patient findByPersonId(int personId);
}