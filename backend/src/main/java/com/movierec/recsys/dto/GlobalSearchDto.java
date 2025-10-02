package com.movierec.recsys.dto;

import lombok.Data;
import java.util.List;

@Data
public class GlobalSearchDto {
    private List<TitleSearchResultDto> titles;
    private List<NameSearchResultDto> names;
}