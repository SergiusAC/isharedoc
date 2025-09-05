import { downloadService } from "@/services/downloadService";
import DownloadCard, { type DownloadSubmitData } from "./components/DownloadCard";
import { useState } from "react";
import MyAlertDialog from "./components/MyAlertDialog";
import type { GenerateDownloadUrlRequest, FileMetadatResponse } from "@/services/models";
import { AlertDialogAction, AlertDialogCancel } from "@/components/ui/alert-dialog";

const DownloadPage = () => {
  const [fileMetadata, setFileMetadata] = useState<FileMetadatResponse>();
  const [promptDialogOpen, setPromptDialogOpen] = useState(false);
  const [downloadRequest, setDownloadRequest] = useState<GenerateDownloadUrlRequest>();
  const [downloading, setDownloading] = useState(false);

  const handleSubmit = async (data: DownloadSubmitData) => {
    setDownloading(true);
    try {
      setDownloadRequest({
        fileId: data.fileId,
        protectionPassword: data.password
      });
      const fileMetadat = await downloadService.getFileMetadata({
        fileId: data.fileId,
        protectionPassword: data.password,
      });
      setFileMetadata(fileMetadat);
      setPromptDialogOpen(true);
    } catch (err) {
      console.error("Upload failed", err);
      alert("Upload failed: " + (err as Error).message)
    } finally {
      setDownloading(false);
    }
  }

  const handleStartDownload = async () => {
    if (!downloadRequest) return;
    setPromptDialogOpen(false);
    setDownloading(true);
    try {
      await downloadService.download({
        fileId: downloadRequest.fileId,
        protectionPassword: downloadRequest.protectionPassword
      });
      setDownloadRequest(undefined);
    } catch (err) {
      console.error("Upload failed", err);
      alert("Upload failed: " + (err as Error).message)
    } finally {
      setDownloading(false);
    }
  }

  return <>
    <DownloadCard onSubmit={handleSubmit} downloading={downloading} />
    <MyAlertDialog 
        isDialogOpen={promptDialogOpen} 
        setIsDialogOpen={setPromptDialogOpen} 
        title="✅ Download Permitted"
        content={<>
          <div className="space-y-2">
            <p><span className="font-semibold">You are about to download the next file.</span></p>
            <p><span className="font-semibold">File ID:</span> {fileMetadata?.fileId}</p>
            <p><span className="font-semibold">Filename:</span> {fileMetadata?.filename}</p>
          </div>
        </>}
        footer={<>
          <AlertDialogAction onClick={handleStartDownload}>
            Download
          </AlertDialogAction>
          <AlertDialogCancel>
            Close
          </AlertDialogCancel>
        </>}
      />
  </>;
};

export default DownloadPage;