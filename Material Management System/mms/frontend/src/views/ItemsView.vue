<template>
  <header :class="['topbar', { shrink: isShrink }]" id="topbar">
    <div class="topbar-inner">
      <div style="display:flex; align-items:center; gap:8px;">
        <div class="hamburger" @click="toggleMenu"><span /></div>
        <div class="title">家庭物料管理系統</div>
      </div>

      <div class="user-info">
        <span v-if="loginUser">歡迎，{{ loginUser.displayName || loginUser.username || '使用者' }}</span>
        <a v-else href="/login" style="color:#e5e7eb; text-decoration:none; font-size:13px;">登入</a>
      </div>
    </div>

    <div :class="['menu-drawer', { open: menuOpen }]">
      <div>
        <div class="menu-section-title">依儲位瀏覽</div>
        <div class="chip-list">
          <a class="chip-link" v-for="loc in locations" :key="loc" href="#section-list"
             @click.prevent="filterByLocation(loc)">{{ loc }}</a>
        </div>
      </div>

      <div>
        <div class="menu-section-title">依物品類別瀏覽</div>
        <div class="chip-list">
          <a class="chip-link" v-for="cat in categories" :key="cat" href="#section-list"
             @click.prevent="filterByCategory(cat)">{{ cat }}</a>
        </div>
      </div>
    </div>
  </header>

  <div v-if="toastMsg" class="toast">{{ toastMsg }}</div>

  <main>
    <section class="section" id="section-create">
      <div class="section-header">
        <div>
          <div class="section-title">新增 / 查詢物品</div>
          <div class="section-sub">查詢時以「名稱」為必填；新增時請填寫儲位與數量。</div>
        </div>
      </div>

      <form @submit.prevent="handleCreate">
        <div class="row">
          <div class="field">
            <label>名稱</label>
            <div class="ai-input-wrap">
              <input v-model.trim="form.name" placeholder="螺絲 M3 / 牛奶 / 洗衣精" />
              <button type="button" class="btn btn-ghost btn-sm btn-ai" @click="aiFill">自動填寫</button>
            </div>
          </div>

          <div class="field">
            <label>SKU</label>
            <input v-model.trim="form.sku" placeholder="唯一代碼（選填）" />
          </div>

          <div class="field">
            <label>物品類別</label>
            <select v-model="categorySelect">
              <option value="">選擇類別（選填）</option>
              <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
              <option value="__NEW__">＋新增類別…</option>
            </select>
            <input v-if="categorySelect==='__NEW__'" v-model.trim="categoryInput" type="text"
                   placeholder="輸入新類別" style="margin-top:8px;" />
          </div>

          <div class="field">
            <label>單位</label>
            <input v-model.trim="form.unit" placeholder="pcs/box/kg（選填）" />
          </div>

          <div class="field" style="max-width:220px">
            <label>數量</label>
            <input type="number" v-model.number="form.quantity" min="0" />
          </div>

          <div class="field">
            <label>儲位（必填）</label>
            <select v-model="locationSelect">
              <option value="">選擇儲位</option>
              <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
              <option value="__NEW__">＋新增儲位…</option>
            </select>
            <input v-if="locationSelect==='__NEW__'" v-model.trim="locationInput" type="text"
                   placeholder="輸入新儲位" style="margin-top:8px;" />
          </div>

          <div class="field">
            <label>有效期限</label>
            <input type="date" v-model="form.expireDate" />
          </div>

          <div class="field--actions">
            <button type="submit" class="btn btn-primary">新增</button>
            <button type="button" class="btn btn-ghost" @click="handleSearch">查詢</button>
          </div>
        </div>
      </form>
    </section>

    <section class="section" id="section-list">
      <div class="section-header">
        <div>
          <div class="section-title">物料清單</div>
          <div class="section-sub">支援 +1 / -1、刪除、修改數量。</div>
        </div>
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th><th>名稱 / 類別</th><th>SKU</th><th>儲位</th><th>單位 / 數量</th><th>有效期限</th><th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="it in items" :key="it.id" :class="{ 'low-stock-row': (it.quantity ?? 0) <= 0 }">
            <td>{{ it.id }}</td>
            <td>
              <div>{{ it.name }}</div>
              <div style="font-size:11px; color:var(--muted);">{{ it.category || '-' }}</div>
            </td>
            <td>{{ it.sku || '-' }}</td>
            <td><div>儲位：<strong>{{ it.location || 'UNASSIGNED' }}</strong></div></td>
            <td>
              <div><span>{{ it.unit || '-' }}</span> × <span>{{ it.quantity ?? 0 }}</span></div>
              <div v-if="(it.quantity ?? 0) <= 0" class="badge badge-low">數量不足</div>
            </td>
            <td>
              <div v-if="it.expireDate">{{ formatDate(it.expireDate) }}</div>
              <div v-else style="color:var(--muted);">-</div>
            </td>
            <td>
              <button class="btn btn-ghost btn-sm" @click="adjust(it.id, +1)">+1</button>
              <button class="btn btn-ghost btn-sm" @click="adjust(it.id, -1)">-1</button>
              <button class="btn btn-ghost btn-sm" @click="remove(it.id)">刪除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'

const API = 'http://localhost:9090' // 你若用 vite proxy 可改成 ''

const isShrink = ref(false)
const menuOpen = ref(false)

const loginUser = ref(null)

const items = ref([])
const categories = ref([])
const locations = ref([])

const toastMsg = ref('')
let toastTimer = null
function showToast(msg){
  toastMsg.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(()=> toastMsg.value = '', 2200)
}

const form = ref({
  name: '',
  sku: '',
  category: '',
  location: '',
  unit: '',
  quantity: 0,
  expireDate: ''
})

const categorySelect = ref('')
const categoryInput  = ref('')
watch([categorySelect, categoryInput], () => {
  form.value.category = (categorySelect.value === '__NEW__')
    ? categoryInput.value.trim()
    : (categorySelect.value || '')
})

const locationSelect = ref('')
const locationInput  = ref('')
watch([locationSelect, locationInput], () => {
  form.value.location = (locationSelect.value === '__NEW__')
    ? locationInput.value.trim()
    : (locationSelect.value || '')
})

function toggleMenu(){ menuOpen.value = !menuOpen.value }
function onScroll(){ isShrink.value = window.scrollY > 20 }

onMounted(async () => {
  window.addEventListener('scroll', onScroll, { passive:true })
  onScroll()
  await loadAll()
})
onUnmounted(() => window.removeEventListener('scroll', onScroll))

async function loadAll(){
  await Promise.all([loadItems(), loadMeta()])
}
async function loadItems(){
  const res = await fetch(`${API}/api/items`)
  items.value = await res.json()
}
async function loadMeta(){
  const res = await fetch(`${API}/api/meta`)
  const data = await res.json()
  categories.value = data.categories || []
  locations.value = data.locations || []
}

async function handleCreate(){
  if (!form.value.name.trim()) return alert('請輸入名稱')
  if (!form.value.location.trim()) return alert('新增時請選擇或輸入儲位')

  const res = await fetch(`${API}/api/items`, {
    method:'POST',
    headers:{ 'Content-Type':'application/json' },
    body: JSON.stringify(form.value)
  })
  if (!res.ok) return alert('新增失敗：' + await res.text())
  showToast('新增成功！')
  await loadAll()
}

async function handleSearch(){
  if (!form.value.name.trim()) return alert('請輸入名稱再查詢')
  const params = new URLSearchParams({
    name: form.value.name.trim(),
    sku: form.value.sku || '',
    category: form.value.category || '',
    location: form.value.location || ''
  })
  const res = await fetch(`${API}/api/items/search?` + params.toString())
  items.value = await res.json()
  showToast('查詢完成')
}

async function filterByCategory(cat){
  const res = await fetch(`${API}/api/items/by-category?name=${encodeURIComponent(cat)}`)
  items.value = await res.json()
  showToast(`類別：${cat}`)
  menuOpen.value = false
}
async function filterByLocation(loc){
  const res = await fetch(`${API}/api/items/by-location?name=${encodeURIComponent(loc)}`)
  items.value = await res.json()
  showToast(`儲位：${loc}`)
  menuOpen.value = false
}

async function adjust(id, delta){
  await fetch(`${API}/api/items/${id}/adjust`, {
    method:'POST',
    headers:{ 'Content-Type':'application/json' },
    body: JSON.stringify({ delta })
  })
  await loadItems()
}
async function remove(id){
  if (!confirm('確定刪除？')) return
  await fetch(`${API}/api/items/${id}`, { method:'DELETE' })
  await loadItems()
}

async function aiFill(){
  const name = form.value.name.trim()
  if (!name) return alert('請先輸入名稱再使用自動填寫')
  const res = await fetch(`${API}/ai/suggest`, {
    method:'POST',
    headers:{ 'Content-Type':'application/json' },
    body: JSON.stringify({ name })
  })
  if (!res.ok) return alert('服務錯誤：' + await res.text())
  const data = await res.json()

  if (data.sku) form.value.sku = data.sku
  if (data.location){
    if (locations.value.includes(data.location)) locationSelect.value = data.location
    else { locationSelect.value='__NEW__'; locationInput.value=data.location }
  }
  if (data.category){
    if (categories.value.includes(data.category)) categorySelect.value = data.category
    else { categorySelect.value='__NEW__'; categoryInput.value=data.category }
  }
  showToast(`已填寫：SKU=${data.sku || '-'} / 儲位=${data.location || '-'}`)
}

function formatDate(s){ return String(s).replaceAll('-', '/') }
</script>

<style scoped>
/* 先用最小必要：等你確認 OK，我再把你原本整套 CSS 完整搬進來 */
.topbar{position:sticky;top:0;z-index:50;width:100%;background:linear-gradient(90deg,#0d254f 0%, #1c64d7 100%);color:#fff}
.topbar-inner{max-width:1200px;margin:0 auto;display:flex;align-items:center;justify-content:space-between;height:84px;padding:0 24px}
.topbar.shrink .topbar-inner{height:54px;padding:0 16px}
.title{font-weight:800;font-size:24px;letter-spacing:2px}
.hamburger{width:40px;height:40px;border-radius:999px;border:1px solid rgba(255,255,255,.6);display:flex;align-items:center;justify-content:center;cursor:pointer;margin-right:12px;background:rgba(255,255,255,.12)}
.hamburger span{display:block;width:18px;height:2px;background:#fff;position:relative}
.hamburger span::before,.hamburger span::after{content:"";position:absolute;left:0;width:18px;height:2px;background:#fff}
.hamburger span::before{top:-6px}.hamburger span::after{top:6px}
.user-info{font-size:13px;opacity:.95}
.menu-drawer{max-width:1200px;margin:0 auto;background:rgba(255,255,255,.98);color:#1f2933;box-shadow:0 14px 30px rgba(15,23,42,.16);border-radius:0 0 18px 18px;padding:16px 24px 20px;display:none;grid-template-columns:1.1fr 1fr;gap:24px}
.menu-drawer.open{display:grid}
.menu-section-title{font-size:13px;font-weight:600;color:#6b7280;margin-bottom:6px}
.chip-list{display:flex;flex-wrap:wrap;gap:8px}
.chip-link{display:inline-flex;align-items:center;justify-content:center;padding:6px 10px;border-radius:999px;border:1px solid #c8dcff;background:#f7f9ff;font-size:12px;text-decoration:none;color:#1f2933}
main{padding:24px;margin-top:84px;max-width:1200px;margin-left:auto;margin-right:auto}
.section{margin-top:28px;background:rgba(255,255,255,.96);border-radius:22px;padding:18px 20px 22px;border:1px solid rgba(184,204,255,.9)}
.section-title{font-size:18px;font-weight:700;color:#123763}
.section-sub{font-size:12px;color:#6b7280}
.row{display:flex;gap:12px;flex-wrap:wrap;align-items:flex-end}
.row>.field{display:flex;flex-direction:column;min-width:180px;flex:1 1 220px}
.field--actions{align-self:flex-end;display:flex;gap:8px;flex-wrap:wrap}
label{font-size:12px;color:#6b7280;margin:2px 0 6px}
input,select,button{height:44px;padding:10px 12px;border:1px solid #c8dcff;border-radius:16px;background:#f4f7ff}
.btn{cursor:pointer;border-radius:12px;display:inline-flex;align-items:center;justify-content:center;gap:4px;font-size:14px}
.btn-primary{background:linear-gradient(135deg,#2f80ed,#2563eb);color:#fff;border-color:transparent}
.btn-ghost{background:#fff;border:1px solid #2f80ed;color:#1b4f9d}
.btn-sm{height:32px;padding:4px 10px;border-radius:999px;font-size:12px}
.ai-input-wrap{display:flex;gap:6px;align-items:center}
.ai-input-wrap input{flex:1}
table{border-collapse:collapse;width:100%;margin-top:10px}
th,td{border:1px solid #c8dcff;padding:8px;vertical-align:top;font-size:13px;background:#fff}
th{background:#e3f2ff;text-align:left}
.badge{display:inline-flex;align-items:center;justify-content:center;border-radius:999px;padding:2px 8px;font-size:11px;margin-top:2px}
.badge-low{background:#fee2e2;color:#ef4444}
.low-stock-row{background:#fff5f5}
.toast{position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);background:#16a34a;color:#fff;padding:10px 16px;border-radius:999px;font-size:13px}
</style>
