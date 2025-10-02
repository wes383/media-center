import React, { useState, useEffect, useCallback } from 'react';
import TitleCard from '../components/TitleCard';
import ControlsBar from '../components/ControlsBar';
import FilterPanel from '../components/FilterPanel';
import SearchBar from '../components/SearchBar';

const LIMIT = 30;

const HomePage = () => {
  const [titles, setTitles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('All');
  const [offset, setOffset] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [showFilterPanel, setShowFilterPanel] = useState(false);
  const [sortBy, setSortBy] = useState('popularity');
  const [filters, setFilters] = useState({});
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  const isFilterActive = Object.values(filters).some(v => Array.isArray(v) ? v.length > 0 : v);

  const buildApiUrl = useCallback((currentOffset) => {
    const params = new URLSearchParams({
      sortBy,
      limit: LIMIT,
      offset: currentOffset,
    });

    if (searchQuery) {
      params.set('search', searchQuery);
    } else {
      if (filter === 'Movies') {
        params.set('titleType', 'movie');
      } else if (filter === 'TV Shows') {
        params.set('titleType', 'tvSeries');
      }

      Object.entries(filters).forEach(([key, value]) => {
        if (Array.isArray(value)) {
          if (value.length > 0) {
            params.set(key, value.join(','));
          }
        } else if (value) {
          params.set(key, value);
        }
      });
    }

    return `/api/v1/titles?${params.toString()}`;
  }, [filter, sortBy, filters, searchQuery]);

  useEffect(() => {
    const fetchInitialTitles = async () => {
      setLoading(true);
      setTitles([]);
      setOffset(0);
      setHasMore(true);
      setError(null);

      try {
        const url = buildApiUrl(0);
        const res = await fetch(url);
        if (!res.ok) throw new Error(`Failed to fetch titles`);
        const data = await res.json();
        const newTitles = data.data || [];

        setTitles(newTitles);
        setOffset(newTitles.length);
        setHasMore(newTitles.length === LIMIT);
      } catch (err) {
        setError(err.message);
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchInitialTitles();
  }, [filter, sortBy, filters, searchQuery]);

  const handleLoadMore = async () => {
    if (loadingMore || !hasMore) return;

    setLoadingMore(true);
    setError(null);
    try {
      const url = buildApiUrl(offset);
      const res = await fetch(url);
      if (!res.ok) throw new Error(`Failed to fetch more titles`);
      const data = await res.json();
      const newTitles = data.data || [];

      setTitles(prev => [...prev, ...newTitles]);
      setOffset(prev => prev + newTitles.length);
      setHasMore(newTitles.length === LIMIT);
    } catch (err) {
      setError(err.message);
      console.error(err);
    } finally {
      setLoadingMore(false);
    }
  };

  if (loading) {
    return <div className="container"><h1>Loading...</h1></div>;
  }

  if (error && titles.length === 0) {
    return <div className="container"><h1>Error: {error}</h1><p>Is the backend server running?</p></div>;
  }

  const handleApply = ({ filters: newFilters, sortBy: newSortBy }) => {
    setFilters(newFilters);
    setSortBy(newSortBy);
    setShowFilterPanel(false);
    setSearchQuery(''); // Clear search when applying filters
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
    setIsSearching(false);
    setShowFilterPanel(false);
    setFilters({});
  };

  const clearSearch = () => {
    setSearchQuery('');
  };

  return (
    <div className="container">
      {isSearching ? (
        <SearchBar onSearch={handleSearch} onClose={() => setIsSearching(false)} />
      ) : (
        <ControlsBar
          activeFilter={filter}
          onFilterChange={(newFilter) => {
            setFilter(newFilter);
            setSearchQuery(''); // Clear search when changing filter
          }}
          onFilterClick={() => {
            setShowFilterPanel(!showFilterPanel);
            setSearchQuery(''); // Clear search when opening filters
          }}
          onSearchClick={() => setIsSearching(true)}
          isFilterActive={isFilterActive}
          isPanelOpen={showFilterPanel}
        />
      )}
      {showFilterPanel && !isSearching && (
        <FilterPanel
          onApply={handleApply}
          initialFilters={filters}
          initialSortBy={sortBy}
        />
      )}
      <section>
        {searchQuery && (
          <div className="search-results-header">
            <h2>Searching for: "{searchQuery}"</h2>
            <button onClick={clearSearch} className="clear-search-btn">Clear</button>
          </div>
        )}
        <div className="titles-grid">
          {titles.map((title) => (
            <TitleCard key={title.tconst} title={title} filter={filter} sortBy={sortBy} />
          ))}
        </div>
        {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
        {hasMore && !searchQuery && (
          <div style={{ display: 'flex', justifyContent: 'center', margin: '2rem 0' }}>
            <button
              onClick={handleLoadMore}
              disabled={loadingMore}
              className="load-more-btn"
            >
              {loadingMore ? 'Loading...' : 'Load More'}
            </button>
          </div>
        )}
      </section>
    </div>
  );
};

export default HomePage;