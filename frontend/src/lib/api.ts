import axios from 'axios';
import { toast } from '@/hooks/use-toast';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

// Create axios instance
export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth token management
export const getAuthToken = () => {
  return localStorage.getItem('auth_token');
};

export const setAuthToken = (token: string) => {
  localStorage.setItem('auth_token', token);
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

export const clearAuthToken = () => {
  localStorage.removeItem('auth_token');
  delete api.defaults.headers.common['Authorization'];
};

// Initialize token on app start
const token = getAuthToken();
if (token) {
  setAuthToken(token);
}

// Response interceptor for error handling (allow per-request suppression)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const suppress = (error?.config as any)?.suppressGlobalError;
    if (!suppress) {
      const message = error.response?.data?.message || 'An error occurred while processing the request.';
      if (error.response?.status === 401) {
        clearAuthToken();
        toast({ variant: "destructive", title: "Session Expired", description: "Please log in again." });
        window.location.href = '/login';
      } else {
        toast({ variant: "destructive", title: "Error", description: message });
      }
    }
    return Promise.reject(error);
  }
);

// API Types
export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface SignupRequest {
  firstName: string;
  lastName: string;
  cityId: number;
  username: string;
  email: string;
  password: string;
  avatarUrl?: string;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  avatarUrl?: string;
  biography?: string;
}

export interface Program {
  id: number;
  name: string;
  description: string;
  difficulty: string; // mapped from backend's difficultyLevel
  duration: number; // minutes
  price: number;
  imageUrl?: string;
  attributes?: Attribute[];
  videoUrl?: string;
  category?: string;
  instructorName?: string;
  instructorAvatarUrl?: string;
  locationName?: string;
}

export interface UserProgram {
  id: number;
  name: string;
  description: string;
  duration: number;
  price: number;
  difficultyLevel: string;
  youtubeUrl?: string;
  locationName: string;
  startDate: string;
  endDate: string;
  status: string;
  instructorName: string;
  instructorId: number;
  purchaseId: number;
}

export interface Attribute {
  id: number;
  name: string;
  value: string;
  categoryId: number;
}

export interface City {
  id: number;
  name: string;
}

export interface Activity {
  id: number;
  activityType: string;
  duration: number;
  intensity: string;
  result?: number;
  logDate: string;
}

export interface Message {
  id: number;
  subject: string;
  content: string;
  fromUserId: number;
  toUserId: number;
  createdAt: string;
}

export interface NewsItem {
  id: number;
  title: string;
  content: string;
  imageUrl?: string;
  createdAt: string;
}

// Auth API
export const authApi = {
  login: (data: LoginRequest) => api.post('/auth/login', data),
  signup: (data: SignupRequest) => api.post('/auth/signup', data),
  activate: (token: string) => api.get(`/auth/activate?token=${token}`),
  // Optional utilities present in backend
  checkUsername: (username: string) => api.post('/auth/check-username', { username }),
  resendEmail: (email: string, token: string) => api.post('/auth/resend-email', { email, token }),
};

// User API
export const userApi = {
  getProfile: () => api.get('/user/info'),
  // Backend returns avatar for the authenticated user; no username param
  getAvatar: () => api.get('/user/avatar'),
  // Backend expects PATCH /user/info
  updateProfile: (data: Partial<User>) => api.patch('/user/info', data),
  // Backend expects PATCH /user/password
  updatePassword: (data: { currentPassword: string; newPassword: string }) =>
    api.patch('/user/password', data),
};

// Programs API
export const programsApi = {
  // Backend returns Page<FitnessProgramHomeResponse>
  getAll: (params?: any) => api.get('/programs', { params }),
  getById: (id: number) => api.get(`/programs/${id}`),
  getMyPrograms: (params?: any) => api.get('/programs/my-programs', { params }),
  joinProgram: (programId: number) => api.post(`/user-programs/${programId}`),
  create: (data: FormData) => api.post('/programs', data, { 
    headers: { 'Content-Type': 'multipart/form-data' } 
  }),
  update: (id: number, data: FormData) => api.put(`/programs/${id}`, data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (id: number) => api.delete(`/programs/${id}`),
};

// Activities API
export const activitiesApi = {
  getAll: (params?: any) => api.get('/activities', { params }),
  create: (data: Partial<Activity>) => api.post('/activities', data),
  downloadPdf: () => api.get('/activities/download-pdf', { responseType: 'blob' }),
};

// Cities API
export const citiesApi = {
  getAll: () => api.get('/cities'),
  addCity: (name: string) => api.post('/cities', { name }),
};

// News API
export const newsApi = {
  getAll: () => api.get('/news'),
};

// Messages API
export const messagesApi = {
  getConversations: () => api.get('/message/conversations'),
  getConversation: (userId: number) => api.get(`/message/conversation/${userId}`),
  sendMessage: (data: { toUserId: number; subject: string; content: string }) =>
    api.post('/message/send', data),
};

// Categories API
export const categoriesApi = {
  getAll: () => api.get('/category'),
  getSubscriptions: () => api.get('/category/subscriptions'),
  subscribe: (categoryId: number) => api.post('/category/subscribe', { categoryId }),
};

// Attributes API
export const attributesApi = {
  getAll: () => api.get('/attributes'),
  getByCategory: (categoryId: number) => api.get(`/attributes/category/${categoryId}`),
};

// Locations API
export const locationsApi = {
  getAll: () => api.get('/location'),
};

// Comments API
export const commentsApi = {
  getForProgram: (programId: number, page = 0, size = 10) =>
    api.get('/comments', { params: { programId, page, size }, // suppress global toast on optional comments
      // @ts-expect-error custom flag
      suppressGlobalError: true }),
  create: (data: { programId: number; content: string }) => api.post('/comments', data),
};

// User-Program enrollment API (used in ProgramDetail)
export const userProgramsApi = {
  createUserProgram: (programId: number) => api.post(`/user-programs/${programId}`),
  getUserPrograms: (params?: any) => api.get('/user-programs', { params }),
  deleteUserProgram: (userProgramId: number) => api.delete(`/user-programs/${userProgramId}`),
};

export default api;