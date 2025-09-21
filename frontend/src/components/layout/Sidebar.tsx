import { Link, useLocation } from "react-router-dom";
import { 
  Home, 
  Dumbbell, 
  Activity, 
  MessageCircle, 
  User, 
  Calendar,
  BarChart3,
  Settings,
  X,
  Users,
  Shield,
  GraduationCap,
  LayoutDashboard
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Card } from "@/components/ui/card-enhanced";

interface SidebarProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

// Default user items
const sidebarItems = [
  { label: "Dashboard", href: "/dashboard", icon: Home },
  { label: "Programs", href: "/programs", icon: Dumbbell },
  { label: "My Programs", href: "/dashboard/my-programs", icon: Calendar },
  { label: "Activities", href: "/dashboard/activities", icon: Activity },
  { label: "Messages", href: "/dashboard/messages", icon: MessageCircle },
  { label: "Profile", href: "/dashboard/profile", icon: User },
];

// Admin specific items
const adminItems = [
  { label: "Admin Dashboard", href: "/admin", icon: LayoutDashboard },
  { label: "User Management", href: "/admin/users", icon: Users },
  { label: "Programs", href: "/admin/programs", icon: Dumbbell },
  { label: "System Analytics", href: "/admin/analytics", icon: BarChart3 },
  { label: "Messages", href: "/dashboard/messages", icon: MessageCircle },
  { label: "Profile", href: "/dashboard/profile", icon: User },
];

// Instructor specific items
const instructorItems = [
  { label: "Instructor Dashboard", href: "/instructor/dashboard", icon: GraduationCap },
  { label: "My Programs", href: "/instructor/programs", icon: Dumbbell },
  { label: "Students", href: "/instructor/students", icon: Users },
  { label: "Analytics", href: "/instructor/analytics", icon: BarChart3 },
  { label: "Schedule", href: "/instructor/schedule", icon: Calendar },
];

export const Sidebar = ({ open, onOpenChange }: SidebarProps) => {
  const location = useLocation();
  const userRole = localStorage.getItem('user_role') || 'USER';
  
  const getItemsToShow = () => {
    const cleanRole = userRole.replace('ROLE_', '');
    if (cleanRole === 'ADMIN') {
      return adminItems; // Only show admin-specific items
    } else if (cleanRole === 'INSTRUCTOR') {
      return instructorItems; // Only show instructor-specific items
    }
    return sidebarItems;
  };
  
  const itemsToShow = getItemsToShow();

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div 
          className="fixed inset-0 z-40 bg-black/20 backdrop-blur-sm md:hidden" 
          onClick={() => onOpenChange(false)}
        />
      )}
      
      {/* Sidebar */}
      <aside className={cn(
        "fixed left-0 top-16 z-50 h-[calc(100vh-4rem)] w-64 transform transition-transform duration-200 ease-in-out md:relative md:top-0 md:h-screen md:translate-x-0",
        open ? "translate-x-0" : "-translate-x-full"
      )}>
        <Card variant="neumorphic" padding="sm" className="h-full border-r border-l-0 rounded-none md:rounded-r-xl">
          <div className="flex items-center justify-between p-4 border-b md:hidden">
            <span className="font-semibold">Menu</span>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onOpenChange(false)}
            >
              <X className="h-5 w-5" />
            </Button>
          </div>
          
          <nav className="p-4 space-y-2">
            {itemsToShow.map((item) => {
              const isActive = location.pathname === item.href;
              const Icon = item.icon;
              
              return (
                <Link
                  key={item.href}
                  to={item.href}
                  onClick={() => onOpenChange(false)}
                  className={cn(
                    "flex items-center gap-3 px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200",
                    isActive
                      ? "bg-gradient-primary text-primary-foreground shadow-card"
                      : "text-muted-foreground hover:text-foreground hover:bg-muted/50"
                  )}
                >
                  <Icon className="h-4 w-4" />
                  {item.label}
                </Link>
              );
            })}
          </nav>
          
          <div className="absolute bottom-4 left-4 right-4">
            <Card variant="gradient" padding="sm" className="text-center">
              <p className="text-xs font-medium">Upgrade to Pro</p>
              <p className="text-xs text-muted-foreground mt-1">
                Get unlimited access to all features
              </p>
              <Button variant="fitness" size="sm" className="mt-2 w-full">
                Upgrade Now
              </Button>
            </Card>
          </div>
        </Card>
      </aside>
    </>
  );
};