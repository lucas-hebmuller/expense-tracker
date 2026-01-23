export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ValidationError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  errors: Record<string, string>;
  path: string;
}
