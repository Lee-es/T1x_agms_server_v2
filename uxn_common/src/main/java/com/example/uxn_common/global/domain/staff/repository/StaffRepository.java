package com.example.uxn_common.global.domain.staff.repository;

import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.patient.Patient;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Staff findByUserId(Integer userId);

    Staff findByEmail(String email);
}