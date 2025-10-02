package com.movierec.recsys.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EpisodeDto {
    private String tconst;
    private Integer episodeNumber;
    private String primaryTitle;
    private BigDecimal averageRating;
}