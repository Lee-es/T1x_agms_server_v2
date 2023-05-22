package com.example.uxn_common.global.domain.note;

import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Note extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String title;

    private String contents;

    private LocalDateTime createDate;

    @ManyToOne
    private User user;

    public void update(String title, String contents){
        this.title = title;
        this.contents = contents;
    }

}
