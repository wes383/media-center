package com.movierec.recsys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class MyWatchHistoryDto {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
    private BigDecimal averageRating; // IMDb rating
    private Integer numVotes;
    @JsonProperty("myRating")
    private Integer rating; // My rating from my_watch_history
    private Timestamp addedAt;
}