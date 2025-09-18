import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Layout } from "./components/layout/Layout";
import { requireAuth } from "./lib/auth";

// Pages
import Index from "./pages/Index";
import { Login } from "./pages/auth/Login";
import { Signup } from "./pages/auth/Signup";
import { Dashboard } from "./pages/Dashboard";
import { Programs } from "./pages/programs/Programs";
import { ProgramDetail } from "./pages/programs/ProgramDetail";
import { Profile } from "./pages/Profile";
import { Activities } from "./pages/Activities";
import { Messages } from "./pages/Messages";
import { MyPrograms } from "./pages/MyPrograms";
import { InstructorDashboard } from "./pages/instructor/InstructorDashboard";
import { AdminDashboard } from "./pages/admin/AdminDashboard";
import NotFound from "./pages/NotFound";

// Protected Route Component
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  return requireAuth() ? <>{children}</> : <Navigate to="/login" replace />;
};

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Layout />}>
            <Route index element={<Index />} />
            <Route path="programs" element={<Programs />} />
            <Route path="programs/:id" element={<ProgramDetail />} />
          </Route>
          
          {/* Auth routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          
          {/* Protected routes - Regular Users */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="my-programs" element={<MyPrograms />} />
            <Route path="activities" element={<Activities />} />
            <Route path="messages" element={<Messages />} />
            <Route path="profile" element={<Profile />} />
            <Route path="settings" element={<div>Settings - Coming Soon</div>} />
          </Route>
          
          {/* Protected routes - Instructors */}
          <Route 
            path="/instructor" 
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/instructor/dashboard" replace />} />
            <Route path="dashboard" element={<InstructorDashboard />} />
            <Route path="programs" element={<div>Instructor Programs - Coming Soon</div>} />
            <Route path="students" element={<div>Students - Coming Soon</div>} />
            <Route path="analytics" element={<div>Instructor Analytics - Coming Soon</div>} />
            <Route path="schedule" element={<div>Schedule - Coming Soon</div>} />
          </Route>
          
          {/* Protected routes - Admins */}
          <Route 
            path="/admin" 
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/admin/dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="users" element={<div>User Management - Coming Soon</div>} />
            <Route path="analytics" element={<div>System Analytics - Coming Soon</div>} />
            <Route path="settings" element={<div>System Settings - Coming Soon</div>} />
          </Route>
          
          {/* Catch-all route */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;