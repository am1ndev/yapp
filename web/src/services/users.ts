import {api} from "../api/client";
import type {User} from "@/types/user";
import {API} from "@/config/env.ts";

export async function user(): Promise<User> {
  const res = await api.get<User>(API.routes.me);
  return res.data;
}
