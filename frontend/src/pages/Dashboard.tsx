import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { 
  Dumbbell, 
  Activity as ActivityIcon, 
  MessageCircle, 
  Calendar,
  TrendingUp,
  Clock,
  Target,
  Award
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { toast } from "@/hooks/use-toast";
import { userApi, userProgramsApi, activitiesApi, messagesApi, type User, type UserProgram, type Activity } from "@/lib/api";

export const Dashboard = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [userPrograms, setUserPrograms] = useState<UserProgram[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [conversations, setConversations] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check user role and redirect if needed
    const userRole = localStorage.getItem('user_role');
    console.log('Dashboard: User role is:', userRole);
    console.log('Dashboard: Current URL:', window.location.pathname);
    
    const cleanRole = userRole?.replace('ROLE_', '') || 'USER';
    if (cleanRole === 'ADMIN') {
      console.log('Dashboard: Redirecting admin to admin dashboard');
      navigate('/admin');
      return;
    } else if (cleanRole === 'INSTRUCTOR') {
      console.log('Dashboard: Redirecting instructor to instructor dashboard');
      navigate('/instructor/dashboard');
      return;
    }

    const fetchData = async () => {
      try {
        const [userResponse, userProgramsResponse, activitiesResponse, conversationsResponse] = await Promise.all([
          userApi.getProfile(),
          userProgramsApi.getUserPrograms(),
          activitiesApi.getAll(),
          messagesApi.getConversations(),
        ]);
        
        setUser(userResponse.data);
        
        // Handle paginated user programs response
        const userProgramsData = userProgramsResponse.data?.content || [];
        console.log('User Programs Response:', userProgramsResponse.data);
        setUserPrograms(userProgramsData);
        
        // Handle activities response
        const activitiesData = activitiesResponse.data || [];
        console.log('Activities Response:', activitiesResponse.data);
        setActivities(activitiesData);
        
        // Handle conversations response
        const conversationsData = conversationsResponse.data || [];
        console.log('Conversations Response:', conversationsResponse.data);
        setConversations(conversationsData);
        
      } catch (error) {
        console.error('Dashboard fetch error:', error);
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to load dashboard data",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  const quickActions = [
    {
      title: "Browse Programs",
      description: "Discover new fitness programs",
      icon: Dumbbell,
      href: "/programs",
      variant: "hero" as const,
    },
    {
      title: "My Programs",
      description: "View enrolled programs",
      icon: Calendar,
      href: "/dashboard/my-programs",
      variant: "fitness" as const,
    },
    {
      title: "Log Activity",
      description: "Record your workout",
      icon: ActivityIcon,
      href: "/dashboard/activities",
      variant: "gradient" as const,
    },
    {
      title: "Messages",
      description: "Check your messages",
      icon: MessageCircle,
      href: "/dashboard/messages",
      variant: "neumorphic" as const,
    },
  ];

  // Calculate real stats from API data
  const activePrograms = userPrograms.filter(up => up.status === 'ACTIVE').length;
  const totalActivities = activities.length;
  const totalHours = Math.round(activities.reduce((sum, activity) => sum + (activity.duration || 0), 0) / 60);
  const achievements = Math.min(12, Math.floor(totalActivities / 2) + Math.floor(activePrograms * 2));

  const stats = [
    {
      title: "Programs Enrolled",
      value: activePrograms.toString(),
      icon: Dumbbell,
      change: `${userPrograms.length} total`,
      trend: "up",
    },
    {
      title: "Activities Logged",
      value: totalActivities.toString(),
      icon: ActivityIcon,
      change: `${activities.filter(a => {
        const activityDate = new Date(a.logDate);
        const weekAgo = new Date();
        weekAgo.setDate(weekAgo.getDate() - 7);
        return activityDate >= weekAgo;
      }).length} this week`,
      trend: "up",
    },
    {
      title: "Hours Trained",
      value: totalHours.toString(),
      icon: Clock,
      change: `${Math.round(activities.filter(a => {
        const activityDate = new Date(a.logDate);
        const weekAgo = new Date();
        weekAgo.setDate(weekAgo.getDate() - 7);
        return activityDate >= weekAgo;
      }).reduce((sum, activity) => sum + (activity.duration || 0), 0) / 60)} this week`,
      trend: "up",
    },
    {
      title: "Achievements",
      value: achievements.toString(),
      icon: Award,
      change: "Based on progress",
      trend: "up",
    },
  ];

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-pulse-soft text-center">
          <div className="w-12 h-12 bg-gradient-primary rounded-full mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  // Debug info
  console.log('Dashboard state:', { user, userPrograms, activities, conversations });

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
        <div className="flex items-center gap-4">
          <Avatar className="w-20 h-20 ring-2 ring-primary/30 shadow-subtle">
            <AvatarImage src={user?.avatarUrl} alt={user?.firstName} />
            <AvatarFallback className="bg-gradient-primary text-white text-xl font-bold">
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </AvatarFallback>
          </Avatar>
          <div>
            <h1 className="text-3xl font-bold bg-gradient-primary bg-clip-text text-transparent">
              Welcome back, {user?.firstName}! 👋
            </h1>
            <p className="text-muted-foreground text-lg">
              Ready to crush your fitness goals today?
            </p>
          </div>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" asChild>
            <Link to="/dashboard/profile">View Profile</Link>
          </Button>
          <Button variant="hero" asChild>
            <Link to="/programs">Browse Programs</Link>
          </Button>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title} variant="neumorphic" className="hover:shadow-glow transition-all duration-300">
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-semibold text-muted-foreground">
                      {stat.title}
                    </p>
                    <p className="text-3xl font-bold text-foreground">{stat.value}</p>
                  </div>
                  <div className="w-14 h-14 bg-gradient-primary/20 rounded-full flex items-center justify-center shadow-subtle">
                    <Icon className="w-7 h-7 text-primary" />
                  </div>
                </div>
                <div className="mt-4 flex items-center gap-1">
                  <TrendingUp className="w-4 h-4 text-primary" />
                  <span className="text-sm text-primary font-semibold">
                    {stat.change}
                  </span>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-2xl font-bold mb-6 bg-gradient-primary bg-clip-text text-transparent">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {quickActions.map((action) => {
            const Icon = action.icon;
            return (
              <Card
                key={action.title}
                variant="neumorphic"
                className="group hover:shadow-glow transition-all duration-300"
              >
                <CardContent className="p-6 text-center">
                  <div className="w-16 h-16 bg-gradient-primary/20 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:bg-gradient-primary/30 transition-all duration-200 shadow-subtle">
                    <Icon className="w-8 h-8 text-primary" />
                  </div>
                  <h3 className="font-bold mb-2 text-foreground">{action.title}</h3>
                  <p className="text-sm text-muted-foreground mb-4">
                    {action.description}
                  </p>
                  <Button variant="hero" size="sm" asChild className="w-full">
                    <Link to={action.href}>Get Started</Link>
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Recent Activities */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Recent Activities</h2>
          <Button variant="ghost" size="sm" asChild>
            <Link to="/dashboard/activities">View All</Link>
          </Button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {activities.length > 0 ? (
            activities.slice(0, 3).map((activity) => (
              <Card key={activity.id} variant="neumorphic" className="group cursor-pointer hover:shadow-glow transition-all duration-300">
                <CardContent className="p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <Badge variant="secondary" className="text-xs">
                      {activity.activityType}
                    </Badge>
                    <span className="text-xs text-muted-foreground">
                      {new Date(activity.logDate).toLocaleDateString()}
                    </span>
                  </div>
                  <h3 className="font-semibold mb-2">
                    {activity.activityType} Session
                  </h3>
                  <div className="space-y-1 text-sm text-muted-foreground">
                    <p>Duration: {activity.duration} minutes</p>
                    <p>Intensity: {activity.intensity}</p>
                    {activity.result && <p>Result: {activity.result}</p>}
                  </div>
                </CardContent>
              </Card>
            ))
          ) : (
            <Card variant="neumorphic" className="col-span-full">
              <CardContent className="p-8 text-center">
                <div className="w-16 h-16 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                  <ActivityIcon className="w-8 h-8 text-primary" />
                </div>
                <h3 className="font-semibold mb-2">Start Your Journey</h3>
                <p className="text-muted-foreground mb-4">
                  Log your first activity to see your progress here.
                </p>
                <Button variant="hero" asChild>
                  <Link to="/dashboard/activities">Log Activity</Link>
                </Button>
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      {/* Recent Messages */}
      {conversations.length > 0 && (
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold">Recent Messages</h2>
            <Button variant="ghost" size="sm" asChild>
              <Link to="/dashboard/messages">View All</Link>
            </Button>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {conversations.slice(0, 4).map((conversation) => (
              <Card key={conversation.id} variant="neumorphic" className="group cursor-pointer hover:shadow-glow transition-all duration-300">
                <CardContent className="p-3">
                  <div className="flex items-center gap-2 mb-2">
                    <Badge variant="secondary" className="text-xs">
                      Message
                    </Badge>
                    <span className="text-xs text-muted-foreground">
                      {new Date(conversation.lastMessageAt).toLocaleDateString()}
                    </span>
                  </div>
                  <h3 className="font-semibold mb-1 text-sm">
                    {conversation.otherUserFirstName} {conversation.otherUserLastName}
                  </h3>
                  <p className="text-xs text-muted-foreground line-clamp-2">
                    {conversation.lastMessageContent}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};