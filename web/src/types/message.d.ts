export interface Message {
  id: string;
  chatId?: string;
  type: "USER" | "BOT";
  content: string;
  createdAt?: string;
  updatedAt?: string;
}
