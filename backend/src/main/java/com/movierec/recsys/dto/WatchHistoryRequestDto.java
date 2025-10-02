package com.movierec.recsys.dto;

import lombok.Data;

@Data
public class WatchHistoryRequestDto {
    private String tconst;
    private Integer rating;
}