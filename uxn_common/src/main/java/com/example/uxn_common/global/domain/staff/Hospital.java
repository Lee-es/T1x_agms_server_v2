package com.example.uxn_common.global.domain.staff;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;

@Embeddable
public class Hospital {

    @JsonProperty("hospital_name")
    private String hospitalName;

    @JsonProperty("hospital_address")
    private String hospitalAddress;

    @JsonProperty("hospital_call")
    private String hospitalCall;
}
