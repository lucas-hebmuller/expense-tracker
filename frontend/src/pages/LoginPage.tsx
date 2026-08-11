import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { useNavigate, Link } from "react-router-dom";
import { authApi } from "@/api/authApi";
import { useAuthStore } from "@/stores/authStore";
import type { LoginRequest } from "@/types/auth.types";
import type { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(1, "Password is required"),
});

type LoginFormData = z.infer<typeof loginSchema>;

function LoginPage() {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const loginMutation = useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
    onSuccess: (response) => {
      setErrorMessage(null);
      login(response.token, response.userId, response.email, response.name);
      navigate("/dashboard");
    },
    onError: (error: AxiosError<ApiError>) => {
      setErrorMessage(
        error.response?.data?.message || "Login failed. Please try again."
      );
    },
  });

  const onSubmit = (data: LoginFormData) => {
    setErrorMessage(null);
    loginMutation.mutate(data);
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Login</h1>

        {errorMessage && (
          <div className="error-message">
            {errorMessage}
            <button onClick={() => setErrorMessage(null)}>✕</button>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label htmlFor="email">Email: </label>
            <input
              id="email"
              type="email"
              {...register("email")}
              placeholder="you@example.com"
            />
            {errors.email && (
              <span className="field-error">{errors.email.message}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password: </label>
            <input
              id="password"
              type="password"
              {...register("password")}
              placeholder="********"
            />
            {errors.password && (
              <span className="field-error">{errors.password.message}</span>
            )}
          </div>

          <button type="submit" disabled={loginMutation.isPending}>
            {loginMutation.isPending ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <p className="auth-footer">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;