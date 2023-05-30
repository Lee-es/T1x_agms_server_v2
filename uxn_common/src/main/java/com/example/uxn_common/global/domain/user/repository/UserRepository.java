package com.example.uxn_common.global.domain.user.repository;

import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Integer id);

    User findByEmail(String email);

    User findByEmailAndPassword(String id, String password);

    void deleteAllByUserId(Integer userId);

    String findAuthorityByIdx(Integer id);

    @Query("SELECT u.email FROM User u WHERE u.email = :userEmail")
    String getUserEmailByUserEmail(String userEmail);

    @Query("SELECT u.idx FROM User u WHERE u.email = :userEmail")
    Long getUserIdByUserEmail(String userEmail);

    @Query("SELECT u.userName, u.email, u.birth, u.isMale FROM User u WHERE u.id = :userId")
    String getUserNameByUserID(Integer userId);

    @Query("SELECT u.createdAt FROM User u WHERE u.id = :userId")
    LocalDateTime getUserVerifyTimeByUserID(Integer userId);

    List<User> findAllByIdxInAndUserNameLike(List<Integer> idx, String name);

    Optional<User> findByUserName(String name);
}