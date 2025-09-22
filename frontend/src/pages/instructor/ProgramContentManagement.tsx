import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/hooks/use-toast";
import { programContentApi, ProgramModule, ProgramLesson, ProgramResource } from "@/lib/api";
import {
  Plus,
  Edit,
  Trash2,
  MoreVertical,
  Eye,
  EyeOff,
  BookOpen,
  Play,
  Clock,
  FileText,
  Download,
  GripVertical,
  ArrowLeft,
  Save,
  X
} from "lucide-react";

export default function ProgramContentManagement() {
  const { programId } = useParams<{ programId: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  
  // State
  const [modules, setModules] = useState<ProgramModule[]>([]);
  const [selectedModule, setSelectedModule] = useState<ProgramModule | null>(null);
  const [selectedLesson, setSelectedLesson] = useState<ProgramLesson | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  
  // Form states
  const [isCreatingModule, setIsCreatingModule] = useState(false);
  const [isCreatingLesson, setIsCreatingLesson] = useState(false);
  const [isCreatingResource, setIsCreatingResource] = useState(false);
  const [editingItem, setEditingItem] = useState<{ type: 'module' | 'lesson' | 'resource', id: number } | null>(null);
  
  const [moduleForm, setModuleForm] = useState({
    title: '',
    description: '',
    isPublished: false
  });
  
  const [lessonForm, setLessonForm] = useState({
    title: '',
    description: '',
    content: '',
    videoUrl: '',
    durationMinutes: 0,
    isPublished: false,
    isPreview: false
  });
  
  const [resourceForm, setResourceForm] = useState({
    title: '',
    description: '',
    fileUrl: '',
    fileType: 'PDF',
    file: null as File | null,
    fileSizeBytes: 0
  });

  useEffect(() => {
    if (programId) {
      fetchModules();
    }
  }, [programId]);

  const fetchModules = async () => {
    if (!programId) return;
    
    try {
      setIsLoading(true);
      const response = await programContentApi.getProgramModules(parseInt(programId));
      setModules(response.data);
      
      // Auto-select first module if available
      if (response.data.length > 0 && !selectedModule) {
        setSelectedModule(response.data[0]);
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load modules"
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateModule = async () => {
    if (!programId) return;
    
    try {
      const response = await programContentApi.createModule(parseInt(programId), moduleForm);
      setModules(prev => [...prev, response.data]);
      setModuleForm({ title: '', description: '', isPublished: false });
      setIsCreatingModule(false);
      toast({
        title: "Success",
        description: "Module created successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to create module"
      });
    }
  };

  const handleCreateLesson = async () => {
    if (!selectedModule) return;
    
    try {
      const response = await programContentApi.createLesson(selectedModule.id, lessonForm);
      setModules(prev => prev.map(module => 
        module.id === selectedModule.id 
          ? { ...module, lessons: [...module.lessons, response.data] }
          : module
      ));
      setLessonForm({ title: '', description: '', content: '', videoUrl: '', durationMinutes: 0, isPublished: false, isPreview: false });
      setIsCreatingLesson(false);
      toast({
        title: "Success",
        description: "Lesson created successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to create lesson"
      });
    }
  };

  const handleCreateResource = async () => {
    if (!selectedLesson) return;
    
    try {
      const formData = new FormData();
      
      // Add file if provided
      if (resourceForm.file) {
        formData.append('file', resourceForm.file);
      }
      
      // Add other form data
      formData.append('title', resourceForm.title);
      if (resourceForm.description) {
        formData.append('description', resourceForm.description);
      }
      if (resourceForm.fileUrl) {
        formData.append('fileUrl', resourceForm.fileUrl);
      }
      if (resourceForm.fileType) {
        formData.append('fileType', resourceForm.fileType);
      }
      if (resourceForm.fileSizeBytes) {
        formData.append('fileSizeBytes', resourceForm.fileSizeBytes.toString());
      }
      
      const response = await programContentApi.createResource(selectedLesson.id, formData);
      setModules(prev => prev.map(module => ({
        ...module,
        lessons: module.lessons.map(lesson =>
          lesson.id === selectedLesson.id
            ? { ...lesson, resources: [...lesson.resources, response.data] }
            : lesson
        )
      })));
      setResourceForm({ title: '', description: '', fileUrl: '', fileType: 'PDF', file: null, fileSizeBytes: 0 });
      setIsCreatingResource(false);
      toast({
        title: "Success",
        description: "Resource created successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to create resource"
      });
    }
  };

  const handleDeleteModule = async (moduleId: number) => {
    try {
      await programContentApi.deleteModule(moduleId);
      setModules(prev => prev.filter(module => module.id !== moduleId));
      if (selectedModule?.id === moduleId) {
        setSelectedModule(null);
        setSelectedLesson(null);
      }
      toast({
        title: "Success",
        description: "Module deleted successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete module"
      });
    }
  };

  const handleDeleteLesson = async (lessonId: number) => {
    try {
      await programContentApi.deleteLesson(lessonId);
      setModules(prev => prev.map(module => ({
        ...module,
        lessons: module.lessons.filter(lesson => lesson.id !== lessonId)
      })));
      if (selectedLesson?.id === lessonId) {
        setSelectedLesson(null);
      }
      toast({
        title: "Success",
        description: "Lesson deleted successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete lesson"
      });
    }
  };

  const handleDeleteResource = async (resourceId: number) => {
    try {
      await programContentApi.deleteResource(resourceId);
      setModules(prev => prev.map(module => ({
        ...module,
        lessons: module.lessons.map(lesson => ({
          ...lesson,
          resources: lesson.resources.filter(resource => resource.id !== resourceId)
        }))
      })));
      toast({
        title: "Success",
        description: "Resource deleted successfully"
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to delete resource"
      });
    }
  };

  const handleToggleModulePublish = async (moduleId: number) => {
    try {
      const module = modules.find(m => m.id === moduleId);
      if (!module) return;
      
      const response = await programContentApi.updateModule(moduleId, {
        ...module,
        isPublished: !module.isPublished
      });
      
      setModules(prev => prev.map(m => 
        m.id === moduleId ? response.data : m
      ));
      
      toast({
        title: "Success",
        description: `Module ${response.data.isPublished ? 'published' : 'unpublished'} successfully`
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update module"
      });
    }
  };

  const handleToggleLessonPublish = async (lessonId: number) => {
    try {
      const lesson = selectedModule?.lessons.find(l => l.id === lessonId);
      if (!lesson) return;
      
      const response = await programContentApi.updateLesson(lessonId, {
        ...lesson,
        isPublished: !lesson.isPublished
      });
      
      setModules(prev => prev.map(module => ({
        ...module,
        lessons: module.lessons.map(l => 
          l.id === lessonId ? response.data : l
        )
      })));
      
      toast({
        title: "Success",
        description: `Lesson ${response.data.isPublished ? 'published' : 'unpublished'} successfully`
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to update lesson"
      });
    }
  };

  const formatDuration = (minutes: number): string => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-6">
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
            <p className="text-muted-foreground">Loading program content...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate('/instructor')}
                className="gap-2"
              >
                <ArrowLeft className="h-4 w-4" />
                Back to Dashboard
              </Button>
              <div>
                <h1 className="text-2xl font-bold">Program Content Management</h1>
                <p className="text-muted-foreground">Manage your course modules, lessons, and resources</p>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Badge variant="outline" className="gap-1">
                <BookOpen className="h-4 w-4" />
                {modules.length} modules
              </Badge>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {/* Modern Three-Panel Layout */}
        <div className="grid grid-cols-1 xl:grid-cols-12 gap-6 h-[calc(100vh-200px)]">
          
          {/* Left Panel - Modules (3.5 columns) */}
          <div className="xl:col-span-4">
            <Card className="h-full flex flex-col">
              <CardHeader className="pb-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div>
                      <CardTitle className="text-lg font-semibold">Course Modules</CardTitle>
                      <p className="text-sm text-muted-foreground mt-1">
                        {modules.length} modules • {modules.reduce((acc, m) => acc + m.lessons.length, 0)} lessons
                      </p>
                    </div>
                    <Button
                      size="sm"
                      onClick={() => setIsCreatingModule(true)}
                      className="gap-2"
                    >
                      <Plus className="h-4 w-4" />
                      Add Module
                    </Button>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="flex-1 p-0 overflow-hidden">
                <ScrollArea className="h-full">
                  <div className="space-y-1 p-2">
                    {modules.map((module, index) => (
                      <div
                        key={module.id}
                        className={`group relative p-3 rounded-lg cursor-pointer transition-all duration-200 hover:bg-muted/50 ${
                          selectedModule?.id === module.id 
                            ? 'bg-primary/10 border border-primary/20 shadow-sm' 
                            : 'hover:shadow-sm'
                        }`}
                        onClick={() => setSelectedModule(module)}
                      >
                        <div className="flex items-start justify-between">
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-1">
                              <span className="text-xs font-medium text-muted-foreground bg-muted px-2 py-1 rounded">
                                {index + 1}
                              </span>
                              <h4 className="font-medium text-sm truncate">{module.title}</h4>
                            </div>
                            <p className="text-xs text-muted-foreground">
                              {module.lessons.length} lessons
                            </p>
                          </div>
                          <div className="flex items-center gap-1 ">
                            {module.isPublished ? (
                              <Eye className="h-4 w-4 text-green-500" />
                            ) : (
                              <EyeOff className="h-4 w-4 text-muted-foreground" />
                            )}
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                                  <MoreVertical className="h-4 w-4" />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end">
                                <DropdownMenuItem onClick={() => setEditingItem({ type: 'module', id: module.id })}>
                                  <Edit className="h-4 w-4 mr-2" />
                                  Edit
                                </DropdownMenuItem>
                                <DropdownMenuItem onClick={() => handleToggleModulePublish(module.id)}>
                                  {module.isPublished ? (
                                    <>
                                      <EyeOff className="h-4 w-4 mr-2" />
                                      Unpublish
                                    </>
                                  ) : (
                                    <>
                                      <Eye className="h-4 w-4 mr-2" />
                                      Publish
                                    </>
                                  )}
                                </DropdownMenuItem>
                                <DropdownMenuItem 
                                  onClick={() => handleDeleteModule(module.id)}
                                  className="text-destructive"
                                >
                                  <Trash2 className="h-4 w-4 mr-2" />
                                  Delete
                                </DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </ScrollArea>
              </CardContent>
            </Card>
          </div>

          {/* Middle Panel - Lessons (3.5 columns) */}
          <div className="xl:col-span-4">
            <Card className="h-full flex flex-col">
              <CardHeader className="pb-3">
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle className="text-lg font-semibold">
                      {selectedModule ? selectedModule.title : 'Select a Module'}
                    </CardTitle>
                    <p className="text-sm text-muted-foreground mt-1">
                      {selectedModule ? `${selectedModule.lessons.length} lessons` : 'Choose a module to view lessons'}
                    </p>
                  </div>
                  {selectedModule && (
                    <Button
                      size="sm"
                      onClick={() => setIsCreatingLesson(true)}
                      className="gap-2"
                    >
                      <Plus className="h-4 w-4" />
                      Add Lesson
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="flex-1 p-0 overflow-hidden">
                {selectedModule ? (
                  <ScrollArea className="h-full">
                    <div className="space-y-1 p-2">
                      {selectedModule.lessons.map((lesson, index) => (
                        <div
                          key={lesson.id}
                          className={`group relative p-3 rounded-lg cursor-pointer transition-all duration-200 hover:bg-muted/50 ${
                            selectedLesson?.id === lesson.id 
                              ? 'bg-primary/10 border border-primary/20 shadow-sm' 
                              : 'hover:shadow-sm'
                          }`}
                          onClick={() => setSelectedLesson(lesson)}
                        >
                          <div className="flex items-start justify-between">
                            <div className="flex-1 min-w-0">
                              <div className="flex items-center gap-2 mb-1">
                                <span className="text-xs font-medium text-muted-foreground bg-muted px-2 py-1 rounded">
                                  {index + 1}
                                </span>
                                <div className="flex items-center gap-1">
                                  {lesson.isPreview ? (
                                    <Play className="h-4 w-4 text-primary" />
                                  ) : (
                                    <BookOpen className="h-4 w-4 text-muted-foreground" />
                                  )}
                                  <h4 className="font-medium text-sm truncate">{lesson.title}</h4>
                                </div>
                              </div>
                              <div className="flex items-center gap-3 text-xs text-muted-foreground">
                                <span className="flex items-center gap-1">
                                  <Clock className="h-4 w-4" />
                                  {formatDuration(lesson.durationMinutes || 0)}
                                </span>
                                <span className="flex items-center gap-1">
                                  <FileText className="h-4 w-4" />
                                  {lesson.resources?.length || 0} resources
                                </span>
                              </div>
                            </div>
                            <div className="flex items-center gap-1 ">
                              {lesson.isPublished ? (
                                <Eye className="h-4 w-4 text-green-500" />
                              ) : (
                                <EyeOff className="h-4 w-4 text-muted-foreground" />
                              )}
                              <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                  <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                                    <MoreVertical className="h-4 w-4" />
                                  </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end">
                                  <DropdownMenuItem onClick={() => setEditingItem({ type: 'lesson', id: lesson.id })}>
                                    <Edit className="h-4 w-4 mr-2" />
                                    Edit
                                  </DropdownMenuItem>
                                  <DropdownMenuItem onClick={() => handleToggleLessonPublish(lesson.id)}>
                                    {lesson.isPublished ? (
                                      <>
                                        <EyeOff className="h-4 w-4 mr-2" />
                                        Unpublish
                                      </>
                                    ) : (
                                      <>
                                        <Eye className="h-4 w-4 mr-2" />
                                        Publish
                                      </>
                                    )}
                                  </DropdownMenuItem>
                                  <DropdownMenuItem 
                                    onClick={() => handleDeleteLesson(lesson.id)}
                                    className="text-destructive"
                                  >
                                    <Trash2 className="h-4 w-4 mr-2" />
                                    Delete
                                  </DropdownMenuItem>
                                </DropdownMenuContent>
                              </DropdownMenu>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </ScrollArea>
                ) : (
                  <div className="flex items-center justify-center h-full">
                    <div className="text-center">
                      <BookOpen className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                      <h3 className="text-lg font-medium text-foreground mb-2">No Module Selected</h3>
                      <p className="text-muted-foreground">Select a module from the left to view its lessons.</p>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Right Panel - Lesson Details & Resources (4 columns) */}
          <div className="xl:col-span-4">
            {selectedLesson ? (
              <div className="h-full flex flex-col space-y-4">
                {/* Lesson Header */}
                <Card>
                  <CardHeader className="pb-3">
                    <div className="flex items-start justify-between">
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-2">
                          {selectedLesson.isPreview ? (
                            <Play className="h-4 w-4 text-primary" />
                          ) : (
                            <BookOpen className="h-4 w-4 text-muted-foreground" />
                          )}
                          <CardTitle className="text-lg font-semibold truncate">{selectedLesson.title}</CardTitle>
                        </div>
                        <p className="text-sm text-muted-foreground line-clamp-2">{selectedLesson.description}</p>
                        <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <Clock className="h-4 w-4" />
                            {formatDuration(selectedLesson.durationMinutes || 0)}
                          </span>
                          <span className="flex items-center gap-1">
                            <FileText className="h-4 w-4" />
                            {selectedLesson.resources?.length || 0} resources
                          </span>
                        </div>
                      </div>
                      <div className="flex items-center gap-2 ml-4">
                        {selectedLesson.isPublished ? (
                          <Badge variant="default" className="bg-green-500 text-white">
                            <Eye className="h-4 w-4 mr-1" />
                            Published
                          </Badge>
                        ) : (
                          <Badge variant="secondary">
                            <EyeOff className="h-4 w-4 mr-1" />
                            Draft
                          </Badge>
                        )}
                        {selectedLesson.isPreview && (
                          <Badge variant="outline" className="border-primary text-primary">
                            Preview
                          </Badge>
                        )}
                      </div>
                    </div>
                  </CardHeader>
                </Card>

                {/* Lesson Content */}
                <Card className="flex-1 flex flex-col">
                  <CardHeader className="pb-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <CardTitle className="text-base font-semibold">Lesson Content</CardTitle>
                        <p className="text-sm text-muted-foreground mt-1">
                          {selectedLesson.content ? 'Content available' : 'No content added yet'}
                        </p>
                      </div>
                      <Dialog open={isCreatingResource} onOpenChange={setIsCreatingResource}>
                        <DialogTrigger asChild>
                          <Button size="sm" className="gap-2">
                            <Plus className="h-4 w-4" />
                            Add Resource
                          </Button>
                        </DialogTrigger>
                        <DialogContent className="max-w-2xl">
                          <DialogHeader>
                            <DialogTitle>Add Resource</DialogTitle>
                            <DialogDescription>
                              Add a new resource to this lesson.
                            </DialogDescription>
                          </DialogHeader>
                          <div className="space-y-4">
                            <div>
                              <Label htmlFor="resource-title">Title *</Label>
                              <Input
                                id="resource-title"
                                value={resourceForm.title}
                                onChange={(e) => setResourceForm(prev => ({ ...prev, title: e.target.value }))}
                                placeholder="Enter resource title"
                              />
                            </div>
                            <div>
                              <Label htmlFor="resource-description">Description</Label>
                              <Textarea
                                id="resource-description"
                                value={resourceForm.description}
                                onChange={(e) => setResourceForm(prev => ({ ...prev, description: e.target.value }))}
                                placeholder="Enter resource description"
                                rows={3}
                              />
                            </div>
                            <div>
                              <Label htmlFor="resource-file">Upload File</Label>
                              <Input
                                id="resource-file"
                                type="file"
                                accept=".pdf,.doc,.docx,.txt,.ppt,.pptx,.xls,.xlsx,.zip,.rar"
                                onChange={(e) => {
                                  const file = e.target.files?.[0];
                                  if (file) {
                                    setResourceForm(prev => ({
                                      ...prev,
                                      file: file,
                                      fileType: file.type || file.name.split('.').pop()?.toUpperCase() || 'FILE',
                                      fileSizeBytes: file.size
                                    }));
                                  }
                                }}
                                className="cursor-pointer"
                              />
                              <p className="text-xs text-muted-foreground mt-1">
                                Supported formats: PDF, DOC, DOCX, TXT, PPT, PPTX, XLS, XLSX, ZIP, RAR
                              </p>
                              {resourceForm.file && (
                                <div className="mt-2 p-2 bg-green-50 border border-green-200 rounded-md">
                                  <div className="flex items-center justify-between">
                                    <div>
                                      <p className="text-sm text-green-800">
                                        <strong>Selected file:</strong> {resourceForm.file.name}
                                      </p>
                                      <p className="text-xs text-green-600">
                                        Size: {(resourceForm.file.size / 1024 / 1024).toFixed(2)} MB
                                      </p>
                                    </div>
                                    <Button
                                      type="button"
                                      variant="ghost"
                                      size="sm"
                                      onClick={() => setResourceForm(prev => ({ ...prev, file: null, fileSizeBytes: 0 }))}
                                      className="text-green-600 hover:text-green-800"
                                    >
                                      Clear
                                    </Button>
                                  </div>
                                </div>
                              )}
                            </div>
                            <div>
                              <Label htmlFor="resource-url">Or File URL (Alternative)</Label>
                              <Input
                                id="resource-url"
                                value={resourceForm.fileUrl}
                                onChange={(e) => setResourceForm(prev => ({ ...prev, fileUrl: e.target.value }))}
                                placeholder="Enter file URL (if not uploading)"
                              />
                            </div>
                          </div>
                          <div className="flex justify-end gap-2 pt-4">
                            <Button variant="outline" onClick={() => setIsCreatingResource(false)}>
                              Cancel
                            </Button>
                            <Button onClick={handleCreateResource}>
                              Create Resource
                            </Button>
                          </div>
                        </DialogContent>
                      </Dialog>
                    </div>
                  </CardHeader>
                  <CardContent className="flex-1 p-0 overflow-hidden">
                    {selectedLesson.content ? (
                      <div className="p-4">
                        <div className="prose prose-sm max-w-none">
                          <div className="whitespace-pre-wrap text-sm text-foreground">
                            {selectedLesson.content}
                          </div>
                        </div>
                      </div>
                    ) : (
                      <div className="flex items-center justify-center h-full p-4">
                        <div className="text-center">
                          <FileText className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                          <h3 className="text-lg font-medium text-foreground mb-2">No Content</h3>
                          <p className="text-muted-foreground text-sm">This lesson doesn't have any content yet.</p>
                        </div>
                      </div>
                    )}
                  </CardContent>
                </Card>

                {/* Resources Section */}
                <Card className="flex-1 flex flex-col">
                  <CardHeader className="pb-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <CardTitle className="text-base font-semibold">Resources</CardTitle>
                        <p className="text-sm text-muted-foreground mt-1">
                          {selectedLesson.resources?.length || 0} resources available
                        </p>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent className="flex-1 p-0 overflow-hidden">
                    {selectedLesson.resources && selectedLesson.resources.length > 0 ? (
                      <ScrollArea className="h-full">
                        <div className="space-y-2 p-4">
                          {selectedLesson.resources.map((resource) => (
                            <div
                              key={resource.id}
                              className="flex items-center justify-between p-3 bg-muted/30 rounded-lg border"
                            >
                              <div className="flex items-center gap-3">
                                <div className="p-2 bg-primary/10 rounded-lg">
                                  <FileText className="h-4 w-4 text-primary" />
                                </div>
                                <div className="flex-1 min-w-0">
                                  <h4 className="font-medium text-sm truncate">{resource.title}</h4>
                                  <p className="text-xs text-muted-foreground">
                                    {resource.fileType} • {resource.fileSizeBytes ? `${(resource.fileSizeBytes / 1024 / 1024).toFixed(2)} MB` : 'Unknown size'}
                                  </p>
                                </div>
                              </div>
                              <div className="flex items-center gap-1">
                                {resource.fileUrl && (
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => {
                                      // Create a temporary anchor element for download
                                      const link = document.createElement('a');
                                      link.href = resource.fileUrl;
                                      link.download = resource.title || 'download';
                                      link.target = '_blank';
                                      document.body.appendChild(link);
                                      link.click();
                                      document.body.removeChild(link);
                                    }}
                                    className="h-8 w-8 p-0"
                                  >
                                    <Download className="h-4 w-4" />
                                  </Button>
                                )}
                                <DropdownMenu>
                                  <DropdownMenuTrigger asChild>
                                    <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                                      <MoreVertical className="h-4 w-4" />
                                    </Button>
                                  </DropdownMenuTrigger>
                                  <DropdownMenuContent align="end">
                                    <DropdownMenuItem onClick={() => setEditingItem({ type: 'resource', id: resource.id })}>
                                      <Edit className="h-4 w-4 mr-2" />
                                      Edit
                                    </DropdownMenuItem>
                                    <DropdownMenuItem 
                                      onClick={() => handleDeleteResource(resource.id)}
                                      className="text-destructive"
                                    >
                                      <Trash2 className="h-4 w-4 mr-2" />
                                      Delete
                                    </DropdownMenuItem>
                                  </DropdownMenuContent>
                                </DropdownMenu>
                              </div>
                            </div>
                          ))}
                        </div>
                      </ScrollArea>
                    ) : (
                      <div className="flex items-center justify-center h-full p-4">
                        <div className="text-center">
                          <FileText className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                          <h3 className="text-lg font-medium text-foreground mb-2">No Resources</h3>
                          <p className="text-muted-foreground text-sm mb-4">This lesson doesn't have any resources yet.</p>
                          <Dialog open={isCreatingResource} onOpenChange={setIsCreatingResource}>
                            <DialogTrigger asChild>
                              <Button size="sm" className="gap-2">
                                <Plus className="h-4 w-4" />
                                Add First Resource
                              </Button>
                            </DialogTrigger>
                          </Dialog>
                        </div>
                      </div>
                    )}
                  </CardContent>
                </Card>
              </div>
            ) : (
              <div className="flex items-center justify-center h-full">
                <div className="text-center">
                  <BookOpen className="h-16 w-16 text-muted-foreground mx-auto mb-4" />
                  <h3 className="text-xl font-semibold text-foreground mb-2">No Lesson Selected</h3>
                  <p className="text-muted-foreground">Select a lesson from the middle panel to view its details and resources.</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Create Module Dialog */}
      <Dialog open={isCreatingModule} onOpenChange={setIsCreatingModule}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create New Module</DialogTitle>
            <DialogDescription>
              Add a new module to your program.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="module-title">Module Title</Label>
              <Input
                id="module-title"
                value={moduleForm.title}
                onChange={(e) => setModuleForm(prev => ({ ...prev, title: e.target.value }))}
                placeholder="Enter module title"
              />
            </div>
            <div>
              <Label htmlFor="module-description">Description</Label>
              <Textarea
                id="module-description"
                value={moduleForm.description}
                onChange={(e) => setModuleForm(prev => ({ ...prev, description: e.target.value }))}
                placeholder="Enter module description"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Switch
                id="module-published"
                checked={moduleForm.isPublished}
                onCheckedChange={(checked) => setModuleForm(prev => ({ ...prev, isPublished: checked }))}
              />
              <Label htmlFor="module-published">Publish module</Label>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsCreatingModule(false)}>
                Cancel
              </Button>
              <Button onClick={handleCreateModule}>
                Create Module
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* Create Lesson Dialog */}
      <Dialog open={isCreatingLesson} onOpenChange={setIsCreatingLesson}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Create New Lesson</DialogTitle>
            <DialogDescription>
              Add a new lesson to the "{selectedModule?.title}" module.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="lesson-title">Lesson Title</Label>
                <Input
                  id="lesson-title"
                  value={lessonForm.title}
                  onChange={(e) => setLessonForm(prev => ({ ...prev, title: e.target.value }))}
                  placeholder="Enter lesson title"
                />
              </div>
              <div>
                <Label htmlFor="lesson-duration">Duration (minutes)</Label>
                <Input
                  id="lesson-duration"
                  type="number"
                  value={lessonForm.durationMinutes}
                  onChange={(e) => setLessonForm(prev => ({ ...prev, durationMinutes: parseInt(e.target.value) || 0 }))}
                  placeholder="0"
                />
              </div>
            </div>
            <div>
              <Label htmlFor="lesson-description">Description</Label>
              <Textarea
                id="lesson-description"
                value={lessonForm.description}
                onChange={(e) => setLessonForm(prev => ({ ...prev, description: e.target.value }))}
                placeholder="Enter lesson description"
              />
            </div>
            <div>
              <Label htmlFor="lesson-content">Content</Label>
              <Textarea
                id="lesson-content"
                value={lessonForm.content}
                onChange={(e) => setLessonForm(prev => ({ ...prev, content: e.target.value }))}
                placeholder="Enter lesson content"
                className="min-h-[100px]"
              />
            </div>
            <div>
              <Label htmlFor="lesson-video">Video URL</Label>
              <Input
                id="lesson-video"
                value={lessonForm.videoUrl}
                onChange={(e) => setLessonForm(prev => ({ ...prev, videoUrl: e.target.value }))}
                placeholder="Enter video URL"
              />
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Switch
                  id="lesson-published"
                  checked={lessonForm.isPublished}
                  onCheckedChange={(checked) => setLessonForm(prev => ({ ...prev, isPublished: checked }))}
                />
                <Label htmlFor="lesson-published">Publish lesson</Label>
              </div>
              <div className="flex items-center space-x-2">
                <Switch
                  id="lesson-preview"
                  checked={lessonForm.isPreview}
                  onCheckedChange={(checked) => setLessonForm(prev => ({ ...prev, isPreview: checked }))}
                />
                <Label htmlFor="lesson-preview">Preview lesson</Label>
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsCreatingLesson(false)}>
                Cancel
              </Button>
              <Button onClick={handleCreateLesson}>
                Create Lesson
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* Edit Modal */}
      {editingItem && (
        <Dialog open={!!editingItem} onOpenChange={() => setEditingItem(null)}>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>
                Edit {editingItem.type === 'module' ? 'Module' : editingItem.type === 'lesson' ? 'Lesson' : 'Resource'}
              </DialogTitle>
              <DialogDescription>
                Update the details for this {editingItem.type}.
              </DialogDescription>
            </DialogHeader>
            {/* Edit form content would go here */}
            <div className="flex justify-end gap-2 pt-4">
              <Button variant="outline" onClick={() => setEditingItem(null)}>
                Cancel
              </Button>
              <Button onClick={() => setEditingItem(null)}>
                Save Changes
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}