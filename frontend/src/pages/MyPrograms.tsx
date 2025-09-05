import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { 
  Calendar, 
  Clock, 
  Star, 
  Eye, 
  Trash2,
  Plus,
  Filter,
  Search,
  TrendingUp,
  Award,
  Play
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { toast } from "@/hooks/use-toast";
import { userProgramsApi, programsApi, type Program, type UserProgram } from "@/lib/api";

export const MyPrograms = () => {
  const [userPrograms, setUserPrograms] = useState<UserProgram[]>([]);
  const [allPrograms, setAllPrograms] = useState<Program[]>([]);
  const [filteredPrograms, setFilteredPrograms] = useState<Program[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("all");
  const [selectedCategory, setSelectedCategory] = useState<string>("all");

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    filterPrograms();
  }, [allPrograms, searchTerm, selectedDifficulty, selectedCategory]);

  const fetchData = async () => {
    try {
      const [userProgramsResponse, allProgramsResponse] = await Promise.all([
        userProgramsApi.getUserPrograms(),
        programsApi.getAll(),
      ]);
      
      const rawUserPrograms = userProgramsResponse.data?.content || [];
      const normalizedUserPrograms: UserProgram[] = rawUserPrograms.map((u: any) => ({
        // Backend returns FitnessProgramListResponse; program id is u.id, purchase id is u.purchaseId
        id: u.purchaseId ?? u.id, // user-program id (used for delete)
        programId: u.id ?? u.programId ?? u.fitnessProgramId ?? u.fitnessProgram?.id,
        status: (u.status ?? (u.active ? 'ACTIVE' : 'INACTIVE') ?? 'ACTIVE') as string,
        startDate: u.startDate ?? u.start_date ?? u.start ?? u.createdAt ?? new Date().toISOString(),
        endDate: u.endDate ?? u.end_date ?? u.end ?? new Date(Date.now() + 30*24*3600*1000).toISOString(),
      })).filter((u: UserProgram) => !!u.programId);
      setUserPrograms(normalizedUserPrograms);
      const page = allProgramsResponse.data;
      const items: Program[] = (page.content || []).map((p: any) => ({
        id: p.id,
        name: p.name,
        description: p.description || p.desc || '',
        difficulty: (() => {
          const d = (p.difficultyLevel || p.difficulty || 'BEGINNER').toString().toUpperCase();
          return d === 'BEGINNER' ? 'Beginner' : d === 'INTERMEDIATE' ? 'Intermediate' : 'Advanced';
        })(),
        duration: Number(p.duration || 0),
        price: Number(p.price ?? 0),
        imageUrl: Array.isArray(p.images) && p.images.length > 0 ? p.images[0]
                 : Array.isArray(p.programImages) && p.programImages.length > 0 ? (p.programImages[0].url || p.programImages[0])
                 : p.imageUrl,
      }));
      setAllPrograms(items);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load programs",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const filterPrograms = () => {
    const list = Array.isArray(allPrograms) ? allPrograms : [];
    let filtered = list.filter((program) => {
      const matchesSearch = program.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           program.description.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesDifficulty = selectedDifficulty === "all" || program.difficulty === selectedDifficulty;
      const matchesCategory = selectedCategory === "all" || program.category === selectedCategory;
      
      return matchesSearch && matchesDifficulty && matchesCategory;
    });

    // Sort by name
    filtered.sort((a, b) => a.name.localeCompare(b.name));
    
    setFilteredPrograms(filtered);
  };

  const enrollInProgram = async (programId: number) => {
    try {
      await userProgramsApi.createUserProgram(programId);
      fetchData();
      setIsDialogOpen(false);
      
      toast({
        title: "Success",
        description: "Successfully enrolled in program",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to enroll in program",
      });
    }
  };

  const unenrollFromProgram = async (programId: number) => {
    try {
      await userProgramsApi.deleteUserProgram(programId);
      fetchData();
      
      toast({
        title: "Success",
        description: "Successfully unenrolled from program",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to unenroll from program",
      });
    }
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty.toLowerCase()) {
      case "beginner":
        return "bg-success/10 text-success border-success/20";
      case "intermediate":
        return "bg-warning/10 text-warning border-warning/20";
      case "advanced":
        return "bg-destructive/10 text-destructive border-destructive/20";
      default:
        return "bg-muted/10 text-muted-foreground border-muted/20";
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "active":
        return "bg-success/10 text-success border-success/20";
      case "inactive":
        return "bg-muted/10 text-muted-foreground border-muted/20";
      default:
        return "bg-muted/10 text-muted-foreground border-muted/20";
    }
  };

  const getEnrolledProgramIds = () => {
    return userPrograms.map(up => up.programId);
  };

  const getAvailablePrograms = () => {
    const enrolledIds = getEnrolledProgramIds();
    return filteredPrograms.filter(program => !enrolledIds.includes(program.id));
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading your programs...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold mb-2">My Programs</h1>
          <p className="text-muted-foreground">
            Manage your enrolled fitness programs and discover new ones
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button variant="hero">
              <Plus className="w-4 h-4 mr-2" />
              Enroll in Program
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-2xl max-h-[600px] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>Available Programs</DialogTitle>
              <DialogDescription>
                Choose a program to enroll in
              </DialogDescription>
            </DialogHeader>
            
            {/* Search and Filters */}
            <div className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Search programs..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="flex-1"
                />
                <Select value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
                  <SelectTrigger className="w-32">
                    <SelectValue placeholder="Difficulty" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Levels</SelectItem>
                    <SelectItem value="Beginner">Beginner</SelectItem>
                    <SelectItem value="Intermediate">Intermediate</SelectItem>
                    <SelectItem value="Advanced">Advanced</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Available Programs */}
              <div className="space-y-3 max-h-[400px] overflow-y-auto">
                {getAvailablePrograms().length > 0 ? (
                  getAvailablePrograms().map((program) => (
                    <Card key={program.id} variant="interactive">
                      <CardContent className="p-4">
                        <div className="flex items-center justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                              <h3 className="font-semibold">{program.name}</h3>
                              <Badge className={getDifficultyColor(program.difficulty)}>
                                {program.difficulty}
                              </Badge>
                            </div>
                            <p className="text-sm text-muted-foreground mb-2 line-clamp-2">
                              {program.description}
                            </p>
                            <div className="flex items-center gap-4 text-xs text-muted-foreground">
                              <div className="flex items-center gap-1">
                                <Clock className="w-3 h-3" />
                                {program.duration} min
                              </div>
                              <div className="flex items-center gap-1">
                                <Star className="w-3 h-3" />
                                ${program.price}
                              </div>
                            </div>
                          </div>
                          <Button
                            size="sm"
                            onClick={() => enrollInProgram(program.id)}
                          >
                            Enroll
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))
                ) : (
                  <div className="text-center py-8">
                    <Award className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                    <p className="text-muted-foreground">
                      {getAvailablePrograms().length === 0 && filteredPrograms.length > 0
                        ? "You're enrolled in all available programs!"
                        : "No programs match your search criteria."
                      }
                    </p>
                  </div>
                )}
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Enrolled Programs</p>
                <p className="text-2xl font-bold">{userPrograms.length}</p>
              </div>
              <Calendar className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Active Programs</p>
                <p className="text-2xl font-bold">
                  {userPrograms.filter(up => up.status === 'ACTIVE').length}
                </p>
              </div>
              <Play className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Total Progress</p>
                <p className="text-2xl font-bold">
                  {userPrograms.length > 0 
                    ? Math.round((userPrograms.filter(up => up.status === 'ACTIVE').length / userPrograms.length) * 100)
                    : 0}%
                </p>
              </div>
              <TrendingUp className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Enrolled Programs */}
      <div>
        <h2 className="text-xl font-semibold mb-4">Your Enrolled Programs</h2>
        {userPrograms.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {userPrograms.map((userProgram) => {
              const program = allPrograms.find(p => p.id === userProgram.programId);
              if (!program) return null;

              return (
                <Card key={userProgram.id} variant="interactive" className="group overflow-hidden">
                  <div className="aspect-video bg-gradient-secondary overflow-hidden">
                    {program.imageUrl ? (
                      <img
                        src={program.imageUrl}
                        alt={program.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                      />
                    ) : (
                      <div className="w-full h-full bg-gradient-hero flex items-center justify-center">
                        <Star className="w-12 h-12 text-white/60" />
                      </div>
                    )}
                  </div>
                  
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between mb-2">
                      <Badge className={getDifficultyColor(program.difficulty)}>
                        {program.difficulty}
                      </Badge>
                      <Badge className={getStatusColor(userProgram.status)}>
                        {userProgram.status}
                      </Badge>
                    </div>
                    <CardTitle className="text-lg group-hover:text-primary transition-colors">
                      {program.name}
                    </CardTitle>
                  </CardHeader>
                  
                  <CardContent className="pt-0">
                    <CardDescription className="line-clamp-3 mb-4">
                      {program.description}
                    </CardDescription>
                    
                    <div className="flex items-center gap-4 text-sm text-muted-foreground mb-4">
                      <div className="flex items-center gap-1">
                        <Clock className="w-4 h-4" />
                        {program.duration} min
                      </div>
                      <div className="flex items-center gap-1">
                        <Calendar className="w-4 h-4" />
                        Started {new Date(userProgram.startDate).toLocaleDateString()}
                      </div>
                    </div>
                    
                    <div className="flex gap-2">
                      <Button variant="hero" size="sm" className="flex-1" asChild>
                        <Link to={`/programs/${program.id}`}>
                          <Eye className="w-4 h-4 mr-2" />
                          View Details
                        </Link>
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => unenrollFromProgram(program.id)}
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        ) : (
          <Card variant="neumorphic" className="py-12">
            <CardContent className="text-center">
              <div className="w-16 h-16 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                <Calendar className="w-8 h-8 text-primary" />
              </div>
              <h3 className="text-lg font-semibold mb-2">No programs enrolled</h3>
              <p className="text-muted-foreground mb-4">
                You haven't enrolled in any programs yet. Browse available programs and start your fitness journey!
              </p>
              <Button variant="hero" onClick={() => setIsDialogOpen(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Browse Programs
              </Button>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
};
