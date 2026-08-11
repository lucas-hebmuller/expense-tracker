import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { useNavigate, Link } from "react-router-dom";
import { authApi } from "@/api/authApi";
import { useAuthStore } from "@/stores/authStore";
import type { RegisterRequest } from "@/types/auth.types";
import type { AxiosError } from "axios";
import type { ValidationError } from "@/types/api.types";

const registerSchema = z
  .object({
    name: z.string().min(2, "Name must be at least 2 characters"),
    email: z.string().email("Invalid email address"),
    password: z.string().min(8, "Password must be at least 8 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type RegisterFormData = z.infer<typeof registerSchema>;

function RegisterPage() {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const registerMutation = useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: (response) => {
      setErrorMessage(null);
      login(response.token, response.userId, response.email, response.name);
      navigate("/dashboard");
    },
    onError: (error: AxiosError<ValidationError>) => {
      setErrorMessage(
        error.response?.data?.message || "Registration failed. Please try again."
      );
    },
  });

  const onSubmit = (data: RegisterFormData) => {
    const { confirmPassword, ...registerData } = data;
    setErrorMessage(null);
    registerMutation.mutate(registerData);
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Register</h1>

        {errorMessage && (
          <div className="error-message">
            {errorMessage}
            <button onClick={() => setErrorMessage(null)}>✕</button>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label htmlFor="name">Full Name: </label>
            <input
              id="name"
              type="text"
              {...register("name")}
              placeholder="John Doe"
            />
            {errors.name && (
              <span className="field-error">{errors.name.message}</span>
            )}
          </div>

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

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password: </label>
            <input
              id="confirmPassword"
              type="password"
              {...register("confirmPassword")}
              placeholder="********"
            />
            {errors.confirmPassword && (
              <span className="field-error">
                {errors.confirmPassword.message}
              </span>
            )}
          </div>

          <button type="submit" disabled={registerMutation.isPending}>
            {registerMutation.isPending
              ? "Creating account..."
              : "Create Account"}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;