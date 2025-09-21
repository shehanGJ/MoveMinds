import { Bell, Menu, User, LogOut, Settings } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useAuth } from "@/lib/auth";
import { Link } from "react-router-dom";

interface HeaderProps {
  onMenuToggle?: () => void;
}

export const Header = ({ onMenuToggle }: HeaderProps) => {
  const { isAuthenticated, logout } = useAuth();
  
  // Get user role for role-based navigation
  const userRole = localStorage.getItem('user_role') || 'USER';
  
  // Get the appropriate dashboard URL based on role
  const getDashboardUrl = () => {
    const cleanRole = userRole.replace('ROLE_', '');
    if (cleanRole === 'ADMIN') return '/admin';
    if (cleanRole === 'INSTRUCTOR') return '/instructor/dashboard';
    return '/dashboard';
  };
  
  const dashboardUrl = getDashboardUrl();
  console.log('Header: User role is:', userRole);
  console.log('Header: Dashboard URL is:', dashboardUrl);

  return (
    <header className="sticky top-0 z-50 w-full border-b border-border bg-gradient-card/95 backdrop-blur supports-[backdrop-filter]:bg-gradient-card/60 shadow-card">
      <div className="container flex h-16 items-center justify-between px-4">
        <div className="flex items-center gap-4">
          {onMenuToggle && (
            <Button
              variant="ghost"
              size="icon"
              onClick={onMenuToggle}
              className="md:hidden"
            >
              <Menu className="h-5 w-5" />
            </Button>
          )}
          
          <Link 
            to="/" 
            className="flex items-center space-x-3 font-bold text-2xl bg-gradient-primary bg-clip-text text-transparent"
          >
            <div className="w-10 h-10 rounded-full bg-gradient-primary flex items-center justify-center shadow-subtle">
              <span className="text-white font-bold text-lg">M</span>
            </div>
            MoveMinds
          </Link>
        </div>

        <nav className="hidden md:flex items-center gap-8">
          <Link 
            to="/programs" 
            className="text-sm font-semibold text-muted-foreground hover:text-primary transition-all duration-200 hover:shadow-subtle px-3 py-2 rounded-lg hover:bg-primary/10"
          >
            Programs
          </Link>
          {isAuthenticated && (
            <>
              <Link 
                to={dashboardUrl} 
                className="text-sm font-semibold text-muted-foreground hover:text-primary transition-all duration-200 hover:shadow-subtle px-3 py-2 rounded-lg hover:bg-primary/10"
              >
                Dashboard
              </Link>
              {userRole === 'USER' && (
                <Link 
                  to="/dashboard/activities" 
                  className="text-sm font-semibold text-muted-foreground hover:text-primary transition-all duration-200 hover:shadow-subtle px-3 py-2 rounded-lg hover:bg-primary/10"
                >
                  Activities
                </Link>
              )}
              <Link 
                to="/dashboard/messages" 
                className="text-sm font-semibold text-muted-foreground hover:text-primary transition-all duration-200 hover:shadow-subtle px-3 py-2 rounded-lg hover:bg-primary/10"
              >
                Messages
              </Link>
            </>
          )}
        </nav>

        <div className="flex items-center gap-2">
          {isAuthenticated ? (
            <>
              <Button variant="ghost" size="icon" className="relative hover:bg-primary/10">
                <Bell className="h-5 w-5" />
                <span className="absolute top-2 right-2 w-2 h-2 bg-primary rounded-full shadow-subtle"></span>
              </Button>
              
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="relative h-10 w-10 rounded-full hover:bg-primary/10">
                    <Avatar className="h-10 w-10 ring-2 ring-primary/30 shadow-subtle">
                      <AvatarImage src="" alt="User" />
                      <AvatarFallback className="bg-gradient-primary text-white font-bold">
                        <User className="h-5 w-5" />
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56 bg-gradient-card border-border shadow-elevated" align="end">
                  <DropdownMenuItem asChild>
                    <Link to="/dashboard/profile" className="flex items-center">
                      <User className="mr-2 h-4 w-4" />
                      Profile
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/dashboard/settings" className="flex items-center">
                      <Settings className="mr-2 h-4 w-4" />
                      Settings
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={logout} className="text-destructive">
                    <LogOut className="mr-2 h-4 w-4" />
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          ) : (
            <div className="flex items-center gap-3">
              <Button variant="ghost" asChild className="hover:bg-primary/10">
                <Link to="/login">Login</Link>
              </Button>
              <Button variant="hero" asChild>
                <Link to="/signup">Sign Up</Link>
              </Button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};