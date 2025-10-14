import {FishSymbol} from "lucide-react"
import {cn} from "@/lib/utils"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {useAuth} from "@/hooks/useAuth.ts";
import React, {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {Spinner} from "@/components/ui/spinner.tsx";

export default function LoginPage() {
  const nav = useNavigate();
  const {login} = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      await login(email, password);
      nav('/chat');
    } catch (err) {
      alert("Login failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="bg-background flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
      <div className="w-full max-w-sm">
        <div className={cn("flex flex-col gap-6")}>
          <form onSubmit={onSubmit}>
            <div className="flex flex-col gap-6">
              <div className="flex flex-col items-center gap-2">
                <a
                  href="/"
                  className="flex flex-col items-center gap-2 font-medium"
                >
                  <div className="flex size-8 items-center justify-center rounded-md">
                    <FishSymbol className="size-10"/>
                  </div>
                  <span className="sr-only">yapp.</span>
                </a>
                <h1 className="text-xl font-bold">Welcome Back to Your Account</h1>
                <div className="text-center text-sm">
                  Don&apos;t have an account? <Link to="/signup" className="underline underline-offset-4">Sign up</Link>
                </div>
              </div>

              <div className="flex flex-col gap-6">
                <div className="grid gap-3">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="Email"
                    required
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                  />
                </div>
                <div className="grid gap-3">
                  <div className="flex items-center">
                    <Label htmlFor="password">Password</Label>
                    <a
                      href="#"
                      className="text-muted-foreground ml-auto inline-block text-sm underline-offset-4 hover:underline"
                    >
                      Forgot your password?
                    </a>
                  </div>
                  <Input
                    id="password"
                    type="password"
                    placeholder="Password"
                    required
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                  />
                </div>

                <Button type="submit" className="w-full" disabled={submitting}>
                  {submitting && <Spinner/>}
                  Login
                </Button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
