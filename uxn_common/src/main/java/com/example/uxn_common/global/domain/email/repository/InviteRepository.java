package com.example.uxn_common.global.domain.email.repository;


import com.example.uxn_common.global.domain.email.Invite;
import com.example.uxn_common.global.domain.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    Invite findByCode(String code);

    void deleteAllByStaff(Staff staff);
}
