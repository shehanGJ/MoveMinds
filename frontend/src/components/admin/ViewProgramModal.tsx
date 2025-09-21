import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { 
  BookOpen, 
  Users, 
  DollarSign, 
  Star, 
  Calendar, 
  Clock,
  GraduationCap,
  TrendingUp,
  CheckCircle,
  XCircle
} from "lucide-react";

interface ViewProgramModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  program?: {
    id: number;
    name: string;
    instructor: string;
    students: number;
    revenue: string;
    rating: number;
    status: string;
    created: string;
    duration: string;
  } | null;
}

export const ViewProgramModal = ({ open, onOpenChange, program }: ViewProgramModalProps) => {
  if (!program) return null;

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'active': return <Badge variant="default">Active</Badge>;
      case 'pending': return <Badge variant="secondary">Pending</Badge>;
      case 'suspended': return <Badge variant="destructive">Suspended</Badge>;
      case 'review': return <Badge variant="secondary">Under Review</Badge>;
      default: return <Badge variant="secondary">{status}</Badge>;
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <BookOpen className="h-5 w-5 text-primary" />
            Program Details
          </DialogTitle>
        </DialogHeader>
        
        <div className="space-y-6">
          {/* Program Header */}
          <div className="flex items-start gap-4">
            <div className="h-16 w-16 bg-gradient-primary rounded-lg flex items-center justify-center">
              <BookOpen className="h-8 w-8 text-primary-foreground" />
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h3 className="text-xl font-semibold">{program.name}</h3>
                {getStatusBadge(program.status)}
              </div>
              <div className="flex items-center gap-2 text-muted-foreground">
                <GraduationCap className="h-4 w-4" />
                <span>Instructor: {program.instructor}</span>
              </div>
            </div>
          </div>

          <Separator />

          {/* Program Stats */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center p-4 bg-background/50 rounded-lg border">
              <Users className="h-6 w-6 mx-auto text-primary mb-2" />
              <div className="text-lg font-semibold">{program.students}</div>
              <div className="text-sm text-muted-foreground">Students</div>
            </div>
            <div className="text-center p-4 bg-background/50 rounded-lg border">
              <DollarSign className="h-6 w-6 mx-auto text-green-400 mb-2" />
              <div className="text-lg font-semibold text-green-400">{program.revenue}</div>
              <div className="text-sm text-muted-foreground">Revenue</div>
            </div>
            <div className="text-center p-4 bg-background/50 rounded-lg border">
              <Star className="h-6 w-6 mx-auto text-yellow-400 mb-2" />
              <div className="text-lg font-semibold">{program.rating}</div>
              <div className="text-sm text-muted-foreground">Rating</div>
            </div>
            <div className="text-center p-4 bg-background/50 rounded-lg border">
              <Clock className="h-6 w-6 mx-auto text-blue-400 mb-2" />
              <div className="text-lg font-semibold">{program.duration}</div>
              <div className="text-sm text-muted-foreground">Duration</div>
            </div>
          </div>

          {/* Program Info */}
          <div className="space-y-4">
            <div className="flex items-center gap-2 text-sm">
              <Calendar className="h-4 w-4 text-muted-foreground" />
              <span>Created: {new Date(program.created).toLocaleDateString()}</span>
            </div>
            
            <div className="flex items-center gap-2 text-sm">
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
              <span>Growth Rate: +15% this month</span>
            </div>
          </div>

          <Separator />

          {/* Recent Students */}
          <div className="space-y-3">
            <h4 className="font-medium flex items-center gap-2">
              <Users className="h-4 w-4" />
              Recent Students
            </h4>
            <div className="space-y-2">
              {['Sarah Johnson', 'Mike Chen', 'Lisa Wang'].map((student, index) => (
                <div key={index} className="flex items-center gap-3 p-2 bg-background/50 rounded-lg">
                  <Avatar className="h-8 w-8">
                    <AvatarImage src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${student}`} />
                    <AvatarFallback>{student.split(' ').map(n => n[0]).join('')}</AvatarFallback>
                  </Avatar>
                  <span className="text-sm">{student}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-2 pt-4">
            {program.status === 'review' && (
              <>
                <Button variant="default" className="flex-1">
                  <CheckCircle className="h-4 w-4 mr-2" />
                  Approve Program
                </Button>
                <Button variant="destructive" className="flex-1">
                  <XCircle className="h-4 w-4 mr-2" />
                  Reject Program
                </Button>
              </>
            )}
            {program.status !== 'review' && (
              <Button variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
                Close
              </Button>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};