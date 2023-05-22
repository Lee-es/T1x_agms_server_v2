package com.example.uxn_common.global.domain.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(StaffAuthority.class)
@Data
public class StaffAuthority implements GrantedAuthority {

    @Id
    private Long idx;

    @Id
    private String authority;

}
