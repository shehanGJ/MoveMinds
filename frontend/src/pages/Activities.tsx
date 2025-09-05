import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { 
  Activity, 
  Plus, 
  Calendar, 
  Clock, 
  Target, 
  Download,
  Filter,
  Search,
  TrendingUp,
  BarChart3
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { toast } from "@/hooks/use-toast";
import { activitiesApi, type Activity as ActivityType } from "@/lib/api";

const activitySchema = z.object({
  activityType: z.string().min(1, "Activity type is required"),
  duration: z.number().min(1, "Duration must be at least 1 minute"),
  intensity: z.enum(["Low", "Moderate", "High"], {
    required_error: "Please select an intensity level",
  }),
  result: z.number().min(0, "Result must be a positive number").optional(),
  logDate: z.string().min(1, "Date is required"),
});

type ActivityFormData = z.infer<typeof activitySchema>;

const activityTypes = [
  "Running", "Walking", "Cycling", "Swimming", "Weight Training",
  "Yoga", "Pilates", "CrossFit", "Dancing", "Hiking", "Basketball",
  "Football", "Tennis", "Boxing", "Martial Arts", "Other"
];

const intensityLevels = ["Low", "Moderate", "High"];

export const Activities = () => {
  const [activities, setActivities] = useState<ActivityType[]>([]);
  const [filteredActivities, setFilteredActivities] = useState<ActivityType[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedType, setSelectedType] = useState<string>("all");
  const [selectedIntensity, setSelectedIntensity] = useState<string>("all");

  const form = useForm<ActivityFormData>({
    resolver: zodResolver(activitySchema),
    defaultValues: {
      logDate: new Date().toISOString().split('T')[0],
    },
  });

  useEffect(() => {
    fetchActivities();
  }, []);

  useEffect(() => {
    filterActivities();
  }, [activities, searchTerm, selectedType, selectedIntensity]);

  const fetchActivities = async () => {
    try {
      const response = await activitiesApi.getAll();
      setActivities(response.data);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to load activities",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const filterActivities = () => {
    let filtered = activities.filter((activity) => {
      const matchesSearch = activity.activityType.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesType = selectedType === "all" || activity.activityType === selectedType;
      const matchesIntensity = selectedIntensity === "all" || activity.intensity === selectedIntensity;
      
      return matchesSearch && matchesType && matchesIntensity;
    });

    // Sort by date (newest first)
    filtered.sort((a, b) => new Date(b.date || b.logDate || '').getTime() - new Date(a.date || a.logDate || '').getTime());
    
    setFilteredActivities(filtered);
  };

  const onSubmit = async (data: ActivityFormData) => {
    try {
      await activitiesApi.create({
        activityType: data.activityType,
        duration: data.duration,
        intensity: data.intensity,
        result: data.result || 0,
        logDate: data.logDate,
      });
      
      form.reset();
      setIsDialogOpen(false);
      fetchActivities();
      
      toast({
        title: "Success",
        description: "Activity logged successfully",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to log activity",
      });
    }
  };

  const downloadReport = async () => {
    try {
      const response = await activitiesApi.downloadPdf();
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'activity-report.pdf';
      link.click();
      window.URL.revokeObjectURL(url);
      
      toast({
        title: "Success",
        description: "Report downloaded successfully",
      });
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to download report",
      });
    }
  };

  const getIntensityColor = (intensity: string) => {
    switch (intensity.toLowerCase()) {
      case "low":
        return "bg-success/10 text-success border-success/20";
      case "moderate":
        return "bg-warning/10 text-warning border-warning/20";
      case "high":
        return "bg-destructive/10 text-destructive border-destructive/20";
      default:
        return "bg-muted/10 text-muted-foreground border-muted/20";
    }
  };

  const getTotalStats = () => {
    const totalActivities = activities.length;
    const totalDuration = activities.reduce((sum, activity) => sum + activity.duration, 0);
    const totalHours = Math.round(totalDuration / 60 * 10) / 10;
    const avgDuration = totalActivities > 0 ? Math.round(totalDuration / totalActivities) : 0;

    return { totalActivities, totalDuration, totalHours, avgDuration };
  };

  const stats = getTotalStats();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading activities...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold mb-2">My Activities</h1>
          <p className="text-muted-foreground">
            Track your fitness activities and monitor your progress
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={downloadReport}>
            <Download className="w-4 h-4 mr-2" />
            Download Report
          </Button>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button variant="hero">
                <Plus className="w-4 h-4 mr-2" />
                Log Activity
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Log New Activity</DialogTitle>
                <DialogDescription>
                  Record your fitness activity to track your progress
                </DialogDescription>
              </DialogHeader>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <div>
                  <Label htmlFor="activityType">Activity Type</Label>
                  <Select
                    value={form.watch("activityType")}
                    onValueChange={(value) => form.setValue("activityType", value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select activity type" />
                    </SelectTrigger>
                    <SelectContent>
                      {activityTypes.map((type) => (
                        <SelectItem key={type} value={type}>
                          {type}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {form.formState.errors.activityType && (
                    <p className="text-sm text-destructive mt-1">
                      {form.formState.errors.activityType.message}
                    </p>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="duration">Duration (minutes)</Label>
                    <Input
                      id="duration"
                      type="number"
                      {...form.register("duration", { valueAsNumber: true })}
                      placeholder="30"
                    />
                    {form.formState.errors.duration && (
                      <p className="text-sm text-destructive mt-1">
                        {form.formState.errors.duration.message}
                      </p>
                    )}
                  </div>

                  <div>
                    <Label htmlFor="intensity">Intensity</Label>
                    <Select
                      value={form.watch("intensity")}
                      onValueChange={(value) => form.setValue("intensity", value as "Low" | "Moderate" | "High")}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select intensity" />
                      </SelectTrigger>
                      <SelectContent>
                        {intensityLevels.map((level) => (
                          <SelectItem key={level} value={level}>
                            {level}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    {form.formState.errors.intensity && (
                      <p className="text-sm text-destructive mt-1">
                        {form.formState.errors.intensity.message}
                      </p>
                    )}
                  </div>
                </div>

                <div>
                  <Label htmlFor="result">Result (optional)</Label>
                  <Input
                    id="result"
                    type="number"
                    step="0.1"
                    {...form.register("result", { valueAsNumber: true })}
                    placeholder="e.g., 5.2 (miles, reps, etc.)"
                  />
                  {form.formState.errors.result && (
                    <p className="text-sm text-destructive mt-1">
                      {form.formState.errors.result.message}
                    </p>
                  )}
                </div>

                <div>
                  <Label htmlFor="logDate">Date</Label>
                  <Input
                    id="logDate"
                    type="date"
                    {...form.register("logDate")}
                  />
                  {form.formState.errors.logDate && (
                    <p className="text-sm text-destructive mt-1">
                      {form.formState.errors.logDate.message}
                    </p>
                  )}
                </div>

                <Button type="submit" className="w-full">
                  <Plus className="w-4 h-4 mr-2" />
                  Log Activity
                </Button>
              </form>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Total Activities</p>
                <p className="text-2xl font-bold">{stats.totalActivities}</p>
              </div>
              <Activity className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Total Hours</p>
                <p className="text-2xl font-bold">{stats.totalHours}</p>
              </div>
              <Clock className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Avg Duration</p>
                <p className="text-2xl font-bold">{stats.avgDuration}m</p>
              </div>
              <Target className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card variant="fitness">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">This Week</p>
                <p className="text-2xl font-bold">
                  {activities.filter(a => {
                    const activityDate = new Date(a.date);
                    const weekAgo = new Date();
                    weekAgo.setDate(weekAgo.getDate() - 7);
                    return activityDate >= weekAgo;
                  }).length}
                </p>
              </div>
              <TrendingUp className="w-8 h-8 text-primary" />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
      <Card variant="neumorphic">
        <CardContent className="p-6">
          <div className="flex flex-col lg:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search activities..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            
            <div className="flex flex-col sm:flex-row gap-4">
              <Select value={selectedType} onValueChange={setSelectedType}>
                <SelectTrigger className="w-full sm:w-40">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Activity Type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Types</SelectItem>
                  {activityTypes.map((type) => (
                    <SelectItem key={type} value={type}>
                      {type}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Select value={selectedIntensity} onValueChange={setSelectedIntensity}>
                <SelectTrigger className="w-full sm:w-40">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Intensity" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Levels</SelectItem>
                  {intensityLevels.map((level) => (
                    <SelectItem key={level} value={level}>
                      {level}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Activities List */}
      {filteredActivities.length > 0 ? (
        <div className="space-y-4">
          {filteredActivities.map((activity) => (
            <Card key={activity.id} variant="interactive">
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-gradient-primary/10 rounded-full flex items-center justify-center">
                      <Activity className="w-6 h-6 text-primary" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-lg">{activity.activityType}</h3>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <Calendar className="w-4 h-4" />
                          {new Date(activity.date).toLocaleDateString()}
                        </div>
                        <div className="flex items-center gap-1">
                          <Clock className="w-4 h-4" />
                          {activity.duration} minutes
                        </div>
                        {activity.result && activity.result > 0 && (
                          <div className="flex items-center gap-1">
                            <Target className="w-4 h-4" />
                            {activity.result}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                  <Badge className={getIntensityColor(activity.intensity)}>
                    {activity.intensity}
                  </Badge>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <Card variant="neumorphic" className="py-12">
          <CardContent className="text-center">
            <div className="w-16 h-16 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <Activity className="w-8 h-8 text-primary" />
            </div>
            <h3 className="text-lg font-semibold mb-2">No activities found</h3>
            <p className="text-muted-foreground mb-4">
              {activities.length === 0 
                ? "Start tracking your fitness activities to see them here."
                : "Try adjusting your search terms or filters to find more activities."
              }
            </p>
            {activities.length === 0 && (
              <Button variant="hero" onClick={() => setIsDialogOpen(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Log Your First Activity
              </Button>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
};
