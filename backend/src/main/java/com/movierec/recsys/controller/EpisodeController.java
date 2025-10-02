package com.movierec.recsys.controller;

import com.movierec.recsys.common.ApiResponse;
import com.movierec.recsys.dto.EpisodeSourceDto;
import com.movierec.recsys.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/episodes")
public class EpisodeController {

    @Autowired
    private TitleService titleService;

    @GetMapping("/{tconst}/source")
    public ApiResponse<EpisodeSourceDto> getEpisodeSource(@PathVariable String tconst) {
        EpisodeSourceDto episodeSource = titleService.getEpisodeSource(tconst);
        return ApiResponse.success(episodeSource);
    }
}