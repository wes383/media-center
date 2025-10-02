package com.movierec.recsys.dto;

import lombok.Data;

@Data
public class EpisodeSourceDto {
    private String seriesTconst;
    private String seriesPrimaryTitle;
    private Integer seriesStartYear;
    private Integer seasonNumber;
    private Integer episodeNumber;
}