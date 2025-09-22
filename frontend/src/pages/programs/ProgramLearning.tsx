import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "@/hooks/use-toast";
import { programContentApi, progressApi, type ProgramLearningContent, type ProgramLesson, type ProgramModule, type ProgramLearningProgressResponse, type LessonProgressResponse } from "@/lib/api";
import {
  Play,
  Clock,
  CheckCircle,
  Circle,
  BookOpen,
  MessageSquare,
  Download,
  Star,
  ArrowLeft,
  FileText,
  Users,
  SkipBack,
  SkipForward
} from "lucide-react";

// Helper function to convert YouTube URL to embed URL
const getYouTubeEmbedUrl = (url: string, autoplay: boolean = false): string => {
  if (!url) return '';
  
  // Handle different YouTube URL formats
  const patterns = [
    /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([^&\n?#]+)/,
    /youtube\.com\/watch\?.*v=([^&\n?#]+)/
  ];
  
  let videoId = '';
  for (const pattern of patterns) {
    const match = url.match(pattern);
    if (match && match[1]) {
      videoId = match[1];
      break;
    }
  }
  
  if (!videoId) {
    // If it's already an embed URL, extract video ID from it
    const embedMatch = url.match(/youtube\.com\/embed\/([^&\n?#]+)/);
    if (embedMatch && embedMatch[1]) {
      videoId = embedMatch[1];
    } else {
      return url; // Return original URL if no pattern matches
    }
  }
  
  // Build embed URL with proper parameters
  const baseUrl = `https://www.youtube.com/embed/${videoId}`;
  const params = new URLSearchParams({
    'rel': '0', // Don't show related videos
    'modestbranding': '1', // Minimal YouTube branding
    'showinfo': '0', // Don't show video info
    'iv_load_policy': '3', // Hide annotations
    'fs': '1', // Allow fullscreen
    'cc_load_policy': '0', // Don't show captions by default
    'playsinline': '1', // Play inline on mobile
  });
  
  if (autoplay) {
    params.set('autoplay', '1');
    params.set('mute', '1'); // Mute is required for autoplay to work
  }
  
  return `${baseUrl}?${params.toString()}`;
};

// Helper function to extract YouTube video ID for thumbnail
const getYouTubeVideoId = (url: string): string => {
  if (!url) return '';
  
  const patterns = [
    /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([^&\n?#]+)/,
    /youtube\.com\/watch\?.*v=([^&\n?#]+)/
  ];
  
  for (const pattern of patterns) {
    const match = url.match(pattern);
    if (match && match[1]) {
      return match[1];
    }
  }
  
  return '';
};


// Types
interface LessonWithModule extends ProgramLesson {
  moduleTitle: string;
  completed?: boolean;
  progress?: LessonProgressResponse;
}

export const ProgramLearning = () => {
  const { programId } = useParams();
  const navigate = useNavigate();
  const [program, setProgram] = useState<ProgramLearningContent | null>(null);
  const [currentLesson, setCurrentLesson] = useState<LessonWithModule | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [notes, setNotes] = useState("");
  const [activeTab, setActiveTab] = useState("overview");
  const [isLoading, setIsLoading] = useState(true);
  const [videoError, setVideoError] = useState<string | null>(null);
  const [progressData, setProgressData] = useState<ProgramLearningProgressResponse | null>(null);

  useEffect(() => {
    if (programId) {
      fetchProgramData();
    }
  }, [programId]);

  const fetchProgramData = async () => {
    try {
      setIsLoading(true);
      
      // Fetch program content and progress data in parallel
      const [programResponse, progressResponse] = await Promise.all([
        programContentApi.getProgramLearningContent(parseInt(programId!)),
        progressApi.getProgramProgress(parseInt(programId!))
      ]);
      
      setProgram(programResponse.data);
      setProgressData(progressResponse.data);
      
      // Set the first lesson as current if available
      if (programResponse.data.modules.length > 0 && programResponse.data.modules[0].lessons.length > 0) {
        const firstLesson = programResponse.data.modules[0].lessons[0];
        
        // Find progress data for this lesson
        const lessonProgress = progressResponse.data.lessonProgress.find(
          p => p.lessonId === firstLesson.id
        );
        
        setCurrentLesson({
          ...firstLesson,
          moduleTitle: programResponse.data.modules[0].title,
          completed: lessonProgress?.isCompleted || false,
          progress: lessonProgress
        });
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load program content"
      });
    } finally {
      setIsLoading(false);
    }
  };

  const getCurrentLesson = (): LessonWithModule | null => {
    if (!program || !currentLesson) return null;
    
    for (const module of program.modules) {
      const lesson = module.lessons.find(l => l.id === currentLesson.id);
      if (lesson) return { ...lesson, moduleTitle: module.title, completed: currentLesson.completed };
    }
    return currentLesson;
  };

  const getNextLesson = () => {
    if (!program || !currentLesson) return null;
    
    const allLessons = program.modules.flatMap(m => m.lessons);
    const currentIndex = allLessons.findIndex(l => l.id === currentLesson.id);
    return currentIndex < allLessons.length - 1 ? allLessons[currentIndex + 1] : null;
  };

  const getPreviousLesson = () => {
    if (!program || !currentLesson) return null;
    
    const allLessons = program.modules.flatMap(m => m.lessons);
    const currentIndex = allLessons.findIndex(l => l.id === currentLesson.id);
    return currentIndex > 0 ? allLessons[currentIndex - 1] : null;
  };

  const toggleLessonCompletion = async () => {
    if (!currentLesson) return;
    
    try {
      if (currentLesson.completed) {
        // Mark as incomplete via API
        await progressApi.markLessonIncomplete(currentLesson.id);
        
        // Update local state
        setCurrentLesson(prev => prev ? { ...prev, completed: false } : null);
        
        // Update progress data
        if (progressData) {
          const updatedProgress = { ...progressData };
          updatedProgress.completedLessons = Math.max(0, updatedProgress.completedLessons - 1);
          updatedProgress.progressPercentage = (updatedProgress.completedLessons / updatedProgress.totalLessons) * 100;
          updatedProgress.lessonProgress = updatedProgress.lessonProgress.map(lesson => 
            lesson.lessonId === currentLesson.id 
              ? { ...lesson, isCompleted: false }
              : lesson
          );
          setProgressData(updatedProgress);
        }
        
        toast({
          title: "Lesson marked as incomplete",
          description: "You can now mark it as complete again.",
        });
      } else {
        // Mark as complete
        await progressApi.markLessonComplete(currentLesson.id, currentLesson.progress?.watchTimeSeconds || 0);
        
        // Update local state
        setCurrentLesson(prev => prev ? { ...prev, completed: true } : null);
        
        // Update progress data
        if (progressData) {
          const updatedProgress = { ...progressData };
          updatedProgress.completedLessons += 1;
          updatedProgress.progressPercentage = (updatedProgress.completedLessons / updatedProgress.totalLessons) * 100;
          updatedProgress.lessonProgress = updatedProgress.lessonProgress.map(lesson => 
            lesson.lessonId === currentLesson.id 
              ? { ...lesson, isCompleted: true }
              : lesson
          );
          setProgressData(updatedProgress);
        }
        
    toast({
      title: "Lesson completed!",
      description: "Great job! Moving to the next lesson.",
    });
        
    const nextLesson = getNextLesson();
    if (nextLesson) {
          // Find progress data for next lesson
          const nextLessonProgress = progressData?.lessonProgress.find(
            p => p.lessonId === nextLesson.id
          );
          
          setCurrentLesson({
            ...nextLesson,
            moduleTitle: program?.modules.find(m => m.lessons.some(l => l.id === nextLesson.id))?.title || "",
            completed: nextLessonProgress?.isCompleted || false,
            progress: nextLessonProgress
          });
        }
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update lesson status"
      });
    }
  };

  const handleVideoError = () => {
    setVideoError("Video failed to load. This might be due to YouTube restrictions or network issues.");
  };

  const openVideoInNewTab = () => {
    if (currentLesson?.videoUrl) {
      window.open(currentLesson.videoUrl, '_blank');
    }
  };

  const handleLessonSelect = (lesson: ProgramLesson, moduleTitle: string) => {
    // Find progress data for this lesson
    const lessonProgress = progressData?.lessonProgress.find(
      p => p.lessonId === lesson.id
    );
    
    setCurrentLesson({
      ...lesson,
      moduleTitle,
      completed: lessonProgress?.isCompleted || false,
      progress: lessonProgress
    });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading program content...</p>
        </div>
      </div>
    );
  }

  if (!program) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <p className="text-muted-foreground">Program not found</p>
          <Button onClick={() => navigate("/my-programs")} className="mt-4">
            Back to Programs
          </Button>
        </div>
      </div>
    );
  }

  const lesson = getCurrentLesson();
  const nextLesson = getNextLesson();
  const previousLesson = getPreviousLesson();

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="border-b border-border bg-card/50">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate("/my-programs")}
                className="text-muted-foreground hover:text-foreground"
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Programs
              </Button>
              <div>
                <h1 className="text-xl font-bold text-foreground">{program.name}</h1>
                <p className="text-sm text-muted-foreground">by {program.instructorName}</p>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <Badge variant="secondary" className="gap-1">
                <Star className="h-3 w-3 fill-current" />
                {program.difficultyLevel}
              </Badge>
              <div className="text-right">
                <div className="text-sm font-medium text-foreground">
                  {Math.round(progressData?.progressPercentage || 0)}% Complete
                </div>
                <Progress value={progressData?.progressPercentage || 0} className="w-32" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Main Video Player */}
          <div className="lg:col-span-3">
            <Card className="mb-6">
              <CardContent className="p-0">
                {/* Video Player */}
                <div className="relative aspect-video bg-black rounded-t-lg overflow-hidden group">
                  {lesson?.videoUrl ? (
                    <>
                      {/* Video Thumbnail/Preview */}
                      {!isPlaying && (
                        <div 
                          className="absolute inset-0 bg-gradient-hero cursor-pointer transition-all duration-300 hover:opacity-90"
                          onClick={() => setIsPlaying(true)}
                        >
                          {/* YouTube Thumbnail Background */}
                          <div 
                            className="absolute inset-0 bg-cover bg-center bg-no-repeat opacity-40"
                            style={{
                              backgroundImage: `url(https://img.youtube.com/vi/${getYouTubeVideoId(lesson.videoUrl)}/maxresdefault.jpg)`
                            }}
                          />
                          
                          {/* Homepage-style overlay */}
                          <div className="absolute inset-0 bg-black/20"></div>
                          
                          {/* Overlay Content */}
                          <div className="absolute inset-0 flex items-center justify-center">
                    <div className="text-center text-white">
                              {/* Large Play Button with Homepage-style gradient */}
                              <div className="w-24 h-24 mx-auto mb-6 rounded-full bg-gradient-primary flex items-center justify-center backdrop-blur-sm shadow-2xl transition-all duration-300 hover:scale-110 group-hover:scale-110 hover:shadow-glow">
                                <Play className="h-10 w-10 ml-1 text-white drop-shadow-lg" />
                              </div>
                              
                              {/* Lesson Info */}
                              <div className="space-y-2">
                                <h3 className="text-xl font-semibold mb-2 bg-gradient-primary bg-clip-text text-transparent drop-shadow-lg">{lesson.title}</h3>
                                <p className="text-sm text-white/80 drop-shadow-md">{lesson.moduleTitle}</p>
                                {lesson.durationMinutes && (
                                  <div className="flex items-center justify-center gap-2 text-sm text-white/70 drop-shadow-md">
                                    <Clock className="h-4 w-4" />
                                    <span>{Math.floor(lesson.durationMinutes / 60)}:{(lesson.durationMinutes % 60).toString().padStart(2, '0')}</span>
                                  </div>
                        )}
                      </div>
                    </div>
                  </div>
                  
                          {/* Hover Effect - Homepage style */}
                          <div className="absolute inset-0 bg-gradient-primary/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                        </div>
                      )}
                      
                      {/* YouTube Video with Default Controls */}
                      {isPlaying && (
                        <div className="relative w-full h-full">
                          {videoError ? (
                            <div className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-red-500/20 to-red-500/5">
                              <div className="text-center text-white p-6">
                                <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-red-500/20 flex items-center justify-center backdrop-blur-sm">
                                  <Play className="h-8 w-8 ml-1" />
                                </div>
                                <h3 className="text-lg font-semibold mb-2">Video Playback Error</h3>
                                <p className="text-sm text-white/80 mb-4">{videoError}</p>
                                <div className="space-y-2">
                        <Button
                                    onClick={() => {
                                      setVideoError(null);
                                      setIsPlaying(false);
                                    }}
                                    className="bg-primary hover:bg-primary/90 text-white"
                                  >
                                    Back to Preview
                        </Button>
                                  <Button
                                    onClick={openVideoInNewTab}
                                    variant="outline"
                                    className="text-white border-white/20 hover:bg-white/10"
                                  >
                                    Watch on YouTube
                        </Button>
                      </div>
                    </div>
                  </div>
                          ) : (
                            <>
                              <iframe
                                src={getYouTubeEmbedUrl(lesson.videoUrl, true)}
                                title={lesson.title}
                                className="w-full h-full"
                                frameBorder="0"
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                                allowFullScreen
                                onError={handleVideoError}
                                onLoad={() => setVideoError(null)}
                              />
                              
                              {/* Simple Back Button Overlay */}
                              <div className="absolute top-4 left-4">
                                <Button
                                  size="sm"
                                  variant="ghost"
                                  onClick={() => setIsPlaying(false)}
                                  className="text-white hover:text-white hover:bg-white/20 bg-black/30 backdrop-blur-sm"
                                >
                                  <ArrowLeft className="h-4 w-4 mr-2" />
                                  Back to Preview
                                </Button>
                              </div>
                            </>
                          )}
                        </div>
                      )}
                    </>
                  ) : (
                    <div className="absolute inset-0 flex items-center justify-center bg-gradient-hero">
                      <div className="absolute inset-0 bg-black/20"></div>
                      <div className="relative text-center text-white">
                        <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-gradient-primary flex items-center justify-center backdrop-blur-sm shadow-2xl">
                          <Play className="h-8 w-8 ml-1 text-white drop-shadow-lg" />
                        </div>
                        <h3 className="text-lg font-semibold mb-2 bg-gradient-primary bg-clip-text text-transparent drop-shadow-lg">{lesson?.title || "No lesson selected"}</h3>
                        <p className="text-sm text-white/80 drop-shadow-md">{lesson?.moduleTitle || ""}</p>
                        {!lesson?.videoUrl && (
                          <p className="text-sm text-white/60 mt-2 drop-shadow-md">No video available for this lesson</p>
                        )}
                      </div>
                    </div>
                  )}
                </div>

                {/* Lesson Info */}
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div>
                      <h2 className="text-2xl font-bold text-foreground mb-2">{lesson?.title || "No lesson selected"}</h2>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Clock className="h-4 w-4" />
                          {lesson?.durationMinutes ? `${Math.floor(lesson.durationMinutes / 60)}:${(lesson.durationMinutes % 60).toString().padStart(2, '0')}` : "0:00"}
                        </span>
                        <span className="flex items-center gap-1">
                          <BookOpen className="h-4 w-4" />
                          {lesson?.moduleTitle || ""}
                        </span>
                      </div>
                    </div>
                    <Button 
                      onClick={toggleLessonCompletion} 
                      className="gap-2"
                      variant={currentLesson?.completed ? "outline" : "default"}
                    >
                      <CheckCircle className="h-4 w-4" />
                      {currentLesson?.completed ? "Mark Incomplete" : "Mark Complete"}
                    </Button>
                  </div>

                  {/* Navigation */}
                  <div className="flex items-center justify-between pt-4 border-t border-border">
                    <Button
                      variant="outline"
                      onClick={() => {
                        if (previousLesson) {
                          const moduleTitle = program.modules.find(m => m.lessons.some(l => l.id === previousLesson.id))?.title || "";
                          setCurrentLesson({ ...previousLesson, moduleTitle, completed: false });
                        }
                      }}
                      disabled={!previousLesson}
                      className="gap-2"
                    >
                      <SkipBack className="h-4 w-4" />
                      Previous
                    </Button>
                    <Button
                      onClick={() => {
                        if (nextLesson) {
                          const moduleTitle = program.modules.find(m => m.lessons.some(l => l.id === nextLesson.id))?.title || "";
                          setCurrentLesson({ ...nextLesson, moduleTitle, completed: false });
                        }
                      }}
                      disabled={!nextLesson}
                      className="gap-2"
                    >
                      Next
                      <SkipForward className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Tabs */}
            <Card>
              <CardHeader>
                <div className="flex space-x-1 border-b border-border -mb-6">
                  {[
                    { id: "overview", label: "Overview", icon: FileText },
                    { id: "notes", label: "Notes", icon: BookOpen },
                    { id: "discussion", label: "Discussion", icon: MessageSquare },
                  ].map((tab) => (
                    <Button
                      key={tab.id}
                      variant={activeTab === tab.id ? "default" : "ghost"}
                      size="sm"
                      onClick={() => setActiveTab(tab.id)}
                      className="gap-2"
                    >
                      <tab.icon className="h-4 w-4" />
                      {tab.label}
                    </Button>
                  ))}
                </div>
              </CardHeader>
              <CardContent className="pt-6">
                {activeTab === "overview" && (
                  <div className="space-y-4">
                    <div>
                      <h3 className="font-semibold text-foreground mb-2">About this lesson</h3>
                      <p className="text-muted-foreground">
                        {lesson?.description || lesson?.content || "No description available for this lesson."}
                      </p>
                    </div>
                    <Separator />
                    <div>
                      <h3 className="font-semibold text-foreground mb-2">Resources</h3>
                      <div className="space-y-2">
                        {lesson?.resources && lesson.resources.length > 0 ? (
                          lesson.resources.map((resource) => (
                            <Button 
                              key={resource.id} 
                              variant="outline" 
                              size="sm" 
                              className="gap-2"
                              onClick={() => window.open(resource.fileUrl, '_blank')}
                            >
                          <Download className="h-4 w-4" />
                              {resource.title}
                        </Button>
                          ))
                        ) : (
                          <p className="text-muted-foreground text-sm">No resources available for this lesson.</p>
                        )}
                      </div>
                    </div>
                  </div>
                )}

                {activeTab === "notes" && (
                  <div>
                    <h3 className="font-semibold text-foreground mb-2">Your Notes</h3>
                    <Textarea
                      placeholder="Take notes about this lesson..."
                      value={notes}
                      onChange={(e) => setNotes(e.target.value)}
                      className="min-h-[200px]"
                    />
                    <Button className="mt-4">Save Notes</Button>
                  </div>
                )}

                {activeTab === "discussion" && (
                  <div className="space-y-4">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Users className="h-4 w-4" />
                      12 comments
                    </div>
                    <div className="space-y-4">
                      {[1, 2, 3].map((comment) => (
                        <div key={comment} className="flex gap-3 p-4 bg-muted/30 rounded-lg">
                          <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center">
                            <Users className="h-4 w-4" />
                          </div>
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <span className="font-medium text-sm">Student {comment}</span>
                              <span className="text-xs text-muted-foreground">2 hours ago</span>
                            </div>
                            <p className="text-sm text-muted-foreground">
                              Great lesson! Really helped me understand the fundamentals.
                            </p>
                          </div>
                        </div>
                      ))}
                    </div>
                    <Textarea placeholder="Add a comment..." className="mt-4" />
                    <Button className="mt-2">Post Comment</Button>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Sidebar - Course Content */}
          <div className="lg:col-span-1">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Course Content</CardTitle>
                <p className="text-sm text-muted-foreground">
                  {program.totalLessons} lessons • {Math.floor(program.totalDurationMinutes / 60)}h {program.totalDurationMinutes % 60}m
                </p>
              </CardHeader>
              <CardContent className="p-0">
                <ScrollArea className="h-[600px]">
                  {program.modules.map((module) => (
                    <div key={module.id} className="border-b border-border last:border-0">
                      <div className="p-4 bg-muted/30">
                        <h4 className="font-medium text-foreground">{module.title}</h4>
                        <p className="text-xs text-muted-foreground mt-1">
                          {module.lessons.length} lessons
                        </p>
                      </div>
                      <div className="space-y-0">
                        {module.lessons.map((lesson) => (
                          <button
                            key={lesson.id}
                            onClick={() => handleLessonSelect(lesson, module.title)}
                            className={`w-full p-3 text-left hover:bg-muted/50 transition-colors border-l-2 ${
                              currentLesson?.id === lesson.id
                                ? "border-primary bg-primary/5"
                                : "border-transparent"
                            }`}
                          >
                            <div className="flex items-center gap-3">
                              {(() => {
                                // Find progress data for this lesson
                                const lessonProgress = progressData?.lessonProgress.find(
                                  p => p.lessonId === lesson.id
                                );
                                
                                if (lessonProgress?.isCompleted) {
                                  return (
                                    <div className="relative flex-shrink-0">
                                      <Circle className="h-4 w-4 text-muted-foreground" />
                                      <CheckCircle className="h-4 w-4 text-green-500 absolute inset-0" />
                                    </div>
                                  );
                                } else {
                                  return <Circle className="h-4 w-4 text-muted-foreground flex-shrink-0" />;
                                }
                              })()}
                              <div className="flex-1 min-w-0">
                                <p className={`text-sm font-medium truncate ${
                                  currentLesson?.id === lesson.id ? "text-primary" : "text-foreground"
                                }`}>
                                  {lesson.title}
                                </p>
                                <p className="text-xs text-muted-foreground">
                                  {lesson.durationMinutes ? `${Math.floor(lesson.durationMinutes / 60)}:${(lesson.durationMinutes % 60).toString().padStart(2, '0')}` : "0:00"}
                                </p>
                              </div>
                            </div>
                          </button>
                        ))}
                      </div>
                    </div>
                  ))}
                </ScrollArea>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};