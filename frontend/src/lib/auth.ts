import { getAuthToken, clearAuthToken } from './api';

export const useAuth = () => {
  const isAuthenticated = !!getAuthToken();

  const logout = () => {
    clearAuthToken();
    window.location.href = '/login';
  };

  return {
    isAuthenticated,
    logout,
  };
};

// Route protection helper
export const requireAuth = () => {
  const token = getAuthToken();
  return !!token;
};