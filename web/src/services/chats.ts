import api from "@/api/client";
import {API} from "@/config/env";
import type {Chat} from "@/types/chat";
import type {Message} from "@/types/message";

export async function getChat(): Promise<Chat> {
  const res = await api.get<Chat>(API.routes.chats);
  return res.data;
}

export async function ask(content: string) {
  const res = await api.post<Message>(API.routes.chats, {content});
  return res.data;
}

