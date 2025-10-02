package com.movierec.recsys.dto;

import java.util.List;

public class PaginatedResponseDto<T> {

    public static class Metadata {
        private long total;
        private int limit;
        private int offset;

        public Metadata() {
        }

        public Metadata(long total, int limit, int offset) {
            this.total = total;
            this.limit = limit;
            this.offset = offset;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    private List<T> data;
    private Metadata metadata;

    public PaginatedResponseDto() {
    }

    public PaginatedResponseDto(List<T> data, Metadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}