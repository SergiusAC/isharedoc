import { uploadService } from "@/services/uploadService";
import { useState } from "react";
import UploadCard, { type UploadSubmitData } from "./components/UploadCard";
import MyAlertDialog from "./components/MyAlertDialog";
import { AlertDialogCancel } from "@/components/ui/alert-dialog";

const UploadPage = () => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [fileId, setFileId] = useState("");
  const [downloadUrl, setDownloadUrl] = useState("");
  const [uploading, setUploading] = useState(false);

  const handleUpload = async (submitData: UploadSubmitData) => {
    setUploading(true);
    try {
      const fileId = await uploadService.upload(submitData.file, {
        filename: submitData.file.name,
        secretKey: submitData.password,
        expiresInSeconds: submitData.expireInMinutes * 60
      });
      setFileId(fileId);
      setDownloadUrl(`${window.location.origin}/download?fileId=${fileId}`);
      setDialogOpen(true);
    } catch (err) {
      console.error("Upload failed", err);
      alert("Upload failed: " + (err as Error).message);
    } finally {
      setUploading(false);
    }
  };

  return <>
    <UploadCard onSubmit={handleUpload} uploading={uploading} />
    <MyAlertDialog 
      isDialogOpen={dialogOpen} 
      setIsDialogOpen={(setDialogOpen)} 
      title="✅ File Uploaded Successfully"
      content={<>
        <div className="space-y-2">
          <p><span className="font-semibold">File ID:</span> {fileId}</p>
          <p>
            <span className="font-semibold">Download URL:</span>{" "}
            <a
              href={downloadUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 underline"
            >
              {downloadUrl}
            </a>
          </p>
        </div>
      </>}
      footer={<>
        <AlertDialogCancel>Close</AlertDialogCancel>
      </>}
    />
  </>
}

export default UploadPage;