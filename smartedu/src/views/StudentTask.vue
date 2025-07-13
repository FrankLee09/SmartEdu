<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="searchParams.name" placeholder="请输入标题查询" prefix-icon="Search"></el-input>
            <el-button type="primary" @click="loadAllTasks">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>

        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="未截止任务" name="active">
                <div class="card-container">
                    <div 
                        class="task-card" 
                        v-for="task in activeTasks" 
                        :key="task.id"
                        :class="{'expired-task': isTaskExpired(task)}"
                    >
                        <h3 class="task-title">{{ task.title }}</h3>
                        <p><strong>内容:</strong> {{ task.content }}</p>
                        <p><strong>课程:</strong> {{ getCourseTitle(task.courseId) }}</p>
                        <p><strong>任务类型:</strong> {{ task.tag }}</p>
                        <p><strong>开始时间:</strong> {{ task.time }}</p>
                        <p><strong>截止时间:</strong> {{ task.dueDate }}</p>
                        <p><strong>任务成绩:</strong> {{ taskGrades[task.id] }}</p>
                        <div class="task-card-actions">
                            <el-button 
                                @click="handleSubmit(task)" 
                                link 
                                type="primary"
                                :disabled="isTaskExpired(task) || (task.completedStudentIds && task.completedStudentIds.includes(currentStudentId))"
                                :style="{opacity: (isTaskExpired(task) || (task.completedStudentIds && task.completedStudentIds.includes(currentStudentId))) ? 0.5 : 1}"
                            >完成任务</el-button>
                        </div>
                    </div>
                </div>
                <div style="margin-top: 15px;">
                    <el-pagination
                        @size-change="handleActiveSizeChange"
                        @current-change="handleActiveCurrentChange"
                        :current-page="activePageInfo.pageNum"
                        :page-size="activePageInfo.pageSize"
                        :page-sizes="[10]"
                        background
                        layout="total, size, prev, pager, next, jumper"
                        :total="activePageInfo.total"
                    />
                </div>
            </el-tab-pane>

            <el-tab-pane label="已截止任务" name="expired">
                <div class="card-container">
                    <div 
                        class="task-card expired-task" 
                        v-for="task in expiredTasks" 
                        :key="task.id"
                    >
                        <h3 class="task-title">{{ task.title }}</h3>
                        <p><strong>内容:</strong> {{ task.content }}</p>
                        <p><strong>课程:</strong> {{ getCourseTitle(task.courseId) }}</p>
                        <p><strong>任务类型:</strong> {{ task.tag }}</p>
                        <p><strong>开始时间:</strong> {{ task.time }}</p>
                        <p><strong>截止时间:</strong> {{ task.dueDate }}</p>
                        <p><strong>任务成绩:</strong> {{ taskGrades[task.id] }}</p>
                        <div class="task-card-actions">
                            <el-button 
                                link 
                                type="primary"
                                disabled
                                style="opacity: 0.5; cursor: not-allowed"
                            >完成任务</el-button>
                        </div>
                    </div>
                </div>
                <div style="margin-top: 15px;">
                    <el-pagination
                        @size-change="handleExpiredSizeChange"
                        @current-change="handleExpiredCurrentChange"
                        :current-page="expiredPageInfo.pageNum"
                        :page-size="expiredPageInfo.pageSize"
                        :page-sizes="[10]"
                        background
                        layout="total, size, prev, pager, next, jumper"
                        :total="expiredPageInfo.total"
                    />
                </div>
            </el-tab-pane>

            <el-tab-pane label="已完成任务" name="completed">
                <div class="card-container">
                    <div 
                        class="task-card" 
                        v-for="task in completedTasks" 
                        :key="task.id"
                    >
                        <h3 class="task-title">{{ task.title }}</h3>
                        <p><strong>内容:</strong> {{ task.content }}</p>
                        <p><strong>课程:</strong> {{ getCourseTitle(task.courseId) }}</p>
                        <p><strong>任务类型:</strong> {{ task.tag }}</p>
                        <p><strong>开始时间:</strong> {{ task.time }}</p>
                        <p><strong>截止时间:</strong> {{ task.dueDate }}</p>
                        <p><strong>任务成绩:</strong> {{ taskGrades[task.id] }}</p>
                        <div class="task-card-actions">
                            <el-button 
                                @click="handleSubmit(task)" 
                                link 
                                type="primary"
                            >完成任务</el-button>
                        </div>
                    </div>
                </div>
                <div style="margin-top: 15px;">
                    <el-pagination
                        @size-change="handleCompletedSizeChange"
                        @current-change="handleCompletedCurrentChange"
                        :current-page="completedPageInfo.pageNum"
                        :page-size="completedPageInfo.pageSize"
                        :page-sizes="[10]"
                        background
                        layout="total, size, prev, pager, next, jumper"
                        :total="completedPageInfo.total"
                    />
                </div>
            </el-tab-pane>
        </el-tabs>

        <el-dialog :title="`${currentTaskTitle} 学生完成情况`" v-model="studentDialogVisible" width="500" destroy-on-close>
            <div>
                <h3>已完成学生</h3>
                <div class="student-container">
                    <div v-for="studentId in currentCompletedStudentIds" :key="studentId" class="student-item">
                        <img v-if="getStudentInfo(studentId).avatar" :src="getStudentInfo(studentId).avatar" alt="" style="display: inline-block; width: 20px; height: 20px; border-radius: 50%; margin-right: 5px;" />
                        {{ getStudentInfo(studentId).name || '未知学生' }}
                    </div>
                </div>
                <h3>未完成学生</h3>
                <div class="student-container">
                    <div v-for="student in currentClassStudents.filter(student =>!currentCompletedStudentIds.includes(student.id))" :key="student.id" class="student-item">
                        <img v-if="student.avatar" :src="student.avatar" alt="" style="display: inline-block; width: 20px; height: 20px; border-radius: 50%; margin-right: 5px;" />
                        {{ student.name || '未知学生' }}
                    </div>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="studentDialogVisible = false">关闭</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog :title="`提交文件 - ${currentTaskTitle}`" v-model="fileDialogVisible" width="500" destroy-on-close>
            <el-form ref="fileFormRef" :rules="fileFormRules" :model="fileForm"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="文件名" prop="filename">
                    <el-input v-model="fileForm.filename" autocomplete="off" placeholder="请输入文件名"/>
                </el-form-item>
                <el-form-item label="文件上传">
                    <el-upload
                        class="file-uploader"
                        action="http://localhost:8080/files/uploadFile"
                        :show-file-list="false"
                        :on-success="handleFileUploadSuccess"
                        :on-error="handleFileUploadError"
                        :before-upload="beforeFileUpload"
                    >
                        <div v-if="fileForm.uploadedFileName" class="uploaded-file-info">
                            <el-icon><Check /></el-icon>
                            <span class="uploaded-filename">{{ fileForm.uploadedFileName }}</span>
                        </div>
                        <el-icon v-else class="uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="fileDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveFileSubmission">保存</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed, watch } from 'vue';
import { Edit, Delete, Search } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();

const getCurrentDate = () => {
    const date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
};

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}')
const currentStudentId = userInfo.id;
const currentStudentClassId = userInfo.classId;

const searchParams = reactive({
    name: null
});

const data = reactive({
    role: userInfo.role,
    username: null,
    tableData: [],
    formVisible: false,
    form: {
        classIds: [],
        time: '',
        dueDate: '',
        courseId: null
    },
    originalStartTime: '',
    classOptions: [],
    courseOptions: [],
    studentList: [],
    ids: [],
    rules: {
        title: [
            { required: true, message: '请输入任务标题', trigger: 'blur' }
        ],
        content: [
            { required: true, message: '请输入任务内容', trigger: 'blur' }
        ],
        tag: [
            { required: true, message: '请选择标签', trigger: 'change' }
        ],
        classIds: [
            { required: true, message: '请选择班级', trigger: 'change' }
        ],
        courseId: [
            { required: true, message: '请选择课程', trigger: 'change' }
        ],
        time: [
            { required: true, message: '请选择开始时间', trigger: 'change' },
            { 
                validator: (rule, value, callback) => {
                    if (!value) {
                        callback(new Error('请选择开始时间'));
                        return;
                    }
                    
                    if (!data.form.id) {
                        if (new Date(value).getTime() < getCurrentDate()) {
                            callback(new Error('开始时间不能早于当前日期'));
                            return;
                        }
                    } 
                    else if (value !== data.originalStartTime) {
                        if (new Date(data.originalStartTime).getTime() < new Date().getTime()) {
                            callback(new Error('已开始的任务，开始时间不可修改'));
                            return;
                        }
                        if (new Date(value).getTime() < getCurrentDate()) {
                            callback(new Error('开始时间不能早于当前日期'));
                            return;
                        }
                    }
                    
                    callback();
                },
                trigger: 'change'
            }
        ],
        dueDate: [
            { required: true, message: '请选择截止时间', trigger: 'change' },
            { 
                validator: (rule, value, callback) => {
                    if (!value) {
                        callback(new Error('请选择截止时间'));
                        return;
                    }
                    
                    if (data.form.time && new Date(value) <= new Date(data.form.time)) {
                        callback(new Error('截止时间必须晚于开始时间'));
                        return;
                    }
                    
                    callback();
                },
                trigger: 'change'
            }
        ]
    }
})

const currentClassStudents = ref([])
const currentCompletedStudentIds = ref([])
const activeTab = ref('active');
const fileDialogVisible = ref(false);
const fileForm = reactive({
    uploadedFileName: '',
    fileUrl: '',
    filename: '',
    taskId: null,
    studentId: currentStudentId
});
const fileFormRef = ref();
const currentSubmissionId = ref<number | null>(null);

const fileFormRules = {
    filename: [
        { required: true, message: '请输入文件名', trigger: 'blur' }
    ]
};

const isStartTimeInPast = computed(() => {
    return data.form.id && data.form.time && new Date(data.form.time).getTime() < new Date().getTime();
});

const activePageInfo = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
});

const expiredPageInfo = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
});

const completedPageInfo = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
});

const isTaskExpired = (task: any) => {
    if (!task.dueDate) return false;
    return new Date(task.dueDate).getTime() < new Date().getTime();
}

const activeTasks = computed(() => {
    return data.tableData
        .filter(task =>!isTaskExpired(task) &&!(task.completedStudentIds && task.completedStudentIds.includes(currentStudentId)))
        .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
        .slice(0, activePageInfo.pageSize);
});

const expiredTasks = computed(() => {
    return data.tableData
        .filter(task => isTaskExpired(task))
        .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
        .slice(0, expiredPageInfo.pageSize);
});

const completedTasks = computed(() => {
    return data.tableData
        .filter(task => task.completedStudentIds && task.completedStudentIds.includes(currentStudentId) &&!isTaskExpired(task))
        .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
        .slice(0, completedPageInfo.pageSize);
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

const loadAllTasks = async () => {
    try {
        const allTasksRes = await request.get('/task/selectPage', {
            params: {
                pageNum: 1,
                pageSize: 10000,
                title: searchParams.name
            }
        });
        
        const allTasks = allTasksRes.data?.list || [];
        
        const filteredTasks = allTasks.filter(task => task.classIds && task.classIds.includes(currentStudentClassId));

        const active = filteredTasks.filter(task =>!isTaskExpired(task) &&!(task.completedStudentIds && task.completedStudentIds.includes(currentStudentId)));
        activePageInfo.total = active.length;

        const expired = filteredTasks.filter(task => isTaskExpired(task));
        expiredPageInfo.total = expired.length;

        const completed = filteredTasks.filter(task => task.completedStudentIds && task.completedStudentIds.includes(currentStudentId) &&!isTaskExpired(task));
        completedPageInfo.total = completed.length;

        const activeStart = (activePageInfo.pageNum - 1) * activePageInfo.pageSize;
        const activeEnd = activeStart + activePageInfo.pageSize;
        const expiredStart = (expiredPageInfo.pageNum - 1) * expiredPageInfo.pageSize;
        const expiredEnd = expiredStart + expiredPageInfo.pageSize;
        const completedStart = (completedPageInfo.pageNum - 1) * completedPageInfo.pageSize;
        const completedEnd = completedStart + completedPageInfo.pageSize;
        
        const activeTasks = active
            .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
            .slice(activeStart, activeEnd);
            
        const expiredTasks = expired
            .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
            .slice(expiredStart, expiredEnd);

        const completedTasks = completed
            .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
            .slice(completedStart, completedEnd);
            
        data.tableData = [...activeTasks, ...expiredTasks, ...completedTasks];

        await loadTaskGrades(data.tableData);
    } catch (error) {
        console.error('加载数据失败:', error);
        ElMessage.error('数据加载失败');
    }
}

onMounted(async () => {
    await Promise.all([loadAllTasks(), loadClasses(), loadCourses(), loadStudents()]);
    console.log('数据加载完成');
})

const reset = () => {
    searchParams.name = null;
    activePageInfo.pageNum = 1;
    expiredPageInfo.pageNum = 1;
    completedPageInfo.pageNum = 1;
    loadAllTasks();
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            data.form.id ? update() : add()
        }
    })
}

const add = () => {
    const tasksToAdd = data.form.classIds.map(classId => {
        return {
            ...data.form,
            classId,
            time: data.form.time,
            dueDate: data.form.dueDate
        };
    });

    tasksToAdd.forEach(task => {
        request.post('/task/add', task).then(res => {
            if (res.code === '200') {
                ElMessage.success('任务发布成功');
            } else {
                ElMessage.error(res.msg);
            }
        }).catch(error => {
            console.error('发布任务失败:', error);
            ElMessage.error('发布任务失败');
        });
    });

    data.formVisible = false;
    loadAllTasks();
}

const update = () => {
    if (data.form.dueDate && new Date(data.form.dueDate).getTime() < new Date().getTime()) {
        ElMessage.warning('已截止的任务不能编辑');
        return;
    }
    
    if (data.form.time !== data.originalStartTime && new Date(data.originalStartTime).getTime() < new Date().getTime()) {
        ElMessage.warning('已开始的任务，开始时间不可修改');
        return;
    }
    
    request.put('/task/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false;
            ElMessage.success('任务更新成功');
            loadAllTasks();
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        console.error('更新任务失败:', error);
        ElMessage.error('更新任务失败');
    });
}

const showStudentDialog = (row) => {
    if (data.studentList.length === 0) {
        ElMessage.warning('学生数据正在加载，请稍后再试');
        return;
    }
    
    currentTaskTitle.value = row.title;
    currentClassStudents.value = getStudentsByClassId(row.classId);
    currentCompletedStudentIds.value = row.completedStudentIds || [];
    studentDialogVisible.value = true;
}

const getCourseTitle = (courseId) => {
    if (!courseId) return '未分配';
    const course = data.courseOptions.find(c => c.id === courseId);
    return course ? course.title : '未知课程';
}

const getStudentInfo = (studentId) => {
    return data.studentList.find(student => student.id === studentId) || {};
}

const getStudentsByClassId = (classId) => {
    return data.studentList.filter(student => student.classId === classId);
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

const handleActiveSizeChange = (newSize: number) => {
    activePageInfo.pageSize = newSize;
    loadAllTasks();
}

const handleActiveCurrentChange = (newPage: number) => {
    activePageInfo.pageNum = newPage;
    loadAllTasks();
}

const handleExpiredSizeChange = (newSize: number) => {
    expiredPageInfo.pageSize = newSize;
    loadAllTasks();
}

const handleExpiredCurrentChange = (newPage: number) => {
    expiredPageInfo.pageNum = newPage;
    loadAllTasks();
}

const handleCompletedSizeChange = (newSize: number) => {
    completedPageInfo.pageSize = newSize;
    loadAllTasks();
}

const handleCompletedCurrentChange = (newPage: number) => {
    completedPageInfo.pageNum = newPage;
    loadAllTasks();
}

const handleTabClick = (tab: any) => {
    activeTab.value = tab.name;
}

const handleSubmit = async (task: any) => {
    if (task.tag === '提交文件') {
        currentTaskTitle.value = task.title;
        fileForm.taskId = task.id;
        fileForm.filename = '';
        fileForm.uploadedFileName = '';
        fileForm.fileUrl = '';
        currentSubmissionId.value = null;

        request.get('/submission/selectPage', {
            params: {
                studentId: currentStudentId,
                taskId: task.id
            }
        }).then(res => {
            const submissions = res.data?.list || [];
            if (submissions.length > 0) {
                const submission = submissions[0];
                fileForm.filename = submission.filename;
                fileForm.fileUrl = submission.fileUrl;
                fileForm.uploadedFileName = submission.fileUrl.split('/').pop() || '';
                currentSubmissionId.value = submission.id;
            }
            fileDialogVisible.value = true;
        }).catch(error => {
            console.error('获取提交信息失败:', error);
            ElMessage.error('获取提交信息失败');
        });
    } else if (task.tag === '查看课程文件') {
        try {
            if (task.fileUrl) {
                window.open(task.fileUrl, '_blank');
            } else {
                ElMessage.warning('未找到课程文件链接');
                return;
            }

            const updatedTask = { ...task };
            updatedTask.completedStudentIds = updatedTask.completedStudentIds || [];
            if (!updatedTask.completedStudentIds.includes(currentStudentId)) {
                updatedTask.completedStudentIds.push(currentStudentId);
                await request.put('/task/update', updatedTask);
            }

            // 保存成绩为100分
            const taskId = task.id;
            const score = 100;
            const existingGradeRes = await request.get('/taskgrade/selectPage', {
                params: {
                    taskId,
                    studentId: currentStudentId,
                    pageNum: 1,
                    pageSize: 1
                }
            });
            const existingGrade = existingGradeRes.data?.list?.[0];

            if (!existingGrade) {
                // 首次打分，调用add接口
                const addRes = await request.post('/taskgrade/add', {
                    taskId,
                    studentId: currentStudentId,
                    score
                });
                if (addRes.code === '200') {
                    ElMessage.success('分数添加成功');
                } else {
                    ElMessage.error(addRes.msg);
                }
            } else {
                // 后续修改分数，调用update接口
                const updateRes = await request.put('/taskgrade/update', {
                    id: existingGrade.id,
                    taskId,
                    studentId: currentStudentId,
                    score
                });
                if (updateRes.code === '200') {
                    ElMessage.success('分数更新成功');
                } else {
                    ElMessage.error(updateRes.msg);
                }
            }

            loadAllTasks();
        } catch (error) {
            console.error('处理查看课程文件任务失败:', error);
            ElMessage.error('处理查看课程文件任务失败');
        }
    } else if (task.tag === '观看课程视频') {
        try {
            const filesRes = await request.get('/files/selectPage', {
                params: {
                    pageNum: 1,
                    pageSize: 1000,
                    filename: task.filename,
                    courseId: task.courseId
                }
            });
            const files = filesRes.data?.list || [];
            const videoFile = files.find(file => file.fileUrl === task.fileUrl && file.filename === task.filename);

            if (videoFile) {
                router.push({ path: `/manager/video/${videoFile.id}` });

                const updatedTask = { ...task };
                updatedTask.completedStudentIds = updatedTask.completedStudentIds || [];
                if (!updatedTask.completedStudentIds.includes(currentStudentId)) {
                    updatedTask.completedStudentIds.push(currentStudentId);
                    await request.put('/task/update', updatedTask);
                }

                // 保存成绩为100分
                const taskId = task.id;
                const score = 100;
                const existingGradeRes = await request.get('/taskgrade/selectPage', {
                    params: {
                        taskId,
                        studentId: currentStudentId,
                        pageNum: 1,
                        pageSize: 1
                    }
                });
                const existingGrade = existingGradeRes.data?.list?.[0];

                if (!existingGrade) {
                    // 首次打分，调用add接口
                    const addRes = await request.post('/taskgrade/add', {
                        taskId,
                        studentId: currentStudentId,
                        score
                    });
                    if (addRes.code === '200') {
                        ElMessage.success('分数添加成功');
                    } else {
                        ElMessage.error(addRes.msg);
                    }
                } else {
                    // 后续修改分数，调用update接口
                    const updateRes = await request.put('/taskgrade/update', {
                        id: existingGrade.id,
                        taskId,
                        studentId: currentStudentId,
                        score
                    });
                    if (updateRes.code === '200') {
                        ElMessage.success('分数更新成功');
                    } else {
                        ElMessage.error(updateRes.msg);
                    }
                }

                loadAllTasks();
            } else {
                ElMessage.warning('未找到对应的视频文件');
            }
        } catch (error) {
            console.error('处理观看课程视频任务失败:', error);
            ElMessage.error('处理观看课程视频任务失败');
        }
    }
}

const beforeFileUpload = (file) => {
    fileForm.uploadedFileName = file.name
    return true
}

const handleFileUploadSuccess = (res) => {
    if (res.code === '200') {
        fileForm.fileUrl = res.data
        ElMessage.success(`文件 "${fileForm.uploadedFileName}" 上传成功`)
    } else {
        ElMessage.error(res.msg)
        fileForm.uploadedFileName = ''
    }
}

const handleFileUploadError = () => {
    ElMessage.error('文件上传失败，请稍后重试')
    fileForm.uploadedFileName = ''
}

const saveFileSubmission = () => {
    fileFormRef.value.validate((valid) => {
        if (valid) {
            if (!fileForm.fileUrl) {
                ElMessage.error('请先上传文件');
                return;
            }
            currentSubmissionId.value ? updateFileSubmission() : addFileSubmission()
        }
    })
}

const addFileSubmission = () => {
    request.post('/submission/add', fileForm).then(res => {
        if (res.code === '200') {
            fileDialogVisible.value = false;
            ElMessage.success('文件提交成功');
            const task = data.tableData.find(t => t.id === fileForm.taskId);
            if (task) {
                task.completedStudentIds = task.completedStudentIds || [];
                if (!task.completedStudentIds.includes(currentStudentId)) {
                    task.completedStudentIds.push(currentStudentId);
                    request.put('/task/update', task).then(() => {
                        loadAllTasks();
                    }).catch(error => {
                        console.error('更新任务完成状态失败:', error);
                        ElMessage.error('更新任务完成状态失败');
                    });
                }
            }
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        console.error('文件提交失败:', error);
        ElMessage.error('文件提交失败');
    });
}

const updateFileSubmission = () => {
    const submission = {
        ...fileForm,
        id: currentSubmissionId.value
    };
    request.put('/submission/update', submission).then(res => {
        if (res.code === '200') {
            fileDialogVisible.value = false;
            ElMessage.success('文件更新成功');
            loadAllTasks();
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        console.error('文件更新失败:', error);
        ElMessage.error('文件更新失败');
    });
}

const formRef = ref()
const studentDialogVisible = ref(false)
const currentTaskTitle = ref('')

const taskGrades = ref<Record<number, string>>({});

const getTaskGrade = async (taskId: number) => {
    try {
        const res = await request.get('/taskgrade/selectPage', {
            params: {
                taskId,
                studentId: currentStudentId,
                pageNum: 1,
                pageSize: 1
            }
        });
        const grade = res.data?.list?.[0];
        return grade ? grade.score : '未评分';
    } catch (error) {
        console.error('获取任务成绩失败:', error);
        return '获取成绩失败';
    }
}

const loadTaskGrades = async (tasks: any[]) => {
    const grades: Record<number, string> = {};
    for (const task of tasks) {
        grades[task.id] = await getTaskGrade(task.id);
    }
    taskGrades.value = grades;
}
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
  margin: 5px;
}

.task-card {
  border: 2px solid #dcdfe6;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  cursor: pointer;
}

.task-card:hover {
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.task-card-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.task-title {
  font-size: 24px;
  margin-bottom: 10px;
}

.check-button {
    font-size: 16px;
}

.el-button.link {
  font-size: inherit;
}

.expired-task {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #909399;
}

.expired-task .task-title {
  color: #909399;
}

.expired-task .el-button--primary {
  cursor: pointer;
}

.expired-task .el-button--danger {
  cursor: pointer;
}

.text-danger {
  color: #f56c6c;
}

.file-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  width: 100%;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.file-uploader:hover {
  border-color: var(--el-color-primary);
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.uploaded-file-info {
  display: flex;
  align-items: center;
}

.uploaded-filename {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}
</style>