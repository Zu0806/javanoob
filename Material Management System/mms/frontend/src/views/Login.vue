<template>
  <div class="login-shell">
    <div class="login-card">
      <div class="logo-wrap">
        <img class="logo" :src="catbox" alt="logo" />
      </div>

      <h1 class="title">登入</h1>
      <p class="subtitle">登入後開始整理你的家庭物資</p>

      <form @submit.prevent="onSubmit">
        <label class="label">帳號</label>
        <input class="input" v-model.trim="username" autocomplete="username" />

        <label class="label">密碼</label>
        <input class="input" v-model="password" type="password" autocomplete="current-password" />

        <div v-if="error" class="error">{{ error }}</div>

        <button class="btn" type="submit" :disabled="loading">
          {{ loading ? "登入中..." : "登入" }}
        </button>
      </form>

      <div class="footer">
        還沒有帳號？ <RouterLink class="link" to="/register">建立帳號</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginApi, setStoredUser } from "../api/auth";
import catbox from "../assets/img/catbox.png"; // ✅ 確保你有放這張圖

const router = useRouter();

const username = ref("");
const password = ref("");
const error = ref("");
const loading = ref(false);

async function onSubmit() {
  error.value = "";
  loading.value = true;
  try {
    const data = await loginApi({ username: username.value, password: password.value });
    setStoredUser(data.user);
    router.replace("/items");
  } catch (e) {
    error.value = e?.response?.data?.message || "登入失敗：請確認帳號密碼";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-shell{
  min-height:100vh;
  display:flex;
  align-items:center;
  justify-content:center;
  background:#eef6ff;
  padding:24px;
}
.login-card{
  width:min(520px,100%);
  background:#fff;
  border-radius:22px;
  box-shadow:0 20px 60px rgba(15,23,42,.12);
  padding:28px;
}
.logo-wrap{ display:flex; justify-content:center; margin:10px 0 12px; }
.logo{ width:84px; height:auto; }
.title{ text-align:center; margin:0; font-size:30px; font-weight:800; letter-spacing:.5px; }
.subtitle{ text-align:center; margin:6px 0 18px; color:#64748b; font-size:14px; }

.label{ display:block; margin:14px 0 8px; font-weight:700; color:#0f172a; }
.input{
  width:100%;
  border:1px solid #cfe0ff;
  border-radius:14px;
  padding:14px 16px;
  outline:none;
  font-size:16px;
  background:#f6fbff;
}
.input:focus{ border-color:#3b82f6; box-shadow:0 0 0 4px rgba(59,130,246,.12); }

.error{
  margin-top:14px;
  background:#ffecec;
  border:1px solid #ffb7b7;
  color:#c02626;
  padding:12px 14px;
  border-radius:14px;
  font-weight:700;
}
.btn{
  width:100%;
  margin-top:16px;
  border:none;
  border-radius:16px;
  padding:14px 16px;
  font-size:18px;
  font-weight:800;
  color:#fff;
  background:#2563eb;
  cursor:pointer;
}
.btn:disabled{ opacity:.7; cursor:not-allowed; }
.footer{ text-align:center; margin-top:14px; color:#64748b; }
.link{ color:#2563eb; font-weight:800; text-decoration:none; }
</style>
