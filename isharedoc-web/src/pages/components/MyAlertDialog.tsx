import { AlertDialog, AlertDialogAction, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog"
import type { ReactNode } from "react";
import type React from "react";

interface MyAlertDialogProps {
  isDialogOpen: boolean;
  setIsDialogOpen: (value: boolean) => void;
  title: string;
  content: ReactNode;
}

const MyAlertDialog: React.FC<MyAlertDialogProps> = ({isDialogOpen, setIsDialogOpen, title, content}) => {
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
          <AlertDialogAction onClick={() => setIsDialogOpen(false)}>
            Close
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </>
}

export default MyAlertDialog;