import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import TitleDetailPage from './pages/TitleDetailPage';
import PersonDetailPage from './pages/PersonDetailPage';
import './App.css';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/title/:tconst" element={<TitleDetailPage />} />
        <Route path="/person/:nconst" element={<PersonDetailPage />} />
      </Routes>
    </div>
  );
}

export default App;