import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  Users, 
  BookOpen, 
  TrendingUp, 
  DollarSign,
  Activity,
  Shield,
  AlertTriangle,
  CheckCircle,
  XCircle,
  Clock,
  MoreVertical,
  UserPlus,
  Settings,
  BarChart3,
  Database,
  Search,
  Filter,
  Download,
  Mail,
  Phone,
  MapPin,
  GraduationCap,
  Eye,
  Edit,
  Trash2,
  Ban,
  UserCheck,
  Crown,
  Briefcase,
  Loader2
} from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { useState, useEffect } from "react";
import { adminApi, userApi, AdminStatsResponse, AdminUserResponse, AdminProgramResponse, AdminAnalyticsResponse, PageResponse, User } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { toast } from "@/hooks/use-toast";
import { DeleteConfirmationDialog } from "@/components/ui/delete-confirmation-dialog";

export const AdminDashboard = () => {
  // Get current user from auth context
  const { user: currentUser } = useAuth();
  
  // State management
  const [stats, setStats] = useState<AdminStatsResponse | null>(null);
  const [users, setUsers] = useState<AdminUserResponse[]>([]);
  const [instructors, setInstructors] = useState<AdminUserResponse[]>([]);
  const [programs, setPrograms] = useState<AdminProgramResponse[]>([]);
  const [analytics, setAnalytics] = useState<AdminAnalyticsResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [allUsers, setAllUsers] = useState<AdminUserResponse[]>([]); // Store all users for frontend filtering
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState<string>("");
  const [selectedVerificationStatus, setSelectedVerificationStatus] = useState<string>("");
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);
  
  // Delete dialog states
  const [deleteUserDialog, setDeleteUserDialog] = useState<{ open: boolean; user: AdminUserResponse | null }>({ open: false, user: null });
  const [deleteProgramDialog, setDeleteProgramDialog] = useState<{ open: boolean; program: AdminProgramResponse | null }>({ open: false, program: null });
  const [isDeleting, setIsDeleting] = useState(false);
  
  // Programs state
  const [programSearchTerm, setProgramSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string>("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("");
  const [selectedStatus, setSelectedStatus] = useState<string>("");

  // Fetch admin statistics
  const fetchStats = async () => {
    try {
      const response = await adminApi.getStats();
      setStats(response.data);
    } catch (error) {
      console.error('Failed to fetch admin stats:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load admin statistics"
      });
    }
  };

  // Apply frontend filtering when filters change
  const applyFrontendFiltering = () => {
    if (allUsers.length === 0) return;
    
    let filteredUsers = [...allUsers];
    
    // Apply role filter
    if (selectedRole && selectedRole.trim() !== '') {
      filteredUsers = filteredUsers.filter(user => user.role === selectedRole);
    }
    
    // Apply search filter
    if (searchTerm && searchTerm.trim() !== '') {
      const searchLower = searchTerm.toLowerCase();
      filteredUsers = filteredUsers.filter(user => 
        user.username.toLowerCase().includes(searchLower) ||
        user.email.toLowerCase().includes(searchLower) ||
        (user.firstName && user.firstName.toLowerCase().includes(searchLower)) ||
        (user.lastName && user.lastName.toLowerCase().includes(searchLower))
      );
    }
    
    // Apply verification status filter
    if (selectedVerificationStatus === 'verified') {
      console.log('Filtering for verified users. Before filter:', filteredUsers.length);
      filteredUsers = filteredUsers.filter(user => {
        console.log(`User ${user.username}: isVerified = ${user.isVerified} (type: ${typeof user.isVerified})`);
        // Handle both boolean and string values
        const isVerified = user.isVerified === true || String(user.isVerified) === 'true' || user.status === 'VERIFIED';
        console.log(`User ${user.username}: computed isVerified = ${isVerified}`);
        return isVerified;
      });
      console.log('After verified filter:', filteredUsers.length);
    } else if (selectedVerificationStatus === 'not_verified') {
      console.log('Filtering for not verified users. Before filter:', filteredUsers.length);
      filteredUsers = filteredUsers.filter(user => {
        console.log(`User ${user.username}: isVerified = ${user.isVerified} (type: ${typeof user.isVerified})`);
        // Handle both boolean and string values
        const isVerified = user.isVerified === true || String(user.isVerified) === 'true' || user.status === 'VERIFIED';
        console.log(`User ${user.username}: computed isVerified = ${isVerified}`);
        return !isVerified;
      });
      console.log('After not verified filter:', filteredUsers.length);
    }
    
    // Apply pagination
    const startIndex = currentPage * pageSize;
    const endIndex = startIndex + pageSize;
    filteredUsers = filteredUsers.slice(startIndex, endIndex);
    
    console.log('Frontend filtering applied:', {
      totalUsers: allUsers.length,
      filteredUsers: filteredUsers.length,
      selectedRole,
      searchTerm,
      selectedVerificationStatus,
      currentPage
    });
    console.log('All users for frontend filtering:', allUsers.map(u => ({ id: u.id, username: u.username, isVerified: u.isVerified, role: u.role })));
    console.log('Final filtered users:', filteredUsers.map(u => ({ id: u.id, username: u.username, isVerified: u.isVerified, role: u.role })));
    
    setUsers(filteredUsers);
  };

  // Fetch all users
  const fetchUsers = async () => {
    try {
      const params: any = {
        page: currentPage,
        size: pageSize
      };
      
      // If verification status filter is active, we need to fetch all users and filter on frontend
      // Otherwise, we can use backend filtering for better performance
      const needsFrontendFiltering = selectedVerificationStatus && selectedVerificationStatus.trim() !== '';
      
      if (needsFrontendFiltering) {
        // When verification status filter is active, fetch all users without search/role filters
        // and do all filtering on the frontend
        params.page = 0;
        params.size = 1000; // Fetch a large number to get all users
        console.log('Fetching all users for frontend filtering with params:', params);
      } else {
        // Normal backend filtering when verification status is not active
        if (selectedRole && selectedRole.trim() !== '') {
          params.role = selectedRole;
        }
        if (searchTerm && searchTerm.trim() !== '') {
          params.search = searchTerm;
        }
        console.log('Fetching users with backend filtering params:', params);
      }
      
      const response = await adminApi.getAllUsers(params);
      console.log('Users response:', response.data);
      console.log('First user isVerified:', response.data.content[0]?.isVerified);
      console.log('First user full object:', response.data.content[0]);
      
      // Filter out the current admin from the users list using username
      let filteredUsers = response.data.content.filter(user => 
        currentUser?.username ? user.username !== currentUser.username : true
      );
      
      // Store all users for frontend filtering
      if (needsFrontendFiltering) {
        setAllUsers(filteredUsers);
      }
      
      // Apply frontend filtering if needed
      if (needsFrontendFiltering) {
        // Apply role filter on frontend
        if (selectedRole && selectedRole.trim() !== '') {
          filteredUsers = filteredUsers.filter(user => user.role === selectedRole);
        }
        
        // Apply search filter on frontend
        if (searchTerm && searchTerm.trim() !== '') {
          const searchLower = searchTerm.toLowerCase();
          filteredUsers = filteredUsers.filter(user => 
            user.username.toLowerCase().includes(searchLower) ||
            user.email.toLowerCase().includes(searchLower) ||
            (user.firstName && user.firstName.toLowerCase().includes(searchLower)) ||
            (user.lastName && user.lastName.toLowerCase().includes(searchLower))
          );
        }
        
        // Apply verification status filter on frontend
        if (selectedVerificationStatus === 'verified') {
          console.log('Backend filtering - Filtering for verified users. Before filter:', filteredUsers.length);
          filteredUsers = filteredUsers.filter(user => {
            console.log(`Backend filtering - User ${user.username}: isVerified = ${user.isVerified} (type: ${typeof user.isVerified})`);
            // Handle both boolean and string values
            const isVerified = user.isVerified === true || String(user.isVerified) === 'true' || user.status === 'VERIFIED';
            console.log(`Backend filtering - User ${user.username}: computed isVerified = ${isVerified}`);
            return isVerified;
          });
          console.log('Backend filtering - After verified filter:', filteredUsers.length);
        } else if (selectedVerificationStatus === 'not_verified') {
          console.log('Backend filtering - Filtering for not verified users. Before filter:', filteredUsers.length);
          filteredUsers = filteredUsers.filter(user => {
            console.log(`Backend filtering - User ${user.username}: isVerified = ${user.isVerified} (type: ${typeof user.isVerified})`);
            // Handle both boolean and string values
            const isVerified = user.isVerified === true || String(user.isVerified) === 'true' || user.status === 'VERIFIED';
            console.log(`Backend filtering - User ${user.username}: computed isVerified = ${isVerified}`);
            return !isVerified;
          });
          console.log('Backend filtering - After not verified filter:', filteredUsers.length);
        }
        
        // Apply pagination on frontend
        const startIndex = currentPage * pageSize;
        const endIndex = startIndex + pageSize;
        filteredUsers = filteredUsers.slice(startIndex, endIndex);
      } else {
        // Apply verification status filter on frontend (when not using frontend filtering for everything)
        if (selectedVerificationStatus && selectedVerificationStatus.trim() !== '') {
          console.log('Mixed filtering - Applying verification status filter:', selectedVerificationStatus);
          console.log('Mixed filtering - Before filter:', filteredUsers.length);
          filteredUsers = filteredUsers.filter(user => {
            console.log(`Mixed filtering - User ${user.username}: isVerified = ${user.isVerified} (type: ${typeof user.isVerified})`);
            // Handle both boolean and string values
            const isVerified = user.isVerified === true || String(user.isVerified) === 'true' || user.status === 'VERIFIED';
            console.log(`Mixed filtering - User ${user.username}: computed isVerified = ${isVerified}`);
            if (selectedVerificationStatus === 'verified') {
              return isVerified;
            } else if (selectedVerificationStatus === 'not_verified') {
              return !isVerified;
            }
            return true;
          });
          console.log('Mixed filtering - After filter:', filteredUsers.length);
        }
      }
      
      console.log('Current user username:', currentUser?.username);
      console.log('Selected verification status:', selectedVerificationStatus);
      console.log('Selected role:', selectedRole);
      console.log('Search term:', searchTerm);
      console.log('All users before filtering:', response.data.content.map(u => ({ id: u.id, username: u.username, isVerified: u.isVerified, role: u.role })));
      console.log('Filtered users (excluding current admin):', filteredUsers.map(u => ({ id: u.id, username: u.username, isVerified: u.isVerified, role: u.role })));
      setUsers(filteredUsers);
    } catch (error) {
      console.error('Failed to fetch users:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load users"
      });
    }
  };

  // Fetch instructors
  const fetchInstructors = async () => {
    try {
      const response = await adminApi.getAllInstructors();
      setInstructors(response.data);
    } catch (error) {
      console.error('Failed to fetch instructors:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load instructors"
      });
    }
  };

  // Fetch all programs
  const fetchPrograms = async () => {
    try {
      const params: any = {
        page: 0,
        size: 20
      };
      
      // Only add filters if they have values
      if (programSearchTerm && programSearchTerm.trim() !== '') {
        params.search = programSearchTerm;
      }
      if (selectedCategory && selectedCategory.trim() !== '') {
        params.category = selectedCategory;
      }
      if (selectedDifficulty && selectedDifficulty.trim() !== '') {
        params.difficulty = selectedDifficulty;
      }
      
      console.log('Fetching programs with params:', params);
      
      // Use different API endpoints based on status filter
      let response;
      if (selectedStatus === 'active') {
        // For activation APIs, only pass pagination params
        const activationParams = { page: params.page, size: params.size };
        response = await adminApi.getActivePrograms(activationParams);
      } else if (selectedStatus === 'inactive') {
        // For activation APIs, only pass pagination params
        const activationParams = { page: params.page, size: params.size };
        response = await adminApi.getInactivePrograms(activationParams);
      } else {
        // For getAllPrograms, pass all filters
        response = await adminApi.getAllPrograms(params);
      }
      
      console.log('Programs response:', response.data);
      console.log('First program isActive:', response.data.content[0]?.isActive);
      console.log('First program status:', response.data.content[0]?.status);
      console.log('First program full object:', response.data.content[0]);
      setPrograms(response.data.content);
    } catch (error) {
      console.error('Failed to fetch programs:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load programs"
      });
    }
  };

  // Fetch analytics data
  const fetchAnalytics = async () => {
    try {
      console.log('Fetching analytics data...');
      const response = await adminApi.getDashboardAnalytics();
      console.log('Analytics response:', response.data);
      setAnalytics(response.data);
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load analytics data"
      });
    }
  };

  // Load data on component mount
  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      console.log('Loading admin dashboard data...');
      try {
      await Promise.all([
        fetchStats(),
        fetchUsers(),
          fetchInstructors(),
          fetchPrograms(),
          fetchAnalytics()
      ]);
        console.log('All data loaded successfully');
      } catch (error) {
        console.error('Error loading admin dashboard data:', error);
      }
      setLoading(false);
    };
    loadData();
  }, []);

  // Refetch users when search or filter changes
  useEffect(() => {
    console.log('useEffect triggered with:', { searchTerm, selectedRole, selectedVerificationStatus, currentPage, loading, allUsersLength: allUsers.length });
    if (!loading) {
      // If we have all users loaded and verification status filter is active, use frontend filtering
      if (allUsers.length > 0 && selectedVerificationStatus && selectedVerificationStatus.trim() !== '') {
        console.log('Using frontend filtering');
        applyFrontendFiltering();
      } else {
        console.log('Using backend filtering');
      fetchUsers();
    }
    }
  }, [searchTerm, selectedRole, selectedVerificationStatus, currentPage]);

  // Refetch programs when search or filter changes
  useEffect(() => {
    if (!loading) {
      fetchPrograms();
    }
  }, [programSearchTerm, selectedCategory, selectedDifficulty, selectedStatus]);

  // Handle user status update
  const handleUserStatusUpdate = async (userId: number, isActive: boolean) => {
    try {
      await adminApi.updateUserStatus(userId, isActive);
      toast({
        title: "Success",
        description: `User ${isActive ? 'verified' : 'unverified'} successfully`
      });
      
      // Update local state immediately for better UX
      setUsers(prevUsers => 
        prevUsers.map(user => 
          user.id === userId 
            ? { ...user, isVerified: isActive }
            : user
        )
      );
      
      setInstructors(prevInstructors => 
        prevInstructors.map(instructor => 
          instructor.id === userId 
            ? { ...instructor, isVerified: isActive }
            : instructor
        )
      );
      
      // Also refresh analytics to show updated counts
      fetchAnalytics();
    } catch (error) {
      console.error('Failed to update user status:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update user status"
      });
    }
  };

  // Handle user role update
  const handleUserRoleUpdate = async (userId: number, role: string) => {
    try {
      await adminApi.updateUserRole(userId, role);
      toast({
        title: "Success",
        description: "User role updated successfully"
      });
      fetchUsers(); // Refresh the list
    } catch (error) {
      console.error('Failed to update user role:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update user role"
      });
    }
  };

  // Handle user deletion
  const handleUserDelete = async (userId: number) => {
    const user = users.find(u => u.id === userId);
    if (user) {
      setDeleteUserDialog({ open: true, user });
    }
  };
    
  // Confirm user deletion
  const confirmUserDelete = async () => {
    if (!deleteUserDialog.user) return;
    
    setIsDeleting(true);
    try {
      await adminApi.deleteUser(deleteUserDialog.user.id);
      toast({
        title: "Success",
        description: "User deleted successfully"
      });
      
      // Refresh users list
      fetchUsers();
      setDeleteUserDialog({ open: false, user: null });
    } catch (error) {
      console.error('Failed to delete user:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete user"
      });
    } finally {
      setIsDeleting(false);
    }
  };

  // Handle program deletion
  const handleProgramDelete = async (programId: number) => {
    const program = programs.find(p => p.id === programId);
    if (program) {
      setDeleteProgramDialog({ open: true, program });
    }
  };

  // Confirm program deletion
  const confirmProgramDelete = async () => {
    if (!deleteProgramDialog.program) return;
    
    setIsDeleting(true);
    try {
      await adminApi.deleteProgram(deleteProgramDialog.program.id);
      toast({
        title: "Success",
        description: "Program deleted successfully"
      });
      
      // Refresh programs list
      fetchPrograms();
      setDeleteProgramDialog({ open: false, program: null });
    } catch (error) {
      console.error('Failed to delete program:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete program"
      });
    } finally {
      setIsDeleting(false);
    }
  };

  // Handle program status update
  const handleProgramStatusUpdate = async (programId: number, isActive: boolean) => {
    try {
      await adminApi.activateProgram(programId, isActive);
      toast({
        title: "Success",
        description: `Program ${isActive ? 'activated' : 'deactivated'} successfully`
      });
      
      // Update the local state immediately for better UX
      setPrograms(prevPrograms => 
        prevPrograms.map(program => 
          program.id === programId 
            ? { ...program, isActive: isActive, status: isActive ? 'ACTIVE' : 'INACTIVE' }
            : program
        )
      );
      
      // Also refresh analytics to show updated counts
      fetchAnalytics();
    } catch (error) {
      console.error('Failed to update program status:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update program status"
      });
    }
  };

  // System stats configuration - using analytics data
  const systemStats = analytics ? [
    { 
      title: "Verified Users", 
      value: analytics.verifiedUsers.toString(), 
      change: `+${analytics.newUsersThisMonth} new this month`, 
      icon: Users, 
      color: "text-primary"
    },
    { 
      title: "Active Programs", 
      value: analytics.activePrograms.toString(), 
      change: `+${analytics.newProgramsThisMonth} new this month`, 
      icon: BookOpen, 
      color: "text-blue-400"
    },
    { 
      title: "Monthly Revenue", 
      value: `$${analytics.revenueThisMonth.toLocaleString()}`, 
      change: `+${analytics.totalEnrollments} enrollments`, 
      icon: DollarSign, 
      color: "text-green-400"
    },
    { 
      title: "System Health", 
      value: `${analytics.systemHealth.serverUptime}%`, 
      change: `${analytics.systemHealth.activeUsers} active users`, 
      icon: Activity, 
      color: "text-green-400"
    },
  ] : [];

  const systemAlerts = [
    { type: "warning", message: "High server load detected", time: "5 min ago", severity: "medium" },
    { type: "info", message: "Database backup completed", time: "1 hour ago", severity: "low" },
    { type: "error", message: "Payment gateway timeout", time: "2 hours ago", severity: "high" },
    { type: "success", message: "System update deployed", time: "1 day ago", severity: "low" },
  ];

  const getAlertIcon = (type: string) => {
    switch (type) {
      case 'error': return <XCircle className="h-4 w-4 text-red-400" />;
      case 'warning': return <AlertTriangle className="h-4 w-4 text-yellow-400" />;
      case 'success': return <CheckCircle className="h-4 w-4 text-green-400" />;
      default: return <Activity className="h-4 w-4 text-blue-400" />;
    }
  };

  const getStatusBadge = (user: any) => {
    // Use isVerified field if available, otherwise fall back to status field
    const isVerified = user.isVerified !== undefined ? user.isVerified : (user.status === 'VERIFIED');
    
    return isVerified ? 
      <Badge className="bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300 border-green-200 dark:border-green-700">Verified</Badge> : 
      <Badge variant="secondary" className="bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-300 border-orange-200 dark:border-orange-700">Not Verified</Badge>;
  };

  const getProgramStatusBadge = (program: any) => {
    // Debug logging
    console.log('AdminDashboard getProgramStatusBadge called with program:', program);
    console.log('program.isActive:', program.isActive);
    console.log('program.status:', program.status);
    
    // Use isActive field if available, otherwise fall back to status field
    const isActive = program.isActive !== undefined ? program.isActive : (program.status === 'ACTIVE');
    
    console.log('Final isActive value:', isActive);
    
    return (
      <span className={`px-2 py-1 text-xs rounded-full font-medium ${
        isActive 
          ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' 
          : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
      }`}>
        {isActive ? 'Active' : 'Pending'}
      </span>
    );
  };

  // Helper function to get program activation status
  const getProgramActivationStatus = (program: any) => {
    // Use isActive field if available, otherwise fall back to status field
    return program.isActive !== undefined ? program.isActive : (program.status === 'ACTIVE');
  };

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'INSTRUCTOR': return <GraduationCap className="h-4 w-4" />;
      case 'ADMIN': return <Crown className="h-4 w-4" />;
      default: return <Users className="h-4 w-4" />;
    }
  };

  const getRoleDisplayName = (role: string) => {
    switch (role) {
      case 'INSTRUCTOR': return 'Instructor';
      case 'ADMIN': return 'Admin';
      case 'USER': return 'User';
      default: return role;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getAvatarUrl = (user: AdminUserResponse) => {
    if (user.avatarUrl) {
      return user.avatarUrl;
    }
    // Fallback to generated avatar based on username
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.username}`;
  };

  const getInitials = (user: AdminUserResponse) => {
    return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="flex items-center gap-2">
          <Loader2 className="h-6 w-6 animate-spin" />
          <span>Loading admin dashboard...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold bg-gradient-primary bg-clip-text text-transparent">
            Admin Dashboard
          </h1>
          <p className="text-muted-foreground mt-1">
            System overview and comprehensive management
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">
            <Download className="h-4 w-4 mr-2" />
            Export Data
          </Button>
          <Button variant="outline" size="sm">
            <Settings className="h-4 w-4 mr-2" />
            Settings
          </Button>
          <Button variant="fitness" size="sm">
            <UserPlus className="h-4 w-4 mr-2" />
            Add User
          </Button>
        </div>
      </div>

      {/* System Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {systemStats.map((stat, index) => (
          <Card key={index} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">{stat.title}</p>
                  <p className="text-2xl font-bold mt-2">{stat.value}</p>
                  <p className={`text-sm ${stat.color} mt-1`}>{stat.change}</p>
                </div>
                <div className={`p-3 rounded-lg bg-background/50 ${stat.color}`}>
                  <stat.icon className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Main Content Tabs */}
      <Tabs defaultValue="users" className="space-y-6">
        <TabsList className="grid w-full grid-cols-5">
          <TabsTrigger value="users">Users</TabsTrigger>
          <TabsTrigger value="instructors">Instructors</TabsTrigger>
          <TabsTrigger value="programs">Programs</TabsTrigger>
          <TabsTrigger value="system">System</TabsTrigger>
          <TabsTrigger value="analytics">Analytics</TabsTrigger>
        </TabsList>

        {/* Users Tab */}
        <TabsContent value="users" className="space-y-6">
          <div className="space-y-4">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
              <div className="flex items-center gap-4 flex-wrap">
                <div className="relative flex-1 max-w-sm min-w-[200px]">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input 
                  placeholder="Search users..." 
                  className="pl-9"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
                <Select value={selectedRole || "all"} onValueChange={(value) => setSelectedRole(value === "all" ? "" : value)}>
                  <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="All Roles" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Roles</SelectItem>
                    <SelectItem value="USER">Users</SelectItem>
                    <SelectItem value="INSTRUCTOR">Instructors</SelectItem>
                    <SelectItem value="ADMIN">Admins</SelectItem>
                  </SelectContent>
                </Select>
                
                <Select value={selectedVerificationStatus || "all"} onValueChange={(value) => {
                  console.log('Verification status filter changed to:', value);
                  setSelectedVerificationStatus(value === "all" ? "" : value);
                }}>
                  <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="All Status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Status</SelectItem>
                    <SelectItem value="verified">Verified</SelectItem>
                    <SelectItem value="not_verified">Not Verified</SelectItem>
                  </SelectContent>
                </Select>
                
                {/* Clear Filters Button */}
                {(selectedRole || selectedVerificationStatus || searchTerm) && (
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => {
                      setSelectedRole("");
                      setSelectedVerificationStatus("");
                      setSearchTerm("");
                    }}
                    className="flex items-center gap-2"
                  >
                    <XCircle className="h-4 w-4" />
                    Clear Filters
                  </Button>
                )}
            </div>
            <Button variant="fitness" size="sm">
              <UserPlus className="h-4 w-4 mr-2" />
              Add User
            </Button>
            </div>
          </div>

          {/* Active Filters Summary */}
          {(selectedRole || selectedVerificationStatus || searchTerm) && (
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-sm text-muted-foreground">Active filters:</span>
              {searchTerm && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  <Search className="h-3 w-3" />
                  Search: "{searchTerm}"
                </Badge>
              )}
              {selectedRole && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  <Users className="h-3 w-3" />
                  Role: {selectedRole}
                </Badge>
              )}
              {selectedVerificationStatus && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  {selectedVerificationStatus === 'verified' ? (
                    <CheckCircle className="h-3 w-3 text-green-500" />
                  ) : (
                    <XCircle className="h-3 w-3 text-orange-500" />
                  )}
                  Status: {selectedVerificationStatus === 'verified' ? 'Verified' : 'Not Verified'}
                </Badge>
              )}
            </div>
          )}

          {/* Results Summary */}
          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              Showing {users.length} user{users.length !== 1 ? 's' : ''}
              {(selectedRole || selectedVerificationStatus || searchTerm) && ' (filtered)'}
            </p>
          </div>

          <div className="grid gap-6">
            {users.length === 0 ? (
              <Card variant="neumorphic">
                <CardContent className="p-8 text-center">
                  <Users className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                  <h3 className="text-lg font-semibold mb-2">No users found</h3>
                  <p className="text-muted-foreground">
                    {searchTerm || selectedRole || selectedVerificationStatus ? 'Try adjusting your search criteria.' : 'No users have been registered yet.'}
                  </p>
                </CardContent>
              </Card>
            ) : (
              users.map((user) => (
                <Card key={user.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-start gap-4">
                      <Avatar className="h-16 w-16">
                        <AvatarImage src={getAvatarUrl(user)} />
                        <AvatarFallback>{getInitials(user)}</AvatarFallback>
                      </Avatar>
                      
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-3">
                          <div>
                            <h3 className="text-lg font-semibold">
                              {user.firstName} {user.lastName}
                            </h3>
                            <div className="flex items-center gap-2 mb-1">
                              {getRoleIcon(user.role)}
                              <span className="text-sm text-muted-foreground">
                                {getRoleDisplayName(user.role)}
                              </span>
                            </div>
                            {getStatusBadge(user)}
                          </div>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon">
                                <MoreVertical className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuItem>
                                <Eye className="h-4 w-4 mr-2" />
                                View Profile
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <Edit className="h-4 w-4 mr-2" />
                                Edit User
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <Mail className="h-4 w-4 mr-2" />
                                Send Message
                              </DropdownMenuItem>
                              {(() => {
                                // Use the same logic as getStatusBadge for consistency
                                const isVerified = user.isVerified !== undefined ? user.isVerified : (user.status === 'VERIFIED');
                                
                                return isVerified ? (
                                <DropdownMenuItem 
                                  className="text-destructive"
                                  onClick={() => handleUserStatusUpdate(user.id, false)}
                                >
                                  <Ban className="h-4 w-4 mr-2" />
                                    Unverify User
                                </DropdownMenuItem>
                              ) : (
                                <DropdownMenuItem
                                  onClick={() => handleUserStatusUpdate(user.id, true)}
                                >
                                  <UserCheck className="h-4 w-4 mr-2" />
                                    Verify User
                                </DropdownMenuItem>
                                );
                              })()}
                              <DropdownMenuItem 
                                className="text-destructive"
                                onClick={() => handleUserDelete(user.id)}
                              >
                                <Trash2 className="h-4 w-4 mr-2" />
                                Delete User
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                        
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                          <div className="flex items-center gap-2 text-sm">
                            <Mail className="h-4 w-4 text-muted-foreground" />
                            <span>{user.email}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <Users className="h-4 w-4 text-muted-foreground" />
                            <span>@{user.username}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <BookOpen className="h-4 w-4 text-muted-foreground" />
                            <span>{user.programCount} programs</span>
                          </div>
                        </div>

                        <div className="flex items-center gap-6 text-xs text-muted-foreground">
                          <span>Joined: {formatDate(user.createdAt)}</span>
                          {user.lastLoginAt && (
                            <span>Last login: {formatDate(user.lastLoginAt)}</span>
                          )}
                          {user.cityName && (
                            <span>Location: {user.cityName}</span>
                          )}
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        {/* Instructors Tab */}
        <TabsContent value="instructors" className="space-y-6">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center gap-4 flex-1">
              <div className="relative flex-1 max-w-sm">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search instructors..." className="pl-9" />
              </div>
            </div>
            <Button variant="fitness" size="sm">
              <UserPlus className="h-4 w-4 mr-2" />
              Add Instructor
            </Button>
          </div>

          <div className="grid gap-6">
            {instructors.length === 0 ? (
              <Card variant="neumorphic">
                <CardContent className="p-8 text-center">
                  <GraduationCap className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                  <h3 className="text-lg font-semibold mb-2">No instructors found</h3>
                  <p className="text-muted-foreground">
                    No instructors have been registered yet.
                  </p>
                </CardContent>
              </Card>
            ) : (
              instructors.map((instructor) => (
                <Card key={instructor.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-start gap-4">
                      <Avatar className="h-20 w-20">
                        <AvatarImage src={getAvatarUrl(instructor)} />
                        <AvatarFallback>{getInitials(instructor)}</AvatarFallback>
                      </Avatar>
                      
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-4">
                          <div>
                            <h3 className="text-xl font-semibold">
                              {instructor.firstName} {instructor.lastName}
                            </h3>
                            <div className="flex items-center gap-2 mb-2">
                              <GraduationCap className="h-4 w-4" />
                              <span className="text-sm text-muted-foreground">Certified Instructor</span>
                            </div>
                            {getStatusBadge(instructor)}
                          </div>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon">
                                <MoreVertical className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuItem>
                                <Eye className="h-4 w-4 mr-2" />
                                View Profile
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <BookOpen className="h-4 w-4 mr-2" />
                                View Programs
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <BarChart3 className="h-4 w-4 mr-2" />
                                View Analytics
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <Edit className="h-4 w-4 mr-2" />
                                Edit Profile
                              </DropdownMenuItem>
                              {(() => {
                                // Use the same logic as getStatusBadge for consistency
                                const isVerified = instructor.isVerified !== undefined ? instructor.isVerified : (instructor.status === 'VERIFIED');
                                return isVerified ? (
                                <DropdownMenuItem 
                                  className="text-destructive"
                                  onClick={() => handleUserStatusUpdate(instructor.id, false)}
                                >
                                  <Ban className="h-4 w-4 mr-2" />
                                    Unverify Instructor
                                </DropdownMenuItem>
                              ) : (
                                <DropdownMenuItem
                                  onClick={() => handleUserStatusUpdate(instructor.id, true)}
                                >
                                  <UserCheck className="h-4 w-4 mr-2" />
                                    Verify Instructor
                                </DropdownMenuItem>
                                );
                              })()}
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                        
                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-primary">{instructor.programCount}</div>
                            <div className="text-xs text-muted-foreground">Programs</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-green-400">{instructor.enrollmentCount}</div>
                            <div className="text-xs text-muted-foreground">Total Enrollments</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold">4.8</div>
                            <div className="text-xs text-muted-foreground">Avg Rating</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold">{instructor.activityCount}</div>
                            <div className="text-xs text-muted-foreground">Activities</div>
                          </div>
                        </div>

                        <div className="flex items-center gap-6 text-xs text-muted-foreground">
                          <span>Joined: {formatDate(instructor.createdAt)}</span>
                          {instructor.lastLoginAt && (
                            <span>Last login: {formatDate(instructor.lastLoginAt)}</span>
                          )}
                          <div className="flex items-center gap-1">
                            <Mail className="h-3 w-3" />
                            <span>{instructor.email}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        {/* Programs Tab */}
        <TabsContent value="programs" className="space-y-6">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center gap-4 flex-1 flex-wrap">
              <div className="relative flex-1 max-w-sm min-w-[200px]">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input 
                  placeholder="Search programs..." 
                  className="pl-9"
                  value={programSearchTerm}
                  onChange={(e) => setProgramSearchTerm(e.target.value)}
                />
              </div>
              <Select value={selectedCategory || "all"} onValueChange={(value) => setSelectedCategory(value === "all" ? "" : value)}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="All Categories" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Categories</SelectItem>
                  <SelectItem value="Cardio">Cardio</SelectItem>
                  <SelectItem value="Strength Training">Strength Training</SelectItem>
                  <SelectItem value="Yoga">Yoga</SelectItem>
                  <SelectItem value="Pilates">Pilates</SelectItem>
                  <SelectItem value="CrossFit">CrossFit</SelectItem>
                  <SelectItem value="Swimming">Swimming</SelectItem>
                  <SelectItem value="Running">Running</SelectItem>
                  <SelectItem value="Dance">Dance</SelectItem>
                </SelectContent>
              </Select>
              
              <Select value={selectedDifficulty || "all"} onValueChange={(value) => setSelectedDifficulty(value === "all" ? "" : value)}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="All Difficulties" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Difficulties</SelectItem>
                  <SelectItem value="BEGINNER">Beginner</SelectItem>
                  <SelectItem value="INTERMEDIATE">Intermediate</SelectItem>
                  <SelectItem value="ADVANCED">Advanced</SelectItem>
                </SelectContent>
              </Select>
              
              <Select value={selectedStatus || "all"} onValueChange={(value) => setSelectedStatus(value === "all" ? "" : value)}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="All Status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Status</SelectItem>
                  <SelectItem value="active">Active</SelectItem>
                  <SelectItem value="inactive">Pending</SelectItem>
                </SelectContent>
              </Select>
              
              {/* Clear Filters Button */}
              {(selectedCategory || selectedDifficulty || selectedStatus || programSearchTerm) && (
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => {
                    setSelectedCategory("");
                    setSelectedDifficulty("");
                    setSelectedStatus("");
                    setProgramSearchTerm("");
                  }}
                  className="flex items-center gap-2"
                >
                  <XCircle className="h-4 w-4" />
                  Clear Filters
              </Button>
              )}
            </div>
          </div>

          {/* Active Filters Summary */}
          {(selectedCategory || selectedDifficulty || selectedStatus || programSearchTerm) && (
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-sm text-muted-foreground">Active filters:</span>
              {programSearchTerm && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  <Search className="h-3 w-3" />
                  Search: "{programSearchTerm}"
                </Badge>
              )}
              {selectedCategory && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  <BookOpen className="h-3 w-3" />
                  Category: {selectedCategory}
                </Badge>
              )}
              {selectedDifficulty && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  <Activity className="h-3 w-3" />
                  Difficulty: {selectedDifficulty}
                </Badge>
              )}
              {selectedStatus && (
                <Badge variant="secondary" className="flex items-center gap-1">
                  {selectedStatus === 'active' ? (
                    <CheckCircle className="h-3 w-3 text-green-500" />
                  ) : (
                    <Clock className="h-3 w-3 text-yellow-500" />
                  )}
                  Status: {selectedStatus === 'active' ? 'Active' : 'Pending'}
                </Badge>
              )}
            </div>
          )}

          {/* Results Summary */}
          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              Showing {programs.length} program{programs.length !== 1 ? 's' : ''}
              {(selectedCategory || selectedDifficulty || selectedStatus || programSearchTerm) && ' (filtered)'}
            </p>
          </div>

          <div className="grid gap-6">
            {programs.length === 0 ? (
            <Card variant="neumorphic">
              <CardContent className="p-8 text-center">
                <BookOpen className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                  <h3 className="text-lg font-semibold mb-2">No programs found</h3>
                <p className="text-muted-foreground">
                    {programSearchTerm || selectedCategory || selectedDifficulty ? 'Try adjusting your search criteria.' : 'No programs have been created yet.'}
                </p>
              </CardContent>
            </Card>
            ) : (
              programs.map((program) => (
                <Card key={program.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-start gap-4">
                      <div className="w-20 h-20 bg-gradient-primary rounded-lg flex items-center justify-center">
                        <BookOpen className="h-8 w-8 text-white" />
                      </div>
                      
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-3">
                          <div>
                            <div className="flex items-center gap-2 mb-1">
                              <h3 className="text-lg font-semibold">
                                {program.name}
                              </h3>
                              {getProgramStatusBadge(program)}
                            </div>
                            <p className="text-sm text-muted-foreground mb-2 line-clamp-2">
                              {program.description}
                            </p>
                            <div className="flex items-center gap-4 text-sm text-muted-foreground">
                              <span className="flex items-center gap-1">
                                <GraduationCap className="h-4 w-4" />
                                {program.instructorName}
                              </span>
                              <span className="flex items-center gap-1">
                                <BookOpen className="h-4 w-4" />
                                {program.categoryName}
                              </span>
                              <span className="flex items-center gap-1">
                                <Activity className="h-4 w-4" />
                                {program.difficultyLevel}
                              </span>
                            </div>
                          </div>
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon">
                                <MoreVertical className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuItem>
                                <Eye className="h-4 w-4 mr-2" />
                                View Details
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <Edit className="h-4 w-4 mr-2" />
                                Edit Program
                              </DropdownMenuItem>
                              {getProgramActivationStatus(program) ? (
                                <DropdownMenuItem 
                                  className="text-destructive"
                                  onClick={() => handleProgramStatusUpdate(program.id, false)}
                                >
                                  <Ban className="h-4 w-4 mr-2" />
                                  Deactivate Program
                                </DropdownMenuItem>
                              ) : (
                                <DropdownMenuItem
                                  onClick={() => handleProgramStatusUpdate(program.id, true)}
                                >
                                  <UserCheck className="h-4 w-4 mr-2" />
                                  Activate Program
                                </DropdownMenuItem>
                              )}
                              <DropdownMenuItem 
                                className="text-destructive"
                                onClick={() => handleProgramDelete(program.id)}
                              >
                                <Trash2 className="h-4 w-4 mr-2" />
                                Delete Program
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                        
                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-primary">{program.duration}</div>
                            <div className="text-xs text-muted-foreground">Minutes</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-green-400">${program.price}</div>
                            <div className="text-xs text-muted-foreground">Price</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-blue-400">{program.enrollmentCount}</div>
                            <div className="text-xs text-muted-foreground">Enrollments</div>
                          </div>
                          <div className="text-center p-3 bg-background/50 rounded-lg">
                            <div className="text-lg font-semibold text-purple-400">{program.commentCount}</div>
                            <div className="text-xs text-muted-foreground">Comments</div>
                          </div>
                        </div>

                        <div className="flex items-center gap-6 text-xs text-muted-foreground">
                          <span>Created: {formatDate(program.createdAt)}</span>
                          <span>Location: {program.locationName}</span>
                          <span>Status: {program.status}</span>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        {/* System Tab */}
        <TabsContent value="system" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* System Alerts */}
            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Shield className="h-5 w-5 text-primary" />
                  System Alerts
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {systemAlerts.map((alert, index) => (
                  <div key={index} className="flex items-start gap-3 p-3 rounded-lg bg-background/50 border border-border/30">
                    {getAlertIcon(alert.type)}
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium">{alert.message}</p>
                      <p className="text-xs text-muted-foreground mt-1">{alert.time}</p>
                    </div>
                    <Badge variant={alert.severity === 'high' ? 'destructive' : alert.severity === 'medium' ? 'secondary' : 'default'} className="text-xs">
                      {alert.severity}
                    </Badge>
                  </div>
                ))}
              </CardContent>
            </Card>

            {/* System Management */}
            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Database className="h-5 w-5 text-primary" />
                  System Management
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 gap-4">
                  <Button variant="outline" className="flex flex-col items-center gap-2 h-20">
                    <BarChart3 className="h-6 w-6" />
                    <span className="text-sm">Analytics</span>
                  </Button>
                  <Button variant="outline" className="flex flex-col items-center gap-2 h-20">
                    <Database className="h-6 w-6" />
                    <span className="text-sm">Database</span>
                  </Button>
                  <Button variant="outline" className="flex flex-col items-center gap-2 h-20">
                    <Shield className="h-6 w-6" />
                    <span className="text-sm">Security</span>
                  </Button>
                  <Button variant="outline" className="flex flex-col items-center gap-2 h-20">
                    <Settings className="h-6 w-6" />
                    <span className="text-sm">Config</span>
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Performance Metrics */}
          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Activity className="h-5 w-5 text-primary" />
                System Performance
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>CPU Usage</span>
                    <span>67%</span>
                  </div>
                  <Progress value={67} className="h-2" />
                </div>
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Memory Usage</span>
                    <span>45%</span>
                  </div>
                  <Progress value={45} className="h-2" />
                </div>
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Storage Used</span>
                    <span>78%</span>
                  </div>
                  <Progress value={78} className="h-2" />
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Analytics Tab */}
        <TabsContent value="analytics" className="space-y-6">
          {analytics ? (
            <>
              {/* Overview Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6">
            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <DollarSign className="h-5 w-5 text-primary" />
                      Total Revenue
                </CardTitle>
              </CardHeader>
              <CardContent>
                    <div className="text-2xl font-bold text-green-400">
                      ${analytics.totalRevenue.toLocaleString()}
                  </div>
                    <div className="text-sm text-muted-foreground">
                      This month: ${analytics.revenueThisMonth.toLocaleString()}
                  </div>
                  </CardContent>
                </Card>

                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Users className="h-5 w-5 text-blue-400" />
                      User Growth
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold text-blue-400">
                      {analytics.newUsersThisMonth}
                  </div>
                    <div className="text-sm text-muted-foreground">
                      New users this month
                </div>
              </CardContent>
            </Card>

            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                      <BookOpen className="h-5 w-5 text-purple-400" />
                      Program Growth
                </CardTitle>
              </CardHeader>
              <CardContent>
                    <div className="text-2xl font-bold text-purple-400">
                      {analytics.newProgramsThisMonth}
                  </div>
                    <div className="text-sm text-muted-foreground">
                      New programs this month
                  </div>
                  </CardContent>
                </Card>

                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Activity className="h-5 w-5 text-orange-400" />
                      Enrollments
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold text-orange-400">
                      {analytics.newEnrollmentsThisMonth}
                  </div>
                    <div className="text-sm text-muted-foreground">
                      New enrollments this month
                </div>
              </CardContent>
            </Card>

            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                      <BookOpen className="h-5 w-5 text-green-400" />
                      Active Programs
                </CardTitle>
              </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold text-green-400">
                      {analytics.activePrograms}
                    </div>
                    <div className="text-sm text-muted-foreground">
                      {analytics.inactivePrograms} pending activation
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Charts Section */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* User Growth Chart */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>User Growth (Last 7 Days)</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                      {analytics.userGrowthChart.map((point, index) => (
                        <div key={index} className="flex items-center justify-between">
                          <span className="text-sm">{point.label}</span>
                          <div className="flex items-center gap-2">
                            <div className="w-20 bg-background rounded-full h-2">
                              <div 
                                className="bg-primary h-2 rounded-full" 
                                style={{ width: `${Math.min((point.value / Math.max(...analytics.userGrowthChart.map(p => p.value))) * 100, 100)}%` }}
                              ></div>
                  </div>
                            <span className="text-sm font-medium w-12 text-right">{point.value}</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* Program Enrollments Chart */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>Program Enrollments (Last 7 Days)</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {analytics.programEnrollmentChart.map((point, index) => (
                        <div key={index} className="flex items-center justify-between">
                          <span className="text-sm">{point.label}</span>
                          <div className="flex items-center gap-2">
                            <div className="w-20 bg-background rounded-full h-2">
                              <div 
                                className="bg-blue-400 h-2 rounded-full" 
                                style={{ width: `${Math.min((point.value / Math.max(...analytics.programEnrollmentChart.map(p => p.value))) * 100, 100)}%` }}
                              ></div>
                            </div>
                            <span className="text-sm font-medium w-12 text-right">{point.value}</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Category and Difficulty Distribution */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Category Distribution */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>Program Categories</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {analytics.categoryDistribution.map((category, index) => (
                        <div key={index} className="space-y-2">
                  <div className="flex justify-between items-center">
                            <span className="text-sm font-medium">{category.category}</span>
                            <span className="text-sm text-muted-foreground">{category.count} programs</span>
                  </div>
                          <div className="w-full bg-background rounded-full h-2">
                            <div 
                              className="bg-gradient-primary h-2 rounded-full" 
                              style={{ width: `${category.percentage}%` }}
                            ></div>
                          </div>
                          <div className="text-xs text-muted-foreground text-right">
                            {category.percentage.toFixed(1)}%
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* Difficulty Distribution */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>Difficulty Levels</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {analytics.difficultyDistribution.map((difficulty, index) => (
                        <div key={index} className="space-y-2">
                  <div className="flex justify-between items-center">
                            <span className="text-sm font-medium">{difficulty.difficulty}</span>
                            <span className="text-sm text-muted-foreground">{difficulty.count} programs</span>
                  </div>
                          <div className="w-full bg-background rounded-full h-2">
                            <div 
                              className="bg-gradient-to-r from-green-400 to-red-400 h-2 rounded-full" 
                              style={{ width: `${difficulty.percentage}%` }}
                            ></div>
                          </div>
                          <div className="text-xs text-muted-foreground text-right">
                            {difficulty.percentage.toFixed(1)}%
                          </div>
                        </div>
                      ))}
                </div>
              </CardContent>
            </Card>
          </div>

              {/* Top Performers */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Top Instructors */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>Top Instructors</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {analytics.topInstructors.map((instructor, index) => (
                        <div key={index} className="flex items-center justify-between p-3 bg-background/50 rounded-lg">
                          <div>
                            <div className="font-medium">{instructor.name}</div>
                            <div className="text-sm text-muted-foreground">{instructor.email}</div>
                          </div>
                          <div className="text-right">
                            <div className="text-sm font-medium">{instructor.programCount} programs</div>
                            <div className="text-xs text-muted-foreground">{instructor.enrollmentCount} enrollments</div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* Top Programs */}
                <Card variant="neumorphic">
                  <CardHeader>
                    <CardTitle>Top Programs</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {analytics.topPrograms.map((program, index) => (
                        <div key={index} className="flex items-center justify-between p-3 bg-background/50 rounded-lg">
                          <div>
                            <div className="font-medium">{program.name}</div>
                            <div className="text-sm text-muted-foreground">{program.instructorName}</div>
                          </div>
                          <div className="text-right">
                            <div className="text-sm font-medium">{program.enrollmentCount} enrollments</div>
                            <div className="text-xs text-muted-foreground">${program.revenue}</div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* System Health */}
              <Card variant="neumorphic">
                <CardHeader>
                  <CardTitle>System Health</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="text-center">
                      <div className="text-2xl font-bold text-green-400">{analytics.systemHealth.serverUptime}%</div>
                      <div className="text-sm text-muted-foreground">Server Uptime</div>
                    </div>
                    <div className="text-center">
                      <div className="text-2xl font-bold text-blue-400">{analytics.systemHealth.activeUsers}</div>
                      <div className="text-sm text-muted-foreground">Active Users</div>
                    </div>
                    <div className="text-center">
                      <div className="text-2xl font-bold text-purple-400">{analytics.systemHealth.averageResponseTime}ms</div>
                      <div className="text-sm text-muted-foreground">Avg Response Time</div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </>
          ) : (
            <Card variant="neumorphic">
              <CardContent className="p-8 text-center">
                <BarChart3 className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <h3 className="text-lg font-semibold mb-2">Loading Analytics</h3>
                <p className="text-muted-foreground">
                  Fetching real-time analytics data...
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>

      {/* Delete Confirmation Dialogs */}
      <DeleteConfirmationDialog
        open={deleteUserDialog.open}
        onOpenChange={(open) => setDeleteUserDialog({ open, user: null })}
        onConfirm={confirmUserDelete}
        title="Delete User"
        description="This action will permanently remove the user from the system."
        itemName={deleteUserDialog.user?.username || ''}
        itemType="user"
        isLoading={isDeleting}
        variant="destructive"
      />

      <DeleteConfirmationDialog
        open={deleteProgramDialog.open}
        onOpenChange={(open) => setDeleteProgramDialog({ open, program: null })}
        onConfirm={confirmProgramDelete}
        title="Delete Program"
        description="This action will permanently remove the program from the system."
        itemName={deleteProgramDialog.program?.name || ''}
        itemType="program"
        isLoading={isDeleting}
        variant="destructive"
      />
    </div>
  );
};