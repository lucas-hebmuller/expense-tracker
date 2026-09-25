import API from "./axiosConfig";
import {
  type AuthResponse,
  type ForgotPasswordRequest,
  type LoginRequest,
  type RegisterRequest,
  type ResetPasswordRequest,
} from "@/types/auth.types";

export const authApi = {
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await API.post<AuthResponse>("/auth/register", data);
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
};
