import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Link, Outlet, useLocation, useNavigate } from "react-router";

const TabsLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const getDefaultTabValue = (): string => {
    if (location.pathname.startsWith("/upload")) {
      return "upload";
    }
    if (location.pathname.startsWith("/download")) {
      return "download";
    }
    return "upload";
  }

  return <>
    {/* Logo / App name */}
    <h1 className="flex justify-center mt-2 text-3xl font-bold text-indigo-600 tracking-tight">
      <Link to="/">iShareDoc</Link>
    </h1>
    
    {/* Tabs */}
    <div className="flex flex-col items-center justify-center pt-24">
      <Tabs defaultValue={getDefaultTabValue()} className="mb-2 w-full max-w-lg">
        <TabsList>
          <TabsTrigger value="upload" onClick={() => navigate("/upload")}>Upload</TabsTrigger>
          <TabsTrigger value="download" onClick={() => navigate("/download")}>Download</TabsTrigger>
        </TabsList>
      </Tabs>
      <Outlet />
    </div>
  </>
};

export default TabsLayout;