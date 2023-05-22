package com.example.uxn_common.global.domain.staff.repository;

import com.example.uxn_common.global.domain.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Staff findByStaffName(String name);

    Staff findByIdx(Long id);

    Staff findByEmail(String email);

    Staff findByEmailAndPassword(String id, String password);

    @Modifying
    @Query("UPDATE Staff s SET s.userCount = s.userCount + 1 WHERE s.idx = :id")
    void updatePlusUserCount(Long id);

    @Modifying
    @Query("UPDATE Staff s SET s.userCount = s.userCount + 1 WHERE s.email = :email")
    void updatePlusUserCountWithEmail(String email);

    @Modifying
    @Query("UPDATE Staff s SET s.userCount = s.userCount - 1 WHERE s.idx = :id")
    void updateMinusUserCount(Long id);


}
