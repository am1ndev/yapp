import React, {useEffect, useRef, useState} from "react";
import {useAuth} from "@/hooks/useAuth.ts";
import {cn} from "@/lib/utils.ts";
import {getChat, postChat, updateChat} from "@/services/chats.ts";
import Navbar from "@/components/Navbar.tsx";
import type {Chat} from "@/types/chat";
import {ChatInput} from "@/components/ChatInput.tsx";
import type {Message} from "@/types/message";

export default function ChatPage() {
  const {authenticated, user, logout} = useAuth();

  const [input, setInput] = useState("");
  const [chat, setChat] = useState<Chat>();
  const [messages, setMessages] = useState<Message[] | undefined>([]);

  const [submitting, setSubmitting] = useState(false);

  const messagesRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesRef.current?.scrollIntoView({behavior: "smooth"});
  }, [messages]);

  useEffect(() => {
    const load = async () => {
      try {
        const loaded = await getChat();
        setChat(loaded);
        setMessages(loaded.messages);
      } catch {
        console.error("Failed to load chats history");
      }
    };
    load();
  }, []);

  const onUpdate = async (t: string) => {
    try {
      const request = {title: t};
      const updated = await updateChat(request);

      setChat(updated);
    } catch {
      alert("Failed to update title");
    }
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) {
      return;
    }

    try {
      setSubmitting(true);
      setInput("");

      const userMsg: Message = {
        type: "USER",
        content: input,
        createdAt: new Date().toISOString(),
      };
      setMessages(prev => [...prev, userMsg]);

      const botMsg = await postChat(userMsg);
      setMessages(prev => [...prev, botMsg]);

      setSubmitting(false);
    } catch {
      alert("Failed to send message");
    }
  };

  return (
    <>
      <Navbar
        authenticated={authenticated}
        chat={chat}
        user={user}
        onLogout={logout}
        onUpdate={onUpdate}
      />

      <div className="flex flex-col h-screen bg-background">
        <div className="pt-16 flex-1 overflow-y-auto pb-[7rem]">
          <div className="mx-auto w-full max-w-3xl px-4 py-6 space-y-4">
            {messages.length === 0 ? (
              <div className="flex flex-col items-center justify-center text-center mt-32 text-muted-foreground">
                <h2 className="text-lg font-semibold mb-2">{`👋 Hey ${user?.name}, Welcome to yapp.`}</h2>
                <p className="text-sm">
                  Start a conversation by typing your message below!
                </p>
              </div>
            ) : (
              messages.map((message, index) => (
                <div
                  key={index}
                  className={cn(
                    "flex w-max max-w-[75%] flex-col gap-2 rounded-lg px-3 py-2 text-sm shadow break-words whitespace-pre-wrap",
                    message.type === "USER"
                      ? "bg-primary text-primary-foreground ml-auto"
                      : "bg-muted"
                  )}
                >
                  {message.content}
                </div>
              ))
            )}
            <div ref={messagesRef}/>
          </div>
        </div>
        <ChatInput
          submitting={submitting}
          input={input}
          setInput={setInput}
          onSubmit={onSubmit}
        />
      </div>
    </>
  );
}
