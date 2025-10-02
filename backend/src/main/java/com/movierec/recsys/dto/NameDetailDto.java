package com.movierec.recsys.dto;

import java.util.List;

public class NameDetailDto {
    private String nconst;
    private String primaryName;
    private Integer birthYear;
    private Integer deathYear;
    private List<String> primaryProfessions;
    private List<FilmographyDto> filmography;

    public String getNconst() {
        return nconst;
    }

    public void setNconst(String nconst) {
        this.nconst = nconst;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public List<String> getPrimaryProfessions() {
        return primaryProfessions;
    }

    public void setPrimaryProfessions(List<String> primaryProfessions) {
        this.primaryProfessions = primaryProfessions;
    }

    public List<FilmographyDto> getFilmography() {
        return filmography;
    }

    public void setFilmography(List<FilmographyDto> filmography) {
        this.filmography = filmography;
    }
}