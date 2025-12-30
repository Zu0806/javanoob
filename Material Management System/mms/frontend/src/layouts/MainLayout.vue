<!-- src/layouts/MainLayout.vue -->
<template>
  <div>
    <!-- Topbar (from items.html) -->
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
            歡迎，
            <span>{{ loginUser.displayName || loginUser.username }}</span>

            <!-- 先留外觀：你之後可改成真的呼叫 API -->
            <button
              type="button"
              @click="logout"
              style="
                background: transparent;
                border: 1px solid #e5e7eb;
                color: #e5e7eb;
                border-radius: 999px;
                padding: 4px 10px;
                font-size: 12px;
                cursor: pointer;
                margin-left: 8px;
              "
            >
              登出
            </button>
          </template>

          <RouterLink
            v-else
            to="/login"
            style="color: #e5e7eb; text-decoration: none; font-size: 13px"
          >
            登入
          </RouterLink>
        </div>
      </div>

      <!-- Menu Drawer (from items.html) -->
      <div class="menu-drawer" id="menuDrawer" :class="{ open: isMenuOpen }" ref="menuDrawerRef">
        <div>
          <div class="menu-section-title">依地點瀏覽</div>
          <div class="chip-list">
            <!-- 先用前端假資料撐畫面；你之後接 API 再換掉 -->
            <a
              v-for="loc in locations"
              :key="loc"
              class="chip-link"
              href="#"
              @click.prevent
            >
              {{ loc }}
            </a>
            <span v-if="locations.length === 0" style="color: rgba(229,231,235,.8); font-size: 12px;">
              （尚未載入）
            </span>
          </div>
        </div>

        <div>
          <div class="menu-section-title">依物品類別瀏覽</div>
          <div class="chip-list">
            <a
              v-for="cat in categories"
              :key="cat"
              class="chip-link"
              href="#"
              @click.prevent
            >
              {{ cat }}
            </a>
            <span v-if="categories.length === 0" style="color: rgba(229,231,235,.8); font-size: 12px;">
              （尚未載入）
            </span>
          </div>
        </div>

        <!-- 你目前有 Items / Notes 兩頁，我順便塞個快速入口（不影響原設計） -->
        <div style="margin-top: 10px;">
          <div class="menu-section-title">功能</div>
          <div class="chip-list">
            <RouterLink class="chip-link" to="/items" @click="closeMenu">Items</RouterLink>
            <RouterLink class="chip-link" to="/notes" @click="closeMenu">Notes</RouterLink>
          </div>
        </div>
      </div>
    </header>

    <!-- Page Content -->
    <RouterView />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from "vue";

const topbarRef = ref(null);
const btnMenuRef = ref(null);
const menuDrawerRef = ref(null);

const isShrink = ref(false);
const isMenuOpen = ref(false);

/** 假資料：只為了先把畫面撐完整（你之後接後端再換掉） */
const locations = ref(["客廳", "廚房", "房間"]);
const categories = ref(["食材", "清潔", "工具"]);

/** 假登入狀態：先讓 UI 不空。你之後接真實登入資料再改 */
const loginUser = ref({ username: "使用者", displayName: "" });

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
  if (!menu.contains(e.target) && !btn.contains(e.target)) {
    closeMenu();
  }
}

function logout() {
  // 先做前端登出效果；你之後改成呼叫 API / 清 token
  loginUser.value = null;
  closeMenu();
}

onMounted(() => {
  document.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  document.addEventListener("click", onDocClick);
});

onBeforeUnmount(() => {
  document.removeEventListener("scroll", onScroll);
  document.removeEventListener("click", onDocClick);
});
</script>
