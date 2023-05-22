package com.example.uxn_common.global.domain.user.repository;

import com.example.uxn_common.global.domain.user.ChangePasswordToken;
import com.example.uxn_common.global.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangePasswordTokenRepository extends JpaRepository<ChangePasswordToken, String> {
    List<ChangePasswordToken> findAllByEmail(String email);
    ChangePasswordToken findTopByTokenOrderByCreateTimeDesc(String token);

}
