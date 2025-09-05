import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { 
  ArrowLeft, 
  Star, 
  Clock, 
  DollarSign, 
  User, 
  MapPin,
  Play,
  Calendar,
  MessageCircle,
  Heart,
  Share2,
  Download
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { toast } from "@/hooks/use-toast";
import { programsApi, userProgramsApi, commentsApi, type Program } from "@/lib/api";

type ProgramComment = {
  id: number;
  userName: string;
  userAvatarUrl?: string;
  content: string;
  createdAt: string;
};

export const ProgramDetail = () => {
  const { id } = useParams<{ id: string }>();
  const [program, setProgram] = useState<Program | null>(null);
  const [comments, setComments] = useState<ProgramComment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [isEnrolling, setIsEnrolling] = useState(false);

  useEffect(() => {
    if (id) {
      fetchProgramData();
    }
  }, [id]);

  const fetchProgramData = async () => {
    try {
      // Fetch program first so UI can render even if comments fail
      const programResponse = await programsApi.getById(parseInt(id!));
      const p = programResponse.data;
      const firstImage = (Array.isArray(p.images) && p.images.length > 0 ? p.images[0]
                        : Array.isArray(p.programImages) && p.programImages.length > 0 ? (p.programImages[0].url || p.programImages[0])
                        : p.imageUrl);
      const difficultyNorm = (p.difficultyLevel || p.difficulty || 'BEGINNER').toString().toUpperCase();
      const mapped: Program = {
        id: p.id,
        name: p.name,
        description: p.description || '',
        difficulty: difficultyNorm === 'BEGINNER' ? 'Beginner' : difficultyNorm === 'INTERMEDIATE' ? 'Intermediate' : 'Advanced',
        duration: Number(p.duration || 0),
        price: Number(p.price ?? 0),
        imageUrl: firstImage,
        category: p.category?.name || p.category,
        instructorName: p.user?.firstName && p.user?.lastName ? `${p.user.firstName} ${p.user.lastName}` : p.instructorName,
        instructorAvatarUrl: p.user?.avatarUrl || p.instructorAvatarUrl,
        locationName: p.location?.name || p.locationName,
      };
      setProgram(mapped);

      // Try to fetch comments; ignore failures (e.g., 500)
      try {
        const commentsResponse = await commentsApi.getForProgram(parseInt(id!), 0, 10);
        setComments(commentsResponse.data || []);
      } catch (_err) {
        setComments([]);
      }

      // Optionally: enrollment status could be fetched here
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load program details",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleEnroll = async () => {
    if (!program) return;
    
    setIsEnrolling(true);
    try {
      await userProgramsApi.createUserProgram(program.id);
      setIsEnrolled(true);
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
    } finally {
      setIsEnrolling(false);
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

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading program details...</p>
        </div>
      </div>
    );
  }

  if (!program) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Program not found</p>
        <Button variant="outline" asChild className="mt-4">
          <Link to="/programs">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Programs
          </Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Back Button */}
      <Button variant="ghost" asChild>
        <Link to="/programs">
          <ArrowLeft className="w-4 h-4 mr-2" />
          Back to Programs
        </Link>
      </Button>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Program Header */}
          <Card variant="neumorphic">
            <div className="aspect-video bg-gradient-secondary overflow-hidden rounded-t-xl">
              {program.imageUrl ? (
                <img
                  src={program.imageUrl}
                  alt={program.name}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full bg-gradient-hero flex items-center justify-center">
                  <Star className="w-16 h-16 text-white/60" />
                </div>
              )}
            </div>
            <CardContent className="p-6">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <div className="flex items-center gap-2 mb-2">
                    <Badge className={getDifficultyColor(program.difficulty || 'Beginner')}>
                      {program.difficulty || 'Beginner'}
                    </Badge>
                    <Badge variant="secondary">
                      {program.category || 'Uncategorized'}
                    </Badge>
                  </div>
                  <h1 className="text-3xl font-bold mb-2">{program.name}</h1>
                  <p className="text-muted-foreground text-lg">
                    {program.description}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm">
                    <Heart className="w-4 h-4" />
                  </Button>
                  <Button variant="outline" size="sm">
                    <Share2 className="w-4 h-4" />
                  </Button>
                </div>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="text-center p-3 bg-muted/50 rounded-lg">
                  <Clock className="w-6 h-6 text-primary mx-auto mb-2" />
                  <p className="text-sm font-medium">Duration</p>
                  <p className="text-lg font-bold">{Number(program.duration || 0)} min</p>
                </div>
                <div className="text-center p-3 bg-muted/50 rounded-lg">
                  <DollarSign className="w-6 h-6 text-primary mx-auto mb-2" />
                  <p className="text-sm font-medium">Price</p>
                  <p className="text-lg font-bold">
                    {program.price === 0 ? "Free" : `$${program.price}`}
                  </p>
                </div>
                <div className="text-center p-3 bg-muted/50 rounded-lg">
                  <User className="w-6 h-6 text-primary mx-auto mb-2" />
                  <p className="text-sm font-medium">Instructor</p>
                  <p className="text-lg font-bold">{program.instructorName || 'Instructor'}</p>
                </div>
                <div className="text-center p-3 bg-muted/50 rounded-lg">
                  <MapPin className="w-6 h-6 text-primary mx-auto mb-2" />
                  <p className="text-sm font-medium">Location</p>
                  <p className="text-lg font-bold">{program.locationName}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Program Details */}
          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle>About This Program</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="prose max-w-none">
                <p className="text-muted-foreground leading-relaxed">
                  {program.description}
                </p>
                
                <Separator className="my-6" />
                
                <h3 className="text-lg font-semibold mb-3">What You'll Learn</h3>
                <ul className="space-y-2 text-muted-foreground">
                  <li className="flex items-center gap-2">
                    <div className="w-2 h-2 bg-primary rounded-full"></div>
                    Proper form and technique for {(program.category || 'program').toLowerCase()} exercises
                  </li>
                  <li className="flex items-center gap-2">
                    <div className="w-2 h-2 bg-primary rounded-full"></div>
                    How to progress safely and effectively
                  </li>
                  <li className="flex items-center gap-2">
                    <div className="w-2 h-2 bg-primary rounded-full"></div>
                    Tips for maintaining motivation and consistency
                  </li>
                  <li className="flex items-center gap-2">
                    <div className="w-2 h-2 bg-primary rounded-full"></div>
                    Nutrition and recovery strategies
                  </li>
                </ul>
              </div>
            </CardContent>
          </Card>

          {/* Comments Section */}
          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MessageCircle className="w-5 h-5" />
                Reviews ({comments.length})
              </CardTitle>
            </CardHeader>
            <CardContent>
              {comments.length > 0 ? (
                <div className="space-y-4">
                  {comments.map((comment) => (
                    <div key={comment.id} className="flex gap-3 p-4 bg-muted/50 rounded-lg">
                      <Avatar className="w-10 h-10">
                        <AvatarImage src={comment.userAvatarUrl} alt={comment.userName} />
                        <AvatarFallback className="bg-gradient-primary text-white text-sm">
                          {comment.userName.split(' ').map(n => n[0]).join('')}
                        </AvatarFallback>
                      </Avatar>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <h4 className="font-medium text-sm">{comment.userName}</h4>
                          <span className="text-xs text-muted-foreground">
                            {new Date(comment.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="text-sm text-muted-foreground">{comment.content}</p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8">
                  <MessageCircle className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                  <p className="text-muted-foreground">No reviews yet. Be the first to review this program!</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Instructor Info */}
          <Card variant="fitness">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="w-5 h-5" />
                Instructor
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-3 mb-4">
                <Avatar className="w-12 h-12">
                  <AvatarImage src={program.instructorAvatarUrl} alt={program.instructorName} />
                  <AvatarFallback className="bg-gradient-primary text-white">
                    {(program.instructorName || 'I N').split(' ').map(n => n[0]).join('')}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <h3 className="font-semibold">{program.instructorName}</h3>
                  <p className="text-sm text-muted-foreground">Certified Instructor</p>
                </div>
              </div>
              <p className="text-sm text-muted-foreground">
                Experienced fitness professional with years of training and expertise in {(program.category || 'program').toLowerCase()}.
              </p>
            </CardContent>
          </Card>

          {/* Enrollment Card */}
          <Card variant="interactive">
            <CardContent className="p-6">
              <div className="text-center mb-6">
                <h3 className="text-2xl font-bold mb-2">
                  {Number(program.price || 0) === 0 ? "Free" : `$${Number(program.price || 0)}`}
                </h3>
                <p className="text-muted-foreground">
                  {program.price === 0 ? "No cost to enroll" : "One-time payment"}
                </p>
              </div>

              {isEnrolled ? (
                <div className="space-y-3">
                  <Button variant="hero" className="w-full" asChild>
                    <Link to="/my-programs">
                      <Play className="w-4 h-4 mr-2" />
                      Continue Program
                    </Link>
                  </Button>
                  <Button variant="outline" className="w-full">
                    <Download className="w-4 h-4 mr-2" />
                    Download Materials
                  </Button>
                </div>
              ) : (
                <div className="space-y-3">
                  <Button 
                    variant="hero" 
                    className="w-full"
                    onClick={handleEnroll}
                    disabled={isEnrolling}
                  >
                    <Calendar className="w-4 h-4 mr-2" />
                    {isEnrolling ? "Enrolling..." : "Enroll Now"}
                  </Button>
                  <p className="text-xs text-muted-foreground text-center">
                    Start your fitness journey today!
                  </p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Program Stats */}
          <Card variant="neumorphic">
            <CardHeader>
              <CardTitle>Program Stats</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">Difficulty</span>
                  <Badge className={getDifficultyColor(program.difficulty || 'Beginner')}>
                    {program.difficulty || 'Beginner'}
                  </Badge>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">Duration</span>
                  <span className="text-sm font-medium">{Number(program.duration || 0)} minutes</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">Category</span>
                  <span className="text-sm font-medium">{program.category || 'Uncategorized'}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-muted-foreground">Location</span>
                  <span className="text-sm font-medium">{program.locationName || '—'}</span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};
