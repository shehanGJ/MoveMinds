import { Link } from "react-router-dom";
import { Dumbbell, TrendingUp, Users, Award, ArrowRight, Star, CheckCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { Badge } from "@/components/ui/badge";
import { citiesApi } from "@/lib/api";
import { useEffect, useState } from "react";

const Index = () => {
  const [apiConnected, setApiConnected] = useState<boolean | null>(null);

  useEffect(() => {
    // Test API connection
    citiesApi.getAll()
      .then(() => setApiConnected(true))
      .catch(() => setApiConnected(false));
  }, []);

  const features = [
    {
      icon: Dumbbell,
      title: "Personalized Programs",
      description: "Discover fitness programs tailored to your goals and fitness level."
    },
    {
      icon: TrendingUp,
      title: "Track Progress",
      description: "Monitor your activities and see your improvement over time."
    },
    {
      icon: Users,
      title: "Community Support",
      description: "Connect with trainers and other fitness enthusiasts."
    },
    {
      icon: Award,
      title: "Achievements",
      description: "Earn badges and rewards as you reach your fitness milestones."
    }
  ];

  const testimonials = [
    {
      name: "Sarah Johnson",
      role: "Fitness Enthusiast",
      content: "MoveMinds has completely transformed my workout routine. The personalized programs are amazing!",
      rating: 5
    },
    {
      name: "Mike Chen",
      role: "Personal Trainer", 
      content: "As a trainer, I love how I can create and share programs with my clients through this platform.",
      rating: 5
    }
  ];

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-hero py-20 lg:py-32">
        <div className="absolute inset-0 bg-black/10"></div>
        <div className="relative container mx-auto px-4 text-center">
          <div className="flex flex-col items-center gap-4 mb-6">
            <Badge className="bg-white/20 text-white border-white/30">
              🎉 Welcome to MoveMinds
            </Badge>
            {apiConnected === true && (
              <Badge className="bg-green-500/20 text-green-100 border-green-300/30 flex items-center gap-2">
                <CheckCircle className="w-4 h-4" />
                Backend Connected
              </Badge>
            )}
            {apiConnected === false && (
              <Badge className="bg-red-500/20 text-red-100 border-red-300/30">
                Backend Disconnected
              </Badge>
            )}
          </div>
          <h1 className="text-4xl lg:text-6xl font-bold text-white mb-6 animate-fade-in">
            Transform Your 
            <span className="block bg-gradient-to-r from-white to-white/80 bg-clip-text text-transparent">
              Fitness Journey
            </span>
          </h1>
          <p className="text-lg lg:text-xl text-white/90 mb-8 max-w-2xl mx-auto animate-slide-up">
            Join thousands of fitness enthusiasts using MoveMinds to reach their goals with personalized programs, progress tracking, and community support.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center animate-scale-in">
            <Button variant="hero" size="xl" asChild className="bg-white text-primary hover:bg-white/90">
              <Link to="/signup">
                Get Started Free
                <ArrowRight className="ml-2 w-5 h-5" />
              </Link>
            </Button>
            <Button
              variant="outline"
              size="xl"
              className="border-white text-white bg-transparent hover:bg-transparent hover:shadow-elevated transform hover:scale-[1.05] active:scale-[0.95]"
              asChild
            >
              <Link to="/programs">
                Browse Programs
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-background">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl lg:text-4xl font-bold mb-4">
              Everything You Need for 
              <span className="bg-gradient-primary bg-clip-text text-transparent"> Success</span>
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Our comprehensive platform provides all the tools you need to achieve your fitness goals.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <Card 
                  key={feature.title} 
                  variant="fitness" 
                  className="text-center group hover:scale-105 transition-all duration-200"
                  style={{ animationDelay: `${index * 100}ms` }}
                >
                  <CardContent className="pt-6">
                    <div className="w-16 h-16 bg-gradient-primary rounded-full flex items-center justify-center mx-auto mb-4 group-hover:shadow-elevated transition-all duration-200">
                      <Icon className="w-8 h-8 text-white" />
                    </div>
                    <h3 className="text-lg font-semibold mb-3">{feature.title}</h3>
                    <p className="text-muted-foreground text-sm leading-relaxed">
                      {feature.description}
                    </p>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section className="py-20 bg-gradient-secondary/20">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl lg:text-4xl font-bold mb-4">
              What Our Users Say
            </h2>
            <p className="text-lg text-muted-foreground">
              Join thousands of satisfied users who have transformed their fitness.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            {testimonials.map((testimonial, index) => (
              <Card 
                key={testimonial.name} 
                variant="elevated" 
                className="animate-fade-in"
                style={{ animationDelay: `${index * 200}ms` }}
              >
                <CardContent className="p-6">
                  <div className="flex items-center gap-1 mb-4">
                    {[...Array(testimonial.rating)].map((_, i) => (
                      <Star key={i} className="w-5 h-5 fill-accent text-accent" />
                    ))}
                  </div>
                  <p className="text-muted-foreground mb-4 italic">
                    "{testimonial.content}"
                  </p>
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-gradient-primary rounded-full flex items-center justify-center">
                      <span className="text-white font-semibold text-sm">
                        {testimonial.name.split(' ').map(n => n[0]).join('')}
                      </span>
                    </div>
                    <div>
                      <p className="font-semibold">{testimonial.name}</p>
                      <p className="text-sm text-muted-foreground">{testimonial.role}</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-primary">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl lg:text-4xl font-bold text-white mb-6">
            Ready to Start Your Fitness Journey?
          </h2>
          <p className="text-lg text-white/90 mb-8 max-w-2xl mx-auto">
            Join MoveMinds today and get access to personalized programs, progress tracking, and a supportive community.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button variant="fitness" size="xl" asChild className="bg-white text-primary hover:bg-white/90">
              <Link to="/signup">
                Start Free Trial
                <ArrowRight className="ml-2 w-5 h-5" />
              </Link>
            </Button>
            <Button
              variant="outline"
              size="xl"
              className="border-white text-white bg-transparent hover:bg-transparent hover:shadow-elevated transform hover:scale-[1.05] active:scale-[0.95]"
              asChild
            >
              <Link to="/login">
                Already a member? Sign In
              </Link>
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Index;
