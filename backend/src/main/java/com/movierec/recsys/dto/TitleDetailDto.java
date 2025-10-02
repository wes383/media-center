package com.movierec.recsys.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TitleDetailDto {
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private Integer startYear;
    private Integer runtimeMinutes;
    private BigDecimal averageRating;
    private Integer numVotes;
    private List<String> genres;
    private List<PrincipalDto> principals;
    private List<SeasonDto> seasons;
}