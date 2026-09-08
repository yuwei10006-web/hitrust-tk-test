import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// 後端 application-dev.yml 的 frontend-result-url 寫死 http://localhost:5173，
// 這裡用 strictPort 鎖住，避免 5173 被佔用時 Vite 自動跳到別的 port 導致對不起來。
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
  },
});
