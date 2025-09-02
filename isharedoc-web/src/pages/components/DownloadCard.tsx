import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { FileKey, Lock } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router";

export interface DownloadSubmitData {
  fileId: string;
  password: string;
}

interface DownloadCardProps {
  onSubmit: (data: DownloadSubmitData) => void;
  downloading: boolean;
}

const DownloadCard: React.FC<DownloadCardProps> = ({ onSubmit, downloading }) => {
  const [searchParams] = useSearchParams();
  const [formFileId, setFormFileId] = useState("");
  const [password, setPassword] = useState("");

    useEffect(() => {
    if (searchParams.get("fileId")) {
      setFormFileId(searchParams.get("fileId")!)
    }
  }, [searchParams])

  const handleSubmit = (e: any) => {
    e.preventDefault();
    if (!formFileId || !password) return;
    onSubmit({ fileId: formFileId, password })
  }

  return <>
    <Card className="w-full max-w-lg shadow-xl rounded-2xl">
      <CardHeader>
        <CardTitle className="text-xl font-semibold text-center">
          File Downloading
        </CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
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
            type="submit"
            disabled={!formFileId || !password}
            className="w-full cursor-pointer"
          >
            {downloading ? "Downloading..." : "Download"}
          </Button>
        </form>
      </CardContent>
    </Card>
  </>
}

export default DownloadCard;