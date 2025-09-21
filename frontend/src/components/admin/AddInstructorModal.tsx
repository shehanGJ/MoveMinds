import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { GraduationCap, Mail, Phone, User, FileText, Award } from "lucide-react";

interface AddInstructorModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export const AddInstructorModal = ({ open, onOpenChange }: AddInstructorModalProps) => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    specialization: "",
    certifications: "",
    bio: ""
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Handle instructor creation
    console.log("Creating instructor:", formData);
    onOpenChange(false);
    // Reset form
    setFormData({ name: "", email: "", phone: "", specialization: "", certifications: "", bio: "" });
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <GraduationCap className="h-5 w-5 text-primary" />
            Add New Instructor
          </DialogTitle>
        </DialogHeader>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name" className="flex items-center gap-2">
              <User className="h-4 w-4" />
              Full Name
            </Label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              placeholder="Enter instructor name"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="email" className="flex items-center gap-2">
              <Mail className="h-4 w-4" />
              Email Address
            </Label>
            <Input
              id="email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              placeholder="Enter email address"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="phone" className="flex items-center gap-2">
              <Phone className="h-4 w-4" />
              Phone Number
            </Label>
            <Input
              id="phone"
              value={formData.phone}
              onChange={(e) => setFormData({...formData, phone: e.target.value})}
              placeholder="Enter phone number"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="specialization" className="flex items-center gap-2">
              <Award className="h-4 w-4" />
              Specialization
            </Label>
            <Input
              id="specialization"
              value={formData.specialization}
              onChange={(e) => setFormData({...formData, specialization: e.target.value})}
              placeholder="e.g. HIIT, Yoga, Strength Training"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="certifications" className="flex items-center gap-2">
              <Award className="h-4 w-4" />
              Certifications
            </Label>
            <Input
              id="certifications"
              value={formData.certifications}
              onChange={(e) => setFormData({...formData, certifications: e.target.value})}
              placeholder="e.g. ACSM, NASM, ACE"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="bio" className="flex items-center gap-2">
              <FileText className="h-4 w-4" />
              Bio
            </Label>
            <Textarea
              id="bio"
              value={formData.bio}
              onChange={(e) => setFormData({...formData, bio: e.target.value})}
              placeholder="Brief instructor biography"
              rows={3}
            />
          </div>

          <div className="flex gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
              Cancel
            </Button>
            <Button type="submit" variant="fitness" className="flex-1">
              Create Instructor
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};