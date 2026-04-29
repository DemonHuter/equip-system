import { createRouter, createWebHistory } from 'vue-router'
import MeasurementLedger from '../views/MeasurementLedger.vue'

const routes = [
  {
    path: '/',
    redirect: '/measurement-ledger'
  },
  {
    path: '/measurement-ledger',
    name: 'MeasurementLedger',
    component: MeasurementLedger
  },
  {
    path: '/equipment-ledger',
    name: 'EquipmentLedger',
    component: () => import('../views/EquipmentLedger.vue')
  },
  {
    path: '/device-standard',
    name: 'DeviceStandard',
    component: () => import('../views/DeviceStandard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router