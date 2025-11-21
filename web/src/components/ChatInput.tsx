import React, {useEffect, useRef} from "react";
import {Button} from "@/components/ui/button.tsx";
import {Textarea} from "@/components/ui/textarea.tsx";
import {ArrowUpIcon} from "lucide-react";
import {cn} from "@/lib/utils.ts";
import {Spinner} from "@/components/ui/spinner.tsx";

interface ChatInputProps {
  submitting: boolean
  input: string
  setInput: (v: string) => void
  onSubmit: (e: React.FormEvent) => void
}

export function ChatInput({submitting, input, setInput, onSubmit}: ChatInputProps) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const el = textareaRef.current;
    if (!el) return;

    el.style.height = "auto";
    el.style.height = Math.min(el.scrollHeight, 5 * 24) + "px"; // max 5 lines
  }, [input]);

  return (
    <form
      onSubmit={onSubmit}
      className="fixed bottom-0 left-0 w-full px-4 py-3 flex justify-center"
    >
      <div className="relative w-full max-w-3xl flex items-end">
        {/* <GlowEffect*/}
        {/*  colors={['#0894FF', '#C959DD', '#FF2E54', '#FF9004']}*/}
        {/*  blur='medium'*/}
        {/*  mode="rotate"*/}
        {/*/>*/}
        <Textarea
          ref={textareaRef}
          id="message"
          placeholder="Message to yapp..."
          className={cn(
            "w-full resize-none border-0 bg-black/10 dark:bg-white/10 backdrop-blur-lg ",
            "focus-visible:ring-0 focus:outline-none rounded-2xl px-3 py-2 pr-10 text-sm leading-relaxed shadow-md"
          )}
          autoComplete="off"
          rows={1}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault(); // prevent new line
              if (input.trim().length > 0) {
                onSubmit(e);
              }
            }
          }}
        />
        <Button
          type="submit"
          size="icon"
          className="absolute right-3 bottom-2 h-7 w-7 rounded-full"
          disabled={input.trim().length === 0}
        >
          {submitting ? <Spinner/> : <ArrowUpIcon className="size-4"/>}
          <span className="sr-only">Send</span>
        </Button>
      </div>
    </form>
  );
}
