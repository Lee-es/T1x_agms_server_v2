package com.example.uxn_common.global.domain.user.repository;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByEmailAndPassword(String id, String password);

    User findByIdx(Long idx);

    List<User> findAllByIdx(Long idx);

    List<User> findAllByIdxInAndUserNameLike(List<Long> idx, String name);


    Optional<User> findByUserName(String name);


//    @Query(value = "SELECT u.userName FROM User u WHERE u.idx = :userId", nativeQuery = true)
    @Query("SELECT u.userName, u.email, u.birth, u.gender FROM User u WHERE u.idx = :userId")
    String getUserNameByUserID(Long userId);

    @Query("SELECT u.email FROM User u WHERE u.email = :userEmail")
    String getUserEmailByUserEmail(String userEmail);

    @Query("SELECT u.idx FROM User u WHERE u.email = :userEmail")
    Long getUserIdByUserEmail(String userEmail);

    @Query("SELECT u.emailVerifyStartTime FROM User u WHERE u.idx = :userId")
    LocalDateTime getUserVerifyTimeByUserID(Long userId);

    @Modifying
    @Query("UPDATE User u SET u.errorCount = u.errorCount + 1 WHERE u.email = :email")
    void updateErrorCount(String email);

    @Modifying
    @Query("UPDATE User u SET u.scanCount = u.scanCount + 1 WHERE u.userName = :userName")
    void updateScanCount(String userName);

}
