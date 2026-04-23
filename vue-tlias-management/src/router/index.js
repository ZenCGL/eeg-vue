import { createRouter, createWebHistory } from 'vue-router'

import IndexView from '@/views/index/index.vue'
import ClazzView from '@/views/clazz/index.vue'
import DeptView from '@/views/dept/index.vue'
import EmpView from '@/views/emp/index.vue'
import LogView from '@/views/log/index.vue'
import StuView from '@/views/stu/index.vue'
import EmpReportView from '@/views/report/emp/index.vue'
import StuReportView from '@/views/report/stu/index.vue'
import LayoutView from '@/views/layout/index.vue'
import LoginView from '@/views/login/index.vue'
import FaceTiredView from '@/views/face_tired/index.vue'
import BrainWaveView from '@/views/brain_wave/index.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
     path: '/', 
     name: '',
     component: LayoutView,
     redirect: '/index', //重定向
     children: [
      {path: 'index', name: 'index', component: IndexView},
      {path: 'clazz', name: 'clazz', component: ClazzView},
      {path: 'face_tired', name: 'face_tired', component: FaceTiredView},
      {path: 'brain_wave', name: 'brain_wave', component: BrainWaveView,props: true},
      {path: 'stu', name: 'stu', component: StuView},
      {path: 'dept', name: 'dept', component: DeptView},
      {path: 'emp', name: 'emp', component: EmpView},
      {path: 'log', name: 'log', component: LogView},
      {path: 'empReport', name: 'empReport', component: EmpReportView},
      {path: 'stuReport', name: 'stuReport', component: StuReportView}
     ]
    },
    {path: '/login', name: 'login', component: LoginView}
  ]
})

export default router
