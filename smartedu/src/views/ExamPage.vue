<template>
  <div class="exam-management">
    <!-- 顶部标题栏 -->
    <div class="header-section">
      <div class="header-content">
        <div class="title-group">
          <h1 class="page-title">考试管理系统</h1>
          <p class="page-subtitle">管理和监控所有考试信息</p>
        </div>
        <el-button type="primary" size="large" icon="Plus" @click="handleAdd">
          新增考试
        </el-button>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card total">
          <div class="stat-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalExams }}</div>
            <div class="stat-label">考试总数</div>
          </div>
        </div>

        <div class="stat-card active">
          <div class="stat-icon">
            <i class="el-icon-clock"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.activeExams }}</div>
            <div class="stat-label">进行中考试</div>
          </div>
        </div>

        <div class="stat-card upcoming">
          <div class="stat-icon">
            <i class="el-icon-calendar"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.upcomingExams }}</div>
            <div class="stat-label">即将开始</div>
          </div>
        </div>

        <div class="stat-card ended">
          <div class="stat-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.endedExams }}</div>
            <div class="stat-label">已结束考试</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <div class="search-header">
          <h3>筛选条件</h3>
          <el-button link @click="resetSearch">重置所有</el-button>
        </div>
        <div class="search-form">
          <div class="search-row">
            <div class="search-field">
              <label>考试名称</label>
              <el-input
                  v-model="searchParams.title"
                  placeholder="输入考试名称搜索"
                  clearable
                  prefix-icon="Search"
              />
            </div>
            <div class="search-field">
              <label>课程ID</label>
              <el-input
                  v-model="searchParams.courseId"
                  placeholder="输入课程ID"
                  clearable
              />
            </div>
            <div class="search-field">
              <label>状态筛选</label>
              <el-select v-model="searchParams.status" placeholder="全部状态" clearable>
                <el-option label="进行中" value="active"></el-option>
                <el-option label="未开始" value="upcoming"></el-option>
                <el-option label="已结束" value="ended"></el-option>
              </el-select>
            </div>
            <div class="search-field">
              <label>标签</label>
              <el-input
                  v-model="searchParams.tag"
                  placeholder="输入标签筛选"
                  clearable
              />
            </div>
          </div>
          <div class="search-actions">
            <el-button type="primary" icon="Search" @click="loadExams">查询</el-button>
            <el-button icon="Refresh" @click="resetSearch">重置</el-button>
          </div>
        </div>
      </div>

      <!-- 数据表格区域 -->
      <div class="table-section">
        <div class="table-header">
          <div class="table-title">
            <h3>考试列表</h3>
            <span class="table-count">共 {{ pageParams.total }} 条记录</span>
          </div>
          <div class="table-actions">
            <el-button
                type="danger"
                icon="Delete"
                @click="delBatch"
                :disabled="selectedIds.length === 0"
            >
              批量删除 {{ selectedIds.length > 0 ? `(${selectedIds.length})` : '' }}
            </el-button>
          </div>
        </div>

        <div class="table-container">
          <el-table
              :data="tableData"
              stripe
              @selection-change="handleSelectionChange"
              empty-text="暂无考试数据"
              v-loading="loading"
          >
            <el-table-column type="selection" width="55" fixed="left"></el-table-column>

            <el-table-column label="考试信息" min-width="250" fixed="left">
              <template #default="{ row }">
                <div class="exam-info">
                  <div class="exam-title">{{ row.title }}</div>
                  <div class="exam-meta">
                    <span class="course-id">课程: {{ row.courseId }}</span>
                    <el-tag v-if="row.tag" size="small" class="exam-tag">{{ row.tag }}</el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="总分" prop="totalScore" width="100" align="center">
              <template #default="{ row }">
                <div class="score-display">{{ row.totalScore }}分</div>
              </template>
            </el-table-column>

            <el-table-column label="考试时间" min-width="200">
              <template #default="{ row }">
                <div class="time-info">
                  <div class="time-item">
                    <i class="el-icon-time"></i>
                    <span>{{ formatDate(row.startTime) }}</span>
                  </div>
                  <div class="time-item">
                    <i class="el-icon-time"></i>
                    <span>{{ formatDate(row.endTime) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag
                    :type="getStatusType(row)"
                    :class="getStatusClass(row)"
                    effect="plain"
                >
                  {{ getStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button
                      type="primary"
                      size="small"
                      icon="Edit"
                      @click="handleUpdate(row)"
                      link
                      class="edit-button"
                  >
                    编辑
                  </el-button>
                  <el-button
                      type="danger"
                      size="small"
                      icon="Delete"
                      @click="del(row.id)"
                      link
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="pageParams.pageNum"
              v-model:page-size="pageParams.pageSize"
              :page-sizes="[5, 10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pageParams.total"
              @size-change="loadExams"
              @current-change="loadExams"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formTitle"
        width="600px"
        destroy-on-close
        :close-on-click-modal="false"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          label-position="left"
      >
        <el-form-item label="考试名称" prop="title">
          <el-input
              v-model="form.title"
              placeholder="请输入考试名称"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="课程ID" prop="courseId">
          <el-input
              v-model="form.courseId"
              placeholder="请输入课程ID"
              maxlength="50"
          />
        </el-form-item>

        <el-form-item label="出卷方式" prop="generationType">
          <el-radio-group v-model="form.generationType">
            <el-radio label="random">随机组卷</el-radio>
            <el-radio label="knowledge">按知识点组卷</el-radio>
            <el-radio label="difficulty">按难度平衡组卷</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 随机组卷参数 -->
        <div v-if="form.generationType === 'random'">
          <el-form-item label="选择题数量" prop="selectCount">
            <el-input-number
                v-model="form.selectCount"
                :min="0"
                :max="50"
                controls-position="right"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="简答题数量" prop="shortAnswerCount">
            <el-input-number
                v-model="form.shortAnswerCount"
                :min="0"
                :max="20"
                controls-position="right"
                style="width: 100%"
            />
          </el-form-item>
        </div>

        <!-- 知识点组卷参数 -->
        <div v-if="form.generationType === 'knowledge'">
          <el-form-item label="知识点" prop="knowledgePoints">
            <el-select
                v-model="form.knowledgePoints"
                multiple
                placeholder="请选择知识点"
                style="width: 100%"
            >
              <el-option
                  v-for="point in knowledgeOptions"
                  :key="point"
                  :label="point"
                  :value="point"
              />
            </el-select>
          </el-form-item>
        </div>

        <!-- 难度组卷参数 -->
        <div v-if="form.generationType === 'difficulty'">
          <el-form-item label="难度分布" prop="difficultyDistribution">
            <div class="difficulty-slider">
              <div class="slider-item">
                <span>简单题比例</span>
                <el-slider v-model="form.easyRatio" :min="0" :max="100" show-input />
              </div>
              <div class="slider-item">
                <span>中等题比例</span>
                <el-slider v-model="form.mediumRatio" :min="0" :max="100" show-input />
              </div>
              <div class="slider-item">
                <span>难题比例</span>
                <el-slider v-model="form.hardRatio" :min="0" :max="100" show-input />
              </div>
            </div>
          </el-form-item>
        </div>

        <el-form-item label="考试时间" required>
          <div class="time-range-container">
            <el-form-item prop="startTime">
              <el-date-picker
                  v-model="form.startTime"
                  type="datetime"
                  placeholder="选择开始时间"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
              />
            </el-form-item>
            <span class="time-separator">至</span>
            <el-form-item prop="endTime">
              <el-date-picker
                  v-model="form.endTime"
                  type="datetime"
                  placeholder="选择结束时间"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
              />
            </el-form-item>
          </div>
        </el-form-item>

        <el-form-item label="标签" prop="tag">
          <el-input
              v-model="form.tag"
              placeholder="请输入标签（可选）"
              maxlength="20"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" @click="saveExam">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import dayjs from 'dayjs';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const API_BASE_URL = 'http://localhost:8080'; // 根据实际后端地址修改

const tableData = ref([]);
const formVisible = ref(false);
const formRef = ref(null);
const selectedIds = ref([]);
const loading = ref(false);
const knowledgeOptions = ref(['基础知识', '进阶应用', '综合能力', '实践操作']); // 示例知识点

const searchParams = reactive({
  title: '',
  courseId: '',
  status: '',
  tag: '',
});

const pageParams = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});

const stats = reactive({
  totalExams: 0,
  activeExams: 0,
  upcomingExams: 0,
  endedExams: 0,
});

const form = reactive({
  id: null,
  title: '',
  courseId: '',
  startTime: '',
  endTime: '',
  tag: '',
  generationType: 'random',
  // 随机组卷参数
  selectCount: 10,
  shortAnswerCount: 5,
  // 知识点组卷参数
  knowledgePoints: [],
  // 难度组卷参数
  easyRatio: 30,
  mediumRatio: 50,
  hardRatio: 20,
});

const rules = reactive({
  title: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }],
  generationType: [{ required: true, message: '请选择出卷方式', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  selectCount: [{ required: true, message: '请选择题型数量', trigger: 'blur' }],
  shortAnswerCount: [{ required: true, message: '请选择题型数量', trigger: 'blur' }],
});

const formTitle = ref('新增考试');

const formatDate = (dateStr) => dayjs(dateStr).format('MM-DD HH:mm');
const isActive = (row) => dayjs().isAfter(dayjs(row.startTime)) && dayjs().isBefore(dayjs(row.endTime));
const isUpcoming = (row) => dayjs().isBefore(dayjs(row.startTime));

const getStatusType = (row) => {
  if (isActive(row)) return 'success';
  if (isUpcoming(row)) return 'warning';
  return 'info';
};

const getStatusClass = (row) => {
  if (isActive(row)) return 'status-active';
  if (isUpcoming(row)) return 'status-upcoming';
  return 'status-ended';
};

const getStatusText = (row) => {
  if (isActive(row)) return '进行中';
  if (isUpcoming(row)) return '未开始';
  return '已结束';
};

// 加载考试数据
const loadExams = async () => {
  loading.value = true;
  try {
    const params = {
      ...searchParams,
      pageNum: pageParams.pageNum,
      pageSize: pageParams.pageSize
    };

    const response = await axios.post(`${API_BASE_URL}/exam/selectAll`, params);

    if (response.data && response.data.data) {
      tableData.value = response.data.data;
      pageParams.total = response.data.total || tableData.value.length;

      // 更新统计信息
      stats.totalExams = pageParams.total;
      stats.activeExams = tableData.value.filter(isActive).length;
      stats.upcomingExams = tableData.value.filter(isUpcoming).length;
      stats.endedExams = tableData.value.filter(item => !isActive(item) && !isUpcoming(item)).length;
    }
  } catch (error) {
    console.error('加载考试数据失败:', error);
    ElMessage.error('加载考试数据失败');
  } finally {
    loading.value = false;
  }
};

// 重置搜索条件
const resetSearch = () => {
  searchParams.title = '';
  searchParams.courseId = '';
  searchParams.status = '';
  searchParams.tag = '';
  loadExams();
};

// 处理表格选择
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id);
};

// 新增考试
const handleAdd = () => {
  formTitle.value = '新增考试';
  Object.keys(form).forEach(key => {
    if (key !== 'id') {
          key === 'generationType' ? 'random' :
              key === 'selectCount' ? 10 :
                  key === 'shortAnswerCount' ? 5 :
                      key === 'knowledgePoints' ? [] :
                          key === 'easyRatio' ? 30 :
                              key === 'mediumRatio' ? 50 :
                                  key === 'hardRatio' ? 20 : '';
    }
  });
  form.id = null;
  formVisible.value = true;
};

// 编辑考试
const handleUpdate = (row) => {
  formTitle.value = '编辑考试';
  Object.assign(form, row);
  formVisible.value = true;
};

// 保存考试
const saveExam = async () => {
  formRef.value.validate(async valid => {
    if (!valid) return;
    try {
      // 1. 生成题目ID数组
      let questionIds = [];
      if (form.generationType === 'random') {
        questionIds = (await generateRandomQuestions()).map(q => q.id);
      } else if (form.generationType === 'knowledge') {
        questionIds = (await generateKnowledgeBasedQuestions()).map(q => q.id);
      } else {
        questionIds = (await generateDifficultyBasedQuestions()).map(q => q.id);
      }

      // 2. 组装基础数据
      const examData = { ...form, questionIds };

      // 3. 自动计算总分 —— 使用 /question/selectById/{id}
           try {
                // 并行请求每道题的详情
                   const promises = questionIds.map(id =>
                      axios.get(`${API_BASE_URL}/question/selectById/${id}`)
                   );
                const responses = await Promise.all(promises);
                // 后端返回格式：{ data: { data: Question } }
                    const totalScore = responses
                     .map(res => res.data.data.score || 0)
                  .reduce((sum, s) => sum + s, 0);
                examData.totalScore = totalScore;
              } catch (err) {
                console.warn('自动计算总分异常，使用默认 0 分：', err);
                examData.totalScore = 0;
              }

      // 4. 调用新增或更新接口
      if (form.id) {
        await axios.put(`${API_BASE_URL}/exam/update`, examData);
        ElMessage.success('考试更新成功');
      } else {
        await axios.post(`${API_BASE_URL}/exam/add`, examData);
        ElMessage.success('考试添加成功');
      }

      formVisible.value = false;
      loadExams();
    } catch (error) {
      console.error('保存考试失败:', error);
      ElMessage.error(`保存考试失败: ${error.response?.data?.message || error.message}`);
    }
  });
};



// 提交考试结果
const submitExam = async (examId, result) => {
  try {
    // 模拟将结果提交到后端
    await axios.post(`${API_BASE_URL}/examgrade/add`, result);

    // 将结果存储到本地存储
    localStorage.setItem(`exam_${examId}_result`, JSON.stringify(result));

    ElMessage.success('考试提交成功');
    router.push({ name: 'ExamResult', query: { examId } });
  } catch (error) {
    console.error('提交考试失败:', error);
    ElMessage.error('提交考试失败，请稍后重试');
  }
};

// 生成随机题目
// 生成随机题目：直接调用后端 /question/random
const generateRandomQuestions = async () => {
  try {
    const resp = await axios.get(`${API_BASE_URL}/question/random`, {
      params: {
        courseId: form.courseId,
        kgPoint: '', // 可根据需要传递知识点
        selectCount: form.selectCount, // 选择题数量
        shortAnswerCount: form.shortAnswerCount // 简答题数量
      }
    });
    const list = resp.data.data || [];
    if (!list.length) {
      ElMessage.warning(`课程 ${form.courseId} 无可用题目或数量不足`);
    } else {
      console.log('后端随机抽取的题目ID:', list.map(q => q.id));
    }
    return list; // 返回题目列表
  } catch (err) {
    console.error('随机组卷失败:', err);
    ElMessage.error('随机组卷失败: ' + (err.response?.data?.message || err.message));
    return [];
  }
};


// 辅助函数：从数组中随机选择指定数量的元素
const getRandomItems = (array, count) => {
  if (!array || array.length === 0 || count <= 0) return [];

  // 创建一个数组的副本
  const shuffled = [...array];

  // Fisher-Yates 洗牌算法
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }

  // 返回前count个元素
  return shuffled.slice(0, count);
};

// 生成知识点题目
const generateKnowledgeBasedQuestions = async () => {
  try {
    const response = await axios.post(`${API_BASE_URL}/question/byKnowledge`, {
      courseId: form.courseId,
      knowledgePoints: form.knowledgePoints
    });
    return response.data?.data || [];
  } catch (error) {
    console.error('知识点组卷失败:', error);
    ElMessage.error('知识点组卷失败: ' + (error.response?.data?.message || error.message));
    return [];
  }
};

// 生成难度题目
const generateDifficultyBasedQuestions = async () => {
  try {
    const response = await axios.post(`${API_BASE_URL}/question/byDifficulty`, {
      courseId: form.courseId,
      easyRatio: form.easyRatio,
      mediumRatio: form.mediumRatio,
      hardRatio: form.hardRatio
    });
    return response.data?.data || [];
  } catch (error) {
    console.error('难度组卷失败:', error);
    ElMessage.error('难度组卷失败: ' + (error.response?.data?.message || error.message));
    return [];
  }
};

// 删除考试
const del = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该考试吗？删除后将无法恢复！', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await axios.delete(`${API_BASE_URL}/exam/delete/${id}`);
    ElMessage.success('考试删除成功');
    loadExams();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除考试失败:', error);
      ElMessage.error('删除考试失败: ' + (error.response?.data?.message || error.message));
    }
  }
};

// 批量删除
const delBatch = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的考试');
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个考试吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await Promise.all(selectedIds.value.map(id =>
        axios.delete(`${API_BASE_URL}/exam/delete/${id}`)
    ));

    ElMessage.success(`成功删除 ${selectedIds.value.length} 个考试`);
    selectedIds.value = [];
    loadExams();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败: ' + (error.response?.data?.message || error.message));
    }
  }
};

onMounted(() => loadExams());
</script>

<style scoped>
/* 顶部标题栏样式 */
.header-section {
  background-color: #eef1f6;
  padding: 25px;
  border-bottom: 2px solid #dcdfe6;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-group {
  color: #2c3e50;
}

.page-title {
  font-size: 26px;
  font-weight: bold;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #95a5a6;
  margin: 5px 0 0;
}

/* 统计卡片样式 */
.stats-section {
  margin: 25px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.stat-card {
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 10px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 40px;
  color: #409eff;
  margin-bottom: 15px;
}

.stat-content {
  color: #34495e;
}

.stat-number {
  font-size: 22px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

/* 搜索区域样式 */
.search-section {
  background-color: #ffffff;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 25px;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 25px;
}

.search-field {
  flex: 1;
  min-width: 220px;
}

.search-actions {
  display: flex;
  gap: 15px;
  margin-top: 15px;
}

/* 表格区域样式 */
.table-section {
  background-color: #ffffff;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.table-title h3 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
}

.table-count {
  font-size: 14px;
  color: #95a5a6;
}

.table-actions {
  display: flex;
  gap: 15px;
}

.table-container {
  margin-top: 15px;
}

/* 分页样式 */
.pagination-container {
  margin-top: 25px;
  text-align: right;
}

/* 弹窗样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

/* 其他样式 */
.difficulty-slider {
  width: 100%;
}

.slider-item {
  margin-bottom: 25px;
}

.slider-item span {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  color: #7f8c8d;
}
</style>