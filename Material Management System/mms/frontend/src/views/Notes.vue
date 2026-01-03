<template>
  <div class="page">
    <div class="container">
      <div class="header-row">
        <div>
          <div class="page-title">📝 記事本總覽</div>
          <div class="page-sub">查看所有家庭成員的留言與代辦事項</div>
        </div>

        <!-- ✅ 新分頁裡回到主頁 -->
        <a href="/items" class="btn btn-outline">&larr; 回到首頁</a>
      </div>

      <!-- 新增 -->
      <form class="create-row" @submit.prevent="createNote">
        <input
          v-model="noteText"
          type="text"
          required
          placeholder="在此輸入新的記事..."
        />
        <button type="submit" class="btn btn-primary" :disabled="creating">
          {{ creating ? "新增中..." : "新增" }}
        </button>
      </form>

      <div style="overflow-x:auto;">
        <table>
          <thead>
            <tr>
              <th class="hide-mobile" width="5%">ID</th>
              <th width="45%">內容事項</th>
              <th width="15%">留言者</th>
              <th width="20%">時間</th>
              <th width="15%">狀態</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="n in notes"
              :key="n.id"
              :class="n.done ? 'done-row' : ''"
            >
              <td class="hide-mobile" style="color:#94a3b8;">
                {{ n.id }}
              </td>

              <td>
                <span class="content-text">{{ n.text }}</span>
              </td>

              <td>
                <span class="badge-user">
                  {{ n.authorName && n.authorName.trim() ? n.authorName : "-" }}
                </span>
              </td>

              <td style="font-size:12px;color:#64748b;">
                {{ formatDateTime(n.createdAt) }}
              </td>

              <td>
                <button
                  type="button"
                  class="btn btn-sm"
                  :style="n.done ? doneBtnStyle : todoBtnStyle"
                  @click="toggleNote(n.id)"
                  :disabled="togglingId === n.id"
                >
                  {{ n.done ? "復原" : "標示完成" }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!loading && notes.length === 0" class="empty">
          目前沒有任何記事，去新增一條吧！
        </div>

        <div v-if="loading" class="empty">載入中...</div>
        <div v-if="error" class="empty" style="color:#ef4444;">
          {{ error }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";

/** ✅ 後端在 9090 */
const API_BASE = "http://localhost:9090";

const notes = ref([]);
const noteText = ref("");

const loading = ref(false);
const creating = ref(false);
const togglingId = ref(null);
const error = ref("");

const doneBtnStyle =
  "background:#e2e8f0; color:#64748b; border:1px solid transparent;";
const todoBtnStyle =
  "background:#dcfce7; color:#166534; border:1px solid #bbf7d0;";

async function loadNotes() {
  loading.value = true;
  error.value = "";
  try {
    const res = await axios.get(`${API_BASE}/api/v2/notes`);
    notes.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    error.value = "載入失敗：請確認後端 9090 與 /api/v2/notes 是否正常。";
  } finally {
    loading.value = false;
  }
}

async function createNote() {
  const t = noteText.value.trim();
  if (!t) return;

  creating.value = true;
  error.value = "";
  try {
    await axios.post(`${API_BASE}/api/v2/notes`, { text: t });
    noteText.value = "";
    await loadNotes();
  } catch (e) {
    error.value = "新增失敗：請確認後端 POST /api/v2/notes 可用。";
  } finally {
    creating.value = false;
  }
}

async function toggleNote(id) {
  togglingId.value = id;
  error.value = "";
  try {
    await axios.post(`${API_BASE}/api/v2/notes/${id}/toggle`);
    await loadNotes();
  } catch (e) {
    error.value = "切換失敗：請確認後端 POST /api/v2/notes/{id}/toggle 可用。";
  } finally {
    togglingId.value = null;
  }
}

function formatDateTime(v) {
  if (!v) return "--";
  // 支援：字串、[y,m,d,hh,mm,...]、物件
  if (Array.isArray(v)) {
    const [y, m, d, hh = 0, mm = 0] = v;
    return `${y}/${pad(m)}/${pad(d)} ${pad(hh)}:${pad(mm)}`;
  }
  if (typeof v === "string") {
    // "2025-12-17T10:00:00" or "2025-12-17 10:00"
    const s = v.replace("T", " ");
    const [date, time] = s.split(" ");
    if (!date) return v;
    const [y, m, d] = date.split("-");
    const hhmm = (time || "").slice(0, 5);
    return `${y}/${m}/${d} ${hhmm || ""}`.trim();
  }
  return String(v);
}
function pad(n) {
  return String(n).padStart(2, "0");
}

onMounted(loadNotes);
</script>

<style>
/* ✅ 這份 style 直接照 notes.html 搬過來（包含背景/容器/表格/按鈕）:contentReference[oaicite:1]{index=1} */
:root{
  --blue:#2f80ed;
  --blue-dark:#1b4f9d;
  --blue-light:#e3f2ff;
  --border:#c8dcff;
  --fg:#1f2933;
  --muted:#6b7280;
  --radius:16px;
  --danger:#ef4444;
  --success:#16a34a;
}
*{ box-sizing:border-box; }

.page{
  min-height: 100vh;
  font-family:system-ui,-apple-system,"Segoe UI",sans-serif;
  color:var(--fg);
  margin:0;
  background:
    radial-gradient(circle at 0 0,rgba(47,128,237,.18),transparent 55%),
    radial-gradient(circle at 100% 100%,rgba(37,99,235,.16),transparent 55%),
    linear-gradient(135deg,#e6f3ff 0%,#f7fbff 45%,#e3f2ff 100%);
  background-attachment:fixed;
  padding: 20px;
}

/* 內容容器 */
.container {
  max-width: 1000px;
  margin: 40px auto;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* 標題 */
.header-row {
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:24px;
  border-bottom:2px solid #f0f4f8;
  padding-bottom:16px;
}
.page-title{
  font-size:24px;
  font-weight:800;
  color:#123763;
  letter-spacing:1px;
}
.page-sub{
  color: var(--muted);
  font-size: 13px;
  margin-top: 4px;
}

/* 按鈕 */
.btn{
  cursor:pointer;
  border-radius:12px;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  padding:8px 16px;
  font-size:14px;
  text-decoration:none;
  border:1px solid transparent;
  transition: all .2s;
  user-select:none;
}
.btn-primary{
  background: linear-gradient(135deg,#2f80ed,#2563eb);
  color:#fff;
}
.btn-primary:hover{ filter: brightness(1.1); }
.btn-outline{
  background: transparent;
  border-color:#2f80ed;
  color:#2f80ed;
}
.btn-outline:hover{ background:#eef6ff; }
.btn-sm{
  padding:4px 12px;
  font-size:12px;
  border-radius:999px;
}

/* 新增列 */
.create-row{
  display:flex;
  gap:8px;
  margin-bottom:24px;
}
.create-row input{
  flex:1;
  padding:10px 14px;
  border:1px solid var(--border);
  border-radius:12px;
  outline:none;
}

/* 表格 */
table{
  width:100%;
  border-collapse:collapse;
  margin-top:10px;
}
th,td{
  padding:12px 16px;
  text-align:left;
  border-bottom:1px solid #eef2f6;
}
th{
  background-color:#f8fafc;
  color:var(--muted);
  font-weight:600;
  font-size:13px;
}
td{
  font-size:14px;
  color:#334155;
}

/* done row */
.done-row td{
  color:#cbd5e1;
  background-color:#fafbfc;
}
.done-row .content-text{
  text-decoration: line-through;
}

/* badge user */
.badge-user{
  display:inline-block;
  background:#e0f2fe;
  color:#0369a1;
  padding:2px 8px;
  border-radius:6px;
  font-size:12px;
  font-weight:500;
}

.empty{
  text-align:center;
  padding:40px;
  color: var(--muted);
}

@media (max-width:600px){
  .container{ padding:15px; margin:20px 10px; }
  th,td{ padding:8px; font-size:13px; }
  .hide-mobile{ display:none; }
}
</style>
