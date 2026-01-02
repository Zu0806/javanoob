<template>
  <div class="container">
    <div class="header-row">
      <div>
        <div class="page-title">📝 記事本總覽</div>
        <div style="color: var(--muted); font-size: 13px; margin-top: 4px;">
          查看所有家庭成員的留言與代辦事項
        </div>
      </div>

      <a href="#" class="btn btn-outline" @click.prevent="goItems">
        &larr; 回到首頁
      </a>
    </div>

    <!-- 新增 -->
    <form @submit.prevent="createNote" style="display:flex; gap:8px; margin-bottom:24px;">
      <input
        v-model.trim="newText"
        type="text"
        required
        placeholder="在此輸入新的記事..."
        style="flex:1; padding:10px 14px; border:1px solid var(--border); border-radius:12px; outline:none;"
      />
      <button type="submit" class="btn btn-primary" :disabled="creating">
        {{ creating ? "新增中..." : "新增" }}
      </button>
    </form>

    <!-- 列表 -->
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
            :class="[{ 'done-row': !!n.done }]"
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

            <td style="font-size:12px; color:#64748b;">
              {{ formatDateTime(n.createdAt) }}
            </td>

            <td>
              <form class="inline" @submit.prevent="toggle(n.id)">
                <button
                  type="submit"
                  class="btn btn-sm"
                  :style="n.done
                    ? 'background:#e2e8f0; color:#64748b;'
                    : 'background:#dcfce7; color:#166534; border:1px solid #bbf7d0;'"
                >
                  {{ n.done ? "復原" : "標示完成" }}
                </button>
              </form>
            </td>
          </tr>
        </tbody>
      </table>

      <div
        v-if="!loading && notes.length === 0"
        style="text-align:center; padding:40px; color:var(--muted);"
      >
        目前沒有任何記事，去新增一條吧！
      </div>

      <div v-if="loading" style="text-align:center; padding:28px; color:var(--muted);">
        載入中...
      </div>

      <div v-if="error" style="text-align:center; padding:18px; color:#b91c1c;">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

const notes = ref([]);
const loading = ref(false);
const creating = ref(false);
const error = ref("");

const newText = ref("");

// 讀取
async function fetchNotes() {
  loading.value = true;
  error.value = "";
  try {
    const res = await axios.get("/api/notes");
    notes.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    error.value = "讀取記事失敗（請確認後端 9090 正在跑 /api/notes）";
  } finally {
    loading.value = false;
  }
}

// 新增
async function createNote() {
  if (!newText.value) return;
  creating.value = true;
  error.value = "";
  try {
    await axios.post("/api/notes", { text: newText.value });
    newText.value = "";
    await fetchNotes();
  } catch (e) {
    error.value = "新增失敗（請確認 POST /api/notes 可用）";
  } finally {
    creating.value = false;
  }
}

// 切換完成
async function toggle(id) {
  error.value = "";
  try {
    await axios.post(`/api/notes/${id}/toggle`);
    await fetchNotes();
  } catch (e) {
    error.value = "切換狀態失敗（請確認 POST /api/notes/{id}/toggle 可用）";
  }
}

function goItems() {
  router.push("/items");
}

// createdAt 可能是：
// - "2025-12-17T10:00:00"
// - 或陣列（Spring Jackson 有時會）[yyyy,MM,dd,HH,mm,ss]
function formatDateTime(v) {
  if (!v) return "-";

  // array format
  if (Array.isArray(v) && v.length >= 5) {
    const [y, m, d, hh, mm] = v;
    return `${y}/${String(m).padStart(2, "0")}/${String(d).padStart(2, "0")} ${String(hh).padStart(2, "0")}:${String(mm).padStart(2, "0")}`;
  }

  // string format
  const s = String(v);
  // try Date parse
  const dt = new Date(s);
  if (!Number.isNaN(dt.getTime())) {
    const y = dt.getFullYear();
    const m = String(dt.getMonth() + 1).padStart(2, "0");
    const d = String(dt.getDate()).padStart(2, "0");
    const hh = String(dt.getHours()).padStart(2, "0");
    const mm = String(dt.getMinutes()).padStart(2, "0");
    return `${y}/${m}/${d} ${hh}:${mm}`;
  }

  // fallback (raw)
  return s.replace("T", " ").slice(0, 16);
}

onMounted(fetchNotes);
</script>

<style scoped>
/* 讓 notes.html 的 class 生效：你 global.css 應該已經有更完整的版本，這裡只補最小需要 */
.header-row{
  display:flex;
  align-items:flex-start;
  justify-content:space-between;
  gap:16px;
  margin-bottom:16px;
}
.page-title{
  font-weight:800;
  font-size:20px;
}
.badge-user{
  display:inline-flex;
  align-items:center;
  padding:4px 10px;
  border-radius:999px;
  background:#eef2ff;
  border:1px solid #dbeafe;
  color:#1e40af;
  font-size:12px;
  font-weight:700;
}
.done-row td{
  background:#f8fafc;
  opacity:.85;
}
.content-text{
  font-weight:700;
}
.hide-mobile{ }
@media (max-width:640px){
  .hide-mobile{ display:none; }
}
</style>
