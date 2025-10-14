import React, {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useAuth} from "@/hooks/useAuth.ts";
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {cn} from "@/lib/utils.ts";
import {FlameKindling} from "lucide-react";
import {Spinner} from "@/components/ui/spinner.tsx";

export default function SignupPage() {
  const {signup} = useAuth();
  const nav = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      await signup(name, email, password);
      nav('/chat');
    } catch (err) {
      alert("Signup failed");
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
                    <FlameKindling className="size-10"/>
                  </div>
                  <span className="sr-only">Yapp Chat.</span>
                </a>
                <h1 className="text-xl font-bold">Create Your Free Account</h1>
                <div className="text-center text-sm">
                  Already have an account? <Link to="/login" className="underline underline-offset-4">Login</Link>
                </div>
              </div>

              <div className="flex flex-col gap-6">
                <div className="grid gap-3">
                  <Label htmlFor="name">Name</Label>
                  <Input
                    id="name"
                    type="text"
                    placeholder="Name"
                    required
                    value={name}
                    onChange={e => setName(e.target.value)}
                  />
                </div>
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
                  <Label htmlFor="password">Password</Label>
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
                  Sign up
                </Button>
              </div>
            </div>
          </form>

          <div
            className="text-muted-foreground *:[a]:hover:text-primary text-center text-xs text-balance *:[a]:underline *:[a]:underline-offset-4">
            By clicking continue, you agree to our <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
          </div>
        </div>
      </div>
    </div>
  );
}
