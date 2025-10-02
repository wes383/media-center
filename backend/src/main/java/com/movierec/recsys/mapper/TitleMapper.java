package com.movierec.recsys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.movierec.recsys.dto.SeasonDto;
import com.movierec.recsys.dto.TitleDetailDto;
import com.movierec.recsys.dto.EpisodeSourceDto;
import com.movierec.recsys.dto.TitleSearchResultDto;
import com.movierec.recsys.model.Title;

import java.util.List;

@Mapper
public interface TitleMapper {

    List<Title> findTitles(@Param("titleType") String titleType,
                           @Param("genres") List<String> genres,
                           @Param("startYear") Integer startYear,
                           @Param("minRating") Float minRating,
                           @Param("minVotes") Integer minVotes,
                           @Param("search") String search,
                           @Param("sortBy") String sortBy,
                           @Param("limit") Integer limit,
                           @Param("offset") Integer offset);

    TitleDetailDto findTitleDetailByTconst(@Param("tconst") String tconst);

    List<TitleSearchResultDto> searchTitlesByQuery(@Param("query") String query);

    List<SeasonDto> findSeasonsByParentTconst(@Param("parentTconst") String parentTconst);

    EpisodeSourceDto findEpisodeSource(@Param("tconst") String tconst);
}