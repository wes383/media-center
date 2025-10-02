package com.movierec.recsys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private long total;
        private int limit;
        private int offset;
    }

    private List<T> data;
    private Metadata metadata;
}