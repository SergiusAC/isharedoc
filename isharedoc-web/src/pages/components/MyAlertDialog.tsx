import { AlertDialog, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog"
import type { ReactNode } from "react";
import type React from "react";

interface MyAlertDialogProps {
  isDialogOpen: boolean;
  setIsDialogOpen: (value: boolean) => void;
  title: string;
  content: ReactNode;
  footer: ReactNode;
}

const MyAlertDialog: React.FC<MyAlertDialogProps> = ({isDialogOpen, setIsDialogOpen, title, content, footer}) => {
  return <>
    <AlertDialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          <AlertDialogDescription>
            {content}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          {footer}
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </>
}

export default MyAlertDialog;