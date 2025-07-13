<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
            <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="data.title" placeholder="请输入课程标题查询" prefix-icon="Search"></el-input>
            <el-button type="primary" @click="load">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column label="标题" prop="title" />
                <el-table-column label="图片">
                    <template #default="scope">
                        <img 
                            v-if="scope.row.imgs" 
                            :src="scope.row.imgs" 
                            :preview-src-list="[scope.row.imgs]" 
                            preview-teleported 
                            alt="" 
                            style="display: block; width: 100px; height: 100px;" 
                        />
                    </template>
                </el-table-column>
                <el-table-column label="课程简介" prop="description" />
                <el-table-column label="发布时间" prop="time" />
                <el-table-column label="负责教师">
                    <template #default="scope">
                        {{ getTeacherName(scope.row.teacherId) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="250px">
                    <template #default="scope">
                        <el-button @click="handleUpdate(scope.row)" link type="primary" :icon="Edit">编辑</el-button>
                        <el-button @click="del(scope.row.id)" link type="danger" :icon="Delete">删除</el-button>
                        <el-button @click="handleManageFiles(scope.row.id, scope.row.title)" link type="primary">管理课程资源</el-button>
                    </template>
                </el-table-column>
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

        <el-dialog :title="data.form.id ? '编辑课程' : '新增课程'" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="课程名称" prop="title">
                    <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入课程标题"/>
                </el-form-item>
                <el-form-item label="封面">
                    <el-upload
                        class="imgs-uploader"
                        action="http://localhost:8080/files/upload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                    >
                        <img 
                            v-if="data.form.imgs" 
                            :src="data.form.imgs" 
                            class="imgs" 
                        />
                        <el-icon v-else class="imgs-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="课程简介" prop="description">
                    <el-input v-model="data.form.description" autocomplete="off" placeholder="请输入课程简介"/>
                </el-form-item>
                <el-form-item label="负责教师" prop="teacherId">
                    <el-select v-model="data.form.teacherId" placeholder="请选择教师" style="width: 100%">
                        <el-option
                            v-for="teacher in data.teacherOptions"
                            :key="teacher.id"
                            :label="teacher.name"
                            :value="teacher.id"
                        />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">保存</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog :title="'管理课程资源 - ' + data.currentCourseTitle" v-model="data.fileManageVisible" width="800" destroy-on-close>
            <div class="card" style="margin-bottom: 5px">
                <el-button type="primary" @click="handleFileAdd">新增</el-button>
                <el-button type="danger" @click="delFileBatch">批量删除</el-button>
                <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="fileSearch.filename" placeholder="请输入文件名查询" prefix-icon="Search"></el-input>
                <el-button type="primary" @click="loadFiles">查询</el-button>
                <el-button type="danger" @click="resetFileSearch">重置</el-button>
            </div>
            <el-table :data="data.fileList" stripe @selection-change="handleFileSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column label="文件名" prop="filename" />
                <el-table-column label="查看文件">
                    <template #default="scope">
                        <el-button 
                            size="small" 
                            type="primary" 
                            @click="openFile(scope.row.originalFileUrl || scope.row.fileUrl)"
                            :loading="scope.row.isLoading"
                        >
                            打开文件
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column label="描述" prop="description" />
                <el-table-column label="标签" prop="tag" />
                <el-table-column label="操作" width="150px">
                    <template #default="scope">
                        <el-button @click="handleFileUpdate(scope.row)" link type="primary" :icon="Edit">编辑</el-button>
                        <el-button @click="delFile(scope.row.id)" link type="danger" :icon="Delete">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div style="margin-top: 15px;">
                <el-pagination
                    @size-change="loadFiles"
                    @current-change="loadFiles"
                    v-model:current-page="fileSearch.pageNum"
                    v-model:page-size="fileSearch.pageSize"
                    :page-sizes="[5, 10, 15, 20]"
                    background
                    layout="total, size, prev, pager, next, jumper"
                    :total="data.fileTotal"
                />
            </div>

            <el-dialog title="文件信息" v-model="data.fileFormVisible" width="500" destroy-on-close>
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
                    <el-form-item label="描述" prop="description">
                        <el-input v-model="fileForm.description" autocomplete="off" placeholder="请输入描述"/>
                    </el-form-item>
                    <el-form-item label="标签" prop="tag">
                        <el-select v-model="fileForm.tag" placeholder="请选择标签">
                            <el-option label="视频" value="视频"></el-option>
                            <el-option label="PPT" value="PPT"></el-option>
                            <el-option label="PDF" value="PDF"></el-option>
                            <el-option label="DOC" value="DOC"></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="data.fileFormVisible = false">取消</el-button>
                        <el-button type="primary" @click="saveFile">保存</el-button>
                    </div>
                </template>
            </el-dialog>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.fileManageVisible = false">关闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { Edit, Delete, Search, Plus, Check } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}');
const currentTeacherId = userInfo.id; // 获取当前登录教师的id

const data = reactive({
    role: userInfo.role,
    username: null,
    name: null,
    title: null,
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    formVisible: false,
    form: {
        teacherId: null
    },
    teacherOptions: [],
    ids: [],
    rules: {
        name: [
            { required: true, message: '请输入班级名称', trigger: 'blur' }
        ],
        classId: [
            { required: true, message: '请输入班级号', trigger: 'blur' }
        ],
        teacherId: [
            { required: true, message: '请选择负责教师', trigger: 'change' }
        ]
    },
    fileManageVisible: false,
    fileList: [],
    fileIds: [],
    currentCourseId: null,
    currentCourseTitle: null,
    fileFormVisible: false,
    fileTotal: 0
})

const formRef = ref()
const fileFormRef = ref()
const fileForm = reactive({
    id: null, // 新增：用于区分新增/编辑的核心字段
    uploadedFileName: '',
    fileUrl: '',
    filename: '',
    description: '',
    tag: '',
    courseId: null
})

const fileSearch = reactive({
    filename: null,
    pageNum: 1,
    pageSize: 10
})

const fileFormRules = {
    filename: [
        { required: true, message: '请输入文件名', trigger: 'blur'}
    ],
    tag: [
        { required: true, message: '请选择标签', trigger: 'change'}
    ]
}

const loadTeachers = async () => {
    try {
        const res = await request.get('/teacher/getAll');
        data.teacherOptions = res.data || [];
    } catch (error) {
        console.error('加载教师列表失败:', error);
        ElMessage.error('教师列表加载失败');
    }
}

const getTeacherName = (teacherId) => {
    if (!teacherId) return '未分配';
    const teacher = data.teacherOptions.find(t => t.id === teacherId);
    return teacher ? teacher.name : '未知教师';
}

const load = async () => {
    try {
        const res = await request.get('/course/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                name: data.name,
                teacherId: currentTeacherId
            }
        });
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
    } catch (error) {
        console.error('加载数据失败:', error);
        ElMessage.error('数据加载失败');
    }
}

onMounted(() => {
    load();
    loadTeachers();
})

const reset = () => {
    data.name = null
    load()
}

const handleAvatarSuccess = (res) => {
    data.form.imgs = res.data
}

const handleAdd = () => {
    data.form = { teacherId: null };
    data.formVisible = true
}

const handleUpdate = (row) => {
    data.form = JSON.parse(JSON.stringify(row))
    data.formVisible = true
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            data.form.id ? update() : add()
        }
    })
}

const add = () => {
    request.post('/course/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('课程添加成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    }).catch(error => {
        console.error('添加课程失败:', error);
        ElMessage.error('添加课程失败')
    })
}

const update = () => {
    request.put('/course/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('课程更新成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    }).catch(error => {
        console.error('更新课程失败:', error);
        ElMessage.error('更新课程失败')
    })
}

const del = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/course/delete/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success('课程删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
        }).catch(error => {
            console.error('删除课程失败:', error);
            ElMessage.error('删除课程失败')
        })
    }).catch()
}
 
const handleSelectionChange = (rows) => {
    data.ids = rows.map(row => row.id)
}

const delBatch = () => {
    if (data.ids.length === 0) {
        ElMessage.warning('请选择要删除的数据')
        return
    }
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/course/deleteBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('批量删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
        }).catch(error => {
            console.error('批量删除失败:', error);
            ElMessage.error('批量删除失败')
        })
    }).catch()
}

const handleManageFiles = async (courseId, courseTitle) => {
    data.currentCourseId = courseId;
    data.currentCourseTitle = courseTitle;
    fileSearch.filename = null;
    fileSearch.pageNum = 1;
    await loadFiles();
    data.fileManageVisible = true;
}

const loadFiles = async () => {
    try {
        const res = await request.get('/files/selectPage', {
            params: {
                pageNum: fileSearch.pageNum,
                pageSize: fileSearch.pageSize,
                filename: fileSearch.filename,
                courseId: data.currentCourseId
            }
        });
        data.fileList = res.data.list.map(item => ({
            ...item,
            isLoading: false,
            originalFileUrl: item.fileUrl,
        }));
        data.fileTotal = res.data.total;
    } catch (error) {
        console.error('加载课程资源失败:', error);
        ElMessage.error('加载课程资源失败');
    }
}

const resetFileSearch = () => {
    fileSearch.filename = null;
    loadFiles();
}

const openFile = (fileUrl: string) => {
    if (!fileUrl) {
        ElMessage.warning('文件链接不存在');
        return;
    }
    
    try {
        window.open(fileUrl, '_blank');
    } catch (error) {
        ElMessage.error('打开文件失败，请稍后重试');
        console.error('Failed to open file:', error);
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

const handleFileAdd = () => {
    // 新增时重置id为null
    Object.assign(fileForm, {
        id: null,
        uploadedFileName: '',
        fileUrl: '',
        filename: '',
        description: '',
        tag: '',
        courseId: data.currentCourseId
    })
    data.fileFormVisible = true
}

const handleFileUpdate = (row) => {
    data.fileFormVisible = true
    // 编辑时赋值完整数据（包含id），保持响应式
    Object.assign(fileForm, {
        ...row,
        fileUrl: row.originalFileUrl || row.fileUrl,
        uploadedFileName: row.fileUrl ? row.fileUrl.split('/').pop() || '' : '',
        courseId: data.currentCourseId
    })
}

const saveFile = () => {
    fileFormRef.value.validate((valid) => {
        if (valid) {
            if (!fileForm.fileUrl) {
                ElMessage.error('请先上传文件');
                return;
            }
            // 根据id判断调用新增还是编辑接口
            fileForm.id ? updateFile() : addFile()
        }
    })
}

const addFile = () => {
    request.post('/files/add', fileForm).then(res => {
        if (res.code === '200') {
            data.fileFormVisible = false
            ElMessage.success('文件添加成功')
            loadFiles()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const updateFile = () => {
    request.put('/files/update', fileForm).then(res => {
        if (res.code === '200') {
            data.fileFormVisible = false
            ElMessage.success('文件修改成功')
            loadFiles()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const delFile = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/files/deleteById/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success('课程资源删除成功');
                loadFiles();
            } else {
                ElMessage.error(res.msg);
            }
        }).catch(error => {
            console.error('删除课程资源失败:', error);
            ElMessage.error('删除课程资源失败');
        });
    }).catch();
}

const handleFileSelectionChange = (rows) => {
    data.fileIds = rows.map(row => row.id);
}

const delFileBatch = () => {
    if (data.fileIds.length === 0) {
        ElMessage.warning('请选择要删除的数据')
        return
    }
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/files/deleteBatch', { data: data.fileIds}).then(res => {
            if (res.code === '200') {
                ElMessage.success('删除成功')
                loadFiles()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}
</script>

<style scoped>
.imgs-uploader .imgs {
    width: 120px;
    height: 120px;
    display: block;
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
    gap: 8px;
    color: var(--el-color-success);
    font-size: 14px;
}

.uploaded-filename {
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>

<style>
.imgs-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.imgs-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.imgs-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
}
</style>