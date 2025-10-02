import React, { useState, useEffect } from 'react';
import './FilterPanel.css';

const FilterPanel = ({ onApply, initialFilters, initialSortBy }) => {
  const [genres, setGenres] = useState([]);
  const [filterValues, setFilterValues] = useState(initialFilters);
  const [sortBy, setSortBy] = useState(initialSortBy);

  useEffect(() => {
    const fetchGenres = async () => {
      try {
        const res = await fetch('/api/v1/genres');
        if (!res.ok) throw new Error('Failed to fetch genres');
        const data = await res.json();
        setGenres(data.data || []);
      } catch (error) {
        console.error(error);
      }
    };
    fetchGenres();
  }, []);

  const handleFilterChange = (key, value) => {
    setFilterValues(prev => {
      const newValue = prev[key] === value ? null : value;
      return { ...prev, [key]: newValue };
    });
  };

  const handleGenreChange = (genre) => {
    setFilterValues(prev => {
        const currentGenres = prev.genre || [];
        const newGenres = currentGenres.includes(genre)
            ? currentGenres.filter(g => g !== genre)
            : [...currentGenres, genre];
        return { ...prev, genre: newGenres };
    });
  };

  const yearOptions = [2020, 2010, 2000, 1990, 1980, 1970, 1960, 1950, 1900];
  const ratingOptions = [9.0, 8.0, 7.0, 6.0, 5.0, 4.0];
  const voteOptions = [500000, 400000, 300000, 200000, 100000, 50000, 20000, 10000, 5000];
  const sortOptions = [
    { key: 'popularity', label: 'Popularity' },
    { key: 'rating', label: 'Rating' },
    { key: 'releaseDate', label: 'Release Date' },
  ];

  return (
    <div className="panel-container">
      <div className="filter-section">
        <h4 className="filter-section-title">Sort By</h4>
        <div className="panel-row">
          {sortOptions.map(option => (
            <button
              key={option.key}
              className={`panel-btn ${sortBy === option.key ? 'active' : ''}`}
              onClick={() => setSortBy(option.key)}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      <div className="filter-section">
        <h4 className="filter-section-title">Genre</h4>
        <div className="panel-row">
          {genres.map(genre => (
            <button
              key={genre}
              className={`panel-btn ${(filterValues.genre || []).includes(genre) ? 'active' : ''}`}
              onClick={() => handleGenreChange(genre)}
            >
              {genre}
            </button>
          ))}
        </div>
      </div>

      <div className="filter-section">
        <h4 className="filter-section-title">Year (After)</h4>
        <div className="panel-row">
          {yearOptions.map(year => (
            <button
              key={year}
              className={`panel-btn ${filterValues.startYear === year ? 'active' : ''}`}
              onClick={() => handleFilterChange('startYear', year)}
            >
              {year}s
            </button>
          ))}
        </div>
      </div>

      <div className="filter-section">
        <h4 className="filter-section-title">Min Rating</h4>
        <div className="panel-row">
          {ratingOptions.map(rating => (
            <button
              key={rating}
              className={`panel-btn ${filterValues.minRating === rating ? 'active' : ''}`}
              onClick={() => handleFilterChange('minRating', rating)}
            >
              {`> ${rating.toFixed(1)}`}
            </button>
          ))}
        </div>
      </div>

      <div className="filter-section">
        <h4 className="filter-section-title">Min Votes</h4>
        <div className="panel-row">
          {voteOptions.map(votes => (
            <button
              key={votes}
              className={`panel-btn ${filterValues.minVotes === votes ? 'active' : ''}`}
              onClick={() => handleFilterChange('minVotes', votes)}
            >
              {`> ${votes >= 1000 ? `${votes / 1000}k` : votes}`}
            </button>
          ))}
        </div>
      </div>

      <div className="panel-row apply-row">
        <button className="panel-btn" onClick={() => onApply({ filters: filterValues, sortBy })}>
          Apply
        </button>
      </div>
    </div>
  );
};

export default FilterPanel;