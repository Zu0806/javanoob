import axios from "axios";

const http = axios.create({
  baseURL: "http://localhost:9090",
  withCredentials: false, // ✅ 讓 session cookie 能進出
  headers: { "Content-Type": "application/json" },
});

export default http;
