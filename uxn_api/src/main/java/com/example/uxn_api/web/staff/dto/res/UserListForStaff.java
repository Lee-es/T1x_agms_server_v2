package com.example.uxn_api.web.staff.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserListForStaff {

    @JsonProperty("user_idx")
    private Long userIdx;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("user_birth")
    private String userBirth;

    @JsonProperty("user_gender")
    private String userGender;

    @JsonProperty("email_verify_date")
    private String emailVerifyDate;

    @JsonProperty("last_data_time")
    private LocalDateTime lastDataTime;

    @JsonProperty("device_id")
    private String deviceID;

    private Double average;

    @JsonProperty("scan_average")
    private int scanAverage; // 평균 스캔 조회 -> 어떻게 구분?

    @JsonProperty("time_sensor_activate")
    private double timeSensorActivate;

    @JsonProperty("under_percent")
    private double underPercent; // 목표값 미만.

    @JsonProperty("goal_percent")
    private double goalPercent; // 목표값

    @JsonProperty("excess_percent")
    private double excessPercent;

    @JsonProperty("low_blood_sugar_count")
    private Long lowBloodSugarCount;

    @JsonProperty("low_blood_sugar_duration")
    private Long lowBloodSugarDuration;

    private List<LowBloodSugarDto> lowBloodSugarDtoList;

    private List<TimeActivate> timeActivateList;

    private HashMap<String, List<Double>> timeActivate;

    private List<ActivateGraphDto> activateGraphDtoList;


}
