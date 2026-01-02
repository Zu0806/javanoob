import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "../layouts/MainLayout.vue";


import Items from "../views/Items.vue";
import Notes from "../views/Notes.vue";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";

const routes = [
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "items", name: "Items", component: Items },
      { path: "notes", name: "Notes", component: Notes },
    ],
  },
  { path: "/login", name: "Login", component: Login },
  { path: "/register", name: "Register", component: Register },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
