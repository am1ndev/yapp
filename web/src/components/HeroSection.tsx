import {Badge} from "@/components/ui/badge";
import {ArrowUpRight} from "lucide-react";
import {Link, useNavigate} from "react-router-dom";
import React, {useState} from "react";
import {InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput} from "@/components/ui/input-group.tsx";

const HeroSection = () => {
  const nav = useNavigate();
  const [email, setEmail] = useState("");

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;

    nav(`/signup?email=${encodeURIComponent(email.trim())}`);
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center px-6 overflow-hidden">
      <div className="relative z-10 text-center max-w-4xl">
        <Badge
          variant="secondary"
          className="rounded-full py-1 border-border"
          asChild
        >
          <Link
            to="https://github.com/am1ndev/yapp"
            target="_blank"
            rel="noopener noreferrer"
          >
            Just released v1.0.0
            <ArrowUpRight className="ml-1 size-4"/>
          </Link>
        </Badge>

        <h1
          className="mt-6 text-4xl sm:text-5xl md:text-6xl lg:text-7xl md:leading-[1.2] font-semibold tracking-tighter">
          <i>
            Meet yapp. — your friend that actually listens
          </i>
        </h1>
        <p className="mt-6 md:text-lg">
          Talk, learn, and create with your own intelligent chat companion.
          yapp remembers context, speaks your language, and keeps every chat private.
        </p>

        <div className="mt-12 flex items-center justify-center gap-4">
          <form onSubmit={onSubmit} className="[--radius:9999px] w-full max-w-md">
            <InputGroup className="bg-background h-12 text-base">
              <InputGroupInput
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Your email address..."
                required
              />
              <InputGroupAddon align="inline-end">
                <InputGroupButton
                  type="submit"
                  variant="default"
                  className="rounded-full px-6 h-full"
                  disabled={!email.trim()}
                >
                  Get Started
                </InputGroupButton>
              </InputGroupAddon>
            </InputGroup>
          </form>
        </div>
      </div>
    </div>
  );
};

export default HeroSection;
