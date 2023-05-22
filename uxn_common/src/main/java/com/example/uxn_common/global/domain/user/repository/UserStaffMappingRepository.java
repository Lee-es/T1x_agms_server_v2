package com.example.uxn_common.global.domain.user.repository;

import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStaffMappingRepository extends JpaRepository<UserStaffMapping, Long> {
    List<UserStaffMapping> findAllByUserId(long userId);

    List<UserStaffMapping> findAllByStaffId(long staffId);

    List<UserStaffMapping> findAllByStaffIdAndRecognize(long staffId, boolean recognize);


    void deleteAllByUserId(long userId);

    void deleteAllByStaffId(long userId);

    UserStaffMapping findByUserIdAndStaffId(long userId, long staffId);
}
