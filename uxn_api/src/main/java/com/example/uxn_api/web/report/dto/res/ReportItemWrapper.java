package com.example.uxn_api.web.report.dto.res;

import lombok.Data;

import java.util.List;

@Data
public class ReportItemWrapper {
    private int lastPage;
    private List<ReportItemDto> list;
}
