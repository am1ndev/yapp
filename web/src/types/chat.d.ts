import type {Message} from "@/types/message";

export interface Chat {
  id: string;
  userId?: string;
  title?: string;
  messages: Message[];
  createdAt?: string;
  updatedAt?: string;
}
