package com.movierec.recsys.mapper.me;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.movierec.recsys.dto.MyWatchHistoryDto;
import com.movierec.recsys.model.me.MyWatchHistory;

import java.util.List;

@Mapper
public interface MyWatchHistoryMapper {
    void upsertWatchHistory(MyWatchHistory history);
    MyWatchHistory findByTconst(@Param("tconst") String tconst);

    List<MyWatchHistoryDto> findMyHistory(@Param("titleType") String titleType,
                                          @Param("genres") List<String> genres,
                                          @Param("startYear") Integer startYear,
                                          @Param("minRating") Float minRating,
                                          @Param("sortBy") String sortBy,
                                          @Param("limit") Integer limit,
                                          @Param("offset") Integer offset);

    long countMyHistory(@Param("titleType") String titleType,
                        @Param("genres") List<String> genres,
                        @Param("startYear") Integer startYear,
                        @Param("minRating") Float minRating);

    void deleteByTconst(@Param("tconst") String tconst);
}