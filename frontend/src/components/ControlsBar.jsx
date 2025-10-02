import React from 'react';
import FilterToggle from './FilterToggle';
import './ControlsBar.css';

const ControlsBar = ({
  activeFilter,
  onFilterChange,
  onFilterClick,
  isFilterActive,
  isPanelOpen,
  onSearchClick,
}) => {
  return (
    <div className="controls-container">
      <div className="advanced-controls">
        <button onClick={onFilterClick} className={`control-btn ${(isFilterActive || isPanelOpen) ? 'active-filter' : ''}`}>Filter & Sort</button>
      </div>
      <div className="filter-toggle-wrapper">
        <FilterToggle activeFilter={activeFilter} onFilterChange={onFilterChange} />
      </div>
      <div className="search-controls">
        <button onClick={onSearchClick} className="control-btn">Search</button>
      </div>
    </div>
  );
};

export default ControlsBar;