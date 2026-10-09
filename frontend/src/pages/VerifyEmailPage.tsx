import { useEffect, useRef, useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { authApi } from "@/api/authApi";
import type { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

type Status = "verifying" | "success" | "error";

function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const [status, setStatus] = useState<Status>("verifying");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const verifyMutation = useMutation({
    mutationFn: (token: string) => authApi.verifyEmail({ token }),
    onSuccess: () => {
      setStatus("success");
    },
    onError: (error: AxiosError<ApiError>) => {
      setStatus("error");
      setErrorMessage(
        error.response?.data?.message ||
          "Something went wrong verifying your email.",
      );
    },
  });

  const hasRun = useRef(false);
  useEffect(() => {
    if (!token) {
      setStatus("error");
      setErrorMessage("This verification link is invalid or missing its token.");
      return;
    }
    if (!hasRun.current) {
      hasRun.current = true;
      verifyMutation.mutate(token);
    }
  }, [token]);

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Email Verification</h1>

        {status === "verifying" && <p>Verifying your email...</p>}

        {status === "success" && (
          <>
            <p>Your email has been verified. You can now log in.</p>
            <p className="auth-footer">
              <Link to="/login">Go to login</Link>
            </p>
          </>
        )}

        {status === "error" && (
          <>
            <div className="error-message">{errorMessage}</div>
            <p className="auth-footer">
              <Link to="/login">Back to login</Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}

export default VerifyEmailPage;