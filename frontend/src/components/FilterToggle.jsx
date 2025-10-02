import React from 'react';
import './FilterToggle.css';

const FilterToggle = ({ activeFilter, onFilterChange }) => {
  const filters = ['All', 'Movies', 'TV Shows'];

  return (
    <div className="filter-toggle-container">
      {filters.map((filter) => (
        <button
          key={filter}
          className={`filter-toggle-btn ${filter.toLowerCase().replace(' ', '-')} ${activeFilter === filter ? 'active' : ''}`}
          onClick={() => onFilterChange(filter)}
        >
          {filter}
        </button>
      ))}
    </div>
  );
};

export default FilterToggle;