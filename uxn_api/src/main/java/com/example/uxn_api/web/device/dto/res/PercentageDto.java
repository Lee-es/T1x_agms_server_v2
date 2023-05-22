package com.example.uxn_api.web.device.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PercentageDto {

    private ArrayList<Double> five;

    @JsonProperty("twenty_five")
    private ArrayList<Double> twentyFive;

    private ArrayList<Double> fifty;

    @JsonProperty("seventy_five")
    private ArrayList<Double> seventyFive;

    @JsonProperty("ninety_five")
    private ArrayList<Double> ninetyFive;

}
