import React, { useState, useEffect } from 'react';
import './TitleCard.css';

const TMDB_API_KEY = import.meta.env.VITE_TMDB_API_KEY;
const TMDB_BASE_URL = 'https://api.themoviedb.org/3';

const fetchPosterUrl = async (tconst, signal) => {
  if (!TMDB_API_KEY || TMDB_API_KEY === 'YOUR_TMDB_API_KEY_HERE') {
    return null;
  }
  try {
    const response = await fetch(
      `${TMDB_BASE_URL}/find/${tconst}?api_key=${TMDB_API_KEY}&external_source=imdb_id`,
      { signal }
    );
    if (!response.ok) return null;
    const data = await response.json();
    const result = data.movie_results?.[0] || data.tv_results?.[0] || data.tv_episode_results?.[0];
    const path = result?.poster_path || result?.still_path;
    return path ? `https://image.tmdb.org/t/p/w500${path}` : null;
  } catch (err) {
    if (err.name !== 'AbortError') console.error('TMDB fetch error:', err);
    return null;
  }
};

const fetchEpisodeSource = async (tconst, signal) => {
  try {
    const res = await fetch(`/api/v1/episodes/${tconst}/source`, { signal });
    if (!res.ok) {
      return null;
    }
    // The API for this endpoint returns the object directly.
    const data = await res.json();
    return data;
  } catch (err) {
    if (err.name !== 'AbortError') {
      console.error(`Network Error for ${tconst}:`, err);
    }
    return null;
  }
};

const formatVotes = (num) => {
  if (num === null || num === undefined) return null;
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
  }
  return num.toString();
};

const TitleCard = ({ title, filter, sortBy }) => {
  const [posterUrl, setPosterUrl] = useState(null);
  const [episodeSource, setEpisodeSource] = useState(null);

  useEffect(() => {
    const controller = new AbortController();
    const { signal } = controller;

    const fetchAllData = async () => {
      // Reset states
      setPosterUrl(null);
      setEpisodeSource(null);

      const posterPromise = fetchPosterUrl(title.tconst, signal);
      
      let sourcePromise = Promise.resolve(null);
      if (title.titleType === 'tvEpisode' && filter === 'All') {
        sourcePromise = fetchEpisodeSource(title.tconst, signal);
      }

      const [poster, source] = await Promise.all([posterPromise, sourcePromise]);

      if (!signal.aborted) {
        setPosterUrl(poster);
        setEpisodeSource(source);
      }
    };

    fetchAllData();

    return () => {
      controller.abort();
    };
  }, [title.tconst, title.titleType, filter]);

  const hasPoster = !!posterUrl;

  const getBadgeText = () => {
    if (!title) return null;
    switch (sortBy) {
      case 'popularity':
        return title.numVotes ? formatVotes(title.numVotes) : null;
      case 'rating':
        return title.averageRating ? title.averageRating.toFixed(1) : null;
      case 'releaseDate':
        return title.startYear || null;
      default:
        return null;
    }
  };

  const badgeText = getBadgeText();

  const isClickable = title.titleType === 'movie' || title.titleType === 'tvSeries' || title.titleType === 'tvMiniSeries';

  const cardContent = (
    <div className={`title-card ${isClickable ? 'clickable' : ''} ${title.titleType === 'tvEpisode' ? 'hoverable' : ''}`}>
      {badgeText && <div className="sort-badge">{badgeText}</div>}
      <div className="poster-container">
        {hasPoster ? (
          <>
            <img src={posterUrl} alt={title.primaryTitle} className="poster-image" />
            <div className="title-overlay">
              {episodeSource && (
                <div className="episode-source-info">
                  <p className="series-title">{episodeSource.seriesPrimaryTitle}</p>
                  <p className="episode-number">
                    S{episodeSource.seasonNumber} E{episodeSource.episodeNumber}
                  </p>
                </div>
              )}
              <h3 className="overlay-title">{title.primaryTitle}</h3>
            </div>
          </>
        ) : (
          <div className="placeholder-no-poster">
            {episodeSource && (
              <>
                <p className="placeholder-series-title">{episodeSource.seriesPrimaryTitle}</p>
                <p className="placeholder-episode-number">
                  S{episodeSource.seasonNumber} E{episodeSource.episodeNumber}
                </p>
              </>
            )}
            <h3 className="placeholder-title">{title.primaryTitle}</h3>
          </div>
        )}
      </div>
    </div>
  );

  if (isClickable) {
    return (
      <a href={`/title/${title.tconst}`} target="_blank" rel="noopener noreferrer" className="title-card-link">
        {cardContent}
      </a>
    );
  }

  return cardContent;
};

export default TitleCard;