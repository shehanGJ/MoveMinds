import React from 'react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card-enhanced';
import { AlertTriangle, Trash2, X } from 'lucide-react';

interface DeleteConfirmationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
  title: string;
  description: string;
  itemName: string;
  itemType: 'user' | 'program' | 'item';
  isLoading?: boolean;
  variant?: 'destructive' | 'warning';
}

export const DeleteConfirmationDialog: React.FC<DeleteConfirmationDialogProps> = ({
  open,
  onOpenChange,
  onConfirm,
  title,
  description,
  itemName,
  itemType,
  isLoading = false,
  variant = 'destructive'
}) => {
  const getItemIcon = () => {
    switch (itemType) {
      case 'user':
        return <Trash2 className="h-6 w-6" />;
      case 'program':
        return <Trash2 className="h-6 w-6" />;
      default:
        return <AlertTriangle className="h-6 w-6" />;
    }
  };

  const getVariantStyles = () => {
    switch (variant) {
      case 'warning':
        return {
          iconColor: 'text-orange-400',
          iconBg: 'bg-orange-500/10',
          borderColor: 'border-orange-500/20',
          buttonVariant: 'outline' as const,
          buttonClass: 'border-orange-500 text-orange-600 hover:bg-orange-50 dark:hover:bg-orange-950/20'
        };
      default:
        return {
          iconColor: 'text-red-400',
          iconBg: 'bg-red-500/10',
          borderColor: 'border-red-500/20',
          buttonVariant: 'destructive' as const,
          buttonClass: ''
        };
    }
  };

  const styles = getVariantStyles();

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <Card variant="neumorphic" className="border-0 shadow-2xl">
          <CardContent className="p-0">
            {/* Header with Icon */}
            <DialogHeader className="p-6 pb-4">
              <div className="flex items-center gap-4">
                <div className={`p-3 rounded-full ${styles.iconBg} ${styles.borderColor} border`}>
                  <div className={styles.iconColor}>
                    {getItemIcon()}
                  </div>
                </div>
                <div className="flex-1">
                  <DialogTitle className="text-xl font-semibold text-foreground">
                    {title}
                  </DialogTitle>
                  <DialogDescription className="text-muted-foreground mt-1">
                    {description}
                  </DialogDescription>
                </div>
              </div>
            </DialogHeader>

            {/* Content */}
            <div className="px-6 pb-4">
              <div className={`p-4 rounded-lg bg-muted/30 border ${styles.borderColor}`}>
                <div className="flex items-center gap-3">
                  <AlertTriangle className={`h-5 w-5 ${styles.iconColor}`} />
                  <div>
                    <p className="font-medium text-foreground">
                      Are you sure you want to delete this {itemType}?
                    </p>
                    <p className="text-sm text-muted-foreground mt-1">
                      <span className="font-semibold text-foreground">"{itemName}"</span> will be permanently removed.
                    </p>
                  </div>
                </div>
              </div>
              
              <div className="mt-4 p-3 bg-destructive/5 border border-destructive/20 rounded-lg">
                <p className="text-sm text-destructive font-medium">
                  ⚠️ This action cannot be undone.
                </p>
              </div>
            </div>

            {/* Footer */}
            <DialogFooter className="p-6 pt-4 gap-3">
              <Button
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={isLoading}
                className="flex-1"
              >
                <X className="h-4 w-4 mr-2" />
                Cancel
              </Button>
              <Button
                variant={styles.buttonVariant}
                onClick={onConfirm}
                disabled={isLoading}
                className={`flex-1 ${styles.buttonClass}`}
              >
                {isLoading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current mr-2" />
                    Deleting...
                  </>
                ) : (
                  <>
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete {itemType === 'user' ? 'User' : itemType === 'program' ? 'Program' : 'Item'}
                  </>
                )}
              </Button>
            </DialogFooter>
          </CardContent>
        </Card>
      </DialogContent>
    </Dialog>
  );
};
