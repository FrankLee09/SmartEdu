<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
            <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="data.filename" placeholder="请输入文件名查询" prefix-icon="Search"></el-input>
            <el-button type="primary" @click="load">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-timeline>
                <el-timeline-item
                    v-for="item in data.tableData"
                    :key="item.id"
                    placement="top"
                >
                    <div class="file-card">
                        <div class="file-card-header">
                            <span class="file-name">{{ item.filename }}</span>
                            <el-button 
                                size="small" 
                                type="primary" 
                                @click="openFile(item.originalFileUrl || item.fileUrl)"
                                :loading="item.isLoading"
                            >
                                打开文件
                            </el-button>
                        </div>
                        <div class="file-card-content">
                            <p><strong>描述:</strong> {{ item.description }}</p>
                            <p><strong>标签:</strong> {{ item.tag }}</p>
                        </div>
                        <div class="file-card-actions">
                            <el-button @click="handleUpdate(item)" link type="primary" :icon="Edit">编辑</el-button>
                            <el-button @click="del(item.id)" link type="danger" :icon="Delete">删除</el-button>
                        </div>
                    </div>
                </el-timeline-item>
            </el-timeline>
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

        <el-dialog title="文件信息" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="文件名" prop="filename">
                    <el-input v-model="data.form.filename" autocomplete="off" placeholder="请输入文件名"/>
                </el-form-item>
                <el-form-item label="文件上传">
                    <el-upload
                        class="file-uploader"
                        action="http://localhost:8080/files/uploadFile"
                        :show-file-list="false"
                        :on-success="handleUploadSuccess"
                        :on-error="handleUploadError"
                        :before-upload="beforeUpload"
                    >
                        <div v-if="data.form.uploadedFileName" class="uploaded-file-info">
                            <el-icon><Check /></el-icon>
                            <span class="uploaded-filename">{{ data.form.uploadedFileName }}</span>
                        </div>
                        <el-icon v-else class="uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="data.form.description" autocomplete="off" placeholder="请输入描述"/>
                </el-form-item>
                <el-form-item label="标签" prop="tag">
                    <el-select v-model="data.form.tag" placeholder="请选择标签">
                        <el-option label="视频" value="视频"></el-option>
                        <el-option label="PPT" value="PPT"></el-option>
                        <el-option label="PDF" value="PDF"></el-option>
                        <el-option label="DOC" value="DOC"></el-option>
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

    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { Edit, Delete, Search, Plus, Check, Document, VideoCamera } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';

const data = reactive({
    filename: null,
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    formVisible: false,
    form: {
        uploadedFileName: '',
        fileUrl: '',
        filename: '',
        description: '',
        tag: ''
    },
    ids: [],
    isEdit: false,
    rules: {
        filename: [
            { required: true, message: '请输入文件名', trigger: 'blur'}
        ],
        tag: [
            { required: true, message: '请选择标签', trigger: 'change'}
        ]
    }
})

const formRef = ref()

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

const load = () => {
    request.get('/files/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            filename: data.filename
        }
    }).then(res => {
        data.tableData = res.data.list.map(item => ({
            ...item,
            isLoading: false,
            originalFileUrl: item.fileUrl,
        }));
        data.total = res.data.total
    })
}
load()

const beforeUpload = (file) => {
    data.form.uploadedFileName = file.name
    return true
}

const handleUploadSuccess = (res) => {
    if (res.code === '200') {
        data.form.fileUrl = res.data
        ElMessage.success(`文件 "${data.form.uploadedFileName}" 上传成功`)
    } else {
        ElMessage.error(res.msg)
        data.form.uploadedFileName = ''
    }
}

const handleUploadError = () => {
    ElMessage.error('文件上传失败，请稍后重试')
    data.form.uploadedFileName = ''
}

const reset = () => {
    data.filename = null
    load()
}

const handleAdd = () => {
    data.formVisible = true
    data.form = {
        uploadedFileName: '',
        fileUrl: '',
        filename: '',
        description: '',
        tag: ''
    }
    data.isEdit = false
}

const handleUpdate = (row) => {
    data.formVisible = true
    data.isEdit = true
    data.form = {
        ...row,
        fileUrl: row.originalFileUrl || row.fileUrl,
        uploadedFileName: row.fileUrl ? row.fileUrl.split('/').pop() || '' : ''
    }
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            if (!data.form.fileUrl) {
                ElMessage.error('请先上传文件');
                return;
            }
            data.isEdit ? update() : add()
        }
    })
}

const add = () => {
    request.post('/files/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('文件添加成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const update = () => {
    request.put('/files/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('文件修改成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const del = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/files/deleteById/' + id).then(res => {
            if (res.code === '200') {
                data.formVisible = false
                ElMessage.success('文件删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
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
        request.delete('/files/deleteBatch', { data: data.ids}).then(res => {
            if (res.code === '200') {
                data.formVisible = false
                ElMessage.success('删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}
</script>

<style scoped>
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

.file-card {
    border: 1px solid var(--el-border-color);
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
}

.file-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.file-name {
    font-size: 16px;
    font-weight: bold;
}

.file-card-content {
    margin-bottom: 10px;
}

.file-card-actions {
    display: flex;
    justify-content: flex-end;
}
</style>