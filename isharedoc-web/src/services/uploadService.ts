import type { GeneralResponse, GenerateUploadUrlRequest, GenerateUploadUrlResponse } from "./models";

const API_URL = import.meta.env.VITE_BASE_API_URL;

export const uploadService = {

  async upload(file: File, request: GenerateUploadUrlRequest): Promise<string> {
    // Request presigned URL
    const presignedUrlResponse = await fetch(
      `${API_URL}/presigned-urls/upload-url`,
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
    console.log(presignedUrlData);
    
    // Upload to S3 using presigned URL
    const uploadRes = await fetch(presignedUrlData.uploadUrl, {
      method: "PUT",
      headers: {
        "Content-Type": file.type || "application/octet-stream",
        ...presignedUrlData.sseHeaders, // <- backend may return SSE-C headers
      },
      mode: "cors",
      body: file,
    });

    if (!uploadRes.ok) {
      throw new Error("S3 upload failed");
    }

    return presignedUrlData.fileId;
  },

  async _parseResponse(resp: Response): Promise<GeneralResponse<GenerateUploadUrlResponse>> {
    try {
      return await resp.json();
    } catch {
      throw new Error(`Backend returned invalid response`);
    }
  }

}