package com.movierec.recsys.controller;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public ApiResponse<List<String>> getAllGenres() {
        List<String> genres = genreService.getAllGenres();
        return ApiResponse.success(genres);
    }
}