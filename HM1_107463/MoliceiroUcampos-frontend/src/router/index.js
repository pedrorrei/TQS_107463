import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Book from '../pages/Book.vue'
import CheckReservation from '../pages/CheckReservation.vue'
import StaffVerification from '../pages/StaffVerification.vue'
import Monitoring from '../pages/Monitoring.vue'
import Menus from '../pages/Menus.vue'
import GroupBooking from '../pages/GroupBooking.vue'


const routes = [
  { path: '/', component: Home },
  { path: '/book', component: Book },
  { path: '/check-reservation', component: CheckReservation },
  { path: '/staff-verification', component: StaffVerification },
  { path: '/monitoring', component: Monitoring },
  { path: '/menus', component: Menus },
  { path: '/group-booking', component: GroupBooking },

]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router