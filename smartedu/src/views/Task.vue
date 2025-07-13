<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
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
                        <p><strong>任务发布班级:</strong> {{ getClassNames(task.classIds) }}</p>
                        <p><strong>学生完成情况:</strong> 
                            <el-button class="check-button" @click="showStudentDialog(task)" link type="primary">查看情况</el-button>
                        </p>
                        <p><strong>课程标题:</strong> {{ getCourseTitle(task.courseId) }}</p>
                        <p><strong>任务类型:</strong> {{ task.tag }}</p>
                        <p><strong>开始时间:</strong> {{ task.time }}</p>
                        <p><strong>截止时间:</strong> {{ task.dueDate }}</p>
                        <div class="task-card-actions">
                            <el-button 
                                @click="handleUpdate(task)" 
                                link 
                                type="primary" 
                                :icon="Edit"
                                :disabled="isTaskExpired(task)"
                                :style="{opacity: isTaskExpired(task) ? 0.5 : 1}"
                            >编辑</el-button>
                            <el-button @click="del(task.id)" link type="danger" :icon="Delete">删除</el-button>
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
                        <p><strong>任务发布班级:</strong> {{ getClassNames(task.classIds) }}</p>
                        <p><strong>学生完成情况:</strong> 
                            <el-button 
                                class="check-button" 
                                @click="showStudentDialog(task)" 
                                link 
                                type="primary"
                                style="color: #409eff; cursor: pointer"
                            >查看情况</el-button>
                        </p>
                        <p><strong>课程标题:</strong> {{ getCourseTitle(task.courseId) }}</p>
                        <p><strong>任务类型:</strong> {{ task.tag }}</p>
                        <p><strong>开始时间:</strong> {{ task.time }}</p>
                        <p><strong>截止时间:</strong> {{ task.dueDate }}</p>
                        <div class="task-card-actions">
                            <el-button 
                                link 
                                type="primary" 
                                :icon="Edit"
                                disabled
                                style="opacity: 0.5; cursor: not-allowed"
                            >编辑</el-button>
                            <el-button 
                                @click="del(task.id)" 
                                link 
                                type="danger" 
                                :icon="Delete"
                                style="color: #f56c6c; cursor: pointer"
                            >删除</el-button>
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
        </el-tabs>

        <el-dialog :title="`${currentTaskTitle} 学生完成情况`" v-model="studentDialogVisible" width="500" destroy-on-close>
            <div>
                <el-select v-model="currentClassId" placeholder="选择班级" style="width: 100%; margin-bottom: 20px" @change="changeClass">
                    <el-option
                        v-for="classId in currentTaskClassIds"
                        :key="classId"
                        :label="getClassName(classId)"
                        :value="classId"
                    />
                </el-select>
                
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

        <el-dialog :title="data.form.id ? '编辑任务' : '发布新任务'" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入任务标题"/>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <el-input v-model="data.form.content" type="textarea" rows="4" autocomplete="off" placeholder="请输入任务内容"/>
                </el-form-item>
                <el-form-item label="任务类型" prop="tag">
                    <el-select v-model="data.form.tag" placeholder="请选择任务类型" style="width: 100%" @change="loadFilesIfNeeded">
                        <el-option label="查看课程文件" value="查看课程文件" />
                        <el-option label="观看课程视频" value="观看课程视频" />
                        <el-option label="提交文件" value="提交文件" />
                    </el-select>
                </el-form-item>
                <el-form-item label="发布班级" prop="classIds">
                    <el-select v-model="data.form.classIds" multiple placeholder="请选择要发布任务的班级" style="width: 100%">
                        <el-option
                            v-for="classItem in data.classOptions"
                            :key="classItem.id"
                            :label="classItem.name"
                            :value="classItem.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="对应课程" prop="courseId">
                    <el-select v-model="data.form.courseId" placeholder="请选择任务对应课程" style="width: 100%" @change="loadFilesIfNeeded">
                        <el-option
                            v-for="course in data.courseOptions"
                            :key="course.id"
                            :label="course.title"
                            :value="course.id"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="文件选择" prop="filename" v-if="shouldShowFileSelect">
                    <el-select v-model="selectedFilename" placeholder="请选择文件" style="width: 100%">
                        <el-option
                            v-for="file in data.fileOptions"
                            :key="file.id"
                            :label="file.filename"
                            :value="file.filename"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="开始时间" prop="time">
                    <el-date-picker
                        v-model="data.form.time"
                        type="datetime"
                        placeholder="选择开始时间"
                        style="width: 100%"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        :disabled-date="disabledStartTime"
                        :disabled="isStartTimeInPast"
                    />
                    <template #append>
                        <span v-if="isStartTimeInPast" class="el-input__suffix-inner text-danger">已开始，不可修改</span>
                    </template>
                </el-form-item>

                <el-form-item label="截止时间" prop="dueDate">
                    <el-date-picker
                        v-model="data.form.dueDate"
                        type="datetime"
                        placeholder="选择截止时间"
                        style="width: 100%"
                        value-format="YYYY-MM-DD HH:mm:ss"
                        :disabled-date="disabledEndTime"
                        :disabled-hours="() => disabledEndHours()"
                        :disabled-minutes="() => disabledEndMinutes()"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">保存</el-button>
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

const getCurrentDate = () => {
    const date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
};

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}')

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
        courseId: null,
        filename: '',
        fileUrl: ''
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
    },
    fileOptions: []
})

const formRef = ref()
const studentDialogVisible = ref(false)
const currentTaskTitle = ref('')
const currentClassStudents = ref([])
const currentCompletedStudentIds = ref([])
const activeTab = ref('active');
const currentClassId = ref(null);
const currentTaskClassIds = ref([]);
const selectedFilename = ref('');

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

const isTaskExpired = (task: any) => {
    if (!task.dueDate) return false;
    return new Date(task.dueDate).getTime() < new Date().getTime();
}

const activeTasks = computed(() => {
    return data.tableData
        .filter(task => !isTaskExpired(task))
        .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
        .slice(0, activePageInfo.pageSize);
});

const expiredTasks = computed(() => {
    return data.tableData
        .filter(task => isTaskExpired(task))
        .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
        .slice(0, expiredPageInfo.pageSize);
});

const disabledStartTime = (time: Date) => {
    if (!data.form.id) {
        return time.getTime() < getCurrentDate();
    }
    
    if (isStartTimeInPast.value) {
        return true;
    }
    
    return time.getTime() < getCurrentDate();
};

const disabledEndTime = (time: Date) => {
    if (!data.form.time) {
        return time.getTime() < getCurrentDate();
    }
    return time.getTime() < new Date(data.form.time).getTime();
};

const disabledEndHours = () => {
    if (!data.form.time) return [];
    
    const startDate = new Date(data.form.time);
    const endDate = new Date(data.form.dueDate || new Date());
    
    if (
        startDate.getFullYear() === endDate.getFullYear() &&
        startDate.getMonth() === endDate.getMonth() &&
        startDate.getDate() === endDate.getDate()
    ) {
        return Array.from({ length: startDate.getHours() }, (_, i) => i);
    }
    return [];
};

const disabledEndMinutes = () => {
    if (!data.form.time) return [];
    
    const startDate = new Date(data.form.time);
    const endDate = new Date(data.form.dueDate || new Date());
    
    if (
        startDate.getFullYear() === endDate.getFullYear() &&
        startDate.getMonth() === endDate.getMonth() &&
        startDate.getDate() === endDate.getDate() &&
        startDate.getHours() === endDate.getHours()
    ) {
        return Array.from({ length: startDate.getMinutes() + 1 }, (_, i) => i);
    }
    return [];
};

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
        
        activePageInfo.total = allTasks.filter(task => !isTaskExpired(task)).length;
        expiredPageInfo.total = allTasks.filter(task => isTaskExpired(task)).length;
        

        const activeStart = (activePageInfo.pageNum - 1) * activePageInfo.pageSize;
        const activeEnd = activeStart + activePageInfo.pageSize;
        const expiredStart = (expiredPageInfo.pageNum - 1) * expiredPageInfo.pageSize;
        const expiredEnd = expiredStart + expiredPageInfo.pageSize;
        
        const activeTasks = allTasks
            .filter(task => !isTaskExpired(task))
            .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
            .slice(activeStart, activeEnd);
            
        const expiredTasks = allTasks
            .filter(task => isTaskExpired(task))
            .sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
            .slice(expiredStart, expiredEnd);
            
        data.tableData = [...activeTasks, ...expiredTasks];
    } catch (error) {
        console.error('加载数据失败:', error);
        ElMessage.error('数据加载失败');
    }
}

const loadFiles = async () => {
    if (data.form.courseId && (data.form.tag === '查看课程文件' || data.form.tag === '观看课程视频')) {
        try {
            const res = await request.get('/files/selectPage', {
                params: {
                    pageNum: 1,
                    pageSize: 1000,
                    courseId: data.form.courseId
                }
            });
            data.fileOptions = res.data.list || [];
        } catch (error) {
            console.error('加载课程文件失败:', error);
            ElMessage.error('加载课程文件失败');
        }
    } else {
        data.fileOptions = [];
    }
}

const shouldShowFileSelect = computed(() => {
    return data.form.courseId && (data.form.tag === '查看课程文件' || data.form.tag === '观看课程视频');
});

const loadFilesIfNeeded = () => {
    loadFiles();
}

onMounted(async () => {
    await Promise.all([loadAllTasks(), loadClasses(), loadCourses(), loadStudents()]);
    console.log('数据加载完成');
})

const reset = () => {
    searchParams.name = null;
    activePageInfo.pageNum = 1;
    expiredPageInfo.pageNum = 1;
    loadAllTasks();
}

const handleAdd = () => {
    data.form = { 
        classIds: [],
        time: '',
        dueDate: '',
        courseId: null,
        filename: '',
        fileUrl: ''
    };
    data.originalStartTime = '';
    data.formVisible = true;
    selectedFilename.value = '';
    data.fileOptions = [];
}

const handleUpdate = (row) => {
    if (isTaskExpired(row)) {
        ElMessage.warning('已截止的任务不能编辑');
        return;
    }
    
    data.form = JSON.parse(JSON.stringify(row));
    data.form.classIds = row.classIds || [];
    data.form.time = row.time || '';
    data.form.dueDate = row.dueDate || '';
    data.form.courseId = row.courseId || null;
    data.form.filename = row.filename || '';
    data.form.fileUrl = row.fileUrl || '';
    
    data.originalStartTime = data.form.time;
    
    data.formVisible = true;
    loadFiles();
    selectedFilename.value = data.form.filename;
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const selectedFile = data.fileOptions.find(file => file.filename === selectedFilename.value);
            if (selectedFile) {
                data.form.filename = selectedFile.filename;
                data.form.fileUrl = selectedFile.fileUrl;
            }
            data.form.id ? update() : add()
        }
    })
}

const add = () => {
    request.post('/task/add', data.form).then(res => {
        if (res.code === '200') {
            ElMessage.success('任务发布成功');
            data.formVisible = false;
            loadAllTasks();
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        console.error('发布任务失败:', error);
        ElMessage.error('发布任务失败');
    });
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

const del = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/task/deleteById/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success('任务删除成功');
                loadAllTasks();
            } else {
                ElMessage.error(res.msg);
            }
        }).catch(error => {
            console.error('删除任务失败:', error);
            ElMessage.error('删除任务失败');
        });
    }).catch();
}

const handleSelectionChange = (rows) => {
    data.ids = rows.map(row => row.id);
}

const delBatch = () => {
    if (data.ids.length === 0) {
        ElMessage.warning('请选择要删除的数据');
        return;
    }
    
    const hasExpiredTasks = data.tableData
        .filter(task => data.ids.includes(task.id))
        .some(task => isTaskExpired(task));
    
    ElMessageBox.confirm(
        `共选择了 ${data.ids.length} 个任务，${hasExpiredTasks ? '其中包含已截止任务，' : ''}删除后将无法恢复，是否确认删除？`, 
        '删除确认', 
        { type: 'warning' }
    ).then(() => {
        request.delete('/task/deleteBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('批量删除成功');
                loadAllTasks();
            } else {
                ElMessage.error(res.msg);
            }
        }).catch(error => {
            console.error('批量删除失败:', error);
            ElMessage.error('批量删除失败');
        });
    }).catch();
}

const showStudentDialog = (row) => {
    if (data.studentList.length === 0) {
        ElMessage.warning('学生数据正在加载，请稍后再试');
        return;
    }
    
    currentTaskTitle.value = row.title;
    currentTaskClassIds.value = row.classIds || [];
    
    // 默认选择第一个班级
    if (currentTaskClassIds.value.length > 0) {
        currentClassId.value = currentTaskClassIds.value[0];
        currentClassStudents.value = getStudentsByClassId(currentClassId.value);
        currentCompletedStudentIds.value = row.completedStudentIds || [];
        studentDialogVisible.value = true;
    } else {
        ElMessage.warning('该任务未分配到任何班级');
    }
}

const changeClass = () => {
    if (currentClassId.value) {
        currentClassStudents.value = getStudentsByClassId(currentClassId.value);
    }
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

const getClassNames = (classIds: number[]) => {
    if (!classIds || classIds.length === 0) return '未分配';
    
    return classIds
        .map(id => {
            const classItem = data.classOptions.find(c => c.id === id);
            return classItem ? classItem.name : '未知班级';
        })
        .join(', ');
}

const getClassName = (classId: number) => {
    const classItem = data.classOptions.find(c => c.id === classId);
    return classItem ? classItem.name : '未知班级';
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

const handleTabClick = (tab: any) => {
    activeTab.value = tab.name;
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
  width: 33.3333%;
  box-sizing: border-box;
  padding: 5px;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.task-card {
  border: 2px solid #dcdfe6;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
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
</style>

<style>
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
</style>