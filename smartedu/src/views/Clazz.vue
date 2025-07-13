<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
            <el-input style="width: 240px; margin-left: 10px; margin-right: 10px" v-model="data.name" placeholder="请输入班级名查询" prefix-icon="Search"></el-input>
            <el-button type="primary" @click="load">查询</el-button>
            <el-button type="danger" @click="reset">重置</el-button>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column label="班级名称" prop="name" />
                <el-table-column label="班级号" prop="id" show-overflow-tooltip />
                <el-table-column label="负责教师">
                    <template #default="scope">
                        {{ getTeacherName(scope.row.teacherId) }}
                    </template>
                </el-table-column>
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

        <el-dialog :title="data.form.id ? '编辑班级' : '新增班级'" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
                <el-form-item label="班级名称" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入班级名称"/>
                </el-form-item>
                <el-form-item label="班级号" prop="id">
                    <el-input v-model="data.form.id" autocomplete="off" placeholder="请输入班级号"/>
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
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { Edit, Delete, Search } from '@element-plus/icons-vue';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';

const userInfo = JSON.parse(localStorage.getItem('db-user') || '{}')
const currentTeacherId = userInfo.id;

const data = reactive({
    role: userInfo.role,
    username: null,
    name: null,
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
        id: [
            { required: true, message: '请输入班级号', trigger: 'blur' }
        ],
        teacherId: [
            { required: true, message: '请选择负责教师', trigger: 'change' }
        ]
    }
})

const formRef = ref()

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
        const res = await request.get('/class/selectPage', {
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

const handleAdd = () => {
    data.form = { id: null, name: null, teacherId: null }; // 初始化表单时添加 id 字段
    data.formVisible = true;
}

const handleUpdate = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    data.formVisible = true;
    data.originalId = row.id; // 保存原始的班级号
}

const save = async () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (data.originalId && data.originalId !== data.form.id) {
                    // 如果班级号被修改，提示用户无法修改班级号
                    ElMessage.error('班级号不可修改');
                    return;
                }
                const res = await request.get(`/class/selectById/${data.form.id}`);
                if (res.code === '200' && res.data) {
                    update(); // 调用更新方法
                } else {
                    add(); // 调用新增方法
                }
            } catch (error) {
                console.error('检查班级是否存在失败:', error);
                ElMessage.error('保存失败，请稍后重试');
            }
        }
    });
}

const add = () => {
    data.form.teacherId = currentTeacherId;
    request.post('/class/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false;
            ElMessage.success('班级添加成功');
            load();
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        console.error('添加班级失败:', error);
        ElMessage.error('添加班级失败');
    });
}

const update = () => {
    request.put('/class/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('班级更新成功')
            load()
        } else {
            ElMessage.error(res.msg)
        }
    }).catch(error => {
        console.error('更新班级失败:', error);
        ElMessage.error('更新班级失败')
    })
}

const del = (id) => {
    ElMessageBox.confirm('删除数据后将无法恢复，是否确认删除？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/class/deleteById/' +id).then(res => {
            if (res.code === '200') {
                ElMessage.success('班级删除成功')
                load()
            } else {
                ElMessage.error(res.msg)
            }
        }).catch(error => {
            console.error('删除班级失败:', error);
            ElMessage.error('删除班级失败')
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
        request.delete('/class/deleteBatch', { data: data.ids}).then(res => {
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