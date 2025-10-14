import React, {useCallback, useEffect, useMemo, useRef, useState} from "react";
import type {AuthContextType} from "@/types/auth";
import api, {refresh} from "@/api/client.ts";
import {parseJwt} from "@/lib/jwt.ts";
import {API, CLIENT} from "@/config/env.ts";
import {setToken} from "@/api/store.ts";
import type {Token} from "@/types/token";
import type {User} from "@/types/user";
import {AuthContext} from "./context.tsx";
import {user} from "@/services/users.ts";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
  const [actor, setActor] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const timer = useRef<number | null>(null);

  const clearTimer = () => {
    if (timer.current) {
      window.clearTimeout(timer.current);
      timer.current = null;
    }
  };

  const schedule = (token: string) => {
    clearTimer();

    const payload = parseJwt(token);
    const now = Math.floor(Date.now() / 1000);

    const exp = payload?.exp;
    if (!exp) {
      timer.current = window.setTimeout(() => void attempt(), CLIENT.fallbackRefreshMs);
      return;
    }

    const secs = exp - now;
    const delay = Math.max((secs - CLIENT.refreshBeforeExpSec) * 1000, 5_000);
    timer.current = window.setTimeout(() => void attempt(), delay);
  };

  const attempt = useCallback(async () => {
    const token = await refresh();
    if (token) {
      schedule(token);
    } else {
      clearTimer();
      setToken(null);
      setActor(null);
    }
    return token;
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const res = await api.post<Token>(API.routes.login, {email, password});

    const token = (res?.data?.token) as string | undefined;
    if (!token) {
      throw new Error("Missing token on login");
    }

    setToken(token);
    schedule(token);

    const u = await user();
    setActor(u);
    return u;
  }, []);

  const signup = useCallback(async (name: string, email: string, password: string) => {
    await api.post(API.routes.signup, {name, email, password});
    return login(email, password);
  }, []);

  const logout = useCallback(async () => {
    try {
      await api.post(API.routes.logout, {});
    } finally {
      clearTimer();
      setToken(null);
      setActor(null);
    }
  }, []);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const token = await attempt();
        if (token) {
          schedule(token);
          const u = await user();
          if (mounted) setActor(u);
        } else {
          if (mounted) setActor(null);
        }
      } catch {
        if (mounted) {
          setToken(null);
          setActor(null);
        }
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => {
      mounted = false;
      clearTimer();
    };
  }, []);

  const value = useMemo<AuthContextType>(() => ({
    user: actor,
    authenticated: !!actor,
    loading,
    login,
    signup,
    logout
  }), [actor, loading, login, signup, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
