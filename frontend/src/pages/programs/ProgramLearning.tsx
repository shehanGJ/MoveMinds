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
import {
  Play,
  Pause,
  SkipForward,
  SkipBack,
  Clock,
  CheckCircle,
  Circle,
  BookOpen,
  MessageSquare,
  Download,
  Star,
  Volume2,
  Settings,
  Maximize,
  ArrowLeft,
  FileText,
  Users
} from "lucide-react";

// Types
interface Lesson {
  id: number;
  title: string;
  duration: string;
  completed: boolean;
  videoUrl: string;
}

interface LessonWithModule extends Lesson {
  moduleTitle: string;
}

// Mock data - replace with API calls
const mockProgram = {
  id: 1,
  title: "Complete Fitness Transformation",
  description: "A comprehensive 12-week program designed to transform your body and mind",
  instructor: "Sarah Johnson",
  totalLessons: 24,
  totalDuration: "8h 45m",
  difficulty: "Intermediate",
  rating: 4.8,
  modules: [
    {
      id: 1,
      title: "Foundation & Assessment",
      lessons: [
        { id: 1, title: "Welcome & Program Overview", duration: "12:30", completed: true, videoUrl: "https://example.com/video1" },
        { id: 2, title: "Fitness Assessment", duration: "18:45", completed: true, videoUrl: "https://example.com/video2" },
        { id: 3, title: "Goal Setting Workshop", duration: "15:20", completed: false, videoUrl: "https://example.com/video3" },
      ]
    },
    {
      id: 2,
      title: "Strength Training Fundamentals",
      lessons: [
        { id: 4, title: "Proper Form & Technique", duration: "22:15", completed: false, videoUrl: "https://example.com/video4" },
        { id: 5, title: "Progressive Overload", duration: "16:30", completed: false, videoUrl: "https://example.com/video5" },
        { id: 6, title: "Compound Movements", duration: "25:10", completed: false, videoUrl: "https://example.com/video6" },
      ]
    },
    {
      id: 3,
      title: "Nutrition & Recovery",
      lessons: [
        { id: 7, title: "Macro Nutrition Basics", duration: "20:45", completed: false, videoUrl: "https://example.com/video7" },
        { id: 8, title: "Meal Planning Strategy", duration: "14:20", completed: false, videoUrl: "https://example.com/video8" },
        { id: 9, title: "Recovery & Sleep", duration: "18:30", completed: false, videoUrl: "https://example.com/video9" },
      ]
    }
  ]
};

export const ProgramLearning = () => {
  const { programId } = useParams();
  const navigate = useNavigate();
  const [currentLesson, setCurrentLesson] = useState(1);
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(35);
  const [notes, setNotes] = useState("");
  const [activeTab, setActiveTab] = useState("overview");

  const getCurrentLesson = (): LessonWithModule => {
    for (const module of mockProgram.modules) {
      const lesson = module.lessons.find(l => l.id === currentLesson);
      if (lesson) return { ...lesson, moduleTitle: module.title };
    }
    return { ...mockProgram.modules[0].lessons[0], moduleTitle: mockProgram.modules[0].title };
  };

  const getNextLesson = () => {
    const allLessons = mockProgram.modules.flatMap(m => m.lessons);
    const currentIndex = allLessons.findIndex(l => l.id === currentLesson);
    return currentIndex < allLessons.length - 1 ? allLessons[currentIndex + 1] : null;
  };

  const getPreviousLesson = () => {
    const allLessons = mockProgram.modules.flatMap(m => m.lessons);
    const currentIndex = allLessons.findIndex(l => l.id === currentLesson);
    return currentIndex > 0 ? allLessons[currentIndex - 1] : null;
  };

  const markAsCompleted = () => {
    // Mock completion logic
    toast({
      title: "Lesson completed!",
      description: "Great job! Moving to the next lesson.",
    });
    const nextLesson = getNextLesson();
    if (nextLesson) {
      setCurrentLesson(nextLesson.id);
    }
  };

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
                <h1 className="text-xl font-bold text-foreground">{mockProgram.title}</h1>
                <p className="text-sm text-muted-foreground">by {mockProgram.instructor}</p>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <Badge variant="secondary" className="gap-1">
                <Star className="h-3 w-3 fill-current" />
                {mockProgram.rating}
              </Badge>
              <div className="text-right">
                <div className="text-sm font-medium text-foreground">{progress}% Complete</div>
                <Progress value={progress} className="w-32" />
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
                <div className="relative aspect-video bg-black rounded-t-lg overflow-hidden">
                  <div className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-primary/20 to-primary/5">
                    <div className="text-center text-white">
                      <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-primary/20 flex items-center justify-center backdrop-blur-sm">
                        {isPlaying ? (
                          <Pause className="h-8 w-8" />
                        ) : (
                          <Play className="h-8 w-8 ml-1" />
                        )}
                      </div>
                      <p className="text-lg font-medium mb-2">{lesson.title}</p>
                      <p className="text-sm text-white/80">{lesson.moduleTitle}</p>
                    </div>
                  </div>
                  
                  {/* Video Controls */}
                  <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-4">
                    <div className="flex items-center justify-between text-white">
                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => setIsPlaying(!isPlaying)}
                          className="text-white hover:text-white hover:bg-white/20"
                        >
                          {isPlaying ? <Pause className="h-4 w-4" /> : <Play className="h-4 w-4" />}
                        </Button>
                        <span className="text-sm">0:00 / {lesson.duration}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button size="sm" variant="ghost" className="text-white hover:text-white hover:bg-white/20">
                          <Volume2 className="h-4 w-4" />
                        </Button>
                        <Button size="sm" variant="ghost" className="text-white hover:text-white hover:bg-white/20">
                          <Settings className="h-4 w-4" />
                        </Button>
                        <Button size="sm" variant="ghost" className="text-white hover:text-white hover:bg-white/20">
                          <Maximize className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Lesson Info */}
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div>
                      <h2 className="text-2xl font-bold text-foreground mb-2">{lesson.title}</h2>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Clock className="h-4 w-4" />
                          {lesson.duration}
                        </span>
                        <span className="flex items-center gap-1">
                          <BookOpen className="h-4 w-4" />
                          {lesson.moduleTitle}
                        </span>
                      </div>
                    </div>
                    <Button onClick={markAsCompleted} className="gap-2">
                      <CheckCircle className="h-4 w-4" />
                      Mark Complete
                    </Button>
                  </div>

                  {/* Navigation */}
                  <div className="flex items-center justify-between pt-4 border-t border-border">
                    <Button
                      variant="outline"
                      onClick={() => previousLesson && setCurrentLesson(previousLesson.id)}
                      disabled={!previousLesson}
                      className="gap-2"
                    >
                      <SkipBack className="h-4 w-4" />
                      Previous
                    </Button>
                    <Button
                      onClick={() => nextLesson && setCurrentLesson(nextLesson.id)}
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
                        In this lesson, you'll learn the fundamental concepts that will serve as the foundation 
                        for your fitness journey. We'll cover proper assessment techniques and help you set 
                        realistic, achievable goals.
                      </p>
                    </div>
                    <Separator />
                    <div>
                      <h3 className="font-semibold text-foreground mb-2">Resources</h3>
                      <div className="space-y-2">
                        <Button variant="outline" size="sm" className="gap-2">
                          <Download className="h-4 w-4" />
                          Download Worksheet
                        </Button>
                        <Button variant="outline" size="sm" className="gap-2">
                          <Download className="h-4 w-4" />
                          Exercise Guide PDF
                        </Button>
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
                  {mockProgram.totalLessons} lessons • {mockProgram.totalDuration}
                </p>
              </CardHeader>
              <CardContent className="p-0">
                <ScrollArea className="h-[600px]">
                  {mockProgram.modules.map((module) => (
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
                            onClick={() => setCurrentLesson(lesson.id)}
                            className={`w-full p-3 text-left hover:bg-muted/50 transition-colors border-l-2 ${
                              currentLesson === lesson.id
                                ? "border-primary bg-primary/5"
                                : "border-transparent"
                            }`}
                          >
                            <div className="flex items-center gap-3">
                              {lesson.completed ? (
                                <CheckCircle className="h-4 w-4 text-primary flex-shrink-0" />
                              ) : (
                                <Circle className="h-4 w-4 text-muted-foreground flex-shrink-0" />
                              )}
                              <div className="flex-1 min-w-0">
                                <p className={`text-sm font-medium truncate ${
                                  currentLesson === lesson.id ? "text-primary" : "text-foreground"
                                }`}>
                                  {lesson.title}
                                </p>
                                <p className="text-xs text-muted-foreground">{lesson.duration}</p>
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