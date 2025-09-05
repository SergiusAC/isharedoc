import type { FileMetadatResponse, GeneralResponse, GenerateDownloadUrlRequest, GenerateDownloadUrlResponse } from "./models";

const API_URL = import.meta.env.VITE_BASE_API_URL;

export const downloadService = {

  async getFileMetadata(request: GenerateDownloadUrlRequest): Promise<FileMetadatResponse> {
    const metadatResponse = await fetch(
      `${API_URL}/presigned-urls/file-metadata?fileId=${request.fileId}&protectionPassword=${request.protectionPassword}`, 
      {
        method: "GET",
        mode: "cors",      
      }
    );
    const parsedMetadata: GeneralResponse<FileMetadatResponse> = await this._parseResponseFileMetadata(metadatResponse);

    if (!metadatResponse.ok) {
      if (parsedMetadata != null && parsedMetadata.errorInfo) {
        throw new Error(`Backend error: ${parsedMetadata?.errorInfo.message}`);
      } else {
        throw new Error("Backend error");
      }  
    }

    return parsedMetadata.data;
  },

  async download(request: GenerateDownloadUrlRequest): Promise<void> {
    // Get file metadata to retrieve filename
    const fileMetadata: FileMetadatResponse = await this.getFileMetadata(request);

    // Request presigned URL
    const presignedUrlResponse = await fetch(
      `${API_URL}/presigned-urls/download-url`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        mode: "cors",
        body: JSON.stringify(request),
      }
    );

    const presignedUrlGeneralResponse = await this._parseResponse(presignedUrlResponse);

    if (!presignedUrlResponse.ok) {
      if (presignedUrlGeneralResponse != null && presignedUrlGeneralResponse.errorInfo) {
        throw new Error(`Backend error: ${presignedUrlGeneralResponse?.errorInfo.message}`);
      } else {
        throw new Error("Backend error");
      }  
    }

    const presignedUrlData = presignedUrlGeneralResponse.data;
    
    // Download from S3 using presigned URL
    const downloadRes = await fetch(presignedUrlData.downloadUrl, {
      method: "GET",
      headers: {
        ...presignedUrlData.sseHeaders, // <- backend may return SSE-C headers
      },
      mode: "cors",
    });

    if (!downloadRes.ok) {
      throw new Error("S3 download failed");
    }

    // 4. Create blob and URL for downloading
    const blob = await downloadRes.blob();
    const url = window.URL.createObjectURL(blob);

    // Auto-download
    const a = document.createElement("a");
    a.href = url;
    a.download = fileMetadata.filename;
    a.click();
    a.remove();
  },

  async _parseResponse(resp: Response): Promise<GeneralResponse<GenerateDownloadUrlResponse>> {
    try {
      return await resp.json();
    } catch {
      throw new Error(`Backend returned invalid response`);
    }
  },

  async _parseResponseFileMetadata(resp: Response): Promise<GeneralResponse<FileMetadatResponse>> {
    try {
      return await resp.json();
    } catch {
      throw new Error(`Backend returned invalid response`);
    }
  }

}