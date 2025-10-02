package com.movierec.recsys.service.me;

import com.movierec.recsys.dto.MyWatchHistoryDto;
import com.movierec.recsys.dto.PaginatedResponseDto;
import com.movierec.recsys.dto.WatchHistoryRequestDto;
import com.movierec.recsys.mapper.me.MyWatchHistoryMapper;
import com.movierec.recsys.model.me.MyWatchHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyWatchHistoryService {

    @Autowired
    private MyWatchHistoryMapper myWatchHistoryMapper;

    public MyWatchHistory addOrUpdateHistory(WatchHistoryRequestDto requestDto) {
        MyWatchHistory history = new MyWatchHistory();
        history.setTconst(requestDto.getTconst());
        history.setRating(requestDto.getRating());

        myWatchHistoryMapper.upsertWatchHistory(history);

        return myWatchHistoryMapper.findByTconst(requestDto.getTconst());
    }

    public PaginatedResponseDto<MyWatchHistoryDto> getMyHistory(
            String titleType, String genre, Integer startYear, Float minRating,
            String sortBy, Integer limit, Integer offset) {

        List<String> genreList = null;
        if (StringUtils.hasText(genre)) {
            genreList = Arrays.stream(genre.split(",")).map(String::trim).collect(Collectors.toList());
        }

        List<String> validSortBy = Arrays.asList("popularity", "rating", "releaseDate", "my_rating", "added_at");
        String safeSortBy = validSortBy.contains(sortBy) ? sortBy : "added_at";

        List<MyWatchHistoryDto> historyList = myWatchHistoryMapper.findMyHistory(
                titleType, genreList, startYear, minRating, safeSortBy, limit, offset);

        long total = myWatchHistoryMapper.countMyHistory(titleType, genreList, startYear, minRating);

        PaginatedResponseDto.Metadata metadata = new PaginatedResponseDto.Metadata(total, limit, offset);
        return new PaginatedResponseDto<>(historyList, metadata);
    }

    public MyWatchHistory getHistoryByTconst(String tconst) {
        return myWatchHistoryMapper.findByTconst(tconst);
    }

    public void deleteHistory(String tconst) {
        myWatchHistoryMapper.deleteByTconst(tconst);
    }
}