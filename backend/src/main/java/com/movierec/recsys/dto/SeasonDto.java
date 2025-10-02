package com.movierec.recsys.dto;

import java.util.List;

public class SeasonDto {
    private Integer seasonNumber;
    private List<EpisodeDto> episodes;

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public List<EpisodeDto> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeDto> episodes) {
        this.episodes = episodes;
    }
}