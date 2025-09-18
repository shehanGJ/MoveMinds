import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
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
  Briefcase
} from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";

export const AdminDashboard = () => {
  const systemStats = [
    { title: "Total Users", value: "2,547", change: "+148", icon: Users, color: "text-primary" },
    { title: "Active Programs", value: "89", change: "+7", icon: BookOpen, color: "text-blue-400" },
    { title: "Monthly Revenue", value: "$45,230", change: "+22%", icon: DollarSign, color: "text-green-400" },
    { title: "System Health", value: "99.2%", change: "+0.1%", icon: Activity, color: "text-green-400" },
  ];

  const allUsers = [
    { 
      id: 1,
      name: "Sarah Johnson", 
      email: "sarah@example.com", 
      role: "Student", 
      status: "active", 
      joined: "2024-01-15",
      lastActive: "2 hours ago",
      phone: "+1 (555) 123-4567",
      programs: 3,
      totalSpent: "$450"
    },
    { 
      id: 2,
      name: "Jessica Wilson", 
      email: "jessica@example.com", 
      role: "Instructor", 
      status: "active", 
      joined: "2023-11-20",
      lastActive: "30 min ago",
      phone: "+1 (555) 987-6543",
      programs: 8,
      totalEarned: "$12,450"
    },
    { 
      id: 3,
      name: "Mike Chen", 
      email: "mike@example.com", 
      role: "Student", 
      status: "pending", 
      joined: "2024-01-22",
      lastActive: "1 day ago",
      phone: "+1 (555) 456-7890",
      programs: 1,
      totalSpent: "$120"
    },
    { 
      id: 4,
      name: "David Miller", 
      email: "david@example.com", 
      role: "Instructor", 
      status: "active", 
      joined: "2023-09-15",
      lastActive: "4 hours ago",
      phone: "+1 (555) 321-0987",
      programs: 6,
      totalEarned: "$8,230"
    },
  ];

  const allPrograms = [
    {
      id: 1,
      name: "HIIT Transformation",
      instructor: "Jessica Wilson",
      students: 45,
      revenue: "$5,250",
      rating: 4.9,
      status: "active",
      created: "2023-12-01",
      duration: "8 weeks"
    },
    {
      id: 2,
      name: "Strength Building",
      instructor: "David Miller",
      students: 32,
      revenue: "$3,980",
      rating: 4.7,
      status: "active",
      created: "2023-11-15",
      duration: "12 weeks"
    },
    {
      id: 3,
      name: "Yoga Flow",
      instructor: "Lisa Zhang",
      students: 28,
      revenue: "$2,840",
      rating: 4.8,
      status: "review",
      created: "2024-01-10",
      duration: "6 weeks"
    },
  ];

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

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'active': return <Badge variant="default">Active</Badge>;
      case 'pending': return <Badge variant="secondary">Pending</Badge>;
      case 'suspended': return <Badge variant="destructive">Suspended</Badge>;
      case 'review': return <Badge variant="secondary">Under Review</Badge>;
      default: return <Badge variant="secondary">{status}</Badge>;
    }
  };

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'Instructor': return <GraduationCap className="h-4 w-4" />;
      case 'Admin': return <Crown className="h-4 w-4" />;
      default: return <Users className="h-4 w-4" />;
    }
  };

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
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center gap-4 flex-1">
              <div className="relative flex-1 max-w-sm">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search users..." className="pl-9" />
              </div>
              <Button variant="outline" size="sm">
                <Filter className="h-4 w-4 mr-2" />
                Filter
              </Button>
            </div>
            <Button variant="fitness" size="sm">
              <UserPlus className="h-4 w-4 mr-2" />
              Add User
            </Button>
          </div>

          <div className="grid gap-6">
            {allUsers.map((user) => (
              <Card key={user.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                <CardContent className="p-6">
                  <div className="flex items-start gap-4">
                    <Avatar className="h-16 w-16">
                      <AvatarImage src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${user.name}`} />
                      <AvatarFallback>{user.name.split(' ').map(n => n[0]).join('')}</AvatarFallback>
                    </Avatar>
                    
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-3">
                        <div>
                          <h3 className="text-lg font-semibold">{user.name}</h3>
                          <div className="flex items-center gap-2 mb-1">
                            {getRoleIcon(user.role)}
                            <span className="text-sm text-muted-foreground">{user.role}</span>
                          </div>
                          {getStatusBadge(user.status)}
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
                            {user.status === 'active' ? (
                              <DropdownMenuItem className="text-destructive">
                                <Ban className="h-4 w-4 mr-2" />
                                Suspend User
                              </DropdownMenuItem>
                            ) : (
                              <DropdownMenuItem>
                                <UserCheck className="h-4 w-4 mr-2" />
                                Activate User
                              </DropdownMenuItem>
                            )}
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                      
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <div className="flex items-center gap-2 text-sm">
                          <Mail className="h-4 w-4 text-muted-foreground" />
                          <span>{user.email}</span>
                        </div>
                        <div className="flex items-center gap-2 text-sm">
                          <Phone className="h-4 w-4 text-muted-foreground" />
                          <span>{user.phone}</span>
                        </div>
                        <div className="flex items-center gap-2 text-sm">
                          <BookOpen className="h-4 w-4 text-muted-foreground" />
                          <span>{user.programs} programs</span>
                        </div>
                      </div>

                      <div className="flex items-center gap-6 text-xs text-muted-foreground">
                        <span>Joined: {new Date(user.joined).toLocaleDateString()}</span>
                        <span>Last active: {user.lastActive}</span>
                        {user.totalSpent && <span className="text-green-400">Spent: {user.totalSpent}</span>}
                        {user.totalEarned && <span className="text-green-400">Earned: {user.totalEarned}</span>}
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
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
              <Button variant="outline" size="sm">
                <Filter className="h-4 w-4 mr-2" />
                Filter
              </Button>
            </div>
            <Button variant="fitness" size="sm">
              <UserPlus className="h-4 w-4 mr-2" />
              Add Instructor
            </Button>
          </div>

          <div className="grid gap-6">
            {allUsers.filter(user => user.role === 'Instructor').map((instructor) => (
              <Card key={instructor.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                <CardContent className="p-6">
                  <div className="flex items-start gap-4">
                    <Avatar className="h-20 w-20">
                      <AvatarImage src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${instructor.name}`} />
                      <AvatarFallback>{instructor.name.split(' ').map(n => n[0]).join('')}</AvatarFallback>
                    </Avatar>
                    
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-4">
                        <div>
                          <h3 className="text-xl font-semibold">{instructor.name}</h3>
                          <div className="flex items-center gap-2 mb-2">
                            <GraduationCap className="h-4 w-4" />
                            <span className="text-sm text-muted-foreground">Certified Instructor</span>
                          </div>
                          {getStatusBadge(instructor.status)}
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
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                      
                      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold text-primary">{instructor.programs}</div>
                          <div className="text-xs text-muted-foreground">Programs</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold text-green-400">{instructor.totalEarned}</div>
                          <div className="text-xs text-muted-foreground">Total Earned</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold">4.8</div>
                          <div className="text-xs text-muted-foreground">Avg Rating</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold">156</div>
                          <div className="text-xs text-muted-foreground">Students</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-6 text-xs text-muted-foreground">
                        <span>Joined: {new Date(instructor.joined).toLocaleDateString()}</span>
                        <span>Last active: {instructor.lastActive}</span>
                        <div className="flex items-center gap-1">
                          <Mail className="h-3 w-3" />
                          <span>{instructor.email}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        {/* Programs Tab */}
        <TabsContent value="programs" className="space-y-6">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center gap-4 flex-1">
              <div className="relative flex-1 max-w-sm">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search programs..." className="pl-9" />
              </div>
              <Button variant="outline" size="sm">
                <Filter className="h-4 w-4 mr-2" />
                Filter
              </Button>
            </div>
          </div>

          <div className="grid gap-6">
            {allPrograms.map((program) => (
              <Card key={program.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                <CardContent className="p-6">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-3">
                        <h3 className="text-xl font-semibold">{program.name}</h3>
                        {getStatusBadge(program.status)}
                      </div>
                      
                      <p className="text-sm text-muted-foreground mb-4">
                        Instructor: <span className="font-medium">{program.instructor}</span>
                      </p>
                      
                      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-4">
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold">{program.students}</div>
                          <div className="text-xs text-muted-foreground">Students</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold text-green-400">{program.revenue}</div>
                          <div className="text-xs text-muted-foreground">Revenue</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold">{program.rating}</div>
                          <div className="text-xs text-muted-foreground">Rating</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-lg font-semibold">{program.duration}</div>
                          <div className="text-xs text-muted-foreground">Duration</div>
                        </div>
                        <div className="text-center p-3 bg-background/50 rounded-lg">
                          <div className="text-xs font-medium">{new Date(program.created).toLocaleDateString()}</div>
                          <div className="text-xs text-muted-foreground">Created</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <Button size="sm" variant="fitness">
                          <Eye className="h-4 w-4 mr-2" />
                          View Details
                        </Button>
                        <Button size="sm" variant="outline">
                          <Edit className="h-4 w-4 mr-2" />
                          Edit
                        </Button>
                        <Button size="sm" variant="outline">
                          <BarChart3 className="h-4 w-4 mr-2" />
                          Analytics
                        </Button>
                        {program.status === 'review' && (
                          <>
                            <Button size="sm" variant="default">
                              <CheckCircle className="h-4 w-4 mr-2" />
                              Approve
                            </Button>
                            <Button size="sm" variant="destructive">
                              <XCircle className="h-4 w-4 mr-2" />
                              Reject
                            </Button>
                          </>
                        )}
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
                          <Users className="h-4 w-4 mr-2" />
                          Manage Students
                        </DropdownMenuItem>
                        <DropdownMenuItem>
                          <Download className="h-4 w-4 mr-2" />
                          Export Data
                        </DropdownMenuItem>
                        <DropdownMenuItem className="text-destructive">
                          <Trash2 className="h-4 w-4 mr-2" />
                          Delete Program
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </CardContent>
              </Card>
            ))}
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
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <DollarSign className="h-5 w-5 text-primary" />
                  Revenue Analytics
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">This Month</span>
                    <span className="font-semibold text-green-400">$45,230</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Last Month</span>
                    <span className="font-semibold">$37,150</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Growth</span>
                    <span className="font-semibold text-green-400">+22%</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users className="h-5 w-5 text-primary" />
                  User Analytics
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Total Users</span>
                    <span className="font-semibold">2,547</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">New This Month</span>
                    <span className="font-semibold text-green-400">+148</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Active Rate</span>
                    <span className="font-semibold">94%</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BookOpen className="h-5 w-5 text-primary" />
                  Program Analytics
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Total Programs</span>
                    <span className="font-semibold">89</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">New This Month</span>
                    <span className="font-semibold text-green-400">+7</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Avg Completion</span>
                    <span className="font-semibold">85%</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
};