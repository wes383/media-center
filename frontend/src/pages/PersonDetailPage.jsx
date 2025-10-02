import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import './PersonDetailPage.css';

const TMDB_API_KEY = import.meta.env.VITE_TMDB_API_KEY;
const TMDB_BASE_URL = 'https://api.themoviedb.org/3';

const formatCategory = (category) => {
  if (!category) return '';
  return category
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};

const PersonDetailPage = () => {
  const { nconst } = useParams();
  const [person, setPerson] = useState(null);
  const [tmdbPerson, setTmdbPerson] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPersonDetails = async () => {
      setLoading(true);
      setError(null);
      try {
        // Fetch from our API
        const personRes = await fetch(`/api/v1/names/${nconst}`);
        if (!personRes.ok) throw new Error('Failed to fetch person data from backend.');
        const personData = await personRes.json();
        setPerson(personData); // Adjusted for direct API response

        // Fetch from TMDB API
        if (TMDB_API_KEY && TMDB_API_KEY !== 'YOUR_TMDB_API_KEY_HERE') {
          const findRes = await fetch(`${TMDB_BASE_URL}/find/${nconst}?api_key=${TMDB_API_KEY}&external_source=imdb_id`);
          if (findRes.ok) {
            const findData = await findRes.json();
            const personResult = findData.person_results?.[0];
            if (personResult?.id) {
              const tmdbPersonRes = await fetch(`${TMDB_BASE_URL}/person/${personResult.id}?api_key=${TMDB_API_KEY}&language=en-US`);
              if (tmdbPersonRes.ok) {
                const tmdbPersonData = await tmdbPersonRes.json();
                setTmdbPerson(tmdbPersonData);
              }
            }
          }
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchPersonDetails();
  }, [nconst]);

  if (loading) return <div className="container"><h1>Loading...</h1></div>;
  if (error) return <div className="container"><h1>Error: {error}</h1></div>;
  if (!person) return <div className="container"><h1>Person not found.</h1></div>;

  const gender = tmdbPerson?.gender === 1 ? 'Female' : tmdbPerson?.gender === 2 ? 'Male' : 'Not specified';

  const coreProfessions = ['actor', 'actress', 'director', 'writer', 'producer'];
  const filteredFilmography = person.filmography
    ? person.filmography
        .filter(item => coreProfessions.includes(item.category))
        .sort((a, b) => b.startYear - a.startYear)
    : [];

  return (
    <div className="container person-detail-page">
      <div className="person-main-info">
        {tmdbPerson?.profile_path ? (
          <img
            src={`https://image.tmdb.org/t/p/w300${tmdbPerson.profile_path}`}
            alt={person.primaryName}
            className="person-profile-img"
          />
        ) : (
          <div className="person-profile-img-placeholder" />
        )}
        <div className="person-details">
          <h1 className="person-name">{person.primaryName}</h1>
          <div className="person-meta">
            <p><strong>Profession:</strong> {person.primaryProfessions?.map(formatCategory).join(', ') || 'N/A'}</p>
            {tmdbPerson?.birthday && <p><strong>Born:</strong> {tmdbPerson.birthday}</p>}
            {tmdbPerson?.deathday && <p><strong>Died:</strong> {tmdbPerson.deathday}</p>}
            {tmdbPerson?.gender && <p><strong>Gender:</strong> {gender}</p>}
          </div>
        </div>
      </div>

      {tmdbPerson?.biography && (
        <div className="person-biography">
          <h2>Biography</h2>
          <p>{tmdbPerson.biography}</p>
        </div>
      )}

      {filteredFilmography.length > 0 && (
        <div className="person-filmography">
          <h2>Known For</h2>
          <div className="filmography-list">
            {filteredFilmography.map(item => (
              <Link to={`/title/${item.tconst}`} key={`${item.tconst}-${item.category}`} className="filmography-item-link">
                <div className="filmography-item">
                  <span className="filmography-title">{item.primaryTitle} ({item.startYear})</span>
                  <div className="filmography-role">
                    <span className="filmography-cat">{formatCategory(item.category)}</span>
                    {item.characters && item.characters !== '\\N' && JSON.parse(item.characters).length > 0 && (
                      <span className="filmography-char">as {JSON.parse(item.characters).join(', ')}</span>
                    )}
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default PersonDetailPage;