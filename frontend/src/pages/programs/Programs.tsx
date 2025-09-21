import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Search, Filter, SortDesc, Clock, Star, DollarSign, Eye, CreditCard, Play } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Badge } from "@/components/ui/badge";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import PaymentModal from "@/components/ui/payment-modal";
import { toast } from "@/hooks/use-toast";
import { programsApi, userProgramsApi, type Program, type UserProgram } from "@/lib/api";

export const Programs = () => {
  const navigate = useNavigate();
  const [programs, setPrograms] = useState<Program[]>([]);
  const [filteredPrograms, setFilteredPrograms] = useState<Program[]>([]);
  const [enrolledPrograms, setEnrolledPrograms] = useState<UserProgram[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>("all");
  const [selectedSort, setSelectedSort] = useState<string>("name");
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
  const [selectedProgram, setSelectedProgram] = useState<Program | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch all programs
        const programsResponse = await programsApi.getAll();
        console.log('Programs API response:', programsResponse.data);
        const programsData = programsResponse.data?.content || [];
        console.log('Programs data:', programsData);
        console.log('Program difficulties:', programsData.map(p => ({ id: p.id, name: p.name, difficultyLevel: p.difficultyLevel, type: typeof p.difficultyLevel })));
        setPrograms(programsData);
        setFilteredPrograms(programsData);

        // Fetch user's enrolled programs
        try {
          const enrolledResponse = await userProgramsApi.getUserPrograms();
          console.log('Enrolled programs response:', enrolledResponse.data);
          const enrolledData = enrolledResponse.data?.content || [];
          console.log('Enrolled programs data:', enrolledData);
          console.log('Enrolled program IDs:', enrolledData.map(p => p.id));
          console.log('Enrolled program names:', enrolledData.map(p => p.name));
          setEnrolledPrograms(enrolledData);
        } catch (enrolledError) {
          console.log('No enrolled programs or error fetching:', enrolledError);
          console.log('Error details:', enrolledError.response?.data);
          setEnrolledPrograms([]);
        }
      } catch (error) {
        console.error('Error fetching programs:', error);
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to load programs",
        });
        // Set empty arrays on error
        setPrograms([]);
        setFilteredPrograms([]);
        setEnrolledPrograms([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    // Ensure programs is an array before filtering
    if (!Array.isArray(programs)) {
      setFilteredPrograms([]);
      return;
    }

    let filtered = programs.filter((program) => {
      const matchesSearch = program.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           program.description.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesDifficulty = selectedDifficulty === "all" || 
                               (program.difficultyLevel && 
                                (program.difficultyLevel.toLowerCase() === selectedDifficulty.toLowerCase() ||
                                 program.difficultyLevel === selectedDifficulty));
      
      return matchesSearch && matchesDifficulty;
    });

    // Sort programs
    filtered.sort((a, b) => {
      switch (selectedSort) {
        case "name":
          return a.name.localeCompare(b.name);
        case "price":
          return a.price - b.price;
        case "difficulty":
          const difficultyOrder = { "beginner": 1, "intermediate": 2, "advanced": 3 };
          const aDifficulty = (a.difficultyLevel || "").toLowerCase();
          const bDifficulty = (b.difficultyLevel || "").toLowerCase();
          return (difficultyOrder[aDifficulty as keyof typeof difficultyOrder] || 0) - 
                 (difficultyOrder[bDifficulty as keyof typeof difficultyOrder] || 0);
        default:
          return 0;
      }
    });

    setFilteredPrograms(filtered);
  }, [programs, searchTerm, selectedDifficulty, selectedSort]);

  const getDifficultyColor = (difficulty: string | undefined | null) => {
    if (!difficulty) {
      return "bg-muted/10 text-muted-foreground border-muted/20";
    }
    
    switch (difficulty.toLowerCase()) {
      case "beginner":
        return "bg-success/10 text-success border-success/20";
      case "intermediate":
        return "bg-warning/10 text-warning border-warning/20";
      case "advanced":
        return "bg-destructive/10 text-destructive border-destructive/20";
      default:
        console.log('Unknown difficulty level:', difficulty);
        return "bg-muted/10 text-muted-foreground border-muted/20";
    }
  };

  const formatDifficulty = (difficulty: string | undefined | null) => {
    if (!difficulty) {
      return "Unknown";
    }
    
    // If the difficulty is already properly formatted (from backend), return as is
    if (difficulty === "Beginner" || difficulty === "Intermediate" || difficulty === "Advanced") {
      return difficulty;
    }
    
    // Handle legacy formats or enum names
    switch (difficulty.toLowerCase()) {
      case "beginner":
        return "Beginner";
      case "intermediate":
        return "Intermediate";
      case "advanced":
        return "Advanced";
      default:
        console.log('Unknown difficulty level for formatting:', difficulty);
        return difficulty; // Return the original value if unknown
    }
  };

  // Helper function to check if a program is enrolled
  const isProgramEnrolled = (programId: number): boolean => {
    const isEnrolled = enrolledPrograms.some(enrolled => enrolled.id === programId);
    console.log(`Checking enrollment for program ${programId}:`, isEnrolled);
    console.log('Available enrolled program IDs:', enrolledPrograms.map(p => p.id));
    console.log('Available enrolled program names:', enrolledPrograms.map(p => p.name));
    if (isEnrolled) {
      const enrolledProgram = enrolledPrograms.find(enrolled => enrolled.id === programId);
      console.log('Found enrolled program:', enrolledProgram);
    }
    return isEnrolled;
  };

  const handleEnrollClick = (program: Program) => {
    setSelectedProgram(program);
    setIsPaymentModalOpen(true);
  };

  const handleContinueProgram = (program: Program) => {
    // Navigate to the program learning page
    navigate(`/dashboard/program/${program.id}/learn`);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading programs...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="text-center max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold mb-4">Fitness Programs</h1>
        <p className="text-muted-foreground">
          Discover the perfect program to achieve your fitness goals. From beginner-friendly routines to advanced challenges.
        </p>
      </div>

      {/* Search and Filters */}
      <Card variant="neumorphic">
        <CardContent className="p-6">
          <div className="flex flex-col lg:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search programs..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            
            <div className="flex flex-col sm:flex-row gap-4">
              <Select value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
                <SelectTrigger className="w-full sm:w-40">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Difficulty" />
                </SelectTrigger>
                <SelectContent className="bg-white">
                  <SelectItem value="all">All Levels</SelectItem>
                  <SelectItem value="Beginner">Beginner</SelectItem>
                  <SelectItem value="Intermediate">Intermediate</SelectItem>
                  <SelectItem value="Advanced">Advanced</SelectItem>
                </SelectContent>
              </Select>

              <Select value={selectedSort} onValueChange={setSelectedSort}>
                <SelectTrigger className="w-full sm:w-40">
                  <SortDesc className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent className="bg-white">
                  <SelectItem value="name">Name</SelectItem>
                  <SelectItem value="price">Price</SelectItem>
                  <SelectItem value="difficulty">Difficulty</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Programs Grid */}
      {filteredPrograms.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPrograms.map((program) => (
            <Card key={program.id} variant="interactive" className="group overflow-hidden">
              <div className="aspect-video bg-gradient-secondary overflow-hidden">
                {(program.images && program.images.length > 0) || program.imageUrl ? (
                  <img
                    src={program.images?.[0] || program.imageUrl}
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
                  <Badge className={getDifficultyColor(program.difficultyLevel)}>
                    {formatDifficulty(program.difficultyLevel)}
                  </Badge>
                  <div className="flex items-center gap-1 text-sm text-muted-foreground">
                    <DollarSign className="w-4 h-4" />
                    {program.price === 0 ? "Free" : `$${program.price}`}
                  </div>
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
                    {program.duration ? `${program.duration} min` : "Duration not specified"}
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <Button variant="outline" className="flex-1" asChild>
                    <Link to={`/programs/${program.id}`}>
                      <Eye className="w-4 h-4 mr-2" />
                      View Details
                    </Link>
                  </Button>
                  {isProgramEnrolled(program.id) ? (
                    <Button 
                      variant="hero" 
                      className="flex-1" 
                      onClick={() => handleContinueProgram(program)}
                    >
                      <Play className="w-4 h-4 mr-2" />
                      Continue Program
                    </Button>
                  ) : (
                    <Button 
                      variant="hero" 
                      className="flex-1" 
                      onClick={() => handleEnrollClick(program)}
                    >
                      <CreditCard className="w-4 h-4 mr-2" />
                      Enroll Now
                    </Button>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card variant="neumorphic" className="py-12">
          <CardContent className="text-center">
            <div className="w-16 h-16 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <Search className="w-8 h-8 text-primary" />
            </div>
            <h3 className="text-lg font-semibold mb-2">No programs found</h3>
            <p className="text-muted-foreground mb-4">
              Try adjusting your search terms or filters to find more programs.
            </p>
            <Button
              variant="outline"
              onClick={() => {
                setSearchTerm("");
                setSelectedDifficulty("all");
                setSelectedSort("name");
              }}
            >
              Clear Filters
            </Button>
          </CardContent>
        </Card>
      )}
      
      {/* Payment Modal */}
      {selectedProgram && (
        <PaymentModal
          isOpen={isPaymentModalOpen}
          onClose={() => {
            setIsPaymentModalOpen(false);
            setSelectedProgram(null);
          }}
          program={selectedProgram}
        />
      )}
    </div>
  );
};