package com.example.uxn_common.global.domain.calibration;

import com.example.uxn_common.global.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Calibration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String title;

    private String contents;

//    private LocalDate createdData;
    private LocalDateTime createdDate;

    @ManyToOne
    private User user;

    public void update(String title, String contents){
        this.title = title;
        this.contents = contents;
    }
}
