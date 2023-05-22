package com.example.uxn_common.global.domain.staff.repository;

import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StaffAuthorityRepository extends JpaRepository<StaffAuthority, Long> {



}
