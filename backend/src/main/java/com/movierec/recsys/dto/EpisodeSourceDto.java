package com.movierec.recsys.dto;

public class EpisodeSourceDto {
    private String seriesTconst;
    private String seriesPrimaryTitle;
    private Integer seriesStartYear;
    private Integer seasonNumber;
    private Integer episodeNumber;

    public String getSeriesTconst() {
        return seriesTconst;
    }

    public void setSeriesTconst(String seriesTconst) {
        this.seriesTconst = seriesTconst;
    }

    public String getSeriesPrimaryTitle() {
        return seriesPrimaryTitle;
    }

    public void setSeriesPrimaryTitle(String seriesPrimaryTitle) {
        this.seriesPrimaryTitle = seriesPrimaryTitle;
    }

    public Integer getSeriesStartYear() {
        return seriesStartYear;
    }

    public void setSeriesStartYear(Integer seriesStartYear) {
        this.seriesStartYear = seriesStartYear;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }
}