package com.example.resumebuilder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    private String company;
    private String role;
    private String startDate;
    private String endDate;
    private boolean current;
    private String description;
}
