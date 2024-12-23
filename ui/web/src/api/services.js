import api from './axios';

export const userService = {
  getUsers: (params) => api.get('/admin/users', { params }),
  createUser: (userData) => api.post('/admin/users', userData),
  deleteUser: (userId) => api.delete(`/admin/users/${userId}`),
  addFollower: (userId, followerId) => api.post(`/users/${userId}/followers/${followerId}`),
  deleteFollower: (userId, followerId) => api.delete(`/users/${userId}/followers/${followerId}`),
};

export const eventService = {
  getPublicEvents: (params) => api.get('/admin/events', { params }),
  getEventById: (id) => api.get(`/events/${id}`),
  getUserEvents: (userId, params) => api.get(`/users/${userId}/events`, { params }),
  createEvent: (userId, eventData) => api.post(`/users/${userId}/events`, eventData),
  updateEvent: (userId, eventId, eventData) => api.patch(`/users/${userId}/events/${eventId}`, eventData),
  getEventRequests: (userId, eventId) => api.get(`/users/${userId}/events/${eventId}/requests`),
  updateEventRequests: (userId, eventId, requestData) => 
    api.patch(`/users/${userId}/events/${eventId}/requests`, requestData),
};

export const categoryService = {
  getCategories: (params) => api.get('/categories', { params }),
  getCategoryById: (id) => api.get(`/categories/${id}`),
  createCategory: (categoryData) => api.post('/admin/categories', categoryData),
  updateCategory: (categoryId, categoryData) => api.patch(`/admin/categories/${categoryId}`, categoryData),
  deleteCategory: (categoryId) => api.delete(`/admin/categories/${categoryId}`),
};

export const compilationService = {
  getCompilations: (params) => api.get('/compilations', { params }),
  getCompilationById: (id) => api.get(`/compilations/${id}`),
  createCompilation: (compilationData) => api.post('/admin/compilations', compilationData),
  updateCompilation: (compilationId, compilationData) => 
    api.patch(`/admin/compilations/${compilationId}`, compilationData),
  deleteCompilation: (compilationId) => api.delete(`/admin/compilations/${compilationId}`),
};