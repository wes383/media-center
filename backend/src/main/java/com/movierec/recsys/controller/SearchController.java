package com.movierec.recsys.controller;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.dto.GlobalSearchDto;
import com.movierec.recsys.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ApiResponse<GlobalSearchDto> search(@RequestParam String query) {
        GlobalSearchDto result = searchService.search(query);
        return ApiResponse.success(result);
    }
}