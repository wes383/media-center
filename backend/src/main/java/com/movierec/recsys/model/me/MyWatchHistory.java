package com.movierec.recsys.model.me;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class MyWatchHistory {
    private String tconst;
    private Integer rating;
    private Timestamp addedAt;
}