package com.movierec.recsys.dto;

import lombok.Data;
import java.util.List;

@Data
public class NameDetailDto {
    private String nconst;
    private String primaryName;
    private Integer birthYear;
    private Integer deathYear;
    private List<String> primaryProfessions;
    private List<FilmographyDto> filmography;
}