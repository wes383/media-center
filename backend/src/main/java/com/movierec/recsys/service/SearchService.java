package com.movierec.recsys.service;

import com.movierec.recsys.dto.GlobalSearchDto;
import com.movierec.recsys.dto.NameSearchResultDto;
import com.movierec.recsys.dto.TitleSearchResultDto;
import com.movierec.recsys.mapper.NameMapper;
import com.movierec.recsys.mapper.TitleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private TitleMapper titleMapper;

    @Autowired
    private NameMapper nameMapper;

    public GlobalSearchDto search(String query) {
        List<TitleSearchResultDto> titles = titleMapper.searchTitlesByQuery(query);
        List<NameSearchResultDto> names = nameMapper.searchNamesByQuery(query);

        GlobalSearchDto searchDto = new GlobalSearchDto();
        searchDto.setTitles(titles);
        searchDto.setNames(names);

        return searchDto;
    }
}