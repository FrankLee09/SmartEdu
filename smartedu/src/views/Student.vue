<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
            <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="data.name" placeholder="请输入名称查询" prefix-icon="Search"></el-input>
            <el-button type="primary" @click="load">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column label="学号" prop="id" />
                <el-table-column label="头像">
                    <template #default="scope">
                        <img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" style="display: block; width: 40px; height: 40px; border-radius: 50%;" />
                    </template>
                </el-table-column>
                <el-table-column label="名称" prop="name" />
                <el-table-column label="班级号" prop="classId" />
                <el-table-column label="操作" width="150px">
                    <template #default="scope">
                        <el-button @click="handleUpdate(scope.row)" link type="primary" :icon="Edit">编辑</el-button>
                        <el-button @click="del(scope.row.id)" link type="danger" :icon="Delete">删除</el-button>
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

        <el-dialog title="用户信息" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="学号" prop="id">
                    <el-input v-model="data.form.id" autocomplete="off" placeholder="请输入学号"/>
                </el-form-item>
                <el-form-item label="头像">
                    <el-upload
                        class="avatar-uploader"
                        action="http://localhost:8080/files/upload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                    >
                        <img v-if="data.form.avatar" :src="data.form.avatar" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"/>
                </el-form-item>
                 <el-form-item label="班级号" prop="classId">
                    <el-input v-model="data.form.classId" autocomplete="off" placeholder="请输入班级号"/>
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
import { User } from '@element-plus/icons-vue';
import { reactive, ref } from 'vue';
import { Edit, Delete, Search } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}')

const data = reactive({
    role: userInfo.role,
    id: null,
    username: null,
    name: null,
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    formVisible: false,
    form: {},
    ids: [],
    isEdit: false,
    rules: {
        id: [
            { required: true, message: '请输入学号', trigger: 'blur'}
        ],
        name: [
            { required: true, message: '请输入名称', trigger: 'blur'}
        ]
    }
})

const formRef = ref()

const load = () => {
    request.get('/student/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            username: data.username,
            name: data.name
        }
    }).then(res => {
        data.tableData = res.data.list
        data.total = res.data.total
    })
}
load()

const handleAvatarSuccess = (res) => {
    console.log(res.data)
    data.form.avatar = res.data
}

const reset = () => {
    data.username = null
    data.name = null
    load()
}

const handleAdd = () => {
    data.formVisible = true
    data.form = {}
    data.isEdit = false
}

const handleUpdate = (row) => {
    data.form = JSON.parse(JSON.stringify(row))
    data.formVisible = true
    data.isEdit = true
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            data.isEdit ? update() : add()
        }
    })
}

const add = () => {
    request.post('/student/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('用户添加成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const update = () => {
    request.put('/student/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('用户修改成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const del = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/student/deleteById/' +id).then(res => {
            if (res.code === '200') {
                data.formVisible = false
                ElMessage.success('用户删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handleSelectionChange = (rows) => {
    data.ids = rows.map(row => row.id)
    console.log(data.ids)
}

const delBatch = () => {
    if (data.ids.length === 0) {
        ElMessage.warning('请选择要删除的数据')
        return
    }
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/student/deleteBatch', { data: data.ids}).then(res => {
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
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
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