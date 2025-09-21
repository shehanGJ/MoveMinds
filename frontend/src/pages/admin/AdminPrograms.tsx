import { useState, useEffect } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { adminApi, AdminProgramResponse } from "@/lib/api";
import { 
  Search, 
  Filter, 
  Plus, 
  MoreHorizontal, 
  Play, 
  Pause, 
  Trash2, 
  Eye,
  Users,
  MessageCircle,
  Star,
  Calendar,
  DollarSign,
  Dumbbell
} from "lucide-react";

export const AdminPrograms = () => {
  const [programs, setPrograms] = useState<AdminProgramResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string>("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("");
  const [selectedStatus, setSelectedStatus] = useState<string>("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const { toast } = useToast();

  // Fetch programs data
  const fetchPrograms = async () => {
    try {
      setLoading(true);
      const params = {
        page: currentPage,
        size: 10,
        sortBy: "createdAt",
        sortDir: "desc"
      };

      // Add filters if selected
      if (searchTerm && searchTerm.trim() !== '') {
        params.search = searchTerm;
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
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Failed to fetch programs:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load programs"
      });
    } finally {
      setLoading(false);
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
    } catch (error) {
      console.error('Failed to update program status:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update program status"
      });
    }
  };

  // Handle program deletion
  const handleProgramDelete = async (programId: number) => {
    if (!window.confirm('Are you sure you want to delete this program?')) {
      return;
    }

    try {
      await adminApi.deleteProgram(programId);
      toast({
        title: "Success",
        description: "Program deleted successfully"
      });
      
      // Remove from local state
      setPrograms(prevPrograms => prevPrograms.filter(p => p.id !== programId));
    } catch (error) {
      console.error('Failed to delete program:', error);
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete program"
      });
    }
  };

  // Clear all filters
  const clearFilters = () => {
    setSearchTerm("");
    setSelectedCategory("");
    setSelectedDifficulty("");
    setSelectedStatus("");
    setCurrentPage(0);
  };

  // Get active filters count
  const getActiveFiltersCount = () => {
    let count = 0;
    if (searchTerm) count++;
    if (selectedCategory) count++;
    if (selectedDifficulty) count++;
    if (selectedStatus) count++;
    return count;
  };

  // Helper function to get program status badge
  const getProgramStatusBadge = (program: any) => {
    // Debug logging
    console.log('getProgramStatusBadge called with program:', program);
    console.log('program.isActive:', program.isActive);
    console.log('program.status:', program.status);
    
    // Use isActive field if available, otherwise fall back to status field
    const isActive = program.isActive !== undefined ? program.isActive : (program.status === 'ACTIVE');
    
    console.log('Final isActive value:', isActive);
    
    return (
      <Badge 
        className={`ml-2 px-3 py-1 text-xs font-semibold shadow-sm ${
          isActive 
            ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300 border-green-200 dark:border-green-700' 
            : 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-300 border-orange-200 dark:border-orange-700'
        }`}
      >
        {isActive ? 'Active' : 'Pending'}
      </Badge>
    );
  };

  // Helper function to get program activation status
  const getProgramActivationStatus = (program: any) => {
    // Use isActive field if available, otherwise fall back to status field
    return program.isActive !== undefined ? program.isActive : (program.status === 'ACTIVE');
  };

  // Load data on component mount and when filters change
  useEffect(() => {
    fetchPrograms();
  }, [currentPage, searchTerm, selectedCategory, selectedDifficulty, selectedStatus]);

  // Reset to first page when filters change
  useEffect(() => {
    setCurrentPage(0);
  }, [searchTerm, selectedCategory, selectedDifficulty, selectedStatus]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Program Management</h1>
          <p className="text-muted-foreground">
            Manage and monitor all fitness programs in the system
          </p>
        </div>
        <Button className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Add Program
        </Button>
      </div>

      {/* Filters */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Filter className="h-5 w-5" />
            Filters
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
              <Input
                placeholder="Search programs..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>

            {/* Category Filter */}
            <Select value={selectedCategory || "all"} onValueChange={(value) => setSelectedCategory(value === "all" ? "" : value)}>
              <SelectTrigger className="w-full">
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

            {/* Difficulty Filter */}
            <Select value={selectedDifficulty || "all"} onValueChange={(value) => setSelectedDifficulty(value === "all" ? "" : value)}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="All Difficulties" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Difficulties</SelectItem>
                <SelectItem value="BEGINNER">Beginner</SelectItem>
                <SelectItem value="INTERMEDIATE">Intermediate</SelectItem>
                <SelectItem value="ADVANCED">Advanced</SelectItem>
              </SelectContent>
            </Select>

            {/* Status Filter */}
            <Select value={selectedStatus || "all"} onValueChange={(value) => setSelectedStatus(value === "all" ? "" : value)}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="All Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Status</SelectItem>
                <SelectItem value="active">Active</SelectItem>
                <SelectItem value="inactive">Pending</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Active Filters Summary */}
          {getActiveFiltersCount() > 0 && (
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-sm text-muted-foreground">Active filters:</span>
              {searchTerm && (
                <Badge variant="secondary" className="gap-1">
                  Search: {searchTerm}
                  <button onClick={() => setSearchTerm("")} className="ml-1 hover:text-destructive">×</button>
                </Badge>
              )}
              {selectedCategory && (
                <Badge variant="secondary" className="gap-1">
                  Category: {selectedCategory}
                  <button onClick={() => setSelectedCategory("")} className="ml-1 hover:text-destructive">×</button>
                </Badge>
              )}
              {selectedDifficulty && (
                <Badge variant="secondary" className="gap-1">
                  Difficulty: {selectedDifficulty}
                  <button onClick={() => setSelectedDifficulty("")} className="ml-1 hover:text-destructive">×</button>
                </Badge>
              )}
              {selectedStatus && (
                <Badge variant="secondary" className="gap-1">
                  Status: {selectedStatus}
                  <button onClick={() => setSelectedStatus("")} className="ml-1 hover:text-destructive">×</button>
                </Badge>
              )}
              <Button variant="outline" size="sm" onClick={clearFilters}>
                Clear All
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Results Summary */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          Showing {programs.length} of {totalElements} programs
        </p>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
          >
            Previous
          </Button>
          <span className="text-sm">
            Page {currentPage + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage >= totalPages - 1}
          >
            Next
          </Button>
        </div>
      </div>

      {/* Programs Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(6)].map((_, i) => (
            <Card key={i} variant="neumorphic" className="animate-pulse overflow-hidden">
              {/* Status bar skeleton */}
              <div className="h-1 bg-muted/30"></div>
              
              <CardContent className="p-0">
                {/* Header skeleton */}
                <div className="p-6 bg-gradient-to-br from-card to-card/80">
                  <div className="flex items-start gap-4">
                    <div className="w-16 h-16 bg-muted/50 rounded-xl"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-5 bg-muted/50 rounded w-3/4"></div>
                      <div className="h-3 bg-muted/30 rounded w-full"></div>
                      <div className="h-3 bg-muted/30 rounded w-2/3"></div>
                      <div className="flex gap-4 mt-3">
                        <div className="h-3 bg-muted/30 rounded w-12"></div>
                        <div className="h-3 bg-muted/30 rounded w-12"></div>
                        <div className="h-3 bg-muted/30 rounded w-12"></div>
                      </div>
                    </div>
                    <div className="w-16 h-6 bg-muted/50 rounded-full"></div>
                  </div>
                </div>

                {/* Content skeleton */}
                <div className="p-6 space-y-4">
                  <div className="flex gap-2">
                    <div className="h-6 bg-muted/30 rounded-full w-20"></div>
                    <div className="h-6 bg-muted/30 rounded-full w-16"></div>
                  </div>
                  
                  <div className="flex items-center gap-3 p-3 bg-muted/20 rounded-lg">
                    <div className="w-8 h-8 bg-muted/50 rounded-full"></div>
                    <div className="space-y-1">
                      <div className="h-3 bg-muted/30 rounded w-24"></div>
                      <div className="h-2 bg-muted/20 rounded w-16"></div>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <div className="w-4 h-4 bg-muted/30 rounded"></div>
                      <div className="space-y-1">
                        <div className="h-2 bg-muted/20 rounded w-12"></div>
                        <div className="h-3 bg-muted/30 rounded w-8"></div>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <div className="w-4 h-4 bg-muted/30 rounded"></div>
                      <div className="space-y-1">
                        <div className="h-2 bg-muted/20 rounded w-12"></div>
                        <div className="h-3 bg-muted/30 rounded w-8"></div>
                      </div>
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
      ) : programs.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <Dumbbell className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">No programs found</h3>
            <p className="text-muted-foreground text-center mb-4">
              {getActiveFiltersCount() > 0 
                ? "Try adjusting your filters to see more results."
                : "No programs have been created yet."
              }
            </p>
            {getActiveFiltersCount() > 0 && (
              <Button variant="outline" onClick={clearFilters}>
                Clear Filters
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {programs.map((program) => (
            <Card 
              key={program.id} 
              variant="neumorphic" 
              className="group hover:shadow-glow transition-all duration-300 hover:-translate-y-1 overflow-hidden relative"
            >
              {/* Status Indicator Bar */}
              <div className={`absolute top-0 left-0 right-0 h-1 ${
                program.isActive 
                  ? 'bg-gradient-to-r from-green-400 to-green-600' 
                  : 'bg-gradient-to-r from-orange-400 to-orange-600'
              }`} />
              
              <CardContent className="p-0">
                {/* Header Section with Gradient Background */}
                <div className="relative p-6 bg-gradient-to-br from-card to-card/80">
                  <div className="flex items-start gap-4">
                    {/* Program Icon with Gradient */}
                    <div className="w-16 h-16 bg-gradient-primary rounded-xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                      <Dumbbell className="h-8 w-8 text-white" />
                    </div>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex-1 min-w-0">
                          <CardTitle className="text-lg font-bold line-clamp-2 group-hover:text-primary transition-colors">
                            {program.name}
                          </CardTitle>
                          <CardDescription className="line-clamp-2 mt-1 text-sm">
                            {program.description}
                          </CardDescription>
                        </div>
                        
                        {/* Status Badge */}
                        {getProgramStatusBadge(program)}
                      </div>
                      
                      {/* Quick Stats */}
                      <div className="flex items-center gap-4 text-xs text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <Users className="h-3 w-3" />
                          <span className="font-medium">{program.enrollmentCount}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <Star className="h-3 w-3" />
                          <span className="font-medium">{program.averageRating.toFixed(1)}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <DollarSign className="h-3 w-3" />
                          <span className="font-medium">${program.price}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Content Section */}
                <div className="p-6 space-y-4">
                  {/* Category & Difficulty Tags */}
                  <div className="flex items-center gap-2 flex-wrap">
                    <Badge 
                      variant="outline" 
                      className="bg-primary/10 text-primary border-primary/20 hover:bg-primary/20 transition-colors"
                    >
                      {program.categoryName}
                    </Badge>
                    <Badge 
                      variant="outline" 
                      className={`${
                        program.difficultyLevel === 'BEGINNER' 
                          ? 'bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-900/20 dark:text-blue-300 dark:border-blue-700'
                          : program.difficultyLevel === 'INTERMEDIATE'
                          ? 'bg-yellow-50 text-yellow-700 border-yellow-200 dark:bg-yellow-900/20 dark:text-yellow-300 dark:border-yellow-700'
                          : 'bg-red-50 text-red-700 border-red-200 dark:bg-red-900/20 dark:text-red-300 dark:border-red-700'
                      }`}
                    >
                      {program.difficultyLevel}
                    </Badge>
                  </div>

                  {/* Instructor Info */}
                  <div className="flex items-center gap-3 p-3 bg-muted/30 rounded-lg">
                    <div className="w-8 h-8 bg-gradient-secondary rounded-full flex items-center justify-center">
                      <span className="text-xs font-bold text-foreground">
                        {program.instructorName.split(' ').map(n => n[0]).join('').toUpperCase()}
                      </span>
                    </div>
                    <div>
                      <p className="text-sm font-medium">{program.instructorName}</p>
                      <p className="text-xs text-muted-foreground">Instructor</p>
                    </div>
                  </div>

                  {/* Detailed Stats */}
                  <div className="grid grid-cols-2 gap-3">
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <MessageCircle className="h-4 w-4 text-muted-foreground" />
                      <div>
                        <p className="text-xs text-muted-foreground">Comments</p>
                        <p className="text-sm font-semibold">{program.commentCount}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 p-2 bg-muted/20 rounded-lg">
                      <Calendar className="h-4 w-4 text-muted-foreground" />
                      <div>
                        <p className="text-xs text-muted-foreground">Duration</p>
                        <p className="text-sm font-semibold">{program.duration} min</p>
                      </div>
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex items-center gap-2 pt-2 border-t border-border/50">
                    <Button
                      variant={getProgramActivationStatus(program) ? "outline" : "default"}
                      size="sm"
                      className={`flex-1 transition-all duration-200 ${
                        getProgramActivationStatus(program) 
                          ? 'hover:bg-destructive hover:text-destructive-foreground hover:border-destructive' 
                          : 'bg-gradient-primary hover:shadow-lg hover:scale-105'
                      }`}
                      onClick={() => handleProgramStatusUpdate(program.id, !getProgramActivationStatus(program))}
                    >
                      {getProgramActivationStatus(program) ? (
                        <>
                          <Pause className="h-4 w-4 mr-2" />
                          Deactivate
                        </>
                      ) : (
                        <>
                          <Play className="h-4 w-4 mr-2" />
                          Activate
                        </>
                      )}
                    </Button>
                    
                    <Button 
                      variant="outline" 
                      size="sm"
                      className="hover:bg-primary/10 hover:text-primary hover:border-primary/30 transition-all duration-200"
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleProgramDelete(program.id)}
                      className="hover:bg-destructive/10 hover:text-destructive hover:border-destructive/30 transition-all duration-200"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
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
