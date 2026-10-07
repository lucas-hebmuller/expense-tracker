import API from "./axiosConfig";
import {
  type AuthResponse,
  type ForgotPasswordRequest,
  type LoginRequest,
  type RegisterRequest,
  type ResendVerificationRequest,
  type ResetPasswordRequest,
  type VerifyEmailRequest,
} from "@/types/auth.types";

export const authApi = {
  register: async (data: RegisterRequest) => {
    const response = await API.post<{ message: string }>("/auth/register", data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await API.post<AuthResponse>("/auth/login", data);
    return response.data;
  },

  forgotPassword: async (data: ForgotPasswordRequest) => {
    const response = await API.post<{ message: string }>("/auth/forgot-password", data);
    return response.data;
  },

  resetPassword: async (data: ResetPasswordRequest) => {
    const response = await API.post<{ message: string }>("/auth/reset-password", data);
    return response.data;
  },

  verifyEmail: async (data: VerifyEmailRequest) => {
    const response = await API.post<{ message: string }>("/auth/verify-email", data);
    return response.data;
  },

  resendVerification: async (data: ResendVerificationRequest) => {
    const response = await API.post<{ message: string }>("/auth/resend-verification", data);
    return response.data;
  }
};
