import { useState, useEffect, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Eye, EyeOff, Dumbbell, CheckCircle, X, Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card-enhanced";
import { toast } from "@/hooks/use-toast";
import { authApi, citiesApi, type City } from "@/lib/api";

const signupSchema = z.object({
  firstName: z.string().min(1, "First name is required"),
  lastName: z.string().min(1, "Last name is required"),
  username: z.string().min(3, "Username must be at least 3 characters"),
  email: z.string().email("Invalid email address"),
  password: z.string()
    .min(8, "Password must be at least 8 characters")
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, "Password must contain uppercase, lowercase, and number"),
  cityId: z.string().min(1, "Please select a city"),
  role: z.enum(["USER", "INSTRUCTOR", "ADMIN"]),
  avatarUrl: z.string().url().optional().or(z.literal("")),
});

type SignupForm = z.infer<typeof signupSchema>;

export const Signup = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [cities, setCities] = useState<City[]>([]);
  const [isSuccess, setIsSuccess] = useState(false);
  const [usernameStatus, setUsernameStatus] = useState<'idle' | 'checking' | 'available' | 'taken'>('idle');
  const [usernameCheckTimeout, setUsernameCheckTimeout] = useState<NodeJS.Timeout | null>(null);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<SignupForm>({
    resolver: zodResolver(signupSchema),
  });

  const watchedUsername = watch("username");

  // Debounced username validation
  const checkUsername = useCallback(async (username: string) => {
    if (username.length < 3) {
      setUsernameStatus('idle');
      return;
    }

    setUsernameStatus('checking');
    try {
      console.log('Making API call for username:', username);
      const response = await authApi.checkUsername(username);
      console.log('API response:', response);
      console.log('Response data type:', typeof response.data);
      console.log('Response data value:', response.data);
      
      // The backend returns a plain boolean, so response.data should be the boolean value
      const isTaken = response.data;
      console.log('Username check for', username, ':', isTaken);
      setUsernameStatus(isTaken ? 'taken' : 'available');
    } catch (error) {
      console.error('Username check error:', error);
      console.error('Error details:', error.response?.data);
      setUsernameStatus('idle');
    }
  }, []);

  // Effect to handle debounced username checking
  useEffect(() => {
    if (usernameCheckTimeout) {
      clearTimeout(usernameCheckTimeout);
    }

    if (watchedUsername && watchedUsername.length >= 3) {
      const timeout = setTimeout(() => {
        checkUsername(watchedUsername);
      }, 500);
      setUsernameCheckTimeout(timeout);
    } else {
      setUsernameStatus('idle');
    }

    return () => {
      if (usernameCheckTimeout) {
        clearTimeout(usernameCheckTimeout);
      }
    };
  }, [watchedUsername, checkUsername]);

  useEffect(() => {
    const fetchCities = async () => {
      try {
        const response = await citiesApi.getAll();
        setCities(response.data);
      } catch (error) {
        toast({
          variant: "destructive",
          title: "Error",
          description: "Failed to load cities",
        });
      }
    };
    fetchCities();
  }, []);

  const onSubmit = async (data: SignupForm) => {
    // Prevent submission if username is taken
    if (usernameStatus === 'taken') {
      toast({
        variant: "destructive",
        title: "Username taken",
        description: "Please choose a different username.",
      });
      return;
    }

    try {
      setIsLoading(true);
      const response = await authApi.signup({
        ...data,
        cityId: parseInt(data.cityId),
        avatarUrl: data.avatarUrl || undefined,
        role: data.role || "USER",
      });
      
      // Store user role and details in localStorage for immediate access
      const { role, username, email } = response.data;
      localStorage.setItem('user_role', role);
      localStorage.setItem('user_username', username);
      localStorage.setItem('user_email', email);
      
      setIsSuccess(true);
      toast({
        title: "Account created successfully!",
        description: "Please check your email to activate your account.",
      });
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "Signup failed",
        description: error.response?.data?.message || "Failed to create account",
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-hero p-4">
        <Card variant="neumorphic" className="w-full max-w-md bg-white/95 backdrop-blur-sm">
          <CardContent className="pt-6 text-center">
            <CheckCircle className="w-16 h-16 text-success mx-auto mb-4" />
            <h2 className="text-2xl font-bold mb-2">Account Created!</h2>
            <p className="text-muted-foreground mb-6">
              We've sent you an activation email. Please check your inbox and click the activation link to complete your registration.
            </p>
            <Button variant="hero" onClick={() => {
              const role = localStorage.getItem('user_role');
              const cleanRole = role?.replace('ROLE_', '') || 'USER';
              if (cleanRole === 'ADMIN') {
                navigate("/admin/dashboard");
              } else if (cleanRole === 'INSTRUCTOR') {
                navigate("/instructor/dashboard");
              } else {
                navigate("/dashboard");
              }
            }} className="w-full">
              Go to Dashboard
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-hero p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 mb-4">
            <div className="w-12 h-12 rounded-full bg-gradient-primary flex items-center justify-center">
              <Dumbbell className="w-6 h-6 text-white" />
            </div>
            <span className="text-2xl font-bold text-white">MoveMinds</span>
          </div>
          <h1 className="text-3xl font-bold text-white mb-2">Join MoveMinds</h1>
          <p className="text-white/80">Start your fitness journey today</p>
        </div>

        <Card variant="neumorphic" className="bg-white/95 backdrop-blur-sm">
          <CardHeader>
            <CardTitle>Create Account</CardTitle>
            <CardDescription>
              Fill in your details to get started
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="firstName">First Name</Label>
                  <Input
                    id="firstName"
                    placeholder="First Name"
                    {...register("firstName")}
                    className={errors.firstName ? "border-destructive" : ""}
                  />
                  {errors.firstName && (
                    <p className="text-xs text-destructive">
                      {errors.firstName.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="lastName">Last Name</Label>
                  <Input
                    id="lastName"
                    placeholder="Last Name"
                    {...register("lastName")}
                    className={errors.lastName ? "border-destructive" : ""}
                  />
                  {errors.lastName && (
                    <p className="text-xs text-destructive">
                      {errors.lastName.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <div className="relative">
                  <Input
                    id="username"
                    placeholder="Enter Username"
                    {...register("username")}
                    className={`${errors.username ? "border-destructive" : ""} ${
                      usernameStatus === 'taken' ? "border-destructive" : 
                      usernameStatus === 'available' ? "border-green-500" : ""
                    }`}
                  />
                  {usernameStatus === 'checking' && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary"></div>
                    </div>
                  )}
                  {usernameStatus === 'available' && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                      <Check className="h-4 w-4 text-green-500" />
                    </div>
                  )}
                  {usernameStatus === 'taken' && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                      <X className="h-4 w-4 text-destructive" />
                    </div>
                  )}
                </div>
                {errors.username && (
                  <p className="text-sm text-destructive">
                    {errors.username.message}
                  </p>
                )}
                {usernameStatus === 'taken' && (
                  <p className="text-sm text-destructive">
                    This username is already taken. Please choose another one.
                  </p>
                )}
                {usernameStatus === 'available' && (
                  <p className="text-sm text-green-600">
                    This username is available!
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="Enter your email"
                  {...register("email")}
                  className={errors.email ? "border-destructive" : ""}
                />
                {errors.email && (
                  <p className="text-sm text-destructive">
                    {errors.email.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="cityId">City</Label>
                <Select onValueChange={(value) => setValue("cityId", value)}>
                  <SelectTrigger className={errors.cityId ? "border-destructive" : ""}>
                    <SelectValue placeholder="Select your city" />
                  </SelectTrigger>
                  <SelectContent className="bg-white">
                    {cities.map((city) => (
                      <SelectItem key={city.id} value={city.id.toString()}>
                        {city.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {errors.cityId && (
                  <p className="text-sm text-destructive">
                    {errors.cityId.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="role">Account Type</Label>
                <Select onValueChange={(value) => setValue("role", value as "USER" | "INSTRUCTOR" | "ADMIN")} defaultValue="USER">
                  <SelectTrigger className={errors.role ? "border-destructive" : ""}>
                    <SelectValue placeholder="Select account type" />
                  </SelectTrigger>
                  <SelectContent className="bg-white">
                    <SelectItem value="USER">Regular User</SelectItem>
                    <SelectItem value="INSTRUCTOR">Fitness Instructor</SelectItem>
                    <SelectItem value="ADMIN">Administrator</SelectItem>
                  </SelectContent>
                </Select>
                {errors.role && (
                  <p className="text-sm text-destructive">
                    {errors.role.message}
                  </p>
                )}
                <p className="text-xs text-muted-foreground">
                  Choose your account type. Instructors can create fitness programs, and Admins have full system access.
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <div className="relative">
                  <Input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="Create a strong password"
                    {...register("password")}
                    className={errors.password ? "border-destructive pr-10" : "pr-10"}
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="absolute right-0 top-0 h-full px-3"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </Button>
                </div>
                {errors.password && (
                  <p className="text-sm text-destructive">
                    {errors.password.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="avatarUrl">Avatar URL (Optional)</Label>
                <Input
                  id="avatarUrl"
                  type="url"
                  placeholder="https://example.com/avatar.jpg"
                  {...register("avatarUrl")}
                  className={errors.avatarUrl ? "border-destructive" : ""}
                />
                {errors.avatarUrl && (
                  <p className="text-sm text-destructive">
                    {errors.avatarUrl.message}
                  </p>
                )}
              </div>

              <Button
                type="submit"
                variant="hero"
                size="lg"
                className="w-full"
                disabled={isLoading || usernameStatus === 'taken' || usernameStatus === 'checking'}
              >
                {isLoading ? "Creating Account..." : "Create Account"}
              </Button>
            </form>

            <div className="mt-6 text-center">
              <p className="text-sm text-muted-foreground">
                Already have an account?{" "}
                <Link
                  to="/login"
                  className="font-medium text-primary hover:text-primary-dark transition-colors"
                >
                  Sign in
                </Link>
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};