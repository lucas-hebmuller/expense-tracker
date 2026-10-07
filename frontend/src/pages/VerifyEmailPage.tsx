import { useEffect, useRef } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { authApi } from "@/api/authApi";
import type { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const verifyMutation = useMutation({
    mutationFn: (token: string) => authApi.verifyEmail({ token }),
  });

  // fire once on mount
  const hasRun = useRef(false);
  useEffect(() => {
    if (token && !hasRun.current) {
      hasRun.current = true;
      verifyMutation.mutate(token);
    }
  }, [token]);

  const renderContent = () => {
    if (token == null) {
      return (
        <>
          <p>This verification link is invalid or missing its token.</p>
          <p className="auth-footer">
            <Link to="/login">Back to login</Link>
          </p>
        </>
      );
    }

    if (verifyMutation.isPending) {
      return <p>Verifying your email...</p>;
    }

    if (verifyMutation.isSuccess) {
      return (
        <>
          <p>Your email has been verified. You can now log in.</p>
          <p className="auth-footer">
            <Link to="/login">Go to login</Link>
          </p>
        </>
      );
    }

    if (verifyMutation.isError) {
      const error = verifyMutation.error as AxiosError<ApiError>;
      const message =
        error.response?.data?.message ||
        "Something went wrong verifying your email.";
      return (
        <>
          <div className="error-message">{message}</div>
          <p className="auth-footer">
            <Link to="/login">Back to login</Link>
          </p>
        </>
      );
    }

    return null;
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Email Verification</h1>
        {renderContent()}
      </div>
    </div>
  );
}

export default VerifyEmailPage;
