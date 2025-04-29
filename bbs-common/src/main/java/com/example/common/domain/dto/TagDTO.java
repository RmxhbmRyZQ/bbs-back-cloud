package com.example.common.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TagDTO {
    private Integer id;

    private Integer optionId;

    private String label;

    private String name;

    private String icon;

    private String detail;

    private TagOptionDTO tagOption;
}
