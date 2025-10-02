package com.movierec.recsys.dto;

import lombok.Data;

@Data
public class TitleSearchResultDto {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
}