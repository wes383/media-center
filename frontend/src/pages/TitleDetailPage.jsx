import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import './TitleDetailPage.css';

const TMDB_API_KEY = import.meta.env.VITE_TMDB_API_KEY;
const TMDB_BASE_URL = 'https://api.themoviedb.org/3';

const getRatingColorClass = (rating) => {
  if (rating === null || rating === undefined) return '';
  if (rating >= 8) return 'rating-green';
  if (rating >= 4) return 'rating-yellow';
  if (rating >= 1) return 'rating-red';
  return '';
};

const TitleDetailPage = () => {
  const { tconst } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState(null);
  const [tmdbDetails, setTmdbDetails] = useState(null);
  const [seasonsData, setSeasonsData] = useState([]);
  const [openSeasons, setOpenSeasons] = useState([]);
  const [posterUrl, setPosterUrl] = useState(null);
  const [castProfileMap, setCastProfileMap] = useState(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTitleDetails = async () => {
      setLoading(true);
      setError(null);
      setPosterUrl(null);
      setTmdbDetails(null);
      setSeasonsData([]);
      setCastProfileMap(new Map());

      try {
        // Fetch main title data from our backend
        const titleRes = await fetch(`/api/v1/titles/${tconst}`);
        if (!titleRes.ok) {
          throw new Error(`Failed to fetch title details. Status: ${titleRes.status}`);
        }
        const titleData = await titleRes.json();
        const finalTitleData = titleData.data || titleData;
        setTitle(finalTitleData);

        // Fetch cast profiles by nconst
        if (TMDB_API_KEY && TMDB_API_KEY !== 'YOUR_TMDB_API_KEY_HERE' && finalTitleData.principals?.length > 0) {
          const profilePromises = finalTitleData.principals.map(async (person) => {
            if (!person.nconst) return null;
            try {
              const findRes = await fetch(`${TMDB_BASE_URL}/find/${person.nconst}?api_key=${TMDB_API_KEY}&external_source=imdb_id`);
              if (findRes.ok) {
                const findData = await findRes.json();
                const personResult = findData.person_results?.[0];
                if (personResult?.profile_path) {
                  return [person.nconst, personResult.profile_path];
                }
              }
            } catch (e) {
              console.error(`Failed to fetch profile for ${person.nconst}`, e);
            }
            return null;
          });

          const profiles = (await Promise.all(profilePromises)).filter(Boolean);
          setCastProfileMap(new Map(profiles));
        }

        // Fetch additional details from TMDB
        if (TMDB_API_KEY && TMDB_API_KEY !== 'YOUR_TMDB_API_KEY_HERE') {
          const findRes = await fetch(
            `${TMDB_BASE_URL}/find/${tconst}?api_key=${TMDB_API_KEY}&external_source=imdb_id`
          );
          if (findRes.ok) {
            const findData = await findRes.json();
            const result = findData.movie_results?.[0] || findData.tv_results?.[0];
            
            if (result?.poster_path) {
              setPosterUrl(`https://image.tmdb.org/t/p/w500${result.poster_path}`);
            }

            if (result?.id) {
              const type = finalTitleData.titleType === 'movie' ? 'movie' : 'tv';
              const detailRes = await fetch(
                `${TMDB_BASE_URL}/${type}/${result.id}?api_key=${TMDB_API_KEY}&language=en-US`
              );
              if (detailRes.ok) {
                const detailData = await detailRes.json();
                setTmdbDetails(detailData);

                // If it's a TV show, fetch all season details
                if (finalTitleData.titleType !== 'movie' && finalTitleData.seasons) {
                  // Fetch all season details from TMDB to get episode overviews, runtimes, etc.
                  const tmdbSeasonPromises = (detailData.seasons || [])
                    .map(season =>
                      fetch(`${TMDB_BASE_URL}/tv/${result.id}/season/${season.season_number}?api_key=${TMDB_API_KEY}&language=en-US`)
                        .then(res => res.ok ? res.json() : Promise.resolve(null))
                    );
                  
                  const tmdbFetchedSeasons = (await Promise.all(tmdbSeasonPromises)).filter(Boolean);

                  // Create a map of TMDB episode data for easy lookup.
                  const tmdbEpisodesMap = new Map();
                  tmdbFetchedSeasons.forEach(season => {
                    if (season && season.episodes) {
                      season.episodes.forEach(ep => {
                        const key = `S${season.season_number}E${ep.episode_number}`;
                        tmdbEpisodesMap.set(key, {
                          overview: ep.overview,
                          air_date: ep.air_date,
                          runtime: ep.runtime,
                        });
                      });
                    }
                  });

                  // Enrich our backend's season data with the corresponding TMDB data.
                  const enrichedSeasons = finalTitleData.seasons.map(backendSeason => {
                    const enrichedEpisodes = backendSeason.episodes.map(backendEpisode => {
                      const key = `S${backendSeason.seasonNumber}E${backendEpisode.episodeNumber}`;
                      const tmdbData = tmdbEpisodesMap.get(key) || {};
                      return { ...backendEpisode, ...tmdbData }; // Merge our data with TMDB's
                    });
                    // The season object from our backend already has seasonNumber and episodes array.
                    // We just need to replace the episodes array with the enriched one.
                    return { ...backendSeason, episodes: enrichedEpisodes };
                  });

                  setSeasonsData(enrichedSeasons);
                }
              }
            }
          }
        }
      } catch (err) {
        setError(err.message);
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    window.scrollTo(0, 0);
    fetchTitleDetails();
  }, [tconst]);

  const formatVotes = (num) => {
    if (num === null || num === undefined) return 'N/A';
    if (num >= 1000000) return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
    return num.toString();
  };

  const formatCurrency = (num) => {
    if (!num) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(num);
  };

  const handleSeasonToggle = (seasonNumber) => {
    setOpenSeasons(prevOpenSeasons => {
      if (prevOpenSeasons.includes(seasonNumber)) {
        return prevOpenSeasons.filter(s => s !== seasonNumber);
      } else {
        return [...prevOpenSeasons, seasonNumber];
      }
    });
  };

  if (loading) {
    return <div className="container"><h1>Loading...</h1></div>;
  }

  if (error) {
    return <div className="container"><h1>Error: {error}</h1></div>;
  }

  if (!title) {
    return <div className="container"><h1>Title not found.</h1></div>;
  }

  return (
    <div className="container detail-page-container">
      <div className="detail-content">
        {posterUrl && (
          <div className="detail-poster">
            <img src={posterUrl} alt={title.primaryTitle} />
          </div>
        )}
        <div className="detail-info" style={!posterUrl ? { gridColumn: '1 / -1' } : {}}>
          <h1 className="detail-title">{title.primaryTitle}</h1>
          <div className="detail-meta">
            <span>{title.startYear}{title.endYear ? `–${title.endYear}` : ''}</span>
            {title.runtimeMinutes && <span>{title.runtimeMinutes} min</span>}
          </div>
          <div className="detail-genres">
            {title.genres?.map(genre => <span key={genre} className="genre-tag">{genre}</span>)}
          </div>
          <div className="detail-rating">
            <div>
              <span className={`rating-value ${getRatingColorClass(title.averageRating)}`}>{title.averageRating?.toFixed(1) || 'N/A'}</span>
              <span className="rating-total">/ 10</span>
            </div>
            <div className="rating-votes">{formatVotes(title.numVotes)} votes</div>
          </div>
          <p className="detail-plot">{tmdbDetails?.overview || title.plot}</p>

          {tmdbDetails && (
            <div className="detail-extra-info">
              {title.titleType === 'movie' ? (
                <>
                  <div className="info-item"><strong>Release Date:</strong> {tmdbDetails.release_date || 'N/A'}</div>
                  <div className="info-item"><strong>Countries:</strong> {tmdbDetails.production_countries?.map(c => c.name).join(', ') || 'N/A'}</div>
                  <div className="info-item"><strong>Languages:</strong> {tmdbDetails.spoken_languages?.map(l => l.english_name).join(', ') || 'N/A'}</div>
                  <div className="info-item"><strong>Budget:</strong> {formatCurrency(tmdbDetails.budget)}</div>
                  <div className="info-item"><strong>Revenue:</strong> {formatCurrency(tmdbDetails.revenue)}</div>
                </>
              ) : (
                <>
                  <div className="info-item"><strong>First Aired:</strong> {tmdbDetails.first_air_date || 'N/A'}</div>
                  <div className="info-item"><strong>Last Aired:</strong> {tmdbDetails.last_air_date || 'N/A'}</div>
                  <div className="info-item"><strong>Status:</strong> {tmdbDetails.in_production ? 'In Production' : 'Ended'}</div>
                  <div className="info-item"><strong>Seasons:</strong> {tmdbDetails.number_of_seasons || 'N/A'}</div>
                  <div className="info-item"><strong>Episodes:</strong> {tmdbDetails.number_of_episodes || 'N/A'}</div>
                  <div className="info-item"><strong>Networks:</strong> {tmdbDetails.networks?.map(n => n.name).join(', ') || 'N/A'}</div>
                  <div className="info-item"><strong>Countries:</strong> {tmdbDetails.origin_country?.join(', ') || 'N/A'}</div>
                  <div className="info-item"><strong>Languages:</strong> {tmdbDetails.spoken_languages?.map(l => l.english_name).join(', ') || 'N/A'}</div>
                </>
              )}
            </div>
          )}

          {seasonsData && seasonsData.length > 0 && (
            <div className="detail-section">
              <h2 className="section-title">Seasons & Episodes</h2>
              <div className="seasons-accordion">
                {seasonsData.map(season => (
                  <div key={season.seasonNumber} className={`season-item ${openSeasons.includes(season.seasonNumber) ? 'open' : ''}`}>
                    <button
                      className="season-header"
                      onClick={() => handleSeasonToggle(season.seasonNumber)}
                    >
                      Season {season.seasonNumber}
                    </button>
                    {openSeasons.includes(season.seasonNumber) && (
                      <div className="episodes-list">
                        {season.episodes.map(ep => (
                          <div key={ep.tconst} className="episode-item">
                            <div className="episode-header">
                              <span className="episode-number">E{ep.episodeNumber}</span>
                              <span className="episode-title">{ep.primaryTitle}</span>
                              <span className="episode-rating">
                                <svg width="13" height="12" fill="currentColor" viewBox="0 0 24 24"><path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"></path></svg>
                                {ep.averageRating?.toFixed(1) || ''}
                              </span>
                            </div>
                            <p className="episode-overview">{ep.overview || ''}</p>
                            <div className="episode-meta">
                              <span>{ep.air_date || ''}</span>
                              <span>{ep.runtime ? `${ep.runtime} min` : ''}</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {title.principals && title.principals.length > 0 && (
            <div className="detail-section">
              <h2 className="section-title">Cast & Crew</h2>
              <div className="cast-list">
                {title.principals.map((person, index) => {
                  const profilePath = castProfileMap.get(person.nconst);
                  return (
                    <Link to={`/person/${person.nconst}`} key={`${person.nconst}-${index}`} className="cast-member-link">
                      <div className="cast-member">
                        {profilePath ? (
                          <img
                            className="cast-avatar"
                            src={`https://image.tmdb.org/t/p/w185${profilePath}`}
                            alt={person.primaryName}
                          />
                        ) : (
                          <div className="cast-avatar" />
                        )}
                        <span className="cast-name">{person.primaryName}</span>
                        <span className="cast-char">
                          {person.category === 'actor' || person.category === 'actress'
                            ? (person.characters ? JSON.parse(person.characters).join(', ') : '')
                            : person.category.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ')
                          }
                        </span>
                      </div>
                    </Link>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TitleDetailPage;