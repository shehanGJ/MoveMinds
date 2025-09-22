import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  Users, 
  BookOpen, 
  TrendingUp, 
  Calendar,
  MessageSquare,
  Award,
  Clock,
  DollarSign,
  Plus,
  MoreVertical,
  Eye,
  Edit,
  Trash2,
  Search,
  Filter,
  Download,
  Mail,
  Phone,
  MapPin,
  GraduationCap,
  Target,
  PlayCircle,
  PauseCircle,
  UserCheck,
  UserX,
  CheckCircle
} from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { instructorApi, InstructorStatsResponse, ProgramEnrollmentResponse, PageResponse, Program, ProgramStatsResponse, categoryApi, locationApi, Category, Location } from '@/lib/api';
import { toast } from '@/hooks/use-toast';

export const InstructorDashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState<InstructorStatsResponse | null>(null);
  const [programs, setPrograms] = useState<PageResponse<Program> | null>(null);
  const [students, setStudents] = useState<PageResponse<ProgramEnrollmentResponse> | null>(null);
  const [programStats, setProgramStats] = useState<Map<number, ProgramStatsResponse>>(new Map());
  const [categories, setCategories] = useState<Category[]>([]);
  const [locations, setLocations] = useState<Location[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'programs' | 'students' | 'analytics'>('overview');
  const [createProgramOpen, setCreateProgramOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);

  useEffect(() => {
    fetchData();
  }, [currentPage]);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      // Fetch public data first (categories and locations)
      const [categoriesResponse, locationsResponse] = await Promise.all([
        categoryApi.getAll(),
        locationApi.getAll()
      ]);
      
      setCategories(categoriesResponse.data);
      setLocations(locationsResponse.data);
      
      // Try to fetch instructor-specific data (may fail if not authenticated)
      try {
        const [statsResponse, programsResponse, studentsResponse] = await Promise.all([
          instructorApi.getStats(),
          instructorApi.getMyPrograms({ page: currentPage, size: pageSize }),
          instructorApi.getStudents({ page: currentPage, size: pageSize })
        ]);
        
        setStats(statsResponse.data);
        setPrograms(programsResponse.data);
        setStudents(studentsResponse.data);
        
        // Fetch program stats for each program
        if (programsResponse.data?.content) {
          const statsPromises = programsResponse.data.content.map(program => 
            instructorApi.getProgramStats(program.id).catch((error) => {
              console.error(`Failed to fetch stats for program ${program.id}:`, error);
              return null;
            })
          );
          const statsResults = await Promise.all(statsPromises);
          
          const statsMap = new Map<number, ProgramStatsResponse>();
          statsResults.forEach((stats, index) => {
            if (stats) {
              statsMap.set(programsResponse.data.content[index].id, stats);
            }
          });
          setProgramStats(statsMap);
        }
      } catch (instructorError) {
        // Instructor data fetch failed (authentication required)
        // Don't show error toast for instructor data
      }
      
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load categories and locations"
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProgram = async (programData: any) => {
    try {
      await instructorApi.createProgram(programData, []);
      toast({
        title: "Success",
        description: "Program created successfully"
      });
      setCreateProgramOpen(false);
      fetchData();
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to create program"
      });
    }
  };

  const handleDeleteProgram = async (programId: number) => {
    if (window.confirm('Are you sure you want to delete this program?')) {
      try {
        await instructorApi.deleteProgram(programId);
        toast({
          title: "Success",
          description: "Program deleted successfully"
        });
        fetchData();
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to delete program"
        });
      }
    }
  };

  const handleUpdateEnrollmentStatus = async (enrollmentId: number, status: string) => {
    try {
      await instructorApi.updateEnrollmentStatus(enrollmentId, status);
      toast({
        title: "Success",
        description: "Enrollment status updated successfully"
      });
      fetchData();
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update enrollment status"
      });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold bg-gradient-primary bg-clip-text text-transparent">
            Instructor Dashboard
          </h1>
          <p className="text-muted-foreground mt-1">
            Manage your programs and track student progress
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">
            <Download className="h-4 w-4 mr-2" />
            Export Data
          </Button>
          <Button 
            variant="fitness" 
            className="flex items-center gap-2"
            onClick={() => setCreateProgramOpen(true)}
          >
            <Plus className="h-4 w-4" />
            Create Program
          </Button>
        </div>
      </div>

      {/* Stats Grid */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total Students</p>
                  <p className="text-2xl font-bold mt-2">{stats.totalStudents}</p>
                  <p className="text-sm text-primary mt-1">Active: {stats.activeEnrollments}</p>
                </div>
                <div className="p-3 rounded-lg bg-background/50 text-primary">
                  <Users className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">My Programs</p>
                  <p className="text-2xl font-bold mt-2">{stats.totalPrograms}</p>
                  <p className="text-sm text-blue-400 mt-1">This month: {stats.programsThisMonth}</p>
                </div>
                <div className="p-3 rounded-lg bg-background/50 text-blue-400">
                  <BookOpen className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Total Revenue</p>
                  <p className="text-2xl font-bold mt-2">${stats.totalRevenue}</p>
                  <p className="text-sm text-green-400 mt-1">This month: ${stats.monthlyRevenue}</p>
                </div>
                <div className="p-3 rounded-lg bg-background/50 text-green-400">
                  <DollarSign className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Avg Rating</p>
                  <p className="text-2xl font-bold mt-2">{stats.averageRating}</p>
                  <p className="text-sm text-yellow-400 mt-1">Based on reviews</p>
                </div>
                <div className="p-3 rounded-lg bg-background/50 text-yellow-400">
                  <Award className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Main Content Tabs */}
      <Tabs defaultValue="programs" className="space-y-6">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="programs">Programs</TabsTrigger>
          <TabsTrigger value="students">Students</TabsTrigger>
          <TabsTrigger value="analytics">Analytics</TabsTrigger>
          <TabsTrigger value="schedule">Schedule</TabsTrigger>
        </TabsList>

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
            {loading ? (
              <div className="text-center py-8">Loading programs...</div>
            ) : programs?.content.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-muted-foreground mb-4">No programs found</p>
                <Button onClick={() => setCreateProgramOpen(true)}>
                  <Plus className="h-4 w-4 mr-2" />
                  Create Your First Program
                </Button>
              </div>
            ) : (
              programs?.content.map((program) => (
                <Card key={program.id} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-3">
                          <h3 className="text-xl font-semibold">{program.name}</h3>
                          <Badge variant="default">
                            Active
                          </Badge>
                          <Badge variant="outline">{program.difficultyLevel}</Badge>
                        </div>
                        
                        <p className="text-muted-foreground mb-4">{program.description}</p>
                        
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Users className="h-4 w-4" />
                            <span>{programStats.get(program.id)?.totalStudents || 0} students</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Clock className="h-4 w-4" />
                            <span>{program.duration} weeks</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <DollarSign className="h-4 w-4" />
                            <span>${program.price}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Award className="h-4 w-4" />
                            <span>N/A rating</span>
                          </div>
                        </div>

                        <div className="flex items-center gap-2">
                          <Button size="sm" variant="fitness" asChild>
                            <Link to={`/instructor/programs/${program.id}/content`}>
                              <BookOpen className="h-4 w-4 mr-2" />
                              Manage Content
                            </Link>
                          </Button>
                          <Button size="sm" variant="outline">
                            <Edit className="h-4 w-4 mr-2" />
                            Edit
                          </Button>
                          <Button 
                            size="sm" 
                            variant="outline"
                            onClick={() => handleDeleteProgram(program.id)}
                            className="text-destructive hover:text-destructive"
                          >
                            <Trash2 className="h-4 w-4 mr-2" />
                            Delete
                          </Button>
                        </div>
                      </div>
                      
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreVertical className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => navigate(`/instructor/programs/${program.id}/content`)}>
                            <BookOpen className="h-4 w-4 mr-2" />
                            Manage Content
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <Users className="h-4 w-4 mr-2" />
                            Manage Students
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <TrendingUp className="h-4 w-4 mr-2" />
                            View Analytics
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <Download className="h-4 w-4 mr-2" />
                            Export Data
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        {/* Students Tab */}
        <TabsContent value="students" className="space-y-6">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center gap-4 flex-1">
              <div className="relative flex-1 max-w-sm">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search students..." className="pl-9" />
              </div>
              <Button variant="outline" size="sm">
                <Filter className="h-4 w-4 mr-2" />
                Filter
              </Button>
            </div>
            <Button variant="fitness" size="sm">
              <Plus className="h-4 w-4 mr-2" />
              Add Student
            </Button>
          </div>

          <div className="grid gap-6">
            {loading ? (
              <div className="text-center py-8">Loading students...</div>
            ) : students?.content.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-muted-foreground">No students enrolled yet</p>
              </div>
            ) : (
              students?.content.map((enrollment) => (
                <Card key={enrollment.enrollmentId} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-start gap-4">
                      <Avatar className="h-16 w-16">
                        <AvatarImage src={enrollment.avatarUrl} />
                        <AvatarFallback>{enrollment.firstName?.[0]}{enrollment.lastName?.[0]}</AvatarFallback>
                      </Avatar>
                      
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-3">
                          <div>
                            <h3 className="text-lg font-semibold">{enrollment.firstName} {enrollment.lastName}</h3>
                            <p className="text-sm text-muted-foreground mb-1">@{enrollment.username}</p>
                            <p className="text-sm text-muted-foreground mb-1">{enrollment.programName}</p>
                            <Badge variant={enrollment.status === 'ACTIVE' ? 'default' : 'secondary'}>
                              {enrollment.status}
                            </Badge>
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
                                <MessageSquare className="h-4 w-4 mr-2" />
                                Send Message
                              </DropdownMenuItem>
                              <DropdownMenuItem>
                                <TrendingUp className="h-4 w-4 mr-2" />
                                View Progress
                              </DropdownMenuItem>
                              <DropdownMenuItem onClick={() => handleUpdateEnrollmentStatus(enrollment.enrollmentId, 'ACTIVE')}>
                                <UserCheck className="h-4 w-4 mr-2" />
                                Activate
                              </DropdownMenuItem>
                              <DropdownMenuItem onClick={() => handleUpdateEnrollmentStatus(enrollment.enrollmentId, 'INACTIVE')}>
                                <UserX className="h-4 w-4 mr-2" />
                                Deactivate
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                        
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                          <div className="flex items-center gap-2 text-sm">
                            <Mail className="h-4 w-4 text-muted-foreground" />
                            <span>{enrollment.email}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <Calendar className="h-4 w-4 text-muted-foreground" />
                            <span>Enrolled: {new Date(enrollment.enrolledAt).toLocaleDateString()}</span>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <DollarSign className="h-4 w-4 text-muted-foreground" />
                            <span>Program: {enrollment.programName}</span>
                          </div>
                        </div>

                        <div className="space-y-2 mb-4">
                          <div className="flex justify-between text-sm">
                            <span>Progress</span>
                            <span>{enrollment.progress || 0}%</span>
                          </div>
                          <Progress value={enrollment.progress || 0} className="h-2" />
                        </div>

                        <div className="flex items-center gap-4 text-xs text-muted-foreground">
                          <span>Program: {enrollment.programName}</span>
                          <span>Status: {enrollment.status}</span>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        {/* Analytics Tab */}
        <TabsContent value="analytics" className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5 text-primary" />
                  Revenue Analytics
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">This Month</span>
                    <span className="font-semibold text-green-400">$4,250</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Last Month</span>
                    <span className="font-semibold">$3,600</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Growth</span>
                    <span className="font-semibold text-green-400">+18%</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card variant="neumorphic">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users className="h-5 w-5 text-primary" />
                  Student Analytics
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Active Students</span>
                    <span className="font-semibold">324</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">New This Month</span>
                    <span className="font-semibold text-green-400">+38</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Completion Rate</span>
                    <span className="font-semibold">87%</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Award className="h-5 w-5 text-primary" />
                Program Performance
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {programs?.content.map((program) => (
                  <div key={program.id} className="p-4 rounded-lg bg-background/50 border border-border/30">
                    <h4 className="font-medium mb-2">{program.name}</h4>
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span>Students:</span>
                        <span>0</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Rating:</span>
                        <span>N/A</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Price:</span>
                        <span className="text-green-400">${program.price}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Schedule Tab */}
        <TabsContent value="schedule" className="space-y-6">
          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar className="h-5 w-5 text-primary" />
                Upcoming Sessions
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  { time: "9:00 AM", program: "HIIT Transformation", students: 12, type: "Live Session" },
                  { time: "11:00 AM", program: "Yoga Flow", students: 8, type: "Virtual Class" },
                  { time: "2:00 PM", program: "Strength Building", students: 15, type: "Live Session" },
                  { time: "4:00 PM", program: "Cardio Blast", students: 6, type: "Virtual Class" },
                ].map((session, index) => (
                  <div key={index} className="flex items-center justify-between p-4 rounded-lg bg-background/50 border border-border/30">
                    <div className="flex items-center gap-4">
                      <div className="text-center">
                        <div className="text-lg font-semibold">{session.time}</div>
                        <div className="text-xs text-muted-foreground">Today</div>
                      </div>
                      <div>
                        <div className="font-medium">{session.program}</div>
                        <div className="text-sm text-muted-foreground">{session.students} students • {session.type}</div>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button size="sm" variant="outline">
                        <MessageSquare className="h-4 w-4 mr-2" />
                        Message
                      </Button>
                      <Button size="sm" variant="fitness">
                        <PlayCircle className="h-4 w-4 mr-2" />
                        Start
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Create Program Dialog */}
      <Dialog open={createProgramOpen} onOpenChange={setCreateProgramOpen}>
        <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Create New Program</DialogTitle>
            <DialogDescription>
              Create a new fitness program for your students
            </DialogDescription>
          </DialogHeader>
          <CreateProgramForm 
            onSubmit={handleCreateProgram} 
            onCancel={() => setCreateProgramOpen(false)}
            categories={categories}
            locations={locations}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

// Program Creation Form Component
const CreateProgramForm = ({ 
  onSubmit, 
  onCancel, 
  categories, 
  locations 
}: { 
  onSubmit: (data: any) => void; 
  onCancel: () => void;
  categories: Category[];
  locations: Location[];
}) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    difficultyLevel: 'BEGINNER' as string,
    duration: 4,
    price: 0,
    categoryId: null as number | null,
    locationId: null as number | null,
    youtubeUrl: '',
    specificAttributes: [] as any[]
  });

  const [currentStep, setCurrentStep] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Set default values for category and location when they are loaded
  useEffect(() => {
    if (categories.length > 0 && locations.length > 0 && formData.categoryId === null && formData.locationId === null) {
      setFormData(prev => ({
        ...prev,
        categoryId: categories[0].id,
        locationId: locations[0].id
      }));
    }
  }, [categories, locations, formData.categoryId, formData.locationId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (currentStep < 3) {
      setCurrentStep(currentStep + 1);
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit(formData);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'BEGINNER': return 'text-green-500 bg-green-50 border-green-200';
      case 'INTERMEDIATE': return 'text-yellow-500 bg-yellow-50 border-yellow-200';
      case 'ADVANCED': return 'text-red-500 bg-red-50 border-red-200';
      default: return 'text-gray-500 bg-gray-50 border-gray-200';
    }
  };

  const renderStep1 = () => (
    <div className="space-y-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-primary flex items-center justify-center">
          <BookOpen className="h-8 w-8 text-white" />
        </div>
        <h3 className="text-xl font-semibold text-foreground">Basic Information</h3>
        <p className="text-muted-foreground">Let's start with the essential details of your program</p>
      </div>

      <div className="space-y-4">
        <div>
          <Label htmlFor="name" className="text-sm font-medium text-foreground">
            Program Name *
          </Label>
          <Input
            id="name"
            value={formData.name}
            onChange={(e) => handleInputChange('name', e.target.value)}
            placeholder="e.g., Yoga for Beginners, HIIT Transformation"
            className="mt-1"
            required
          />
        </div>

        <div>
          <Label htmlFor="description" className="text-sm font-medium text-foreground">
            Program Description *
          </Label>
          <Textarea
            id="description"
            value={formData.description}
            onChange={(e) => handleInputChange('description', e.target.value)}
            placeholder="Describe what students will learn, the benefits, and what makes your program unique..."
            className="mt-1 min-h-[100px]"
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <Label htmlFor="category" className="text-sm font-medium text-foreground">
              Category *
            </Label>
            <Select 
              value={formData.categoryId?.toString() || ''} 
              onValueChange={(value) => handleInputChange('categoryId', parseInt(value))}
            >
              <SelectTrigger className="mt-1">
                <SelectValue placeholder="Select category" />
              </SelectTrigger>
              <SelectContent>
                {categories.map((category) => (
                  <SelectItem key={category.id} value={category.id.toString()}>
                    {category.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label htmlFor="location" className="text-sm font-medium text-foreground">
              Location *
            </Label>
            <Select 
              value={formData.locationId?.toString() || ''} 
              onValueChange={(value) => handleInputChange('locationId', parseInt(value))}
            >
              <SelectTrigger className="mt-1">
                <SelectValue placeholder="Select location" />
              </SelectTrigger>
              <SelectContent>
                {locations.map((location) => (
                  <SelectItem key={location.id} value={location.id.toString()}>
                    {location.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>
    </div>
  );

  const renderStep2 = () => (
    <div className="space-y-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-primary flex items-center justify-center">
          <Target className="h-8 w-8 text-white" />
        </div>
        <h3 className="text-xl font-semibold text-foreground">Program Details</h3>
        <p className="text-muted-foreground">Set the difficulty, duration, and pricing for your program</p>
      </div>

      <div className="space-y-4">
        <div>
          <Label className="text-sm font-medium text-foreground">Difficulty Level *</Label>
          <div className="grid grid-cols-3 gap-3 mt-2">
            {['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].map((level) => (
              <button
                key={level}
                type="button"
                onClick={() => handleInputChange('difficultyLevel', level)}
                className={`p-3 rounded-lg border-2 transition-all ${
                  formData.difficultyLevel === level
                    ? `${getDifficultyColor(level)} border-current`
                    : 'border-border hover:border-primary/50'
                }`}
              >
                <div className="text-sm font-medium capitalize">{level.toLowerCase()}</div>
                <div className="text-xs text-muted-foreground mt-1">
                  {level === 'BEGINNER' && 'Perfect for newcomers'}
                  {level === 'INTERMEDIATE' && 'Some experience needed'}
                  {level === 'ADVANCED' && 'For experienced practitioners'}
                </div>
              </button>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <Label htmlFor="duration" className="text-sm font-medium text-foreground">
              Duration (weeks) *
            </Label>
            <Input
              id="duration"
              type="number"
              min="1"
              max="52"
              value={formData.duration}
              onChange={(e) => {
                const value = parseInt(e.target.value);
                handleInputChange('duration', isNaN(value) ? 1 : value);
              }}
              className="mt-1"
              required
            />
            <p className="text-xs text-muted-foreground mt-1">Recommended: 4-12 weeks</p>
          </div>

          <div>
            <Label htmlFor="price" className="text-sm font-medium text-foreground">
              Price ($) *
            </Label>
            <Input
              id="price"
              type="number"
              min="0"
              step="0.01"
              value={formData.price}
              onChange={(e) => {
                const value = parseFloat(e.target.value);
                handleInputChange('price', isNaN(value) ? 0 : value);
              }}
              className="mt-1"
              required
            />
            <p className="text-xs text-muted-foreground mt-1">Set to 0 for free programs</p>
          </div>
        </div>

        <div>
          <Label htmlFor="youtubeUrl" className="text-sm font-medium text-foreground">
            YouTube URL (Optional)
          </Label>
          <Input
            id="youtubeUrl"
            value={formData.youtubeUrl}
            onChange={(e) => handleInputChange('youtubeUrl', e.target.value)}
            placeholder="https://www.youtube.com/watch?v=..."
            className="mt-1"
          />
          <p className="text-xs text-muted-foreground mt-1">Add a preview video for your program</p>
        </div>
      </div>
    </div>
  );

  const renderStep3 = () => (
    <div className="space-y-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-primary flex items-center justify-center">
          <CheckCircle className="h-8 w-8 text-white" />
        </div>
        <h3 className="text-xl font-semibold text-foreground">Review & Create</h3>
        <p className="text-muted-foreground">Review your program details before creating</p>
      </div>

      <div className="space-y-4">
        <Card variant="neumorphic" className="p-4">
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Program Name</span>
              <span className="font-semibold">{formData.name}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Category</span>
              <span>{categories.find(c => c.id === formData.categoryId)?.name}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Location</span>
              <span>{locations.find(l => l.id === formData.locationId)?.name}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Difficulty</span>
              <Badge className={getDifficultyColor(formData.difficultyLevel)}>
                {formData.difficultyLevel}
              </Badge>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Duration</span>
              <span>{formData.duration} weeks</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Price</span>
              <span className="font-semibold text-green-600">
                ${formData.price === 0 ? 'Free' : formData.price}
              </span>
            </div>
          </div>
        </Card>

        <div>
          <Label className="text-sm font-medium text-foreground">Description</Label>
          <div className="mt-2 p-3 rounded-lg bg-muted/50 text-sm">
            {formData.description}
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Progress Indicator */}
      <div className="flex items-center justify-center space-x-4 mb-6">
        {[1, 2, 3].map((step) => (
          <div key={step} className="flex items-center">
            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
              step <= currentStep 
                ? 'bg-gradient-primary text-white' 
                : 'bg-muted text-muted-foreground'
            }`}>
              {step}
            </div>
            {step < 3 && (
              <div className={`w-12 h-0.5 mx-2 ${
                step < currentStep ? 'bg-gradient-primary' : 'bg-muted'
              }`} />
            )}
          </div>
        ))}
      </div>

      {/* Step Content */}
      {currentStep === 1 && renderStep1()}
      {currentStep === 2 && renderStep2()}
      {currentStep === 3 && renderStep3()}

      {/* Navigation Buttons */}
      <div className="flex justify-between pt-6 border-t">
        <Button 
          type="button" 
          variant="outline" 
          onClick={currentStep === 1 ? onCancel : () => setCurrentStep(currentStep - 1)}
        >
          {currentStep === 1 ? 'Cancel' : 'Back'}
        </Button>
        
        <Button 
          type="submit" 
          disabled={isSubmitting}
          className="min-w-[120px]"
        >
          {isSubmitting ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
              Creating...
            </>
          ) : currentStep === 3 ? (
            'Create Program'
          ) : (
            'Next'
          )}
        </Button>
      </div>
    </form>
  );
};