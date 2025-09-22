import { useState } from "react";
import { Outlet } from "react-router-dom";
import { Header } from "./Header";
import { Sidebar } from "./Sidebar";
import { useAuth } from "@/lib/auth";
import { useDropdownLock } from "@/hooks/use-dropdown-lock";

export const Layout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { isAuthenticated } = useAuth();
  
  // Use dropdown lock to prevent layout shifts
  useDropdownLock();

  return (
    <div className="min-h-screen bg-background">
      <Header onMenuToggle={() => setSidebarOpen(!sidebarOpen)} />
      
      <div className="flex">
        {isAuthenticated && (
          <Sidebar 
            open={sidebarOpen} 
            onOpenChange={setSidebarOpen}
          />
        )}
        
        <main className="flex-1 overflow-hidden md:ml-64 pt-16">
          <div className="container mx-auto p-4 md:p-6">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};