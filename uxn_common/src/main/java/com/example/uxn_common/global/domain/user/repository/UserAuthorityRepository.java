package com.example.uxn_common.global.domain.user.repository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    UserAuthority findByIdx(Long userId);
}
