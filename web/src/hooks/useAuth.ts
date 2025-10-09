import {useContext} from "react";
import type {AuthContextType} from "@/types/auth";
import {AuthContext} from "@/api/auth/context.tsx";

export function useAuth(): AuthContextType {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return ctx;
}
