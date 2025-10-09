export const API = {
  baseURL: import.meta.env.VITE_API_BASE as string,
  routes: {
    login: "/auth/login",
    signup: "/auth/signup",
    refresh: "/auth/refresh",
    logout: "/auth/logout",
    me: "/users/me",
    chats: "/chats"
  }
};

export const CLIENT = {
  refreshBeforeExpSec: 60,
  fallbackRefreshMs: 30_000
};
