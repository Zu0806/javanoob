<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import catbox from "../assets/img/catbox.png";
import { registerApi } from "../api/auth";

const router = useRouter();

const displayName = ref("");
const username = ref("");
const password = ref("");

const error = ref("");
const loading = ref(false);

async function onSubmit() {
  error.value = "";
  loading.value = true;

  try {
    await registerApi({
      username: username.value.trim(),
      password: password.value,
      displayName: displayName.value.trim() || null,
    });

    // ✅ 註冊成功 -> 去登入頁
    router.replace("/login");
  } catch (e) {
    const msg =
      e?.response?.data?.message ||
      e?.response?.data?.error ||
      e?.response?.data?.msg ||
      "註冊失敗：請看後端回應";
    error.value = msg;
    console.error("register failed:", e?.response?.status, e?.response?.data || e);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="register-shell">
    <div class="register-card">
      <div class="logo-wrap">
        <img :src="catbox" alt="catbox" />
      </div>

      <h1 class="title">建立帳號</h1>
      <p class="subtitle">註冊後就能使用物資/記事功能</p>

      <form class="form" @submit.prevent="onSubmit">
        <label class="label">顯示名稱（可選）</label>
        <input class="input" v-model="displayName" placeholder="Morris" autocomplete="name" />

        <label class="label">帳號</label>
        <input class="input" v-model="username" placeholder="morris940216" autocomplete="username" required />

        <label class="label">密碼</label>
        <input class="input" v-model="password" type="password" placeholder="••••••••" autocomplete="new-password" required />

        <div v-if="error" class="error-box">註冊失敗：{{ error }}</div>

        <button class="btn-primary" :disabled="loading">
          {{ loading ? "註冊中…" : "註冊" }}
        </button>

        <div class="footer">
          已有帳號？
          <a class="link" href="/login">前往登入</a>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.register-shell{
  min-height:100vh;
  display:flex;
  align-items:center;
  justify-content:center;
  background:#eef6ff;
  padding:24px;
}
.register-card{
  width:min(520px,100%);
  background:#fff;
  border-radius:22px;
  box-shadow:0 20px 60px rgba(15,23,42,.12);
  padding:28px;
}
.logo-wrap{
  display:flex;
  justify-content:center;
  margin-bottom:10px;
}
.logo-wrap img{ width:88px; height:auto; }
.title{ text-align:center; font-size:28px; margin:6px 0 4px; }
.subtitle{ text-align:center; color:#64748b; margin:0 0 18px; }

.form{ display:flex; flex-direction:column; gap:10px; }
.label{ font-weight:700; color:#111827; margin-top:6px; }
.input{
  border:1px solid #cfe1ff;
  border-radius:14px;
  padding:14px 14px;
  outline:none;
  font-size:16px;
  background:#f7fbff;
}
.input:focus{ border-color:#3b82f6; box-shadow:0 0 0 4px rgba(59,130,246,.12); }

.error-box{
  background:#ffecec;
  border:1px solid #ffbcbc;
  color:#b91c1c;
  padding:12px 14px;
  border-radius:12px;
  margin-top:6px;
}
.btn-primary{
  margin-top:8px;
  background:#2563eb;
  color:#fff;
  border:none;
  border-radius:14px;
  padding:14px 16px;
  font-weight:800;
  font-size:18px;
  cursor:pointer;
}
.btn-primary:disabled{ opacity:.7; cursor:not-allowed; }

.footer{ text-align:center; margin-top:10px; color:#475569; }
.link{ color:#2563eb; font-weight:800; text-decoration:none; }
.link:hover{ text-decoration:underline; }
</style>
