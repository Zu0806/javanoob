// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

import MainLayout from "../layouts/MainLayout.vue";
import Items from "../views/Items.vue";
import Notes from "../views/Notes.vue";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";

import { getStoredUser } from "../api/auth";

const routes = [
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  {
    path: "/",
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "", redirect: "/items" },
      { path: "items", component: Items },
      { path: "notes", component: Notes },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const user = getStoredUser();
  const needAuth = to.matched.some((r) => r.meta?.requiresAuth);

  if (needAuth && !user) {
    return { path: "/login" };
  }
  if ((to.path === "/login" || to.path === "/register") && user) {
    return { path: "/items" };
  }
});

export default router;
