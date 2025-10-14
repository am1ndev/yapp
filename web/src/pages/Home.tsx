import {useAuth} from "@/hooks/useAuth.ts";
import Navbar from "@/components/Navbar.tsx";
import LiquidEther from '@/components/LiquidEther';
import {useTheme} from "next-themes";
import HeroSection from "@/components/HeroSection.tsx";

export default function HomePage() {
  const {authenticated} = useAuth();
  const {theme} = useTheme();

  const colors = theme === "dark"
    ? ['#22D3EE', '#3B82F6', '#8B5CF6']
    : ['#A5B4FC', '#C7D2FE', '#E0E7FF']

  return (
    <div className="relative w-full h-screen overflow-hidden">
      <div className="absolute inset-0 -z-10">
        <LiquidEther
          colors={colors}
          mouseForce={20}
          cursorSize={100}
          isViscous={false}
          viscous={30}
          iterationsViscous={32}
          iterationsPoisson={32}
          resolution={0.5}
          isBounce={false}
          autoDemo={true}
          autoSpeed={0.5}
          autoIntensity={2.2}
          takeoverDuration={0.25}
          autoResumeDelay={3000}
          autoRampDuration={0.6}
        />
      </div>

      <Navbar authenticated={authenticated}  />

      <HeroSection/>
    </div>
  );
}
