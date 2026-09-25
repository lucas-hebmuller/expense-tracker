import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { authApi } from "@/api/authApi";
import type { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

const forgotPasswordSchema = z.object({
  email: z.string().email("Invalid email address"),
});

type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;

function ForgotPasswordPage() {
  const [submitted, setSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const forgotPasswordMutation = useMutation({
    mutationFn: (data: ForgotPasswordFormData) => authApi.forgotPassword(data),
    onSuccess: () => {
      setErrorMessage(null);
      setSubmitted(true);
    },
    onError: (error: AxiosError<ApiError>) => {
      setErrorMessage(
        error.response?.data?.message ||
          "Something went wrong. Please try again.",
      );
    },
  });

  const onSubmit = (data: ForgotPasswordFormData) => {
    setErrorMessage(null);
    forgotPasswordMutation.mutate(data);
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Forgot Password</h1>

        {submitted ? (
          <>
            <p>
              If an account exists for that email, a reset link has been sent.
              Check your inbox.
            </p>
            <p className="auth-footer">
              <Link to="/login">Back to login</Link>
            </p>
          </>
        ) : (
          <>
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

              <button type="submit" disabled={forgotPasswordMutation.isPending}>
                {forgotPasswordMutation.isPending
                  ? "Sending..."
                  : "Send reset link"}
              </button>
            </form>

            <p className="auth-footer">
              Remembered your password? <Link to="/login">Login</Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}

export default ForgotPasswordPage;
