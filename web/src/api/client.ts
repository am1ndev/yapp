import axios, {AxiosError, type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig} from "axios";
import {API} from "@/config/env";
import {getToken, setToken} from "./store.ts";
import Cookies from "js-cookie";

let refreshing: Promise<string | null> | null = null;
let subscribers: Array<(token: string | null) => void> = [];

/**
 * helpers
 */

function subscribe(cb: (token: string | null) => void) {
  subscribers.push(cb);
}

function notify(token: string | null) {
  subscribers.forEach((cb) => cb(token));
  subscribers = [];
}

function csrf(): string | null {
  return Cookies.get("XSRF-TOKEN") ?? "";
}

function result(token: string | null): string | null {
  setToken(token);
  notify(token);
  return token;
}

/**
 * api instance
 */

export const api: AxiosInstance = axios.create({
  baseURL: API.baseURL,
  withCredentials: true,
  headers: {Accept: "application/json"}
});

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getToken();
  if (token) {
    config.headers.set("Authorization", `Bearer ${token}`);
  }

  const xsrf = csrf();
  if (xsrf) {
    config.headers.set("X-XSRF-TOKEN", xsrf);
  }

  return config;
});

api.interceptors.response.use((res) => res, async (error: AxiosError) => {
    const original = error.config as AxiosRequestConfig & { _retry?: boolean } | undefined;
    if (!original || error.response?.status !== 401) {
      return Promise.reject(error);
    }

    const url = original.url ?? "";
    if ([API.routes.refresh, API.routes.login, API.routes.signup].some((p) => url.endsWith(p))) {
      return Promise.reject(error);
    }

    if (original._retry) {
      return Promise.reject(error);
    }

    original._retry = true;

    if (refreshing) {
      return new Promise((resolve, reject) => {
        subscribe(async (token) => {
          if (!token) {
            reject(error);
            return;
          }
          if (original.headers) {
            original.headers["Authorization"] = `Bearer ${token}`;
          }
          try {
            const res = await api(original);
            resolve(res);
          } catch (e) {
            reject(e);
          }
        });
      });
    }

    try {
      const token = await refresh();
      if (!token) {
        return Promise.reject(error);
      }
      if (original.headers) {
        original.headers["Authorization"] = `Bearer ${token}`;
      }
      return api(original);
    } catch (e) {
      return Promise.reject(e);
    }
  }
);

/**
 * refresh instance
 */

const refresher = axios.create({
  baseURL: API.baseURL,
  withCredentials: true,
  headers: {Accept: "application/json"}
});

export async function refresh(): Promise<string | null> {
  if (refreshing) return refreshing;

  refreshing = (async () => {
    try {
      const xsrf = csrf();
      const headers = xsrf ? {"X-XSRF-TOKEN": xsrf, "Content-Type": "application/json"} : undefined;

      const res = await refresher.post<{ token?: string }>(API.routes.refresh, {}, {headers});
      const token = res.data?.token ?? null;

      return result(token);
    } catch (err) {
      return result(null);
    } finally {
      refreshing = null;
    }
  })();

  return refreshing;
}

export default api;
