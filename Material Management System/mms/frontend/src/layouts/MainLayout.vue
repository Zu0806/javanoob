<!-- src/layouts/MainLayout.vue -->
<template>
  <div>
    <!-- Topbar -->
    <header class="topbar" id="topbar" :class="{ shrink: isShrink }" ref="topbarRef">
      <div class="topbar-inner">
        <div style="display: flex; align-items: center; gap: 8px">
          <div class="hamburger" id="btnMenu" ref="btnMenuRef" @click="toggleMenu">
            <span></span>
          </div>
          <div class="title">家庭物料管理系統</div>
        </div>

        <div class="user-info">
          <template v-if="loginUser">
            <span>Hi, <b>{{ loginUser.displayName || loginUser.username }}</b></span>

            <button type="button" class="btn btn-outline btn-sm" @click="logout" style="margin-left: 10px">
              登出
            </button>
          </template>

          <RouterLink
            v-else
            to="/login"
            style="color: #0b3b7a; text-decoration: none; font-size: 13px; font-weight: 700"
          >
            登入
          </RouterLink>
        </div>
      </div>

      <!-- Menu Drawer -->
      <div class="menu-drawer" id="menuDrawer" :class="{ open: isMenuOpen }" ref="menuDrawerRef">
        <div>
          
          

          <div class="menu-section-title" style="margin-top: 14px">依類別瀏覽</div>
          <div class="chip-list">
            <a
              v-for="c in sortedCategories"
              :key="c"
              class="chip-link"
              href="#"
              @click.prevent="goItemsFilter({ category: c })"
            >
              {{ c }}
            </a>
            <div v-if="!sortedCategories.length" style="font-size: 12px; color: #6b7280; padding: 4px 2px">
              （尚無分類）
            </div>
          </div>

          <div style="margin-top: 10px">
            <div class="menu-section-title">功能</div>
            <div class="chip-list">
              <RouterLink class="chip-link" to="/items" @click="closeMenu">物資</RouterLink>
              <RouterLink class="chip-link" to="/notes" @click="closeMenu">記事</RouterLink>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Page Content -->
    <RouterView />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { getStoredUser } from "../api/auth";

const router = useRouter();

const topbarRef = ref(null);
const btnMenuRef = ref(null);
const menuDrawerRef = ref(null);

const isShrink = ref(false);
const isMenuOpen = ref(false);

const loginUser = ref(getStoredUser());

// ===== Drawer dynamic categories =====
const categories = ref([]);
const RECENT_CATS_KEY = "mms_recent_categories";

function readRecentCats() {
  try {
    const raw = localStorage.getItem(RECENT_CATS_KEY);
    const arr = raw ? JSON.parse(raw) : [];
    return Array.isArray(arr) ? arr : [];
  } catch {
    return [];
  }
}

const sortedCategories = computed(() => {
  const recent = readRecentCats();
  const all = Array.isArray(categories.value) ? categories.value : [];
  const rest = all.filter((c) => !recent.includes(c)).sort((a, b) => String(a).localeCompare(String(b), "zh-Hant"));
  return [...recent.filter((c) => all.includes(c)), ...rest];
});

async function fetchDrawerMeta() {
  try {
    const res = await axios.get("/api/v2/items/meta");
    categories.value = res.data?.categories || [];
  } catch (e) {
    // 不要讓 topbar 因為 meta 失敗而爆掉
    console.warn("fetchDrawerMeta failed", e);
  }
}

const shrinkAt = 20;
function onScroll() {
  isShrink.value = window.scrollY > shrinkAt;
}

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value;
}
function closeMenu() {
  isMenuOpen.value = false;
}

function onDocClick(e) {
  const menu = menuDrawerRef.value;
  const btn = btnMenuRef.value;
  if (!menu || !btn) return;
  if (!menu.contains(e.target) && !btn.contains(e.target)) closeMenu();
}

function goItemsFilter({ placeholder = "", category = "" } = {}) {
  router.push({
    path: "/items",
    query: {
      placeholder: placeholder || undefined,
      category: category || undefined,
    },
  });
  closeMenu();
}

async function logout() {
  // 你登入狀態是存在 mms_auth_user，所以登出要刪這個
  localStorage.removeItem("mms_auth_user");
  // 保險：舊 key 也一起清
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("displayName");

  loginUser.value = null;
  window.dispatchEvent(new Event("mms-auth-changed"));
  closeMenu();
  await router.replace("/login");
}

function refreshUser() {
  loginUser.value = getStoredUser();
}

onMounted(async () => {
  document.addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  document.addEventListener("click", onDocClick);

  await fetchDrawerMeta();

  // 同 tab 登入/登出後即時更新
  window.addEventListener("mms-auth-changed", refreshUser);
  // 跨 tab 更新
  window.addEventListener("storage", refreshUser);

  // Items 新增分類後，會丟這個事件讓 Drawer 重新抓 meta
  window.addEventListener("mms-meta-changed", fetchDrawerMeta);
});

onBeforeUnmount(() => {
  document.removeEventListener("scroll", onScroll);
  document.removeEventListener("click", onDocClick);
  window.removeEventListener("mms-auth-changed", refreshUser);
  window.removeEventListener("storage", refreshUser);
  window.removeEventListener("mms-meta-changed", fetchDrawerMeta);
});
</script>

<style>
    .page-container{
  max-width: 1280px;  /* 你之前的寬度核心 */
  margin: 0 auto;
  padding: 24px;
  width: 100%;
}
</style>