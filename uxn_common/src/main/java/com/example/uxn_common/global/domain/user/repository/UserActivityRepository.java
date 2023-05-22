package com.example.uxn_common.global.domain.user.repository;
import com.example.uxn_common.global.domain.user.UserActivity;
import com.example.uxn_common.global.domain.user.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

}
