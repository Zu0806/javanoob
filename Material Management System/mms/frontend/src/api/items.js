import { createRouter, createWebHistory } from "vue-router";

import MainLayout from "../layouts/MainLayout.vue";
import Items from "../views/Items.vue";
import Notes from "../views/Notes.vue";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";

const routes = [
  { path: "/", redirect: "/items" },

  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "items", component: Items },
      { path: "notes", component: Notes },
    ],
  },

  { path: "/login", component: Login },
  { path: "/register", component: Register },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
