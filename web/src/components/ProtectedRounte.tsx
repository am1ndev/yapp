import {Navigate} from "react-router-dom";
import {useAuth} from "@/hooks/useAuth.ts";
import React from "react";
import {Spinner} from "@/components/ui/spinner.tsx";

export const ProtectedRoute: React.FC<{ children: React.ReactElement, fallback?: string }> = ({children, fallback = "/login"}) => {
  const {authenticated, loading} = useAuth();

  if (loading) {
    return (
      <div className="w-full h-screen flex justify-center items-center">
        <Spinner className="size-8"/>
      </div>
    );
  }

  return authenticated
    ? children
    : <Navigate to={fallback} replace/>;
};
