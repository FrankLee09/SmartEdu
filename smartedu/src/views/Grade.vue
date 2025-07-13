<template>
    <div>
        <div class="card" style="margin-bottom: 15px; padding: 15px;">
            <el-form :model="filterForm" inline>
                <el-form-item label="选择任务">
                    <el-select 
                        v-model="filterForm.taskId" 
                        placeholder="请选择任务"
                        @change="handleTaskChange"
                        size="large"
                        style="width: 200px">
                        <el-option 
                            v-for="task in data.tableData" 
                            :key="task.id" 
                            :label="task.title" 
                            :value="task.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                
                <el-form-item label="选择班级">
                    <el-select 
                        v-model="filterForm.classId" 
                        placeholder="请先选择任务"
                        :disabled="!filterForm.taskId"
                        @change="loadStudentTable"
                        size="large"
                        style="width: 200px">
                        <el-option 
                            v-for="classItem in filteredClassOptions" 
                            :key="classItem.id" 
                            :label="classItem.name" 
                            :value="classItem.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                
                <el-form-item>
                    <el-button type="primary" @click="resetFilter" size="large">重置筛选</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="card" style="margin-bottom: 5px">
            <el-input 
                style="width: 240px; margin-left: 10px; margin-right: 10px" 
                v-model="data.name" 
                placeholder="请输入标题查询" 
                prefix-icon="Search"
            ></el-input>
            <el-button type="primary" @click="load">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>

        <div class="card" style="margin-bottom: 15px;">
            <div style="margin-bottom: 10px; font-weight: bold;">任务列表</div>
            <el-table :data="data.tableData" stripe>
                <el-table-column label="标题" prop="title" />
                <el-table-column label="内容" prop="content" />
                <el-table-column label="任务发布班级">
                    <template #default="scope">
                        {{ getClassNames(scope.row.classIds) }}
                    </template>
                </el-table-column>
                <el-table-column label="课程标题">
                    <template #default="scope">
                        {{ getCourseTitle(scope.row.courseId) }}
                    </template>
                </el-table-column>
                <el-table-column label="标签" prop="tag" />
                <el-table-column label="发布时间" prop="time" />
                <el-table-column label="截止时间" prop="dueDate" />
            </el-table>
            <div style="margin-top: 15px;">
                <el-pagination
                    @size-change="load"
                    @current-change="load"
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    :page-sizes="[5, 10, 15, 20]"
                    background
                    layout="total, size, prev, pager, next, jumper"
                    :total="data.total"
                />
            </div>
        </div>

        <div class="card" v-if="filterForm.taskId && filterForm.classId">
            <div style="margin-bottom: 10px; font-weight: bold;">
                {{ currentTaskTitle }} - {{ getClassName(filterForm.classId) }} 学生完成情况
            </div>
            <el-table :data="currentClassStudents" stripe>
                <el-table-column label="头像" width="80">
                    <template #default="scope">
                        <img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" 
                             style="width: 30px; height: 30px; border-radius: 50%;" />
                        <img v-else :src="defaultAvatar" alt="默认头像"
                             style="width: 30px; height: 30px; border-radius: 50%;" />
                    </template>
                </el-table-column>
                <el-table-column label="学生姓名" prop="name" width="220px"/>
                <el-table-column label="学号" prop="id" width="160px"/>
                <el-table-column label="完成情况">
                    <template #default="scope">
                        <el-tag 
                            :type="isStudentCompleted(scope.row.id) ? 'success' : 'danger'"
                            :effect="isStudentCompleted(scope.row.id) ? 'light' : 'plain'">
                            {{ isStudentCompleted(scope.row.id) ? '已完成' : '未完成' }}
                        </el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column 
                    v-if="currentTaskTag === '提交文件'"
                    label="查看提交文件">
                    <template #default="scope">
                        <el-button 
                            v-if="isStudentCompleted(scope.row.id)"
                            @click="viewSubmission(scope.row.id)"
                            link 
                            type="primary">
                            查看提交
                        </el-button>
                    </template>
                </el-table-column>
                
                <el-table-column label="分数">
                    <template #default="scope">
                        <div class="score-container" v-if="isStudentCompleted(scope.row.id)" style="width: 300px;">
                            <el-input 
                                v-model="scope.row.score"
                                :disabled="!scope.row.isEditing"
                                placeholder="请输入分数"
                                size="large"
                                style="width: 200px; margin-right: 10px; height: 36px; line-height: 36px;">
                            </el-input>
                            <el-button
                                @click="toggleEdit(scope.row)"
                                link 
                                type="primary" 
                                style="margin-right: 10px;">
                                {{ scope.row.isEditing ? '保存' : '修改' }}
                            </el-button>
                            <el-button
                                type="primary" 
                                @click="aiGrading(scope.row)"
                                :disabled="!isStudentCompleted(scope.row.id) || currentTaskTag !== '提交文件'"
                                link>
                                大模型批改分数
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue';
import { Search } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage, ElLoading } from 'element-plus';

const getCurrentDate = () => {
    const date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
};

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}')

const data = reactive({
    role: userInfo.role,
    username: null,
    name: null,
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    classOptions: [],
    courseOptions: [],
    studentList: [],
})

const formRef = ref()
const studentDialogVisible = ref(false)
const currentTaskTitle = ref('')
const currentTaskTag = ref('')
const currentClassStudents = ref([])
const currentCompletedStudentIds = ref([])
const currentCompletionTimes = ref({})
const taskGrades = ref({})

const filterForm = reactive({
    taskId: null,
    classId: null
})

const filteredClassOptions = computed(() => {
    if (!filterForm.taskId) return [];
    
    const selectedTask = data.tableData.find(task => task.id === filterForm.taskId);
    if (!selectedTask) return [];
    
    if (selectedTask.classId) {
        const classItem = data.classOptions.find(cls => cls.id === selectedTask.classId);
        return classItem ? [classItem] : [];
    }
    
    if (selectedTask.classIds && Array.isArray(selectedTask.classIds)) {
        return data.classOptions.filter(cls => selectedTask.classIds.includes(cls.id));
    }
    
    return [];
});

const loadClasses = async () => {
    try {
        const res = await request.get('/class/getAll');
        data.classOptions = res.data || [];
    } catch (error) {
        console.error('加载班级列表失败:', error);
        ElMessage.error('班级列表加载失败');
    }
}

const loadCourses = async () => {
    try {
        const res = await request.get('/course/selectPage', {
            params: {
                pageNum: 1,
                pageSize: 1000
            }
        });
        data.courseOptions = res.data?.list || [];
    } catch (error) {
        console.error('加载课程列表失败:', error);
        ElMessage.error('课程列表加载失败');
    }
}

const loadStudents = async () => {
    try {
        const res = await request.get('/student/selectPage', {
            params: {
                pageNum: 1,
                pageSize: 1000
            }
        });
        data.studentList = res.data?.list || [];
    } catch (error) {
        console.error('加载学生列表失败:', error);
        ElMessage.error('学生列表加载失败');
    }
}

const load = async () => {
    try {
        const res = await request.get('/task/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                title: data.name
            }
        });
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
    } catch (error) {
        console.error('加载数据失败:', error);
        ElMessage.error('数据加载失败');
    }
}

const loadStudentTable = async () => {
    if (!filterForm.taskId || !filterForm.classId) return;
    
    const selectedTask = data.tableData.find(task => task.id === filterForm.taskId);
    if (!selectedTask) return;
    
    currentTaskTitle.value = selectedTask.title;
    currentTaskTag.value = selectedTask.tag || '';
    currentClassStudents.value = getStudentsByClassId(filterForm.classId);
    currentCompletedStudentIds.value = selectedTask.completedStudentIds || [];
    
    if (selectedTask.studentCompletionTimes) {
        currentCompletionTimes.value = selectedTask.studentCompletionTimes;
    } else {
        currentCompletionTimes.value = {};
    }

    try {
        const res = await request.get('/taskgrade/selectPage', {
            params: {
                taskId: filterForm.taskId,
                pageNum: 1,
                pageSize: 1000
            }
        });
        const grades = res.data?.list || [];
        taskGrades.value = grades.reduce((acc, grade) => {
            acc[grade.studentId] = grade.score;
            return acc;
        }, {});
        currentClassStudents.value.forEach(student => {
            student.score = taskGrades.value[student.id] || '';
            student.isEditing = false;
        });
    } catch (error) {
        console.error('加载成绩信息失败:', error);
        ElMessage.error('加载成绩信息失败');
    }
}

const isStudentCompleted = (studentId) => {
    return currentCompletedStudentIds.value.includes(studentId);
}

const getCompletionTime = (studentId) => {
    return currentCompletionTimes.value[studentId] || '';
}

const handleTaskChange = () => {
    filterForm.classId = null;
    currentClassStudents.value = [];
    currentCompletedStudentIds.value = [];
    currentTaskTag.value = '';
}

const resetFilter = () => {
    filterForm.taskId = null;
    filterForm.classId = null;
    currentClassStudents.value = [];
    currentCompletedStudentIds.value = [];
    currentTaskTag.value = '';
}

const reset = () => {
    data.name = null;
    load();
}

const getStudentInfo = (studentId) => {
    return data.studentList.find(student => student.id === studentId) || {};
}

const getStudentsByClassId = (classId) => {
    return data.studentList.filter(student => student.classId === classId);
}

const getCourseTitle = (courseId) => {
    if (!courseId) return '未分配';
    const course = data.courseOptions.find(c => c.id === courseId);
    return course ? course.title : '未知课程';
}

const getClassName = (classId) => {
    const classItem = data.classOptions.find(c => c.id === classId);
    return classItem ? classItem.name : '未知班级';
}

const getClassNames = (classIds: number[]) => {
    if (!classIds || classIds.length === 0) return '未分配';
    
    return classIds
        .map(id => {
            const classItem = data.classOptions.find(c => c.id === id);
            return classItem ? classItem.name : '未知班级';
        })
        .join(', ');
}

const viewSubmission = async (studentId) => {
    try {
        const res = await request.get('/submission/selectPage', {
            params: {
                studentId,
                taskId: filterForm.taskId
            }
        });
        const submissions = res.data?.list || [];
        if (submissions.length > 0) {
            const submission = submissions[0];
            window.open(submission.fileUrl, '_blank');
        } else {
            ElMessage.warning('未找到该学生的提交文件');
        }
    } catch (error) {
        console.error('获取提交信息失败:', error);
        ElMessage.error('获取提交信息失败');
    }
}

const toggleEdit = (student) => {
    if (student.isEditing) {
        saveScore(student.id, student.score);
    }
    student.isEditing = !student.isEditing;
}

const saveScore = async (studentId, score) => {
    if (!isStudentCompleted(studentId)) return;

    const taskId = filterForm.taskId;

    try {
        // 先查询是否已存在成绩记录
        const existingGradeRes = await request.get('/taskgrade/selectPage', {
            params: {
                taskId,
                studentId,
                pageNum: 1,
                pageSize: 1
            }
        });
        const existingGrade = existingGradeRes.data?.list?.[0];

        if (!existingGrade) {
            // 首次打分，调用add接口
            const res = await request.post('/taskgrade/add', {
                taskId,
                studentId,
                score
            });
            if (res.code === '200') {
                taskGrades.value[studentId] = score;
                ElMessage.success('分数添加成功');
            } else {
                ElMessage.error(res.msg);
            }
        } else {
            // 后续修改分数，调用update接口，需要传递id
            const res = await request.put('/taskgrade/update', {
                id: existingGrade.id,
                taskId,
                studentId,
                score
            });
            if (res.code === '200') {
                taskGrades.value[studentId] = score;
                ElMessage.success('分数更新成功');
            } else {
                ElMessage.error(res.msg);
            }
        }

        const student = currentClassStudents.value.find(s => s.id === studentId);
        if (student) {
            student.isEditing = false;
        }
    } catch (error) {
        console.error('保存分数失败:', error);
        ElMessage.error('保存分数失败');
    }
}

const aiGrading = async (student) => {
    if (!isStudentCompleted(student.id) || currentTaskTag.value !== '提交文件') {
        ElMessage.warning('只能为已完成的文件提交任务进行AI批改');
        return;
    }

    try {
        // 先获取学生提交的文件信息
        const submissionRes = await request.get('/submission/selectPage', {
            params: {
                studentId: student.id,
                taskId: filterForm.taskId
            }
        });

        const submissions = submissionRes.data?.list || [];
        if (submissions.length === 0) {
            ElMessage.warning('未找到该学生的提交文件');
            return;
        }

        const submission = submissions[0];
        if (!submission.fileUrl) {
            ElMessage.warning('提交文件URL无效');
            return;
        }

        // 显示加载状态
        const loading = ElLoading.service({
            lock: true,
            text: '正在使用AI批改作业，请稍候...',
            background: 'rgba(0, 0, 0, 0.7)'
        });

        try {
            // 下载文件并创建FormData
            const fileResponse = await fetch(submission.fileUrl);
            const blob = await fileResponse.blob();
            const fileName = submission.fileUrl.split('/').pop() || 'report.pdf';
            const file = new File([blob], fileName, { type: blob.type });

            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', student.id.toString());

            // 使用原生fetch API直接调用后端接口，绕过request拦截器
            const fetchResponse = await fetch('http://localhost:8080/dify/reportScore', {
                method: 'POST',
                body: formData,
                // 不设置Content-Type，让浏览器自动处理multipart/form-data和boundary
            });

            if (!fetchResponse.ok) {
                throw new Error(`HTTP error! Status: ${fetchResponse.status}`);
            }

            const result = await fetchResponse.json();

            if (result.code === '200' && result.data) {
                const aiScore = result.data;
                student.score = aiScore;
                ElMessage.success(`AI批改完成，得分: ${aiScore}`);

                // 保存AI批改的分数
                await saveScore(student.id, aiScore);
            } else {
                ElMessage.error(result.msg || 'AI批改失败');
            }
        } catch (error) {
            ElMessage.error('AI批改过程中发生错误: ' + (error.message || '未知错误'));
            console.error('AI批改错误:', error);
        } finally {
            // 确保��论成功或失败都关闭加载提示
            loading.close();
        }
    } catch (error) {
        ElMessage.error('获取提交信息失败: ' + (error.message || '未知错误'));
        console.error('获取提交信息错误:', error);
    }
}

onMounted(async () => {
    await Promise.all([load(), loadClasses(), loadCourses(), loadStudents()]);
    console.log('数据加载完成');
})
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}

.student-container {
  display: flex;
  flex-wrap: wrap;
}

.student-item {
  width: 33.3333%;
  box-sizing: border-box;
  padding: 5px;
}

.task-card {
  border: 1px solid #dcdfe6;
  border-radius: 5px;
  padding: 15px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.task-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.task-card-actions {
  margin-top: 15px;
}

.score-container {
  display: flex;
  align-items: center;
  white-space: nowrap;
}
</style>