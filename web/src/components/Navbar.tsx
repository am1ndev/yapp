import {useEffect, useState} from "react"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {ChevronDown, LogOut, Settings, Stars, SunMoon} from "lucide-react"
import {Avatar, AvatarFallback} from "@/components/ui/avatar";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {useTheme} from "@/components/ui/theme-provider.tsx";
import type {User} from "@/types/user";
import type {Chat} from "@/types/chat";

interface NavbarProps {
  authenticated: boolean
  user?: User | null
  chat?: Chat | null
  onLogout?: () => void
  onUpdate?: (title: string) => void
}

export default function Navbar({authenticated, user, chat, onLogout, onUpdate}: NavbarProps) {
  const {theme, setTheme} = useTheme();
  const location = useLocation();
  const nav = useNavigate();

  const [editing, setEditing] = useState(false);
  const [newTitle, setNewTitle] = useState(chat?.title ?? "");

  useEffect(() => {
    if (chat?.title) {
      setNewTitle(chat.title);
    }
  }, [chat?.title]);

  const chatting = location.pathname === "/chat";

  const onThemeToggle = () => setTheme(theme === "light" ? "dark" : "light");
  const onNavigation = () => nav(authenticated ? "/chat" : "/login");

  const onTitleUpdate = () => {
    setEditing(false)
    if (newTitle && newTitle.trim() !== chat?.title) {
      onUpdate?.(newTitle.trim())
    }
  };

  return (
    <header className="fixed top-0 z-50 w-full h-16 border-b border-border bg-background/15 backdrop-blur-md">
    <div className="max-w-6xl relative mx-auto h-full flex items-center justify-between px-4">
        <div className="flex items-center space-x-2 w-1/2">
          <Link className={"font-semibold text-xl"} to="/">
            {'✦ yapp.'}
          </Link>
        </div>
        {authenticated && chatting && (
          <div className="flex-1 flex justify-center">
            {editing ? (
              <Input
                className="w-48 text-center"
                value={newTitle}
                onChange={(e) => setNewTitle(e.target.value)}
                onBlur={onTitleUpdate}
                onKeyDown={(e) => e.key === "Enter" && onTitleUpdate()}
                autoFocus
              />
            ) : (
              <span
                className="w-48 text-center font-semibold cursor-pointer hover:underline"
                onClick={() => setEditing(true)}
              >
                {chat?.title || "Untitled Chat"}
              </span>
            )}
          </div>
        )}
        <div className="flex justify-end w-1/2">
          {authenticated && chatting ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button
                  className="flex items-center space-x-2 rounded-md px-2 py-1 hover:bg-muted transition-colors cursor-pointer">
                  <Avatar className="border shadow-md">
                    <AvatarFallback className="bg-gradient-to-br from-gray-700 to-gray-800 text-white">
                      {user?.name?.charAt(0).toUpperCase()}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex flex-col text-left">
                    <span className="text-sm font-medium">{user?.name}</span>
                    <span className="text-xs text-muted-foreground truncate max-w-[120px]">
                      {user?.email}
                    </span>
                  </div>
                  <ChevronDown className="h-4 w-4 ml-1 text-muted-foreground"/>
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <DropdownMenuItem className="flex-col items-start" disabled>
                  <div className="flex items-center gap-1">
                    <Stars className="mr-1"/>
                    <span>Upgrade</span>
                  </div>
                  <p className="text-muted-foreground">
                    get yapp. plus now
                  </p>
                </DropdownMenuItem>
                <DropdownMenuSeparator/>
                <DropdownMenuItem onClick={onThemeToggle}>
                  <SunMoon className="mr-1"/>
                  Theme
                </DropdownMenuItem>
                <DropdownMenuItem disabled>
                  <Settings className="mr-1"/>
                  Settings
                </DropdownMenuItem>
                <DropdownMenuSeparator/>
                <DropdownMenuItem variant="destructive" onClick={onLogout}>
                  <LogOut className="mr-1"/>
                  Logout
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Button className="w-24" onClick={onNavigation}>
              {authenticated ? 'Chat' : 'Login'}
            </Button>
          )}
        </div>
      </div>
    </header>
  )
}
