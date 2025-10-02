package com.movierec.recsys.dto;

public class WatchHistoryRequestDto {
    private String tconst;
    private Integer rating;

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}