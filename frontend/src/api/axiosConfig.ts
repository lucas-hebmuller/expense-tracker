import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import type { ApiError } from "@/types/api.types";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

API.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem("token");
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

API.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    const isAuthEndpoint = error.config?.url?.includes("/auth/");
    if (error.response?.status === 401 && !isAuthEndpoint) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export default API;
