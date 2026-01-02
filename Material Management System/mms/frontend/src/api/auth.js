import http from "./http";

const KEY = "mms_auth_user";

export function setStoredUser(user) {
  localStorage.setItem(KEY, JSON.stringify(user || null));
}

export function getStoredUser() {
  try {
    const s = localStorage.getItem(KEY);
    return s ? JSON.parse(s) : null;
  } catch {
    return null;
  }
}

export function clearStoredUser() {
  localStorage.removeItem(KEY);
}

export async function registerApi(payload) {
  const res = await http.post("/api/auth/register", payload);
  return res.data;
}

export async function loginApi(payload) {
  const res = await http.post("/api/auth/login", payload);
  return res.data;
}

export async function meApi() {
  const res = await http.get("/api/auth/me");
  return res.data;
}

export async function logoutApi() {
  const res = await http.post("/api/auth/logout");
  return res.data;
}
