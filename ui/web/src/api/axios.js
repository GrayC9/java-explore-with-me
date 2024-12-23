// src/api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Handle API errors
      const apiError = error.response.data;
      console.error('API Error:', apiError);
    }
    return Promise.reject(error);
  }
);

export default api;
