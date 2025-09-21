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
  role?: 'USER' | 'INSTRUCTOR' | 'ADMIN';
}

export interface JwtAuthenticationResponse {
  token: string;
  role: string;
  username: string;
  email: string;
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
  difficultyLevel: string; // matches backend's difficultyLevel field
  duration: number; // minutes
  price: number;
  images?: string[]; // array of image URLs
  imageUrl?: string; // first image for backward compatibility
  attributes?: Attribute[];
  videoUrl?: string;
  category?: string;
  instructorName?: string;
  instructorAvatarUrl?: string;
  locationName?: string;
  instructorId?: number;
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
  senderId: number;
  recipientId: number;
  subject: string;
  content: string;
  sentAt: string;
  readAt?: string;
}

export interface Conversation {
  userId: number;
  avatarUrl?: string;
  username: string;
  lastMessage?: string;
  lastMessageTime?: string;
  unread: boolean;
}

export interface NonAdvisersResponse {
  userId: number;
  name: string;
}

// Admin API Types
export interface AdminUserResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  isVerified: boolean;
  avatarUrl?: string;
  biography?: string;
  cityName?: string;
  createdAt: string;
  lastLoginAt?: string;
  programCount: number;
  enrollmentCount: number;
  activityCount: number;
  status?: string;
}

export interface AdminProgramResponse {
  id: number;
  name: string;
  description: string;
  difficultyLevel: string;
  duration: number;
  price: number;
  youtubeUrl?: string;
  createdAt: string;
  instructorId: number;
  instructorName: string;
  instructorEmail: string;
  categoryId: number;
  categoryName: string;
  locationId: number;
  locationName: string;
  enrollmentCount: number;
  commentCount: number;
  averageRating: number;
  status: string;
  imageUrl?: string;
  isActive: boolean;
}

export interface ProgramActivationResponse {
  programId: number;
  programName: string;
  isActive: boolean;
  adminNotes?: string;
  activationDate: string;
  activatedBy: string;
  message: string;
  instructorName: string;
  categoryName: string;
  difficultyLevel: string;
}

export interface ProgramActivationStats {
  totalPrograms: number;
  activePrograms: number;
  inactivePrograms: number;
  pendingActivation: number;
  activationRate: number;
}

// Analytics Types
export interface ChartDataPoint {
  label: string;
  value: number;
  date: string;
}

export interface CategoryDistribution {
  category: string;
  count: number;
  percentage: number;
}

export interface DifficultyDistribution {
  difficulty: string;
  count: number;
  percentage: number;
}

export interface TopInstructor {
  id: number;
  name: string;
  email: string;
  programCount: number;
  enrollmentCount: number;
  averageRating: number;
  totalRevenue: number;
}

export interface TopProgram {
  id: number;
  name: string;
  instructorName: string;
  category: string;
  enrollmentCount: number;
  averageRating: number;
  revenue: number;
}

export interface SystemHealthMetrics {
  serverUptime: number;
  activeUsers: number;
  totalRequests: number;
  averageResponseTime: number;
  databaseStatus: string;
  lastBackup: string;
}

export interface AdminAnalyticsResponse {
  totalUsers: number;
  verifiedUsers: number;
  notVerifiedUsers: number;
  totalInstructors: number;
  totalPrograms: number;
  activePrograms: number;
  inactivePrograms: number;
  totalEnrollments: number;
  totalRevenue: number;
  averageRating: number;
  newUsersThisMonth: number;
  newProgramsThisMonth: number;
  newEnrollmentsThisMonth: number;
  revenueThisMonth: number;
  userGrowthChart: ChartDataPoint[];
  programEnrollmentChart: ChartDataPoint[];
  revenueChart: ChartDataPoint[];
  categoryDistribution: CategoryDistribution[];
  difficultyDistribution: DifficultyDistribution[];
  topInstructors: TopInstructor[];
  topPrograms: TopProgram[];
  systemHealth: SystemHealthMetrics;
}

export interface AdminStatsResponse {
  totalUsers: number;
  totalInstructors: number;
  totalAdmins: number;
  totalPrograms: number;
  totalEnrollments: number;
  totalActivities: number;
  activeUsers: number;
  inactiveUsers: number;
  newUsersThisMonth: number;
  newProgramsThisMonth: number;
  totalRevenue: number;
  monthlyRevenue: number;
}

// Instructor API Types
export interface InstructorStatsResponse {
  totalPrograms: number;
  totalStudents: number;
  totalEnrollments: number;
  activeEnrollments: number;
  completedEnrollments: number;
  totalRevenue: number;
  monthlyRevenue: number;
  averageRating: number;
  totalReviews: number;
  newEnrollmentsThisMonth: number;
  programsThisMonth: number;
}

export interface ProgramEnrollmentResponse {
  enrollmentId: number;
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  avatarUrl?: string;
  programId: number;
  programName: string;
  startDate: string;
  endDate: string;
  status: string;
  enrolledAt: string;
  progress: number;
  cityName?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
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
  login: (data: LoginRequest) => api.post<JwtAuthenticationResponse>('/auth/login', data),
  signup: (data: SignupRequest) => api.post<JwtAuthenticationResponse>('/auth/signup', data),
  activate: (token: string) => api.get(`/auth/activate?token=${token}`),
  // Optional utilities present in backend
  checkUsername: (username: string) => api.post<boolean>('/auth/check-username', { username }),
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
    api.patch('/user/password', {
      oldPassword: data.currentPassword,
      newPassword: data.newPassword
    }),
  // Get non-advisers (regular users) for messaging
  getNonAdvisers: () => api.get('/user/non-advisers'),
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
  getConversations: () => api.get<Conversation[]>('/message/conversations'),
  getMessagesForConversation: (conversationUserId: number) => 
    api.get<Message[]>(`/message/conversation/${conversationUserId}`),
  sendMessage: (data: { recipientId: number; subject: string; content: string }) =>
    api.post<Message>('/message/send', data),
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

// Admin API
export const adminApi = {
  getStats: () => api.get<AdminStatsResponse>('/admin/stats'),
  getAllUsers: (params?: { page?: number; size?: number; role?: string; search?: string }) => 
    api.get<PageResponse<AdminUserResponse>>('/admin/users', { params }),
  getUserById: (userId: number) => api.get<AdminUserResponse>(`/admin/users/${userId}`),
  updateUserRole: (userId: number, role: string) => 
    api.put<AdminUserResponse>(`/admin/users/${userId}/role?newRole=${role}`),
  updateUserStatus: (userId: number, active: boolean) => 
    api.put<AdminUserResponse>(`/admin/users/${userId}/status?isActive=${active}`),
  deleteUser: (userId: number) => api.delete(`/admin/users/${userId}`),
  getAllInstructors: () => api.get<AdminUserResponse[]>('/admin/users/instructors'),
  promoteToInstructor: (userId: number) => api.post<AdminUserResponse>(`/admin/users/${userId}/promote-instructor`),
  demoteFromInstructor: (userId: number) => api.post<AdminUserResponse>(`/admin/users/${userId}/demote-instructor`),
  getSystemLogs: (limit?: number) => api.get<string[]>(`/admin/logs?limit=${limit || 100}`),
  // Programs management
  getAllPrograms: (params?: { page?: number; size?: number; search?: string; category?: string; difficulty?: string }) => 
    api.get<PageResponse<AdminProgramResponse>>('/admin/programs', { params }),
  getProgramById: (programId: number) => api.get<AdminProgramResponse>(`/admin/programs/${programId}`),
  deleteProgram: (programId: number) => api.delete(`/admin/programs/${programId}`),
  updateProgramStatus: (programId: number, isActive: boolean) => 
    api.put<AdminProgramResponse>(`/admin/programs/${programId}/status?isActive=${isActive}`),
  getProgramStatistics: () => api.get<Object>('/admin/programs/statistics'),
  // Program activation management
  activateProgram: (programId: number, isActive: boolean, adminNotes?: string) => 
    api.post<ProgramActivationResponse>('/admin/programs/activation/toggle', {
      programId,
      isActive,
      adminNotes
    }),
  getActivePrograms: (params?: { page?: number; size?: number; sortBy?: string; sortDir?: string }) => 
    api.get<PageResponse<AdminProgramResponse>>('/admin/programs/activation/active', { params }),
  getInactivePrograms: (params?: { page?: number; size?: number; sortBy?: string; sortDir?: string }) => 
    api.get<PageResponse<AdminProgramResponse>>('/admin/programs/activation/inactive', { params }),
  getProgramsPendingActivation: (params?: { page?: number; size?: number; sortBy?: string; sortDir?: string }) => 
    api.get<PageResponse<AdminProgramResponse>>('/admin/programs/activation/pending', { params }),
  getProgramActivationStats: () => api.get<ProgramActivationStats>('/admin/programs/activation/stats'),
  // Analytics
  getAnalyticsOverview: () => api.get<AdminAnalyticsResponse>('/admin/analytics/overview'),
  getDashboardAnalytics: () => api.get<AdminAnalyticsResponse>('/admin/analytics/dashboard'),
  getUserGrowthAnalytics: (startDate: string, endDate: string) => 
    api.get<Object>(`/admin/analytics/user-growth?startDate=${startDate}&endDate=${endDate}`),
  getProgramAnalytics: () => api.get<Object>('/admin/analytics/programs'),
  getRevenueAnalytics: (startDate: string, endDate: string) => 
    api.get<Object>(`/admin/analytics/revenue?startDate=${startDate}&endDate=${endDate}`),
  getRealTimeMetrics: () => api.get<Object>('/admin/analytics/real-time'),
};

// Instructor API
export const instructorApi = {
  getStats: () => api.get<InstructorStatsResponse>('/instructor/stats'),
  createProgram: (programData: any, files: File[] = []) => {
    return api.post<Program>('/instructor/programs', programData);
  },
  updateProgram: (programId: number, programData: any, files?: File[], removedImages?: string[]) => {
    const formData = new FormData();
    formData.append('program', JSON.stringify(programData));
    if (files) files.forEach(file => formData.append('files', file));
    if (removedImages) formData.append('removedImages', JSON.stringify(removedImages));
    return api.put<Program>(`/instructor/programs/${programId}`, formData);
  },
  deleteProgram: (programId: number) => api.delete(`/instructor/programs/${programId}`),
  getMyPrograms: (params?: { page?: number; size?: number; sort?: string }) => 
    api.get<PageResponse<Program>>('/instructor/programs', { params }),
  getProgramDetails: (programId: number) => api.get<Program>(`/instructor/programs/${programId}`),
  getProgramEnrollments: (programId: number) => api.get<ProgramEnrollmentResponse[]>(`/instructor/programs/${programId}/enrollments`),
  getAllEnrollments: (params?: { page?: number; size?: number }) => 
    api.get<PageResponse<ProgramEnrollmentResponse>>('/instructor/enrollments', { params }),
  updateEnrollmentStatus: (enrollmentId: number, status: string) => 
    api.put<ProgramEnrollmentResponse>(`/instructor/enrollments/${enrollmentId}/status?status=${status}`),
  getStudents: (params?: { page?: number; size?: number }) => 
    api.get<PageResponse<ProgramEnrollmentResponse>>('/instructor/students', { params }),
};

// Category and Location API
export const categoryApi = {
  getAll: () => api.get<Category[]>('/category'),
  getWithSubscriptions: () => api.get<Category[]>('/category/subscriptions'),
  subscribe: (categoryId: number) => api.post<Category>(`/category/subscribe`, { categoryId }),
};

export const locationApi = {
  getAll: () => api.get<Location[]>('/location'),
};

export interface Category {
  id: number;
  name: string;
  description?: string;
}

export interface Location {
  id: number;
  name: string;
}

// Payment API
export const paymentApi = {
  createPayHerePayment: (data: {
    programId: number;
    customerName: string;
    customerEmail: string;
    customerPhone: string;
    customerAddress: string;
    customerCity: string;
    customerCountry: string;
    amount: number;
    currency: string;
    orderId: string;
    itemName: string;
    itemDescription: string;
  }) => api.post<{
    status: string;
    message: string;
    paymentUrl: string;
    orderId: string;
    merchantId: string;
    merchantSecret: string;
    amount: string;
    currency: string;
    customerName: string;
    customerEmail: string;
    customerPhone: string;
    customerAddress: string;
    customerCity: string;
    customerCountry: string;
    itemName: string;
    itemDescription: string;
    returnUrl: string;
    cancelUrl: string;
    notifyUrl: string;
    hash: string;
  }>('/api/payment/payhere/create', data),
  
  completePayment: (orderId: string, programId: string) => 
    api.post<string>(`/api/payment/payhere/complete?orderId=${orderId}&programId=${programId}`),
};

export default api;