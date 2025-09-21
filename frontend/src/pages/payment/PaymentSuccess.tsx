import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle, ArrowRight, Home } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { paymentApi } from "@/lib/api";

export default function PaymentSuccess() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [isProcessing, setIsProcessing] = useState(true);
  const [isCompleted, setIsCompleted] = useState(false);

  const orderId = searchParams.get("order_id");
  const programId = searchParams.get("program_id");

  useEffect(() => {
    const completePayment = async () => {
      if (orderId && programId) {
        try {
          await paymentApi.completePayment(orderId, programId);
          setIsCompleted(true);
          toast({
            title: "Payment Successful!",
            description: "You have been successfully enrolled in the program.",
          });
        } catch (error: any) {
          console.error("Error completing payment:", error);
          toast({
            title: "Payment Error",
            description: error.response?.data?.message || "Failed to complete payment",
            variant: "destructive",
          });
        } finally {
          setIsProcessing(false);
        }
      } else {
        setIsProcessing(false);
        toast({
          title: "Invalid Payment",
          description: "Missing payment information",
          variant: "destructive",
        });
      }
    };

    completePayment();
  }, [orderId, programId]);

  if (isProcessing) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted">
        <Card variant="neumorphic" className="w-full max-w-md">
          <CardContent className="pt-6">
            <div className="flex flex-col items-center space-y-4">
              <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
              <p className="text-muted-foreground">Processing your payment...</p>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted">
      <Card variant="neumorphic" className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            <CheckCircle className="w-16 h-16 text-success" />
          </div>
          <CardTitle className="text-2xl">
            {isCompleted ? "Payment Successful!" : "Payment Received"}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="text-center space-y-2">
            <p className="text-muted-foreground">
              {isCompleted 
                ? "You have been successfully enrolled in the program. You can now start learning!"
                : "Your payment has been received and is being processed."
              }
            </p>
            {orderId && (
              <p className="text-sm text-muted-foreground">
                Order ID: {orderId}
              </p>
            )}
          </div>

          <div className="flex flex-col gap-3">
            {isCompleted && programId && (
              <Button 
                onClick={() => navigate(`/dashboard/program/${programId}/learn`)}
                className="w-full"
              >
                <ArrowRight className="w-4 h-4 mr-2" />
                Start Learning
              </Button>
            )}
            <Button 
              variant="outline" 
              onClick={() => navigate("/dashboard")}
              className="w-full"
            >
              <Home className="w-4 h-4 mr-2" />
              Go to Dashboard
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
