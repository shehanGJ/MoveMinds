import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { useToast } from '@/hooks/use-toast';
import { adminApi, userApi, AdminUserResponse, User } from '@/lib/api';
import { useAuth } from '@/lib/auth';
import { 
  Search, 
  Filter, 
  MoreVertical, 
  Eye, 
  Edit, 
  Mail, 
  Ban, 
  UserCheck, 
  Trash2, 
  Users, 
  Crown, 
  GraduationCap,
  Loader2,
  UserPlus,
  Shield,
  Activity,
  Calendar,
  MessageCircle,
  Star,
  BookOpen
} from 'lucide-react';

const AdminUsers: React.FC = () => {
  // Get current user from auth context
  const { user: currentUser } = useAuth();
  
  const [users, setUsers] = useState<AdminUserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const { toast } = useToast();


  // Fetch users from API
  const fetchUsers = async () => {
    try {
      setLoading(true);
      
      const response = await adminApi.getAllUsers({
        page: currentPage,
        size: 12,
        role: selectedRole || undefined,
        search: searchTerm || undefined
      });
      
      console.log('AdminUsers - API Response:', response.data.content);
      console.log('AdminUsers - First user isVerified:', response.data.content[0]?.isVerified);
      console.log('AdminUsers - First user full object:', response.data.content[0]);
      
      // Filter out the current admin from the users list using username
      const filteredUsers = response.data.content.filter(user => 
        currentUser?.username ? user.username !== currentUser.username : true
      );
      console.log('AdminUsers - Current user username:', currentUser?.username);
      console.log('AdminUsers - Filtered users (excluding current admin):', filteredUsers);
      setUsers(filteredUsers);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Failed to fetch users:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: `Failed to load users: ${error instanceof Error ? error.message : 'Unknown error'}`
      });
    } finally {
      setLoading(false);
    }
  };

  // Load data on component mount and when dependencies change
  useEffect(() => {
    fetchUsers();
  }, [currentPage, selectedRole, searchTerm]);

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
            ? { ...user, isVerified: isActive, status: isActive ? 'VERIFIED' : 'NOT_VERIFIED' }
            : user
        )
      );
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
    if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      return;
    }
    
    try {
      await adminApi.deleteUser(userId);
      toast({
        title: "Success",
        description: "User deleted successfully"
      });
      fetchUsers(); // Refresh the list
    } catch (error) {
      console.error('Failed to delete user:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete user"
      });
    }
  };

  // Helper functions
  const getStatusBadge = (user: any) => {
    // Use isVerified field if available, otherwise fall back to status field
    const isVerified = user.isVerified !== undefined ? user.isVerified : (user.status === 'VERIFIED');
    
    return isVerified ? 
      <Badge className="bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300 border-green-200 dark:border-green-700">Verified</Badge> : 
      <Badge variant="secondary" className="bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-300 border-orange-200 dark:border-orange-700">Not Verified</Badge>;
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
      case 'ADMIN': return 'Administrator';
      default: return 'User';
    }
  };

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'INSTRUCTOR': return 'bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-900/20 dark:text-blue-300 dark:border-blue-700';
      case 'ADMIN': return 'bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-900/20 dark:text-purple-300 dark:border-purple-700';
      default: return 'bg-gray-50 text-gray-700 border-gray-200 dark:bg-gray-900/20 dark:text-gray-300 dark:border-gray-700';
    }
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

  const clearFilters = () => {
    setSearchTerm('');
    setSelectedRole('');
    setCurrentPage(0);
  };

  const hasActiveFilters = searchTerm || selectedRole;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">User Management</h1>
          <p className="text-muted-foreground">
            Manage all users, instructors, and administrators in the system
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button className="bg-gradient-primary hover:shadow-lg">
            <UserPlus className="h-4 w-4 mr-2" />
            Add User
          </Button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="hover:shadow-glow transition-all duration-300">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gradient-primary rounded-lg flex items-center justify-center">
                <Users className="h-6 w-6 text-white" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Total Users</p>
                <p className="text-2xl font-bold">{totalElements}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-glow transition-all duration-300">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
                <GraduationCap className="h-6 w-6 text-white" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Instructors</p>
                <p className="text-2xl font-bold">{users.filter(u => u.role === 'INSTRUCTOR').length}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-glow transition-all duration-300">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg flex items-center justify-center">
                <Crown className="h-6 w-6 text-white" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Administrators</p>
                <p className="text-2xl font-bold">{users.filter(u => u.role === 'ADMIN').length}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-glow transition-all duration-300">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-lg flex items-center justify-center">
                <Activity className="h-6 w-6 text-white" />
              </div>
                <div>
                  <p className="text-sm text-muted-foreground">Verified Users</p>
                  <p className="text-2xl font-bold">{users.filter(u => u.isVerified).length}</p>
                </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
       <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Filter className="h-5 w-5" />
            Filters & Search
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Search users by name, email, or username..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            
            <Select value={selectedRole || "all"} onValueChange={(value) => setSelectedRole(value === "all" ? "" : value)}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="All Roles" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Roles</SelectItem>
                <SelectItem value="USER">Users</SelectItem>
                <SelectItem value="INSTRUCTOR">Instructors</SelectItem>
                <SelectItem value="ADMIN">Administrators</SelectItem>
              </SelectContent>
            </Select>

            {hasActiveFilters && (
              <Button variant="outline" onClick={clearFilters}>
                Clear Filters
              </Button>
            )}
          </div>

          {hasActiveFilters && (
            <div className="mt-4 p-3 bg-muted/30 rounded-lg">
              <p className="text-sm text-muted-foreground">
                <strong>Active Filters:</strong>
                {searchTerm && ` Search: "${searchTerm}"`}
                {selectedRole && ` Role: ${getRoleDisplayName(selectedRole)}`}
              </p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Results Summary */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          Showing {users.length} of {totalElements} users
        </p>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
            disabled={currentPage === 0}
          >
            Previous
          </Button>
          <span className="text-sm text-muted-foreground">
            Page {currentPage + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
            disabled={currentPage >= totalPages - 1}
          >
            Next
          </Button>
        </div>
      </div>

      {/* Users Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(6)].map((_, i) => (
            <Card key={i} className="animate-pulse overflow-hidden">
              {/* Status bar skeleton */}
              <div className="h-1 bg-muted/30"></div>
              
              <CardContent className="p-0">
                {/* Header skeleton */}
                <div className="p-6 bg-gradient-to-br from-card to-card/80">
                  <div className="flex items-start gap-4">
                    <div className="w-16 h-16 bg-muted/50 rounded-full"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-5 bg-muted/50 rounded w-3/4"></div>
                      <div className="h-3 bg-muted/30 rounded w-1/2"></div>
                      <div className="h-4 bg-muted/30 rounded w-16"></div>
                    </div>
                    <div className="w-6 h-6 bg-muted/50 rounded"></div>
                  </div>
                </div>

                {/* Content skeleton */}
                <div className="p-6 space-y-4">
                  <div className="flex gap-2">
                    <div className="h-6 bg-muted/30 rounded-full w-20"></div>
                    <div className="h-6 bg-muted/30 rounded-full w-16"></div>
                  </div>
                  
                  <div className="space-y-2">
                    <div className="h-3 bg-muted/30 rounded w-full"></div>
                    <div className="h-3 bg-muted/30 rounded w-2/3"></div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <div className="w-4 h-4 bg-muted/30 rounded"></div>
                      <div className="h-3 bg-muted/30 rounded w-16"></div>
                    </div>
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <div className="w-4 h-4 bg-muted/30 rounded"></div>
                      <div className="h-3 bg-muted/30 rounded w-12"></div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 pt-2 border-t border-border/50">
                    <div className="h-8 bg-muted/30 rounded flex-1"></div>
                    <div className="h-8 w-8 bg-muted/30 rounded"></div>
                    <div className="h-8 w-8 bg-muted/30 rounded"></div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : users.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <Users className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">No users found</h3>
            <p className="text-muted-foreground text-center mb-4">
              {searchTerm || selectedRole ? 'Try adjusting your search criteria.' : 'No users have been registered yet.'}
            </p>
            <Button className="bg-gradient-primary">
              <UserPlus className="h-4 w-4 mr-2" />
              Add First User
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {users.map((user) => (
            <Card 
              key={user.id} 
              className="group hover:shadow-glow transition-all duration-300 hover:-translate-y-1 overflow-hidden relative"
            >
              {/* Status Indicator Bar */}
              <div className={`absolute top-0 left-0 right-0 h-1 ${
                user.isVerified 
                  ? 'bg-gradient-to-r from-green-400 to-green-600' 
                  : 'bg-gradient-to-r from-orange-400 to-orange-600'
              }`} />
              
              <CardContent className="p-0">
                {/* Header Section with Gradient Background */}
                <div className="relative p-6 bg-gradient-to-br from-card to-card/80">
                  <div className="flex items-start gap-4">
                    {/* User Avatar */}
                    <Avatar className="h-16 w-16 border-2 border-background shadow-lg group-hover:scale-110 transition-transform duration-300">
                      <AvatarImage src={getAvatarUrl(user)} />
                      <AvatarFallback className="bg-gradient-primary text-white font-bold">
                        {getInitials(user)}
                      </AvatarFallback>
                    </Avatar>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex-1 min-w-0">
                          <CardTitle className="text-lg font-bold line-clamp-1 group-hover:text-primary transition-colors">
                            {user.firstName} {user.lastName}
                          </CardTitle>
                          <div className="flex items-center gap-2 mb-2">
                            {getRoleIcon(user.role)}
                            <span className="text-sm text-muted-foreground">
                              {getRoleDisplayName(user.role)}
                            </span>
                          </div>
                        </div>
                        
                        {/* Status Badge */}
                        <div className="ml-2">
                          {getStatusBadge(user)}
                        </div>
                      </div>
                      
                      {/* Quick Info */}
                      <div className="flex items-center gap-4 text-xs text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <Mail className="h-3 w-3" />
                          <span className="font-medium truncate max-w-[120px]">{user.email}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <Users className="h-3 w-3" />
                          <span className="font-medium">@{user.username}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Content Section */}
                <div className="p-6 space-y-4">
                  {/* Role Badge */}
                  <div className="flex items-center gap-2">
                    <Badge 
                      variant="outline" 
                      className={`${getRoleColor(user.role)} hover:opacity-80 transition-opacity`}
                    >
                      {getRoleDisplayName(user.role)}
                    </Badge>
                  </div>

                  {/* User Stats */}
                  <div className="grid grid-cols-2 gap-3">
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <BookOpen className="h-4 w-4 text-muted-foreground" />
                      <div>
                        <p className="text-xs text-muted-foreground">Programs</p>
                        <p className="text-sm font-semibold">{user.programCount || 0}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <Activity className="h-4 w-4 text-muted-foreground" />
                      <div>
                        <p className="text-xs text-muted-foreground">Activities</p>
                        <p className="text-sm font-semibold">{user.activityCount || 0}</p>
                      </div>
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex items-center gap-2 pt-2 border-t border-border/50">
                    {(() => {
                      // Use the same logic as getStatusBadge for consistency
                      const isVerified = user.isVerified !== undefined ? user.isVerified : (user.status === 'VERIFIED');
                      
                      return (
                        <Button
                          variant={isVerified ? "outline" : "default"}
                          size="sm"
                          className={`flex-1 transition-all duration-200 ${
                            isVerified 
                              ? 'hover:bg-destructive hover:text-destructive-foreground hover:border-destructive' 
                              : 'bg-gradient-primary hover:shadow-lg hover:scale-105'
                          }`}
                          onClick={() => handleUserStatusUpdate(user.id, !isVerified)}
                        >
                          {isVerified ? (
                            <>
                              <Ban className="h-4 w-4 mr-2" />
                              Unverify
                            </>
                          ) : (
                            <>
                              <UserCheck className="h-4 w-4 mr-2" />
                              Verify
                            </>
                          )}
                        </Button>
                      );
                    })()}
                    
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="outline" size="sm" className="hover:bg-primary/10 hover:text-primary hover:border-primary/30 transition-all duration-200">
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
                        {user.role !== 'ADMIN' && (
                          <DropdownMenuItem onClick={() => handleUserRoleUpdate(user.id, user.role === 'INSTRUCTOR' ? 'USER' : 'INSTRUCTOR')}>
                            <Shield className="h-4 w-4 mr-2" />
                            {user.role === 'INSTRUCTOR' ? 'Demote to User' : 'Promote to Instructor'}
                          </DropdownMenuItem>
                        )}
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
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default AdminUsers;
