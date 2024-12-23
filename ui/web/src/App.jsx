import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import theme from './theme';
import Navigation from './components/Navigation';
import Events from './pages/Events';
import Users from './pages/Users';
import Categories from './pages/Categories';
import Compilations from './pages/Compilations';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Navigation />
        <Routes>
          <Route path="/" element={<Events />} />
          <Route path="/users" element={<Users />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/compilations" element={<Compilations />} />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
