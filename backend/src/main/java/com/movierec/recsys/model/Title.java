package com.movierec.recsys.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Title {
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private String originalTitle;
    private Boolean isAdult;
    private Integer startYear;
    private Integer endYear;
    private Integer runtimeMinutes;
    private BigDecimal averageRating;
    private Integer numVotes;
}