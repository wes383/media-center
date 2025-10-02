package com.movierec.recsys.service;

import com.movierec.recsys.mapper.GenreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreMapper genreMapper;

    public List<String> getAllGenres() {
        return genreMapper.findAllGenreNames();
    }
}