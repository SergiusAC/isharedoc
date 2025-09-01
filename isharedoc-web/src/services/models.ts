export interface GeneralResponse<T> {
  timestamp: string;
  status: number;
  requestId: string;
  errorInfo: ErrorInfo | null;
  data: T;
}

export interface ErrorInfo {
  method: string;
  path: string;
  message: string;
}

export interface GenerateUploadUrlRequest {
  filename: string;
  secretKey: string;
  expiresInSeconds: number;
}

export interface GenerateUploadUrlResponse {
  fileId: string;
  uploadUrl: string;
  sseHeaders: object;
}

export interface GenerateDownloadUrlRequest {
  fileId: string;
  secretKey: string;
}

export interface GenerateDownloadUrlResponse {
  fileId: string;
  downloadUrl: string;
  sseHeaders: object;
}

export interface FileMetadatResponse {
  fileId: string;
  filename: string;
}