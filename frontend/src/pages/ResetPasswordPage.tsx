import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useMutation } from "@tanstack/react-query";
import { Link, useSearchParams } from "react-router-dom";
import { authApi } from "@/api/authApi";
import type { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

const resetPasswordSchema = z
  .object({
    newPassword: z.string().min(8, "Password must be at least 8 characters"),
    confirmNewPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: "Passwords don't match",
    path: ["confirmNewPassword"],
  });

type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;

function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const [submitted, setSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
  });

  const resetPasswordMutation = useMutation({
    mutationFn: (data: { token: string; newPassword: string }) =>
      authApi.resetPassword(data),
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

  const onSubmit = (data: ResetPasswordFormData) => {
    if (token == null) return;
    setErrorMessage(null);
    resetPasswordMutation.mutate({ token, newPassword: data.newPassword });
  };

  if (token == null) {
    return (
      <div className="auth-container">
        <div className="auth-card">
          <h1>Reset Password</h1>
          <p>This reset link is invalid or missing its token.</p>
          <p className="auth-footer">
            <Link to="/forgot-password">Request a new link</Link>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Reset Password</h1>

        {submitted ? (
          <>
            <p>
              Your password has been reset. You can now log in with your new
              password.
            </p>
            <p className="auth-footer">
              <Link to="/login">Go to login</Link>
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
                <label htmlFor="newPassword">New Password: </label>
                <input
                  id="newPassword"
                  type="password"
                  {...register("newPassword")}
                  placeholder="********"
                />
                {errors.newPassword && (
                  <span className="field-error">
                    {errors.newPassword.message}
                  </span>
                )}
              </div>

              <div className="form-group">
                <label htmlFor="confirmNewPassword">
                  Confirm New Password:{" "}
                </label>
                <input
                  id="confirmNewPassword"
                  type="password"
                  {...register("confirmNewPassword")}
                  placeholder="********"
                />
                {errors.confirmNewPassword && (
                  <span className="field-error">
                    {errors.confirmNewPassword.message}
                  </span>
                )}
              </div>

              <button type="submit" disabled={resetPasswordMutation.isPending}>
                {resetPasswordMutation.isPending
                  ? "Resetting..."
                  : "Reset password"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}

export default ResetPasswordPage;
