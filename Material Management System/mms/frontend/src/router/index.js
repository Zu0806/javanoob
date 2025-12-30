import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '../layouts/MainLayout.vue'
import Items from '../views/Items.vue'
import Notes from '../views/Notes.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/items' },
      { path: 'items', component: Items },
      { path: 'notes', component: Notes }
    ]
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
