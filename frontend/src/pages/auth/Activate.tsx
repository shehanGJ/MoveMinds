import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { CheckCircle, XCircle, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { authApi } from "@/lib/api";
import { toast } from "@/hooks/use-toast";

export const Activate = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const activateAccount = async () => {
      const token = searchParams.get('token');
      
      if (!token) {
        setStatus('error');
        setMessage('No activation token provided');
        return;
      }

      try {
        const response = await authApi.activate(token);
        setStatus('success');
        setMessage(response.data || 'Account activated successfully!');
        
        toast({
          title: "Account Activated!",
          description: "Your account has been successfully activated. You can now log in.",
        });
      } catch (error: any) {
        setStatus('error');
        setMessage(error.response?.data || 'Failed to activate account. The token may be invalid or expired.');
        
        toast({
          variant: "destructive",
          title: "Activation Failed",
          description: "Unable to activate your account. Please try signing up again.",
        });
      }
    };

    activateAccount();
  }, [searchParams]);

  const handleGoToLogin = () => {
    navigate('/login');
  };

  const handleGoToSignup = () => {
    navigate('/signup');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-hero p-4">
      <Card variant="neumorphic" className="w-full max-w-md bg-white/95 backdrop-blur-sm">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Account Activation</CardTitle>
          <CardDescription>
            {status === 'loading' && 'Activating your account...'}
            {status === 'success' && 'Your account has been activated!'}
            {status === 'error' && 'Activation failed'}
          </CardDescription>
        </CardHeader>
        <CardContent className="text-center">
          {status === 'loading' && (
            <div className="flex flex-col items-center gap-4">
              <Loader2 className="w-12 h-12 text-primary animate-spin" />
              <p className="text-muted-foreground">Please wait while we activate your account...</p>
            </div>
          )}

          {status === 'success' && (
            <div className="flex flex-col items-center gap-4">
              <CheckCircle className="w-16 h-16 text-success" />
              <div>
                <h3 className="text-lg font-semibold text-success mb-2">Activation Successful!</h3>
                <p className="text-muted-foreground mb-4">
                  {message}
                </p>
                <p className="text-sm text-muted-foreground mb-6">
                  You can now log in to your account and start your fitness journey with MoveMinds.
                </p>
              </div>
              <Button 
                variant="hero" 
                onClick={handleGoToLogin} 
                className="w-full"
              >
                Go to Login
              </Button>
            </div>
          )}

          {status === 'error' && (
            <div className="flex flex-col items-center gap-4">
              <XCircle className="w-16 h-16 text-destructive" />
              <div>
                <h3 className="text-lg font-semibold text-destructive mb-2">Activation Failed</h3>
                <p className="text-muted-foreground mb-4">
                  {message}
                </p>
                <p className="text-sm text-muted-foreground mb-6">
                  This could happen if the activation link has expired or is invalid.
                </p>
              </div>
              <div className="flex gap-2 w-full">
                <Button 
                  variant="outline" 
                  onClick={handleGoToSignup} 
                  className="flex-1"
                >
                  Sign Up Again
                </Button>
                <Button 
                  variant="hero" 
                  onClick={handleGoToLogin} 
                  className="flex-1"
                >
                  Try Login
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
