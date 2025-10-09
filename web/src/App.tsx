import {BrowserRouter, Route, Routes} from "react-router-dom";
import LoginPage from "@/pages/Login.tsx";
import SignupPage from "@/pages/Signup.tsx";
import {ProtectedRoute} from "@/components/ProtectedRounte.tsx";
import ChatPage from "@/pages/Chat.tsx";
import HomePage from "@/pages/Home.tsx";
import {ThemeProvider} from './components/ui/theme-provider.tsx';
import {Toaster} from "sonner";

function App() {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomePage/>}/>
          <Route path="/login" element={<LoginPage/>}/>
          <Route path="/signup" element={<SignupPage/>}/>
          <Route
            path="/chat"
            element={
              <ProtectedRoute>
                <ChatPage/>
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-center" closeButton/>
    </ThemeProvider>
  );
}

export default App;
