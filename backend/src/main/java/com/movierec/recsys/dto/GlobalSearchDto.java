package com.movierec.recsys.dto;

import java.util.List;

public class GlobalSearchDto {
    private List<TitleSearchResultDto> titles;
    private List<NameSearchResultDto> names;

    public List<TitleSearchResultDto> getTitles() {
        return titles;
    }

    public void setTitles(List<TitleSearchResultDto> titles) {
        this.titles = titles;
    }

    public List<NameSearchResultDto> getNames() {
        return names;
    }

    public void setNames(List<NameSearchResultDto> names) {
        this.names = names;
    }
}