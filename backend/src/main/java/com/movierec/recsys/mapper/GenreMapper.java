package com.movierec.recsys.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GenreMapper {
    List<String> findAllGenreNames();
}