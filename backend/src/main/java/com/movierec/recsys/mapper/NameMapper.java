package com.movierec.recsys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.movierec.recsys.dto.NameDetailDto;
import com.movierec.recsys.dto.NameSearchResultDto;

import java.util.List;

@Mapper
public interface NameMapper {
    NameDetailDto findNameDetailByNconst(@Param("nconst") String nconst);

    List<NameSearchResultDto> searchNamesByQuery(@Param("query") String query);
}