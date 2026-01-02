<template>
  <div>
    <!-- ✅ 100% from items.html -->
    <header class="topbar" id="topbar" :class="{ shrink: isShrink }">
      <div class="topbar-inner">
        <div style="display:flex; align-items:center; gap:8px;">
          <div class="hamburger" id="btnMenu" @click="toggleMenu">
            <span></span>
          </div>
          <div class="title">家庭物料管理系統</div>
        </div>

        <!-- ✅ user-info 結構照 items.html（先用前端假登入狀態） -->
        <div class="user-info">
          <template v-if="isLoggedIn">
            <span>
              歡迎，
              <span>{{ displayName }}</span>
            </span>

            <button
              type="button"
              style="display:inline; margin-left:8px; background:transparent;border:1px solid #e5e7eb;color:#e5e7eb;
                        border-radius:999px;padding:4px 10px;font-size:12px;cursor:pointer;"
              @click="logout"
            >
              登出
            </button>
          </template>

          <template v-else>
            <a
              href="#"
              @click.prevent="goLogin"
              style="color:#e5e7eb; text-decoration:none; font-size:13px;"
            >
              登入
            </a>
          </template>
        </div>
      </div>

      <!-- ✅ drawer 結構照 items.html（只有地點/類別） -->
      <div class="menu-drawer" id="menuDrawer" :class="{ open: menuOpen }">
        <div>
          <div class="menu-section-title">依地點瀏覽</div>
          <div class="chip-list">
            <a class="chip-link" href="#" @click.prevent="goItemsAll">全部</a>
            <a
              v-for="loc in locations"
              :key="`loc-${loc}`"
              class="chip-link"
              href="#"
              @click.prevent="goItemsByLocation(loc)"
            >
              {{ loc }}
            </a>
          </div>
        </div>

        <div>
          <div class="menu-section-title">依物品類別瀏覽</div>
          <div class="chip-list">
            <a class="chip-link" href="#" @click.prevent="goItemsAll">全部</a>
            <a
              v-for="cat in categories"
              :key="`cat-${cat}`"
              class="chip-link"
              href="#"
              @click.prevent="goItemsByCategory(cat)"
            >
              {{ cat }}
            </a>
          </div>
        </div>
      </div>
    </header>

    <!-- 內容 -->
    <div class="layout-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

/** ====== items.html 行為：shrink + drawer toggle + click outside close ====== */
const isShrink = ref(false);
const menuOpen = ref(false);

const shrinkAt = 20;
function onScroll() {
  isShrink.value = window.scrollY > shrinkAt;
}
function toggleMenu() {
  menuOpen.value = !menuOpen.value;
}
function closeMenu() {
  menuOpen.value = false;
}
function onClickOutside(e) {
  if (!menuOpen.value) return;
  const drawer = document.getElementById("menuDrawer");
  const btn = document.getElementById("btnMenu");
  if (!drawer || !btn) return;
  if (drawer.contains(e.target) || btn.contains(e.target)) return;
  closeMenu();
}

/** ====== meta（跟你後端一致） ====== */
const categories = ref([]);
const locations = ref([]);

async function loadMeta() {
  // GET /api/v2/items/meta -> { categories, locations }
  const res = await axios.get("/api/v2/items/meta");
  categories.value = res.data?.categories || [];
  locations.value = res.data?.locations || [];
}

/** ====== drawer chips 導航（對齊 v2 query） ====== */
function goItemsAll() {
  closeMenu();
  router.push("/items");
}
function goItemsByLocation(loc) {
  closeMenu();
  router.push({ path: "/items", query: { location: loc } });
}
function goItemsByCategory(cat) {
  closeMenu();
  router.push({ path: "/items", query: { category: cat } });
}

/** ====== 先用假登入狀態（你之後接 auth 再換） ====== */
const isLoggedIn = ref(false);
const displayName = ref("使用者");

function goLogin() {
  // 你之後有 /login 就導過去，沒有就回 items
  closeMenu();
  router.push("/login").catch(() => router.push("/items"));
}
function logout() {
  isLoggedIn.value = false;
  closeMenu();
  router.push("/items");
}

onMounted(async () => {
  window.addEventListener("scroll", onScroll, { passive: true });
  document.addEventListener("click", onClickOutside);

  onScroll();
  await loadMeta();
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", onScroll);
  document.removeEventListener("click", onClickOutside);
});
</script>
<style scoped>
/* ✅ 下面 CSS：直接從 items.html 複製（topbar / hamburger / drawer / chip / user-info） */

.topbar{
  position:sticky; top:0; z-index:50;
  width:100%;
  background:linear-gradient(90deg,#0d254f 0%, #1c64d7 100%);
  color:#fff;
  box-shadow:0 2px 12px rgba(15,23,42,.18);
  transition:all .25s ease;
}
.topbar-inner{
  max-width:1200px; margin:0 auto;
  display:flex; align-items:center; justify-content:space-between;
  height:84px; padding:0 24px;
  transition:height .25s ease, padding .25s ease;
}
.title{
  font-weight:800; font-size:24px; letter-spacing:2px;
  transition:all .25s ease; white-space:nowrap;
}
.topbar.shrink .topbar-inner{ height:54px; padding:0 16px; }
.topbar.shrink .title{ font-weight:700; font-size:18px; letter-spacing:1px; }

.hamburger{
  width:40px; height:40px;
  border-radius:999px;
  border:1px solid rgba(255,255,255,.6);
  display:flex; align-items:center; justify-content:center;
  cursor:pointer;
  margin-right:12px;
  background:rgba(255,255,255,.12);
}
.hamburger span{
  display:block; width:18px; height:2px; background:#fff;
  position:relative;
}
.hamburger span::before,
.hamburger span::after{
  content:""; position:absolute; left:0; width:18px; height:2px; background:#fff;
}
.hamburger span::before{ top:-6px;}
.hamburger span::after{ top:6px;}

.user-info{ font-size:13px; opacity:.95; }

.menu-drawer{
  max-width:1200px; margin:0 auto;
  background:rgba(255,255,255,.98);
  color:#0f172a;
  box-shadow:0 14px 30px rgba(15,23,42,.16);
  border-radius:0 0 18px 18px;
  padding:16px 24px 20px;
  display:none;
  grid-template-columns:1.1fr 1fr;
  gap:24px;
  border-inline:1px solid rgba(180,198,255,.8);
  border-bottom:1px solid rgba(180,198,255,.8);
}
.menu-drawer.open{ display:grid; }

.menu-section-title{ font-size:13px; font-weight:600; color:#64748b; margin-bottom:6px; }
.chip-list{ display:flex; flex-wrap:wrap; gap:8px; }
.chip-link{
  display:inline-flex; align-items:center; justify-content:center;
  padding:6px 10px;
  border-radius:999px;
  border:1px solid #e2e8f0;
  background:#f7f9ff;
  font-size:12px;
  text-decoration:none;
  color:#0f172a;
  cursor:pointer;
}
.chip-link:hover{
  border-color:#2f80ed;
  color:#2f80ed;
  background:#e5f0ff;
}


.layout-content{
    padding:24px;
    margin-top:84px;
    max-width:1200px;
    margin-left:auto;
    margin-right:auto;
  }


@media (max-width:640px){
  .topbar-inner{ height:72px; }
  .menu-drawer{
    grid-template-columns:1fr;
  }
}
</style>

