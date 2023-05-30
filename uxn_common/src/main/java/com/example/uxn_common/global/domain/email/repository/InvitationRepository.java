package com.example.uxn_common.global.domain.email.repository;


import com.example.uxn_common.global.domain.email.Invitation;
import com.example.uxn_common.global.domain.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    Invitation findByCode(String code);

    void deleteAllByStaffId(Integer staffId);
}
