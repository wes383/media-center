package com.movierec.recsys.controller;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.dto.TitleDetailDto;
import com.movierec.recsys.model.Title;
import com.movierec.recsys.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    @GetMapping
    public ApiResponse<List<Title>> getTitles(
            @RequestParam(required = false) String titleType,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Integer minVotes,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {

        List<Title> titles = titleService.searchTitles(titleType, genre, startYear, minRating, minVotes, search, sortBy, limit, offset);
        return ApiResponse.success(titles);
    }

    @GetMapping("/{tconst}")
    public ApiResponse<TitleDetailDto> getTitleById(@PathVariable String tconst) {
        TitleDetailDto titleDetail = titleService.getTitleDetails(tconst);
        return ApiResponse.success(titleDetail);
    }
}