package com.example.uxn_common.global.domain.user.repository;

import com.example.uxn_common.global.domain.user.PatientStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientStaffRepository extends JpaRepository<PatientStaff, Integer> {
    List<PatientStaff> findAllByPatientId(Integer patientId);

    List<PatientStaff> findAllByStaffId(Integer staffId);

    List<PatientStaff> findAllByStaffIdAndIsRecognized(Integer staffId, Boolean isRecognized);


    void deleteAllByPatientId(Integer patientId);

    void deleteAllByStaffId(Integer staffId);

    PatientStaff findByPatientIdAndStaffId(Integer patientId, Integer staffId);
}
