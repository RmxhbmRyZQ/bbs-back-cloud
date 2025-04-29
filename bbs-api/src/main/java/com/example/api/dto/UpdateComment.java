package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateComment {
    private Long pid;
    private String createTime;
}
