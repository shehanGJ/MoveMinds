import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { CreditCard, Lock, DollarSign, ExternalLink } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/lib/auth";
import { paymentApi } from "@/lib/api";

interface PaymentModalProps {
  isOpen: boolean;
  onClose: () => void;
  program: {
    id: number;
    name: string;
    price: number;
    difficultyLevel: string;
    description: string;
  };
}

export const PaymentModal = ({ isOpen, onClose, program }: PaymentModalProps) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentData, setPaymentData] = useState({
    customerName: "",
    customerEmail: user?.email || "",
    customerPhone: "",
    customerAddress: "",
    customerCity: "",
    customerCountry: "Sri Lanka"
  });

  const handleInputChange = (field: string, value: string) => {
    setPaymentData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handlePayHerePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);

    try {
      console.log("Program object:", program);
      console.log("Program description:", program.description);
      
      const paymentRequest = {
        programId: program.id,
        ...paymentData,
        amount: program.price,
        currency: "LKR",
        orderId: `MM_${Date.now()}_${program.id}`,
        itemName: program.name,
        itemDescription: program.description || "Fitness Program"
      };

      console.log("Creating PayHere payment request:", paymentRequest);
      const response = await paymentApi.createPayHerePayment(paymentRequest);
      console.log("PayHere payment response:", response.data);
      
      if (response.data.status === "success") {
        // Redirect to PayHere payment page
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = response.data.paymentUrl;
        form.target = '_blank';

        // Add all the payment data as hidden fields
        const fields = [
          'merchant_id', 'return_url', 'cancel_url', 'notify_url', 'order_id',
          'items', 'currency', 'amount', 'first_name', 'last_name', 'email',
          'phone', 'address', 'city', 'country', 'hash'
        ];

        const paymentFields = {
          merchant_id: response.data.merchantId,
          return_url: response.data.returnUrl,
          cancel_url: response.data.cancelUrl,
          notify_url: response.data.notifyUrl,
          order_id: response.data.orderId,
          items: response.data.itemName,
          currency: response.data.currency,
          amount: response.data.amount,
          first_name: response.data.customerName.split(' ')[0] || '',
          last_name: response.data.customerName.split(' ').slice(1).join(' ') || '',
          email: response.data.customerEmail,
          phone: response.data.customerPhone,
          address: response.data.customerAddress,
          city: response.data.customerCity,
          country: response.data.customerCountry,
          hash: response.data.hash
        };

        fields.forEach(field => {
          const input = document.createElement('input');
          input.type = 'hidden';
          input.name = field;
          input.value = paymentFields[field as keyof typeof paymentFields] || '';
          form.appendChild(input);
        });

        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);

        toast({
          title: "Redirecting to Payment",
          description: "You will be redirected to PayHere to complete your payment.",
        });

        onClose();
      } else {
        toast({
          title: "Payment Error",
          description: response.data.message || "Failed to create payment request",
          variant: "destructive",
        });
      }
    } catch (error: any) {
      console.error("Payment error:", error);
      console.error("Error response:", error.response);
      console.error("Error status:", error.response?.status);
      console.error("Error data:", error.response?.data);
      
      let errorMessage = "Failed to process payment";
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.status === 401) {
        errorMessage = "Please login to continue";
      } else if (error.response?.status === 403) {
        errorMessage = "Access denied. Please check your permissions.";
      } else if (error.response?.status === 500) {
        errorMessage = "Server error. Please try again later.";
      }
      
      toast({
        title: "Payment Error",
        description: errorMessage,
        variant: "destructive",
      });
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <CreditCard className="w-5 h-5" />
            Complete Payment
          </DialogTitle>
          <DialogDescription>
            You will be redirected to PayHere to complete your secure payment.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Program Summary */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-lg">{program.name}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Difficulty</span>
                <Badge variant="secondary">{program.difficultyLevel || "Unknown"}</Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">Price</span>
                <div className="flex items-center gap-1">
                  <DollarSign className="w-4 h-4" />
                  <span className="font-semibold">
                    {program.price === 0 ? "Free" : `LKR ${program.price.toFixed(2)}`}
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Customer Information Form */}
          <form onSubmit={handlePayHerePayment} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="customerName">Full Name *</Label>
                <Input
                  id="customerName"
                  value={paymentData.customerName}
                  onChange={(e) => handleInputChange("customerName", e.target.value)}
                  required
                  placeholder="Enter your full name"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="customerEmail">Email *</Label>
                <Input
                  id="customerEmail"
                  type="email"
                  value={paymentData.customerEmail}
                  onChange={(e) => handleInputChange("customerEmail", e.target.value)}
                  required
                  placeholder="Enter your email"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="customerPhone">Phone *</Label>
                <Input
                  id="customerPhone"
                  value={paymentData.customerPhone}
                  onChange={(e) => handleInputChange("customerPhone", e.target.value)}
                  required
                  placeholder="Enter your phone number"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="customerCity">City *</Label>
                <Input
                  id="customerCity"
                  value={paymentData.customerCity}
                  onChange={(e) => handleInputChange("customerCity", e.target.value)}
                  required
                  placeholder="Enter your city"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="customerAddress">Address *</Label>
              <Input
                id="customerAddress"
                value={paymentData.customerAddress}
                onChange={(e) => handleInputChange("customerAddress", e.target.value)}
                required
                placeholder="Enter your address"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="customerCountry">Country</Label>
              <Input
                id="customerCountry"
                value={paymentData.customerCountry}
                onChange={(e) => handleInputChange("customerCountry", e.target.value)}
                placeholder="Enter your country"
              />
            </div>

            {/* Security Notice */}
            <Card className="bg-muted/20">
              <CardContent className="pt-4">
                <div className="flex items-start gap-3">
                  <Lock className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div className="space-y-1">
                    <p className="text-sm font-medium">Secure Payment</p>
                    <p className="text-xs text-muted-foreground">
                      Your payment will be processed securely through PayHere. 
                      We do not store your payment information.
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Action Buttons */}
            <div className="flex gap-3 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                className="flex-1"
                disabled={isProcessing}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                className="flex-1"
                disabled={isProcessing}
              >
                {isProcessing ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                    Processing...
                  </>
                ) : (
                  <>
                    <ExternalLink className="w-4 h-4 mr-2" />
                    Pay with PayHere
                  </>
                )}
              </Button>
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default PaymentModal;