import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { 
  Dumbbell, 
  Activity, 
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
import { userApi, newsApi, type User, type NewsItem } from "@/lib/api";

export const Dashboard = () => {
  const [user, setUser] = useState<User | null>(null);
  const [news, setNews] = useState<NewsItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [userResponse, newsResponse] = await Promise.all([
          userApi.getProfile(),
          newsApi.getAll(),
        ]);
        
        setUser(userResponse.data);
        setNews(newsResponse.data.slice(0, 3)); // Get latest 3 news items
      } catch (error) {
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
      href: "/my-programs",
      variant: "fitness" as const,
    },
    {
      title: "Log Activity",
      description: "Record your workout",
      icon: Activity,
      href: "/activities/new",
      variant: "gradient" as const,
    },
    {
      title: "Messages",
      description: "Check your messages",
      icon: MessageCircle,
      href: "/messages",
      variant: "neumorphic" as const,
    },
  ];

  const stats = [
    {
      title: "Programs Enrolled",
      value: "3",
      icon: Dumbbell,
      change: "+1 this week",
      trend: "up",
    },
    {
      title: "Activities Logged",
      value: "24",
      icon: Activity,
      change: "+4 this week",
      trend: "up",
    },
    {
      title: "Hours Trained",
      value: "36",
      icon: Clock,
      change: "+8 this week",
      trend: "up",
    },
    {
      title: "Achievements",
      value: "12",
      icon: Award,
      change: "+2 this month",
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

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
        <div className="flex items-center gap-4">
          <Avatar className="w-16 h-16">
            <AvatarImage src={user?.avatarUrl} alt={user?.firstName} />
            <AvatarFallback className="bg-gradient-primary text-white text-lg">
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </AvatarFallback>
          </Avatar>
          <div>
            <h1 className="text-2xl font-bold">
              Welcome back, {user?.firstName}! 👋
            </h1>
            <p className="text-muted-foreground">
              Ready to crush your fitness goals today?
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" asChild>
            <Link to="/profile">View Profile</Link>
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
            <Card key={stat.title} variant="fitness" className="relative overflow-hidden">
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-muted-foreground">
                      {stat.title}
                    </p>
                    <p className="text-2xl font-bold">{stat.value}</p>
                  </div>
                  <div className="w-12 h-12 bg-gradient-primary/10 rounded-full flex items-center justify-center">
                    <Icon className="w-6 h-6 text-primary" />
                  </div>
                </div>
                <div className="mt-4 flex items-center gap-1">
                  <TrendingUp className="w-4 h-4 text-success" />
                  <span className="text-sm text-success font-medium">
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
        <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {quickActions.map((action) => {
            const Icon = action.icon;
            return (
              <Card
                key={action.title}
                variant="interactive"
                className="group hover:scale-105 transition-all duration-200"
              >
                <CardContent className="p-6 text-center">
                  <div className="w-12 h-12 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:bg-gradient-primary/20 transition-colors">
                    <Icon className="w-6 h-6 text-primary" />
                  </div>
                  <h3 className="font-semibold mb-2">{action.title}</h3>
                  <p className="text-sm text-muted-foreground mb-4">
                    {action.description}
                  </p>
                  <Button variant={action.variant} size="sm" asChild className="w-full">
                    <Link to={action.href}>Get Started</Link>
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Latest News */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Latest News</h2>
          <Button variant="ghost" size="sm" asChild>
            <Link to="/news">View All</Link>
          </Button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {news.length > 0 ? (
            news.map((item) => (
              <Card key={item.id} variant="elevated" className="group cursor-pointer">
                <CardContent className="p-0">
                  {item.imageUrl && (
                    <div className="aspect-video bg-gradient-secondary rounded-t-xl overflow-hidden">
                      <img
                        src={item.imageUrl}
                        alt={item.title}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                      />
                    </div>
                  )}
                  <div className="p-4">
                    <div className="flex items-center gap-2 mb-2">
                      <Badge variant="secondary" className="text-xs">
                        News
                      </Badge>
                      <span className="text-xs text-muted-foreground">
                        {new Date(item.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                    <h3 className="font-semibold mb-2 line-clamp-2">
                      {item.title}
                    </h3>
                    <p className="text-sm text-muted-foreground line-clamp-3">
                      {item.content}
                    </p>
                  </div>
                </CardContent>
              </Card>
            ))
          ) : (
            <Card variant="neumorphic" className="col-span-full">
              <CardContent className="p-8 text-center">
                <div className="w-16 h-16 bg-gradient-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Target className="w-8 h-8 text-primary" />
                </div>
                <h3 className="font-semibold mb-2">Stay Updated</h3>
                <p className="text-muted-foreground">
                  News and updates will appear here as they become available.
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
};