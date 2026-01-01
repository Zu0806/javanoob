<template>
  <div class="container">
    <!-- ================== 新增 / 查詢 ================== -->
    <section class="section" id="section-create">
      <div class="section-header">
        <div>
          <div class="section-title">新增 / 查詢</div>
          <div class="section-sub">
            新增會先做「同名 + 同儲位 + 同到期日」判斷，若重複會跳出「合併 / 新建」。
          </div>
        </div>
      </div>

      <form class="grid-form" @submit.prevent>
        <div class="field">
          <label>物品名稱（查詢必填 / 新增必填）</label>
          <input v-model.trim="form.name" type="text" placeholder="例如：牛奶 / 螺絲 / 洗衣精" />
        </div>

        <div class="field">
          <label>SKU</label>
          <input v-model.trim="form.sku" type="text" placeholder="可留空" />
        </div>

        <div class="field">
          <label>分類（下拉 / 自訂）</label>
          <div class="row">
            <select v-model="categorySelect" @change="syncCategory">
              <option value="">（不指定）</option>
              <option v-for="c in meta.categories" :key="c" :value="c">{{ c }}</option>
              <option value="__NEW__">+ 自訂分類…</option>
            </select>
            <input
              v-show="categorySelect === '__NEW__'"
              v-model.trim="categoryCustom"
              type="text"
              placeholder="輸入自訂分類"
              @input="syncCategory"
            />
          </div>
        </div>

        <div class="field">
          <label>房間</label>
          <input v-model.trim="form.room" type="text" placeholder="例如：廚房 / 客廳 / 倉庫" />
        </div>

        <div class="field">
          <label>儲位（新增必填）</label>
          <div class="row">
            <select v-model="locationSelect" @change="syncLocation">
              <option value="">（請選擇）</option>
              <option v-for="l in meta.locations" :key="l" :value="l">{{ l }}</option>
              <option value="__NEW__">+ 自訂儲位…</option>
            </select>

            <input
              v-show="locationSelect === '__NEW__'"
              v-model.trim="locationCustom"
              type="text"
              placeholder="輸入自訂儲位"
              @input="syncLocation"
            />
          </div>
          <div class="hint">⚠️ 新增時一定要選擇或輸入儲位（與你後端一致）</div>
        </div>

        <div class="field">
          <label>單位</label>
          <input v-model.trim="form.unit" type="text" placeholder="例如：pcs / 瓶 / 包" />
        </div>

        <div class="field">
          <label>數量</label>
          <input v-model.number="form.quantity" type="number" min="0" />
        </div>

        <div class="field">
          <label>有效期限</label>
          <input v-model="form.expireDate" type="date" />
          <div class="hint">不填也可；同批次判斷會把「同日期或都空」視為同批次。</div>
        </div>

        <div class="actions">
          <button type="button" class="btn btn-primary" @click="onCreateClick">新增</button>
          <button type="button" class="btn btn-outline" @click="onSearchClick">查詢</button>
          <button type="button" class="btn btn-ghost" @click="resetForm">清空</button>
          <button type="button" class="btn btn-ghost" @click="reloadAll">重新載入</button>
        </div>

        <div v-if="successMessage" class="success">{{ successMessage }}</div>
        <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      </form>
    </section>

    <!-- ================== 物料清單 ================== -->
    <section class="section" id="section-list">
      <div class="section-header">
        <div>
          <div class="section-title">物料清單</div>
          <div class="section-sub">支援 +1 / -1、直接改數量、刪除。</div>
        </div>
      </div>

      <table>
        <thead>
          <tr>
            <th style="width: 6%;">ID</th>
            <th style="width: 24%;">名稱 / 分類</th>
            <th style="width: 12%;">SKU</th>
            <th style="width: 14%;">房間</th>
            <th style="width: 14%;">儲位</th>
            <th style="width: 12%;">單位 / 數量</th>
            <th style="width: 12%;">有效期限</th>
            <th style="width: 20%;">操作</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="it in items"
            :key="it.id"
            :class="(it.quantity ?? 0) <= 0 ? 'low-stock-row' : ''"
          >
            <td>{{ it.id }}</td>

            <td>
              <div style="font-weight: 600;">{{ it.name }}</div>
              <div style="font-size: 11px; color: var(--muted);">
                {{ it.category && it.category.trim() ? it.category : '-' }}
              </div>
            </td>

            <td>{{ it.sku && it.sku.trim() ? it.sku : '-' }}</td>

            <td>{{ it.room && it.room.trim() ? it.room : '-' }}</td>

            <td>
              <strong>{{ it.location && it.location.trim() ? it.location : 'UNASSIGNED' }}</strong>
            </td>

            <td>
              <div>
                <span>{{ it.unit && it.unit.trim() ? it.unit : '-' }}</span>
                ×
                <span style="font-weight: 700;">{{ it.quantity ?? 0 }}</span>
              </div>

              <div v-if="(it.quantity ?? 0) <= 0" class="badge badge-low">數量不足</div>
            </td>

            <td>
              <div v-if="it.expireDate">{{ fmtDate(it.expireDate) }}</div>
              <div v-else style="color: var(--muted);">-</div>

              <div v-if="daysToExpire(it) !== null">
                <span class="badge badge-exp7" v-if="daysToExpire(it) <= 7 && daysToExpire(it) >= 0">
                  7 天內到期
                </span>
                <span class="badge badge-exp14" v-else-if="daysToExpire(it) <= 14 && daysToExpire(it) > 7">
                  14 天內到期
                </span>
              </div>
            </td>

            <td>
              <div class="inline-actions">
                <button class="btn btn-ghost btn-sm" type="button" @click="adjust(it.id, +1)">+1</button>
                <button class="btn btn-ghost btn-sm" type="button" @click="adjust(it.id, -1)">-1</button>

                <button class="btn btn-ghost btn-sm" type="button" @click="openQtyModal(it)">
                  更改數量
                </button>

                <button class="btn btn-ghost btn-sm" type="button" @click="remove(it.id)">
                  刪除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-if="items.length === 0" style="font-size: 13px; color: var(--muted); margin-top: 10px;">
        目前沒有任何物品 ✅
      </p>
    </section>

    <!-- ================== 過期/即將到期 ================== -->
    <section class="section" id="section-expire">
      <div class="section-header">
        <div>
          <div class="section-title">過期 / 即將到期物品</div>
          <div class="section-sub">自動分成「已過期」、「7 天內」、「14 天內」。</div>
        </div>
      </div>

      <div class="sub-title">已過期</div>
      <table>
        <thead>
          <tr>
            <th style="width: 8%;">ID</th>
            <th>名稱</th>
            <th style="width: 20%;">儲位</th>
            <th style="width: 18%;">有效期限</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="it in expiredItems" :key="'ex-' + it.id">
            <td>{{ it.id }}</td>
            <td>{{ it.name }}</td>
            <td>{{ it.location && it.location.trim() ? it.location : 'UNASSIGNED' }}</td>
            <td>{{ it.expireDate ? fmtDate(it.expireDate) : '-' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-if="expiredItems.length === 0" class="empty-tip">目前沒有已過期物品 ✅</p>

      <div class="sub-title">7 天內到期</div>
      <table>
        <thead>
          <tr>
            <th style="width: 8%;">ID</th>
            <th>名稱</th>
            <th style="width: 20%;">儲位</th>
            <th style="width: 18%;">有效期限</th>
            <th style="width: 14%;">剩餘</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="it in exp7Items" :key="'e7-' + it.id">
            <td>{{ it.id }}</td>
            <td>{{ it.name }}</td>
            <td>{{ it.location && it.location.trim() ? it.location : 'UNASSIGNED' }}</td>
            <td>{{ it.expireDate ? fmtDate(it.expireDate) : '-' }}</td>
            <td><span class="badge badge-exp7">{{ daysToExpire(it) }} 天</span></td>
          </tr>
        </tbody>
      </table>
      <p v-if="exp7Items.length === 0" class="empty-tip">目前沒有 7 天內到期物品 ✅</p>

      <div class="sub-title">14 天內到期</div>
      <table>
        <thead>
          <tr>
            <th style="width: 8%;">ID</th>
            <th>名稱</th>
            <th style="width: 20%;">儲位</th>
            <th style="width: 18%;">有效期限</th>
            <th style="width: 14%;">剩餘</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="it in exp14Items" :key="'e14-' + it.id">
            <td>{{ it.id }}</td>
            <td>{{ it.name }}</td>
            <td>{{ it.location && it.location.trim() ? it.location : 'UNASSIGNED' }}</td>
            <td>{{ it.expireDate ? fmtDate(it.expireDate) : '-' }}</td>
            <td><span class="badge badge-exp14">{{ daysToExpire(it) }} 天</span></td>
          </tr>
        </tbody>
      </table>
      <p v-if="exp14Items.length === 0" class="empty-tip">目前沒有 14 天內到期物品 ✅</p>
    </section>

    <!-- ================== 更改數量 Modal ================== -->
    <div class="modal-backdrop" :class="{ show: qtyModal.show }">
      <div class="modal">
        <h3>更改數量</h3>
        <p>請輸入新的數量，按「確認」後才會送出。</p>
        <input type="number" min="0" v-model.number="qtyModal.value" />
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost btn-sm" @click="closeQtyModal">取消</button>
          <button type="button" class="btn btn-primary btn-sm" @click="confirmQty">確認</button>
        </div>
      </div>
    </div>

    <!-- ================== 合併/新建 Modal ================== -->
    <div class="modal-backdrop" :class="{ show: mergeModal.show }" style="z-index: 9999;">
      <div class="modal" style="width: 320px;">
        <h3>⚠️ 發現重複物品</h3>
        <p>
          在儲位 <strong>{{ mergeModal.location }}</strong> 已經有相同批次（同名 + 同到期日）的物品。
        </p>
        <p>你要把數量合併到舊資料，還是建立一筆新的？</p>

        <div class="modal-actions" style="margin-top: 20px;">
          <button type="button" class="btn btn-primary btn-sm" @click="confirmMerge(false)">
            合併數量
          </button>
          <button type="button" class="btn btn-ghost btn-sm" @click="confirmMerge(true)">
            建立新的一筆
          </button>
          <button type="button" class="btn btn-sm" style="border: none; color: #999;" @click="cancelMerge">
            取消
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <div v-if="toast.show" class="toast">{{ toast.text }}</div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import axios from "axios";

/**
 * ✅ 你後端已確認（9090）：
 * - GET    /api/v2/items
 * - POST   /api/v2/items?forceNew=
 * - POST   /api/v2/items/{id}/adjust?delta=
 * - PUT    /api/v2/items/{id}/quantity   body {quantity}
 * - DELETE /api/v2/items/{id}
 * - GET    /api/v2/items/meta   -> {categories, locations}
 */

const items = ref([]);

const meta = reactive({
  categories: [],
  locations: [],
});

const form = reactive({
  name: "",
  sku: "",
  category: "",
  room: "",
  location: "",
  unit: "",
  quantity: 0,
  expireDate: "", // yyyy-mm-dd
});

const categorySelect = ref("");
const categoryCustom = ref("");

const locationSelect = ref("");
const locationCustom = ref("");

const successMessage = ref("");
const errorMessage = ref("");

const toast = reactive({ show: false, text: "", t: null });

const qtyModal = reactive({
  show: false,
  id: null,
  value: 0,
});

const mergeModal = reactive({
  show: false,
  location: "",
  payload: null,
});

// ---------- UI helpers ----------
function toastGreen(msg) {
  toast.text = msg;
  toast.show = true;
  if (toast.t) clearTimeout(toast.t);
  toast.t = setTimeout(() => (toast.show = false), 2200);
}

function setSuccess(msg) {
  successMessage.value = msg;
  errorMessage.value = "";
  setTimeout(() => (successMessage.value = ""), 1700);
}

function setError(msg) {
  errorMessage.value = msg;
  setTimeout(() => (errorMessage.value = ""), 2500);
}

function isBlank(s) {
  return !s || !String(s).trim();
}

function normalizeCategoryLocation() {
  if (categorySelect.value === "__NEW__") form.category = (categoryCustom.value || "").trim();
  else form.category = (categorySelect.value || "").trim();

  if (locationSelect.value === "__NEW__") form.location = (locationCustom.value || "").trim();
  else form.location = (locationSelect.value || "").trim();
}

function syncCategory() {
  normalizeCategoryLocation();
}

function syncLocation() {
  normalizeCategoryLocation();
}

function resetForm() {
  form.name = "";
  form.sku = "";
  form.category = "";
  form.room = "";
  form.location = "";
  form.unit = "";
  form.quantity = 0;
  form.expireDate = "";
  categorySelect.value = "";
  categoryCustom.value = "";
  locationSelect.value = "";
  locationCustom.value = "";
}

// ---------- date helpers ----------
function parseLocalDate(val) {
  if (!val) return null;
  if (typeof val === "string") {
    const s = val.slice(0, 10);
    const [y, m, d] = s.split("-").map((x) => parseInt(x, 10));
    if (!y || !m || !d) return null;
    return new Date(y, m - 1, d);
  }
  return null;
}

function fmtDate(val) {
  const d = parseLocalDate(val);
  if (!d) return String(val || "-");
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}/${mm}/${dd}`;
}

function daysToExpire(it) {
  if (!it?.expireDate) return null;
  const d = parseLocalDate(it.expireDate);
  if (!d) return null;

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const target = new Date(d.getTime());
  target.setHours(0, 0, 0, 0);

  const diffMs = target.getTime() - today.getTime();
  return Math.round(diffMs / (1000 * 60 * 60 * 24));
}

// ---------- computed expire sections ----------
const expiredItems = computed(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return (items.value || [])
    .filter((i) => i.expireDate)
    .filter((i) => {
      const d = parseLocalDate(i.expireDate);
      if (!d) return false;
      d.setHours(0, 0, 0, 0);
      return d.getTime() < today.getTime();
    })
    .sort((a, b) => (parseLocalDate(a.expireDate)?.getTime() || 0) - (parseLocalDate(b.expireDate)?.getTime() || 0));
});

const exp7Items = computed(() =>
  (items.value || [])
    .filter((i) => i.expireDate)
    .filter((i) => {
      const d = daysToExpire(i);
      return d !== null && d >= 0 && d <= 7;
    })
    .sort((a, b) => (parseLocalDate(a.expireDate)?.getTime() || 0) - (parseLocalDate(b.expireDate)?.getTime() || 0))
);

const exp14Items = computed(() =>
  (items.value || [])
    .filter((i) => i.expireDate)
    .filter((i) => {
      const d = daysToExpire(i);
      return d !== null && d > 7 && d <= 14;
    })
    .sort((a, b) => (parseLocalDate(a.expireDate)?.getTime() || 0) - (parseLocalDate(b.expireDate)?.getTime() || 0))
);

// ---------- API ----------
async function fetchMeta() {
  const res = await axios.get("/api/v2/items/meta");
  meta.categories = res.data?.categories || [];
  meta.locations = res.data?.locations || [];
}

async function fetchItems(params) {
  const res = await axios.get("/api/v2/items", { params: params || undefined });
  items.value = Array.isArray(res.data) ? res.data : [];
}

async function reloadAll() {
  await fetchMeta();
  await fetchItems();
  toastGreen("已重新載入");
}

async function adjust(id, delta) {
  await axios.post(`/api/v2/items/${id}/adjust`, null, { params: { delta } });
  await fetchItems();
}

async function remove(id) {
  if (!confirm("確定刪除？")) return;
  await axios.delete(`/api/v2/items/${id}`);
  setSuccess("刪除成功！");
  await fetchMeta();
  await fetchItems();
}

function openQtyModal(it) {
  qtyModal.id = it.id;
  qtyModal.value = it.quantity ?? 0;
  qtyModal.show = true;
}

function closeQtyModal() {
  qtyModal.show = false;
  qtyModal.id = null;
  qtyModal.value = 0;
}

async function confirmQty() {
  const id = qtyModal.id;
  if (!id) return;
  const q = Math.max(0, Number(qtyModal.value || 0));
  await axios.put(`/api/v2/items/${id}/quantity`, { quantity: q });
  closeQtyModal();
  setSuccess("數量已更新！");
  await fetchItems();
}

// ---------- 重複判斷（前端本地，不依賴舊 API） ----------
function isSameBatch(existing, payload) {
  const sameName = String(existing.name || "").trim().toLowerCase() === String(payload.name || "").trim().toLowerCase();
  const sameLoc = String(existing.location || "").trim().toLowerCase() === String(payload.location || "").trim().toLowerCase();

  const exDate = (existing.expireDate || "").toString().slice(0, 10);
  const inDate = (payload.expireDate || "").toString().slice(0, 10);

  const sameDate = (!exDate && !inDate) || (exDate && inDate && exDate === inDate);

  return sameName && sameLoc && sameDate;
}

// ---------- Create / Search ----------
async function onCreateClick() {
  normalizeCategoryLocation();

  if (isBlank(form.name)) {
    alert("請先輸入名稱再新增");
    return;
  }
  if (isBlank(form.location)) {
    alert("新增時請選擇或輸入儲位");
    return;
  }

  const payload = {
    name: form.name.trim(),
    sku: isBlank(form.sku) ? null : form.sku.trim(),
    category: isBlank(form.category) ? null : form.category.trim(),
    room: isBlank(form.room) ? null : form.room.trim(),
    location: form.location.trim(),
    unit: isBlank(form.unit) ? null : form.unit.trim(),
    quantity: Number(form.quantity || 0),
    expireDate: isBlank(form.expireDate) ? null : form.expireDate,
  };

  // 本地判斷是否重複批次（完全對齊你後端 create 的合併條件）
  const hit = (items.value || []).some((ex) => isSameBatch(ex, payload));
  if (hit) {
    mergeModal.payload = payload;
    mergeModal.location = payload.location;
    mergeModal.show = true;
    return;
  }

  await doCreate(payload, false);
}

async function doCreate(payload, forceNew) {
  try {
    await axios.post("/api/v2/items", payload, { params: { forceNew } });
    setSuccess(forceNew ? "已建立新的一筆！" : "新增成功（若同批次則已合併）！");
    await fetchMeta();
    await fetchItems();
    location.hash = "#section-list";
  } catch (e) {
    console.error(e);
    setError("新增失敗：請確認後端 9090 與資料格式");
  }
}

async function confirmMerge(forceNew) {
  const payload = mergeModal.payload;
  mergeModal.show = false;
  mergeModal.payload = null;
  if (!payload) return;
  await doCreate(payload, forceNew);
}

function cancelMerge() {
  mergeModal.show = false;
  mergeModal.payload = null;
}

async function onSearchClick() {
  if (isBlank(form.name)) {
    alert("請輸入名稱再查詢");
    return;
  }
  normalizeCategoryLocation();

  const params = {
    name: isBlank(form.name) ? undefined : form.name.trim(),
    sku: isBlank(form.sku) ? undefined : form.sku.trim(),
    category: isBlank(form.category) ? undefined : form.category.trim(),
    room: isBlank(form.room) ? undefined : form.room.trim(),
    location: isBlank(form.location) ? undefined : form.location.trim(),
  };

  await fetchItems(params);
  toastGreen("已更新查詢結果");
  location.hash = "#section-list";
}

onMounted(async () => {
  await fetchMeta();
  await fetchItems();
});
</script>

<style scoped>
/* 這些只是必要的排版補齊；你全域 global.css / theme.css 會套主要風格 */

.grid-form {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 12px;
  align-items: end;
}

.field {
  grid-column: span 4;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field label {
  font-size: 12px;
  color: var(--muted);
}

.field input,
.field select {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 12px;
  outline: none;
  background: transparent;
  color: inherit;
}

.row {
  display: flex;
  gap: 10px;
}

.hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--muted);
}

.actions {
  grid-column: span 12;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.success {
  grid-column: span 12;
  margin-top: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(34, 197, 94, 0.12);
  border: 1px solid rgba(34, 197, 94, 0.25);
  color: #16a34a;
  font-size: 13px;
}

.error {
  grid-column: span 12;
  margin-top: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(239, 68, 68, 0.10);
  border: 1px solid rgba(239, 68, 68, 0.18);
  color: #b91c1c;
  font-size: 13px;
}

.inline-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.sub-title {
  font-weight: 700;
  color: #123763;
  margin: 6px 0;
}

.empty-tip {
  font-size: 13px;
  color: var(--muted);
  margin: 6px 0 14px;
}

/* Modal（如果 global.css 已有可刪，但保留不衝突） */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.35);
  display: none;
  align-items: center;
  justify-content: center;
  z-index: 9998;
}

.modal-backdrop.show {
  display: flex;
}

.modal {
  width: 360px;
  max-width: calc(100vw - 28px);
  background: white;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.25);
}

.modal h3 {
  margin: 0 0 8px;
}

.modal p {
  margin: 0 0 10px;
  font-size: 13px;
  color: #475569;
}

.modal input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

/* Toast */
.toast {
  position: fixed;
  left: 50%;
  bottom: 22px;
  transform: translateX(-50%);
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(34, 197, 94, 0.92);
  color: white;
  font-size: 13px;
  z-index: 10000;
}

/* RWD */
@media (max-width: 900px) {
  .field { grid-column: span 12; }
  .row { flex-direction: column; }
}
</style>
