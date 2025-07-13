<template>
  <div class="student-exam-container">
    <!-- 头部 -->
    <div class="header-section">
      <div class="header-content">
        <div class="title-group">
          <h1>我的考试</h1>
          <p>查看所有需要参加的考试</p>
        </div>
      </div>
    </div>

    <!-- 考试列表 -->
    <div class="exam-list-section">
      <div class="list-header">
        <h2>考试列表</h2>
        <div class="filter-section">
          <el-select v-model="filterStatus" placeholder="考试状态" clearable>
            <el-option label="全部" value="all"></el-option>
            <el-option label="未开始" value="upcoming"></el-option>
            <el-option label="进行中" value="active"></el-option>
            <el-option label="已结束" value="ended"></el-option>
          </el-select>
          <el-input
              v-model="searchText"
              placeholder="搜索考试名称"
              clearable
              prefix-icon="Search"
              style="width: 250px;"
          />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-overlay">
        <el-icon class="is-loading" size="40"><Loading /></el-icon>
      </div>

      <table class="exam-table" v-if="filteredExams.length > 0">
        <thead>
        <tr>
          <th style="width: 35%;">考试信息</th>
          <th style="width: 25%;">考试时间</th>
          <th style="width: 15%;">状态</th>
          <th style="width: 15%;">总分</th>
          <th style="width: 10%;">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="exam in filteredExams"
            :key="exam.id"
            :class="{ 'active-exam': exam.status === 'active' || exam.status === 'upcoming' }"
        >
          <td>
            <div class="exam-info">
              <div class="exam-title">{{ exam.title }}</div>
              <div class="exam-course">
                <span>课程ID: {{ exam.courseId }}</span>
                <span class="exam-tag">{{ exam.tag || '常规考试' }}</span>
              </div>
            </div>
          </td>
          <td>
            <div class="exam-time">
              <div class="time-item">
                <el-icon><Clock /></el-icon>
                <span>开始: {{ formatDate(exam.startTime) }}</span>
              </div>
              <div class="time-item">
                <el-icon><Clock /></el-icon>
                <span>结束: {{ formatDate(exam.endTime) }}</span>
              </div>
            </div>
          </td>
          <td>
            <div :class="['status-tag', `status-${exam.status}`]">
              {{ getStatusText(exam) }}
            </div>
          </td>
          <td>
            <div class="score-display">{{ exam.totalScore }}分</div>
          </td>
          <td>
            <button
                :class="['action-button',
                        exam.status === 'active' ? 'enter-exam' : 'disabled-button']"
                @click="enterExam(exam)"
                :disabled="exam.status !== 'active'"
            >
              <el-icon><Promotion /></el-icon>
              {{ exam.status === 'active' ? '进入考试' : '不可进入' }}
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <div class="empty-state" v-else-if="!loading">
        <div class="empty-icon">
          <el-icon><Notebook /></el-icon>
        </div>
        <div class="empty-text">没有找到符合条件的考试</div>
        <p>请尝试更改筛选条件或稍后再来查看</p>
      </div>
    </div>

    <div class="footer-note">
      <p>注意：只有在进行中的考试才能进入，请按时参加考试</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Clock, Promotion, Loading, Notebook } from '@element-plus/icons-vue'
import axios from 'axios'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'

export default {
  name: 'StudentExam',
  components: {
    UserFilled,
    Clock,
    Promotion,
    Loading,
    Notebook
  },
  setup() {
    const API_BASE_URL = 'http://localhost:8080'
    const router = useRouter()

    const filterStatus = ref('all')
    const searchText = ref('')
    const exams = ref([])
    const loading = ref(true)

    // 从后端加载考试数据
    const loadExams = async () => {
      try {
        loading.value = true

        // 尝试不同的请求方式
        let response
        try {
          // 首先尝试POST请求
          response = await axios.post(`${API_BASE_URL}/exam/selectAll`, {}, {
            headers: {
              'Content-Type': 'application/json'
            }
          })
        } catch (postError) {
          console.log('POST请求失败，尝试GET请求:', postError.message)
          // 如果POST失败，尝试GET请求
          response = await axios.get(`${API_BASE_URL}/exam/selectAll`)
        }

        console.log('API响应:', response.data)

        if (response.data) {
          let examData = []

          // 处理不同的响应格式
          if (response.data.data) {
            examData = response.data.data
          } else if (Array.isArray(response.data)) {
            examData = response.data
          } else if (response.data.code === 200 && response.data.data) {
            examData = response.data.data
          }

          // 添加状态字段
          exams.value = examData.map(exam => {
            const now = dayjs()
            const startTime = dayjs(exam.startTime)
            const endTime = dayjs(exam.endTime)

            let status = 'ended'
            if (now.isBefore(startTime)) {
              status = 'upcoming'
            } else if (now.isAfter(startTime) && now.isBefore(endTime)) {
              status = 'active'
            }

            return {
              ...exam,
              status: status
            }
          })

          console.log('处理后的考试数据:', exams.value)
        } else {
          exams.value = []
        }
      } catch (error) {
        console.error('加载考试数据失败:', error)
        console.error('错误详情:', error.response?.data)

        // 提供更详细的错误信息
        let errorMessage = '加载考试数据失败'
        if (error.response?.status === 400) {
          errorMessage += ': 请求参数错误'
        } else if (error.response?.status === 404) {
          errorMessage += ': 接口不存在'
        } else if (error.response?.status === 500) {
          errorMessage += ': 服务器内部错误'
        } else if (error.code === 'ECONNREFUSED') {
          errorMessage += ': 无法连接到服务器'
        } else {
          errorMessage += ': ' + error.message
        }

        ElMessage.error(errorMessage)
        exams.value = []

        // 加载模拟数据用于测试
        loadMockData()
      } finally {
        loading.value = false
      }
    }

    // 加载模拟数据
    const loadMockData = () => {
      console.log('加载模拟数据...')
      const mockExams = [
        {
          id: 1,
          title: '数据结构与算法期末考试',
          courseId: 'CS101',
          startTime: '2024-12-20 09:00:00',
          endTime: '2024-12-20 11:00:00',
          totalScore: 100,
          tag: '期末考试'
        },
        {
          id: 2,
          title: '计算机网络期中考试',
          courseId: 'CS201',
          startTime: '2024-12-25 14:00:00',
          endTime: '2024-12-25 16:00:00',
          totalScore: 100,
          tag: '期中考试'
        },
        {
          id: 3,
          title: '操作系统原理测验',
          courseId: 'CS301',
          startTime: '2024-12-15 10:00:00',
          endTime: '2024-12-15 11:30:00',
          totalScore: 80,
          tag: '随堂测验'
        }
      ]

      // 添加状态字段
      exams.value = mockExams.map(exam => {
        const now = dayjs()
        const startTime = dayjs(exam.startTime)
        const endTime = dayjs(exam.endTime)

        let status = 'ended'
        if (now.isBefore(startTime)) {
          status = 'upcoming'
        } else if (now.isAfter(startTime) && now.isBefore(endTime)) {
          status = 'active'
        }

        return {
          ...exam,
          status: status
        }
      })

      ElMessage.info('已加载模拟数据供测试使用')
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      return dayjs(dateStr).format('MM-DD HH:mm')
    }

    // 获取状态文本
    const getStatusText = (exam) => {
      if (exam.status === 'active') return '进行中'
      if (exam.status === 'upcoming') return '未开始'
      return '已结束'
    }

    // 进入考试
    const enterExam = (exam) => {
      if (exam.status === 'active') {
        ElMessage.success(`正在进入考试: ${exam.title}`)
        // 使用Vue Router跳转到考试答题页面
        router.push({
          name: 'ExamAnswer', // 确保路由配置中有名为'ExamAnswer'的路由
          query: { examId: exam.id }
        })
      } else {
        ElMessage.warning('考试未开始或已结束，无法进入')
      }
    }

    // 过滤考试列表
    const filteredExams = computed(() => {
      let result = [...exams.value]

      // 按状态过滤
      if (filterStatus.value && filterStatus.value !== 'all') {
        result = result.filter(exam => exam.status === filterStatus.value)
      }

      // 按搜索文本过滤
      if (searchText.value) {
        const searchLower = searchText.value.toLowerCase()
        result = result.filter(exam =>
            exam.title.toLowerCase().includes(searchLower) ||
            (exam.tag && exam.tag.toLowerCase().includes(searchLower))
        )
      }

      // 按时间排序 - 未开始和进行中的在前
      result.sort((a, b) => {
        if (a.status === 'active' && b.status !== 'active') return -1
        if (a.status !== 'active' && b.status === 'active') return 1
        if (a.status === 'upcoming' && b.status !== 'upcoming') return -1
        if (a.status !== 'upcoming' && b.status === 'upcoming') return 1
        return new Date(b.startTime) - new Date(a.startTime)
      })

      return result
    })

    // 组件挂载时加载考试数据
    onMounted(() => {
      loadExams()
    })

    return {
      filterStatus,
      searchText,
      exams,
      filteredExams,
      loading,
      formatDate,
      getStatusText,
      enterExam
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

body {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
  min-height: 100vh;
  padding: 20px;
}

.student-exam-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 头部样式 */
.header-section {
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  color: white;
  border-radius: 12px;
  padding: 25px 30px;
  margin-bottom: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-group h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
}

.title-group p {
  font-size: 16px;
  opacity: 0.9;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.user-details {
  text-align: right;
}

.user-name {
  font-size: 18px;
  font-weight: 500;
}

.user-id {
  font-size: 14px;
  opacity: 0.8;
}

/* 考试列表样式 */
.exam-list-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  position: relative;
}

.list-header {
  padding: 20px 25px;
  border-bottom: 1px solid #eaeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-header h2 {
  font-size: 22px;
  color: #1a73e8;
  font-weight: 600;
}

.filter-section {
  display: flex;
  gap: 15px;
  align-items: center;
}

.exam-table {
  width: 100%;
  border-collapse: collapse;
}

.exam-table th {
  background-color: #f8fafc;
  color: #1a73e8;
  font-weight: 600;
  text-align: left;
  padding: 15px 20px;
  border-bottom: 1px solid #eaeef5;
}

.exam-table td {
  padding: 20px;
  border-bottom: 1px solid #eaeef5;
  transition: background-color 0.3s;
}

.exam-table tr:last-child td {
  border-bottom: none;
}

.exam-table tr:hover td {
  background-color: #f8fafc;
}

.exam-table tr.active-exam:hover {
  cursor: pointer;
  background-color: #e3f2fd;
}

.exam-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exam-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.exam-course {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.exam-tag {
  background: #e8f5e9;
  color: #43a047;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.exam-time {
  display: flex;
  flex-direction: column;
  gap: 5px;
  color: #666;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-tag {
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
}

.status-upcoming {
  background: #fff8e1;
  color: #ff8f00;
}

.status-active {
  background: #e8f5e9;
  color: #43a047;
}

.status-ended {
  background: #f5f5f5;
  color: #9e9e9e;
}

.action-button {
  padding: 8px 18px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.enter-exam {
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  color: white;
}

.enter-exam:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}

.disabled-button {
  background: #f5f5f5;
  color: #9e9e9e;
  cursor: not-allowed;
}

.footer-note {
  text-align: center;
  margin-top: 30px;
  color: #666;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #9e9e9e;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  color: #e0e0e0;
}

.empty-text {
  font-size: 18px;
  margin-bottom: 20px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .user-info {
    flex-direction: column;
    text-align: center;
  }

  .filter-section {
    flex-direction: column;
    width: 100%;
  }

  .exam-table {
    display: block;
    overflow-x: auto;
  }

  .exam-table th,
  .exam-table td {
    min-width: 150px;
  }
}
</style>