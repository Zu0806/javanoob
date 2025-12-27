<template>
  <div style="max-width:900px;margin:24px auto;font-family:system-ui;">
    <h2>家庭物料管理（Vue3 + SpringBoot + DB）</h2>

    <!-- 新增 -->
    <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:end;padding:12px;border:1px solid #ddd;border-radius:12px;">
      <div style="display:flex;flex-direction:column;min-width:220px;flex:1;">
        <label>名稱</label>
        <input v-model="form.name" placeholder="牛奶 / 螺絲 M3" />
      </div>

      <div style="display:flex;flex-direction:column;min-width:160px;">
        <label>SKU</label>
        <input v-model="form.sku" placeholder="選填" />
      </div>

      <div style="display:flex;flex-direction:column;min-width:160px;">
        <label>類別</label>
        <input v-model="form.category" placeholder="食材/五金..." />
      </div>

      <div style="display:flex;flex-direction:column;min-width:160px;">
        <label>儲位</label>
        <input v-model="form.location" placeholder="A1-3 / fridge..." />
      </div>

      <div style="display:flex;flex-direction:column;min-width:120px;">
        <label>數量</label>
        <input type="number" v-model.number="form.quantity" min="0" />
      </div>

      <div style="display:flex;gap:8px;">
        <button @click="aiSuggest">AI 填寫</button>
        <button @click="createItem">新增</button>
      </div>
    </div>

    <!-- 綠色提示 -->
    <div v-if="toast" style="margin-top:10px;background:#16a34a;color:#fff;padding:8px 12px;border-radius:999px;display:inline-block;">
      {{ toast }}
    </div>

    <!-- 清單 -->
    <h3 style="margin-top:18px;">物品清單</h3>
    <table border="1" cellpadding="8" cellspacing="0" style="width:100%;border-collapse:collapse;">
      <thead>
        <tr>
          <th>ID</th><th>名稱</th><th>SKU</th><th>類別</th><th>儲位</th><th>數量</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="it in items" :key="it.id">
          <td>{{ it.id }}</td>
          <td>{{ it.name }}</td>
          <td>{{ it.sku }}</td>
          <td>{{ it.category }}</td>
          <td>{{ it.location }}</td>
          <td>
            <button @click="adjust(it, -1)">-</button>
            <span style="padding:0 10px">{{ it.quantity ?? 0 }}</span>
            <button @click="adjust(it, +1)">+</button>
          </td>
          <td>
            <button @click="removeItem(it.id)">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>

  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const items = ref([])
const toast = ref('')
let toastTimer = null

const form = ref({
  name: '',
  sku: '',
  category: '',
  location: '',
  quantity: 0
})

function showToast(msg){
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(()=> toast.value = '', 2200)
}

async function load(){
  const res = await fetch('/api/items')
  items.value = await res.json()
}

async function createItem(){
  if(!form.value.name.trim()){
    alert('名稱必填')
    return
  }
  if(!form.value.location.trim()){
    alert('儲位必填')
    return
  }
  const res = await fetch('/api/items', {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify(form.value)
  })
  if (!res.ok) {
  const text = await res.text()
  showToast(text || '操作失敗')
  return
}

  showToast('新增成功！')
  // 清空部分欄位
  form.value.name = ''
  form.value.quantity = 0
  await load()
}

async function removeItem(id){
  await fetch(`/api/items/${id}`, { method:'DELETE' })
  showToast('已刪除')
  await load()
}

async function adjust(it, delta){
  const next = Math.max(0, (it.quantity ?? 0) + delta)
  await fetch(`/api/items/${it.id}/quantity?quantity=${next}`, { method:'PUT' })
  await load()
}

async function aiSuggest(){
  const name = form.value.name.trim()
  if(!name){
    alert('請先輸入名稱再使用 AI')
    return
  }
  const res = await fetch('/ai/suggest', {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify({ name })
  })
  if(!res.ok){
    alert(await res.text())
    return
  }
  const data = await res.json()
  if(data.sku) form.value.sku = data.sku
  if(data.location) form.value.location = data.location
  if(data.category) form.value.category = data.category
  showToast(`AI 已填寫：SKU=${data.sku || '-'} / 儲位=${data.location || '-'}`)
}

onMounted(load)
</script>
