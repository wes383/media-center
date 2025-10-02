package com.movierec.recsys.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeasonDto {
    private Integer seasonNumber;
    private List<EpisodeDto> episodes;
}