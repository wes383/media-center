package com.movierec.recsys.dto;

import lombok.Data;

@Data
public class FilmographyDto {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
    private String category;
    private String characters;
}