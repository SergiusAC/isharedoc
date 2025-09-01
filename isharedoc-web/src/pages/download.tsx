import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { downloadService } from "@/services/downloadService";
import { FileKey, Lock } from "lucide-react";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router";

const DownloadPage = () => {
  const [searchParams] = useSearchParams();
  const [formFileId, setFormFileId] = useState("");
  const [password, setPassword] = useState("");
  const [downloading, setDownloading] = useState(false);

  useEffect(() => {
    if (searchParams.get("fileId")) {
      setFormFileId(searchParams.get("fileId")!)
    }
  }, [searchParams])

  const handleDownload = async () => {
    setDownloading(true);
    try {
      await downloadService.download({
        fileId: formFileId,
        secretKey: password,
      })
    } catch (err) {
      console.error("Upload failed", err);
      alert("Upload failed: " + (err as Error).message)
    } finally {
      setDownloading(false);
    }
  }

  return <>
    <Card className="w-full max-w-lg shadow-xl rounded-2xl">
      <CardHeader>
        <CardTitle className="text-xl font-semibold text-center">
          File Downloading
        </CardTitle>
      </CardHeader>
      <CardContent>
        {/* File ID  */}
        <div className="flex items-center gap-2 mb-4">
          {/* <Lock className="w-5 h-5 text-gray-500" /> */}
          <FileKey className="w-5 h-5 text-gray-500" />
          <Input
            type="text"
            placeholder="File ID"
            value={formFileId}
            onChange={(e) => setFormFileId(e.target.value)}
            className="flex-1"
            required
          />
        </div>
        {/* Protection Password  */}
        <div className="flex items-center gap-2 mb-4">
          <Lock className="w-5 h-5 text-gray-500" />
          <Input
            type="password"
            placeholder="Protection Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="flex-1"
            required
          />
        </div>

        {/* Download Button  */}
        <Button
          onClick={handleDownload}
          disabled={!formFileId || !password}
          className="w-full"
        >
          {downloading ? "Downloading..." : "Download"}
        </Button>
      </CardContent>
    </Card>
  </>;
};

export default DownloadPage;