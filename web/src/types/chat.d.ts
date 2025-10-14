import type {Message} from "@/types/message";

export interface Chat {
  id?: string;
  title?: string;
  active?: boolean;
  messages?: Message[];
  createdAt?: string;
  updatedAt?: string;
}
