import { getAuthToken, clearAuthToken } from './api';

export const useAuth = () => {
  const isAuthenticated = !!getAuthToken();
  
  // Get user info from localStorage
  const user = {
    role: localStorage.getItem('user_role') || 'USER',
    username: localStorage.getItem('user_username') || '',
    email: localStorage.getItem('user_email') || ''
  };

  const logout = () => {
    clearAuthToken();
    localStorage.removeItem('user_role');
    localStorage.removeItem('user_username');
    localStorage.removeItem('user_email');
    window.location.href = '/login';
  };

  return {
    isAuthenticated,
    user,
    logout,
  };
};

// Route protection helper
export const requireAuth = () => {
  const token = getAuthToken();
  return !!token;
};