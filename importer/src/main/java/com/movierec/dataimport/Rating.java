package com.movierec.dataimport;

import java.math.BigDecimal;

import java.math.BigDecimal;

public class Rating {
    private final BigDecimal averageRating;
    private final Integer numVotes;

    public Rating(BigDecimal averageRating, Integer numVotes) {
        this.averageRating = averageRating;
        this.numVotes = numVotes;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }
}