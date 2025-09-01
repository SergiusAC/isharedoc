import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Clock, File, Lock, UploadCloud } from "lucide-react"
import { motion, number } from "framer-motion"
import React, { useState } from "react"

export interface UploadSubmitData {
  file: File;
  password: string;
  expireInMinutes: number;
}

interface UploadCardProps {
  onSubmit: (data: UploadSubmitData) => void;
}

const UploadCard: React.FC<UploadCardProps> = ({ onSubmit }) => {
  const [file, setFile] = useState<File | null>(null);
  const [password, setPassword] = useState("");
  const [expiration, setExpiration] = useState<number | undefined>(undefined);
  const [uploading, setUploading] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;
    setFile(e.target.files[0]);
  };

  const handleUpload = () => {
    if (!file || !expiration) return;

    try {
      setUploading(true);
      onSubmit({
        file,
        password,
        expireInMinutes: expiration
      })
    } finally {
      setUploading(false);
    }
  }
  
  return <>
    <Card className="w-full max-w-lg shadow-xl rounded-2xl">
      <CardHeader>
        <CardTitle className="text-xl font-semibold text-center">
          Secure File Sharing
        </CardTitle>
      </CardHeader>
      <CardContent>
        {/* File Input */}
        <motion.div
          className="border-2 border-dashed border-gray-300 rounded-xl p-8 flex flex-col items-center justify-center gap-4 bg-white mb-4"
          whileHover={{ scale: 1.02 }}
        >
          {file ? (
            <div className="flex items-center gap-2 text-gray-700">
              <File className="w-5 h-5" />
              <span>{file.name}</span>
            </div>
          ) : (
            <>
              <UploadCloud className="w-12 h-12 text-gray-400" />
              <p className="text-gray-500 text-sm text-center">
                Drag & drop your file here or click to browse
              </p>
            </>
          )}
          <input
            type="file"
            onChange={handleFileChange}
            className="hidden"
            id="file-input"
          />
          <label
            htmlFor="file-input"
            className="cursor-pointer text-sm font-medium text-indigo-600 hover:underline"
          >
            {file ? "Change file" : "Choose a file"}
          </label>
        </motion.div>

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

        {/* Expiration (minutes) */}
        <div className="flex items-center gap-2 mb-4">
          <Clock className="w-5 h-5 text-gray-500" />
          <Input
            type="number"
            placeholder="Expiration (minutes)"
            value={expiration}
            onChange={(e) => setExpiration(number.parse(e.target.value))}
            className="flex-1"
            required
            min={1}
            max={10080} // 1 week
          />
        </div>

        {/* Upload Button  */}
        <Button
          onClick={handleUpload}
          disabled={!file || !password || !expiration}
          className="w-full"
        >
          {uploading ? "Uploading..." : "Upload"}
        </Button>
      </CardContent>
    </Card>
  </>
}

export default UploadCard