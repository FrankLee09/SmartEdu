import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/manager',
      component: () => import('../views/Manager.vue'),
      children: [
        { path: 'studentPerson', meta: { title: '学生个人信息页面', role: '学生' }, component: () => import('../views/StudentPerson.vue') },
        { path: 'teacherPerson', meta: { title: '教师个人信息页面', role: '教师' }, component: () => import('../views/TeacherPerson.vue') },
        { path: 'courses', meta: { title: '课程页面', role: '学生' }, component: () => import('../views/Courses.vue') },
        { path: 'courseDetail/:id', meta: { title: '学生查看课程详细信息页面', role: '学生' }, component: () => import('../views/CourseDetail.vue') },
        { path: 'video/:fileId', meta: { title: '学生观看视频页面', role: '学生' }, component: () => import('../views/Video.vue') },
        { path: 'clazz', meta: { title: '教师班级管理页面', role: '教师' }, component: () => import('../views/Clazz.vue') },
        { path: 'student', meta: { title: '教师学生管理页面', role: '教师' }, component: () => import('../views/Student.vue') },
        { path: 'task', meta: { title: '教师任务管理页面', role: '教师' }, component: () => import('../views/Task.vue') },
        { path: 'grade', meta: { title: '教师成绩管理页面', role: '教师' }, component: () => import('../views/Grade.vue') },
        { path: 'courseManager', meta: { title: '教师课程管理页面', role: '教师' }, component: () => import('../views/CourseManager.vue') },
        { path: 'studentHome', meta: { title: '学生端首页', role: '学生' }, component: () => import('../views/StudentHome.vue') },
        { path: 'teacherHome', meta: { title: '教师端首页', role: '教师' }, component: () => import('../views/TeacherHome.vue') },
        { path: 'studentTask', meta: { title: '学生提交任务页面', role: '学生' }, component: () => import('../views/StudentTask.vue') },
        { path: 'analysis', meta: { title: '学生学习分析页面', role: '学生' }, component: () => import('../views/Analysis.vue') },
        { path: 'knowledgeGraph', meta: { title: '学生知识图谱页面', role: '学生' }, component: () => import('../views/KnowledgeGraph.vue') },
        { path: 'exam', meta: { title: '考试页面'}, component: () => import('../views/ExamPage.vue') },
        { path: 'examList', meta: { title: '学生考试列表', role: '学生'}, component: () => import('../views/ExamList.vue') },
      ]
    },
    {
      path: '/exam-answer',
      name: 'ExamAnswer',
      component: () => import('../views/ExamAnswer.vue'),
      props: (route) => ({ examId: route.query.examId })
    },
    {
      path: '/exam-result',
      name: 'ExamResult',
      component: () => import('../views/ExamResult.vue')
    },
    { path: '/login', meta: { title: '登录系统', public: true }, component: () => import('../views/Login.vue') },
    { path: '/404', meta: { title: '404找不到页面', public: true }, component: () => import('../views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404' }
  ]
})

router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || 'Smartedu'
  
  // 检查是否为公共页面
  if (to.meta.public) {
    return next()
  }
  
  // 获取用户信息
  const user = JSON.parse(localStorage.getItem('db-user') || '{}')
  
  // 如果用户未登录，重定向到登录页
  if (!user.role) {
    return next('/login')
  }
  
  // 允许访问"both"权限的页面
  if (to.meta.role === 'both') {
    return next()
  }
  
  // 验证用户角色权限
  if (to.meta.role && to.meta.role !== user.role) {
    return next('/404') // 无权限访问，重定向到404
  }
  
  next()
})

export default router