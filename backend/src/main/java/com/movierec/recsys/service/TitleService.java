package com.movierec.recsys.service;

import com.movierec.recsys.common.ResourceNotFoundException;
import com.movierec.recsys.dto.EpisodeSourceDto;
import com.movierec.recsys.dto.SeasonDto;
import com.movierec.recsys.dto.TitleDetailDto;
import com.movierec.recsys.mapper.TitleMapper;
import com.movierec.recsys.model.Title;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TitleService {

    @Autowired
    private TitleMapper titleMapper;

    public List<Title> searchTitles(String titleType, String genre, Integer startYear,
                                    Float minRating, Integer minVotes, String search,
                                    String sortBy, Integer limit, Integer offset) {

        List<String> genreList = null;
        if (StringUtils.hasText(genre)) {
            genreList = Arrays.stream(genre.split(",")).map(String::trim).collect(Collectors.toList());
        }

        // Whitelist sortBy values to prevent SQL injection
        List<String> validSortBy = Arrays.asList("popularity", "rating", "releaseDate");
        String safeSortBy = validSortBy.contains(sortBy) ? sortBy : null;

        return titleMapper.findTitles(titleType, genreList, startYear, minRating, minVotes, search, safeSortBy, limit, offset);
    }

    public TitleDetailDto getTitleDetails(String tconst) {
        TitleDetailDto titleDetail = titleMapper.findTitleDetailByTconst(tconst);
        if (titleDetail != null && "tvSeries".equals(titleDetail.getTitleType())) {
            List<SeasonDto> seasons = titleMapper.findSeasonsByParentTconst(tconst);
            titleDetail.setSeasons(seasons);
        }
        return titleDetail;
    }

    public EpisodeSourceDto getEpisodeSource(String tconst) {
        EpisodeSourceDto episodeSource = titleMapper.findEpisodeSource(tconst);
        if (episodeSource == null) {
            throw new ResourceNotFoundException("Episode with tconst " + tconst + " not found or is not an episode.");
        }
        return episodeSource;
    }
}