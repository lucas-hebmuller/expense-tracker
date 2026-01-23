import API from "./axiosConfig";
import {
  type AuthResponse,
  type LoginRequest,
  type RegisterRequest,
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
};
