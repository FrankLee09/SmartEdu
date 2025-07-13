<template>
    <div style="display: flex; height: 100vh; width: 100vw; overflow: hidden; position: relative;">
        <div 
            :style="{ 
                width: isMenuCollapsed ? '90px' : '220px', 
                height: '100%',
                borderRight: '1.5px solid rgba(0, 0, 0, 0.1)',
                transition: 'width 0.3s ease-in-out',
                position: 'fixed',
                left: 0,
                top: 0,
            }"
        >
            <el-menu 
                router 
                :default-active="router.currentRoute.value.path" 
                :collapse="isMenuCollapsed" 
                class="custom-side-menu"
                style="border: 0; height: 100%; background-color: #fff; width: 100%;"
            >
                <div style="width: 280px; display: flex; align-items: center; padding-left: 5px; margin-top: 10px; margin-bottom: 15px;">
                    <img 
                        :style="{
                            width: '75px',
                            transition: 'all 0.5s ease-in-out',
                            opacity: isMenuCollapsed ? 1 : 1,
                            transform: isMenuCollapsed ? 'scale(1)' : 'scale(1)'
                        }" 
                        src="@/assets/logo.png" 
                        alt=""
                    >
                    <span 
                        :style="{
                            fontSize: '20px', 
                            color: 'black',
                            opacity: isMenuCollapsed ? 0 : 1,
                            transition: 'opacity 0.3s ease-in-out 0.1s, transform 0.3s ease-in-out 0.1s',
                            transform: isMenuCollapsed ? 'translateX(-10px)' : 'translateX(0)'
                        }"
                    >{{ data.user.role === '教师' ? '教师端系统' : data.user.role === '学生' ? '学生端系统' : '' }}</span>
                </div>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/teacherHome" 
                    class="custom-menu-item"
                >
                    <el-icon><House /></el-icon>
                    <template #title>首页</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/clazz" 
                    class="custom-menu-item"
                >
                    <el-icon><DataBoard /></el-icon>
                    <template #title>班级管理</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/student" 
                    class="custom-menu-item"
                >
                    <el-icon><Avatar /></el-icon>
                    <template #title>学生管理</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/courseManager" 
                    class="custom-menu-item"
                >
                    <el-icon><Memo /></el-icon>
                    <template #title>课程管理</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/task" 
                    class="custom-menu-item"
                >
                    <el-icon><List /></el-icon>
                    <template #title>任务管理</template>
                </el-menu-item>

                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/exam"
                    class="custom-menu-item"
                >
                    <el-icon><MessageBox /></el-icon>
                    <template #title>考试管理</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/grade" 
                    class="custom-menu-item"
                >
                    <el-icon><Histogram /></el-icon>
                    <template #title>成绩管理</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '教师'" 
                    index="/manager/teacherPerson" 
                    class="custom-menu-item"
                >
                    <el-icon><UserFilled /></el-icon>
                    <template #title>个人信息</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '学生'" 
                    index="/manager/studentHome" 
                    class="custom-menu-item"
                >
                    <el-icon><House /></el-icon>
                    <template #title>首页</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '学生'" 
                    index="/manager/studentTask" 
                    class="custom-menu-item"
                >
                    <el-icon><EditPen /></el-icon>
                    <template #title>作业提交</template>
                </el-menu-item>


                <el-menu-item
                    v-if="data.user.role === '学生'"
                    index="/manager/examList"
                    class="custom-menu-item"
                >
                    <el-icon><Tickets /></el-icon>
                    <template #title>我的考试</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '学生'" 
                    index="/manager/analysis" 
                    class="custom-menu-item"
                >
                    <el-icon><DataLine /></el-icon>
                    <template #title>学习分析</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '学生'" 
                    index="/manager/knowledgeGraph" 
                    class="custom-menu-item"
                >
                    <el-icon><Compass /></el-icon>
                    <template #title>知识图谱</template>
                </el-menu-item>
                
                <el-menu-item 
                    v-if="data.user.role === '学生'" 
                    index="/manager/studentPerson" 
                    class="custom-menu-item"
                >
                    <el-icon><UserFilled /></el-icon>
                    <template #title>个人信息</template>
                </el-menu-item>
                
                <el-menu-item @click="logout" class="custom-menu-item">
                    <el-icon><CircleClose /></el-icon>
                    <template #title>退出登录</template>
                </el-menu-item>
            </el-menu>
        </div>

        <div 
            style="flex: 1; display: flex; flex-direction: column; height: 100%; position: absolute; left: 220px; top: 0; right: 0;"
            :style="{ 
                left: isMenuCollapsed ? '90px' : '220px',
                transition: 'left 0.3s ease-in-out'
            }"
        >
            <header 
                style="height: 60px; background-color: white; display: flex; align-items: center; border-bottom: 1.5px solid rgba(0, 0, 0, 0.1);
                       position: fixed; top: 0; left: 220px; right: 0; background-color: #fff; z-index: 1; border-left: 1.5px solid rgba(0, 0, 0, 0.1);"
                :style="{ 
                    left: isMenuCollapsed ? '90px' : '220px',
                    transition: 'left 0.3s ease-in-out'
                }"
            >
                <div style="width: 300px; display: flex; align-items: center; padding-left: 20px;">
                    <el-icon @click="toggleMenu" class="toggle-icon" style="font-size: 25px;">
                        <template v-if="isMenuCollapsed"><Expand /></template>
                        <template v-else><Fold /></template>
                    </el-icon>
                </div>
                <div style="flex: 1"></div>
                <div style="width: fit-content; display: flex; align-items: center; padding-right: 10px;">
                    <img :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="" style="width: 40px; height: 40px; border-radius: 50%;">
                    <span style="color: black; margin-left: 5px;">{{data.user ? data.user.name : '未知用户'}}</span>
                </div>
            </header>
            
            <main 
                style="flex: 1; background-color: white; padding: 10px; overflow: auto;
                       position: absolute; top: 60px; left: 0; right: 0; bottom: 0; border-left: 1.5px solid rgba(0, 0, 0, 0.1)"
            >
                <RouterView @updateUser="updateUser"/>
            </main>    
        </div>    
    </div>
</template>

<script setup lang="ts">
import router from '@/router/index.ts';
import { Document, UserFilled, ArrowLeft, Expand, Fold, House, User, Lock, CircleClose, Setting, Avatar, DataBoard, DataLine, Compass, EditPen, Histogram, Memo, List, MessageBox } from '@element-plus/icons-vue';
import { ref, reactive, onMounted } from 'vue';

const storedUser = localStorage.getItem('db-user');
const data = reactive({
    user: storedUser ? JSON.parse(storedUser) : {}
});

const isMenuCollapsed = ref(false);

const logout = () => {
    localStorage.removeItem('db-user')
    location.href = '/login'
}

const updateUser = () => {
    data.user = JSON.parse(localStorage.getItem('db-user') || '{}')
}

const toggleMenu = () => {
    isMenuCollapsed.value = !isMenuCollapsed.value;
}

onMounted(() => {
    updateUser();
});
</script>

<style scoped>
:global(html), :global(body) {
    height: 100%;
    width: 100%;
    margin: 0;
    padding: 0;
    overflow: hidden;
    position: relative;
}

.toggle-icon {
    margin-left: 15px;
    cursor: pointer;
    transition: transform 0.5s ease-in-out;
}

.toggle-icon svg {
    transform: rotate(0deg);
    transition: transform 0.3s ease-in-out;
}

.is-collapsed .toggle-icon svg {
    transform: rotate(180deg);
}

.custom-side-menu {
    border-right: none;
}

:deep(.custom-menu-item) {
    height: 48px !important;
    line-height: 48px !important;
    padding: 0 16px !important;
    font-size: 16px !important;
    font-weight: 600 !important;
    color: rgba(51, 51, 51, 0.6) !important;
    border-radius: 999px !important;
    margin: 10px 4px !important;
    display: flex !important;
    align-items: center !important;
}

:deep(.custom-menu-item .el-icon) {
    margin-right: 8px !important;
    flex-shrink: 0; 
}

:deep(.custom-menu-item:hover) {
    background-color: rgba(0, 0, 0, 0.03) !important;
    color: #333 !important;
}

:deep(.custom-menu-item.is-active) {
    background-color: rgba(0, 0, 0, 0.03) !important;
    color: #333 !important;
}

:deep(.el-menu--collapse) {
    :deep(.custom-menu-item) {
        padding: 0 12px !important;
    }
    
    :deep(.custom-menu-item span) {
        opacity: 0;
        transform: translateX(-10px);
    }
    
    :deep(.custom-menu-item .el-icon) {
        margin-right: 0 !important; 
        transform: translateX(0) !important;
    }
    
    :deep(.custom-menu-item.is-active) {
        .el-icon {
            color: #333 !important;
        }
    }
    
    :deep(.custom-menu-item:hover) {
        .el-icon {
            color: #333 !important;
        }
    }
}

:deep(.el-menu-item .el-icon) {
    transition: all 0.3s ease-in-out;
}
</style>