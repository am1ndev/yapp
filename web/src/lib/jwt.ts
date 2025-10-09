export type JwtPayload = {
  sub?: string;
  exp?: number;
  iat?: number;
  [k: string]: unknown;
};

/** decode base64url payload of JWT without validation (for expiry checks/local info only) */
export function parseJwt(token: string | null): JwtPayload | null {
  if (!token) return null;
  try {
    const parts = token.split(".");
    if (parts.length < 2) {
      return null;
    }

    const payload = JSON.parse(decodeURIComponent(atob(parts[1]).split('').map(c =>
      '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    ).join('')));

    return payload as JwtPayload;
  } catch {
    return null;
  }
}



