import { createRouter, createWebHistory } from 'vue-router';

import RegPage from './page/RegPage.vue';
import LogPage from './page/LogPage.vue';
import Main from './page/Main.vue';
import Create from '@/page/CreateTests.vue'
import Test from '@/page/Test.vue';

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/reg', component: RegPage },
    { path: '/log', component: LogPage, alias: '/' },
    { path: '/main', component: Main },
    { path: '/create', component: Create },
    { path: '/test/:id', name: 'Test', component: Test },

    // {path:"/auth/main",component:AuthMain},
  ],
});