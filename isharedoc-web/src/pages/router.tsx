import { createBrowserRouter } from "react-router";
import DownloadPage from "./download";
import UploadPage from "./upload";
import TabsLayout from "@/layouts/TabsLayout";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <TabsLayout />,
    children: [
      {
        path: "/",
        element: <UploadPage />
      },
      {
        path: "/download",
        element: <DownloadPage />
      },
      {
        path: "/upload",
        element: <UploadPage />
      },
    ]
  },
]);