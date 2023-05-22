package com.example.uxn_common.global.domain.user.repository;

import com.example.uxn_common.global.domain.user.UserStaffMapping;
import com.example.uxn_common.global.domain.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    List<UserToken> findAllByEmail(String email);

    List<UserToken> findAllByEmailAndDeviceAndTokenNot(String email,String device, String token);
    List<UserToken> findAllByEmailAndToken(String email,String token);
    List<UserToken> findAllByToken(String token);

    List<UserToken> findAllByRefreshToken(String refreshToken);


    List<UserToken> findAllByEmailAndDevice(String email, String device);
    List<UserToken> findAllByEmailAndDeviceNot(String email, String device);
    List<UserToken> findAllByTokenAndDevice(String token, String device);

    List<UserToken> findAllByTokenAndIp(String token, String ip);

    List<UserToken> findAllByTokenAndIpAndDevice(String token, String ip, String device);

}
