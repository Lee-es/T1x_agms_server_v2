package com.example.uxn_common.global.domain.patient.repository;

import com.example.uxn_common.global.domain.patient.PatientDevice;
import com.example.uxn_common.global.domain.user.PatientStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientDeviceRepository extends JpaRepository<PatientDevice, Integer> {
    List<PatientDevice> findAllByPatientId(Integer patientId);
}
