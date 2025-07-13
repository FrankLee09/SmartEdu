<template>
  <div class="exam-container" v-if="!loading && !error">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <h1 class="exam-title">{{ exam.title }}</h1>
      <div class="exam-meta">
        <div class="exam-info">
          <div class="exam-info-item">
            <el-icon><Notebook /></el-icon>
            <span>课程ID: {{ exam.courseId }}</span>
          </div>
          <div class="exam-info-item">
            <el-icon><Clock /></el-icon>
            <span>开始时间: {{ formatDateTime(exam.startTime) }}</span>
          </div>
          <div class="exam-info-item">
            <el-icon><Clock /></el-icon>
            <span>结束时间: {{ formatDateTime(exam.endTime) }}</span>
          </div>
          <div class="exam-info-item">
            <el-icon><Medal /></el-icon>
            <span>总分: {{ exam.totalScore }}分</span>
          </div>
        </div>
        <div class="countdown" :class="countdownClass">
          {{ countdownText }}
        </div>
      </div>
    </div>

    <!-- 考试内容区域 -->
    <div class="exam-content">
      <!-- 左侧题目导航 -->
      <div class="question-nav">
        <div class="nav-header">
          <div class="nav-title">题目导航</div>
          <div>已完成: {{ answeredCount }}/{{ questions.length }}</div>
        </div>
        <div class="question-buttons">
          <div
              v-for="(q, index) in questions"
              :key="q.id"
              class="q-btn"
              :class="{ current: currentQuestionIndex === index, answered: isAnswered(q), marked: q.marked }"
              @click="goToQuestion(index)">
            {{ index + 1 }}
            <el-icon v-if="q.marked"><Star /></el-icon>
          </div>
        </div>
      </div>

      <!-- 右侧题目内容 -->
      <div class="question-content">
        <!-- 题头 -->
        <div class="question-header">
          <div class="question-meta">
            <div class="question-type">
              {{ currentQuestion.type === 'select' ? '选择题' : '简答题' }}
            </div>
            <div class="question-score">{{ currentQuestion.score }}分</div>
          </div>
        </div>

        <!-- 题目主体 -->
        <div class="question-body">
          <div class="question-text" v-html="currentQuestion.description"></div>
          <div class="options-container" v-if="currentQuestion.type === 'select'">
            <div
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                class="option-item"
                :class="{ selected: currentQuestion.studentAnswer === option.key }"
                @click="selectOption(option.key)">
              <div class="option-letter">{{ option.key }}</div>
              <div class="option-text">{{ option.text }}</div>
            </div>
          </div>
          <div class="answer-area" v-else>
            <label class="answer-label">请在此输入您的答案：</label>
            <textarea
                class="answer-textarea"
                v-model="currentQuestion.studentAnswer"
                placeholder="请输入您的答案..."
                @input="saveAnswer"></textarea>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="action-buttons">
          <div class="nav-buttons">
            <el-button @click="prevQuestion" :disabled="currentQuestionIndex === 0">
              <el-icon><ArrowLeft /></el-icon> 上一题
            </el-button>
            <el-button @click="nextQuestion" :disabled="currentQuestionIndex === questions.length - 1">
              下一题 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="submit-section">
            <el-button class="mark-button" :class="{ marked: currentQuestion.marked }" @click="toggleMark">
              <el-icon><Star /></el-icon>
              {{ currentQuestion.marked ? '已标记' : '标记本题' }}
            </el-button>
            <el-button class="save-button" @click="saveAnswer">
              <el-icon><Check /></el-icon> 保存答案
            </el-button>
            <el-button class="submit-button" type="primary" @click="submitExam" :disabled="answeredCount !== questions.length">
              <el-icon><Promotion /></el-icon> 提交试卷
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-note">
      注意：考试结束前请确保已提交所有答案。倒计时结束后系统将自动提交试卷。
    </div>
  </div>

  <!-- 加载遮罩 -->
  <div class="loading-overlay" v-if="loading">
    <el-icon class="is-loading" size="40"><Loading /></el-icon>
    <span style="margin-left: 10px;">加载考试数据中...</span>
  </div>

  <!-- 提交等待遮罩 -->
  <div class="submitting-overlay" v-if="isSubmitting">
    <el-icon class="is-loading" size="40"><Loading /></el-icon>
    <span style="margin-left: 10px;">正在批改，请稍候...</span>
  </div>

  <!-- 错误遮罩 -->
  <div class="error-message" v-if="error">
    <h2>加载考试数据失败</h2>
    <p>{{ errorMessage }}</p>
    <el-button type="primary" @click="retryLoading">重新加载</el-button>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import {
  Notebook, Clock, Medal, Star, ArrowLeft, ArrowRight,
  Check, Promotion, Loading, Warning
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'




// 路由相关
const router = useRouter()
const route = useRoute()
const examId = ref(route.query.examId || null)

const user = JSON.parse(localStorage.getItem('db-user') || '{}')
const studentId = user.id

// 考试数据
const exam = ref({})
const questions = ref([])
const userAnswers = ref([])
const markedQuestions = reactive({})
const answeredQuestions = reactive({})

// 状态管理
const loading = ref(true)
const error = ref(false)
const errorMessage = ref('')
const isSubmitting = ref(false)
const submitDialogVisible = ref(false)
const currentIndex = ref(0)
const timeRemaining = ref(0)
const timerId = ref(null)

// 获取考试数据
// 获取考试数据

// 获取考试数据
const fetchExamData = async () => {
  loading.value = true;
  error.value = false;

  try {
    // 获取考试基本信息
    const examResponse = await request.get(`/exam/selectById/${examId.value}`);
    if (examResponse.code === "200") {
      exam.value = examResponse.data;

      // 获取题目列表
      const questionsResponse = await request.get(`/exam/selectQuestionsById/${examId.value}`);
      if (questionsResponse.code === "200") {
        // 处理题目数据，转换为前端需要的格式
        const rawQuestions = questionsResponse.data || [];
        questions.value = rawQuestions.map(q => {
          // 判断题目类型（是否为选择题）
          const isChoice = q.selectA || q.selectB || q.selectC || q.selectD;

          // 处理选项
          let options = [];
          if (isChoice) {
            if (q.selectA) options.push({key: 'A', text: q.selectA});
            if (q.selectB) options.push({key: 'B', text: q.selectB});
            if (q.selectC) options.push({key: 'C', text: q.selectC});
            if (q.selectD) options.push({key: 'D', text: q.selectD});
          }

          return {
            ...q,
            // 如果description为空，使用title作为题目内容
            description: q.description || q.title,
            type: isChoice ? 'select' : 'essay',
            options: options,
            studentAnswer: '',
            marked: false
          };
        });

        console.log("处理后的题目数据:", questions.value);

        // 初始化用户答案数组
        userAnswers.value = Array(questions.value.length).fill(null).map((_, i) => ({
          questionId: questions.value[i].id,
          answer: '',
          index: i
        }));

        // 计算考试剩余时间
        const endTime = dayjs(exam.value.endTime);
        const now = dayjs();
        timeRemaining.value = Math.max(0, endTime.diff(now, 'second'));

        // 如果有剩余时间，启动计时器
        if (timeRemaining.value > 0) {
          startTimer();
        }

        // 加载本地保存的答案
        loadSavedAnswers();
      } else {
        throw new Error('获取题目失败');
      }
    } else {
      throw new Error('获取考试信息失败');
    }
  } catch (error) {
    console.error('获取考试数据失败', error);
    error.value = true;
    errorMessage.value = error.message || '网络错误，请重试';
  } finally {
    loading.value = false;
  }
};

// 计时器
const startTimer = () => {
  if (timerId.value) clearInterval(timerId.value)

  timerId.value = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      clearInterval(timerId.value)
      ElMessageBox.alert('考试时间已结束，系统将自动提交您的答案', '时间到', {
        confirmButtonText: '确定',
        callback: () => {
          submitExam()
        }
      })
    }
  }, 1000)
}

// 获取剩余时间格式化
const formattedTimeRemaining = computed(() => {
  const hours = Math.floor(timeRemaining.value / 3600)
  const minutes = Math.floor((timeRemaining.value % 3600) / 60)
  const seconds = timeRemaining.value % 60

  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 计算属性
const currentQuestion = computed(() => questions.value[currentIndex.value] || {})
const answeredCount = computed(() => Object.values(answeredQuestions).filter(v => v).length)
const countdownClass = computed(() => {
  if (timeRemaining.value > 1800) return 'normal' // 大于30分钟
  if (timeRemaining.value > 300) return 'warning' // 大于5分钟
  return 'danger' // 少于5分钟
})

// 题目选项
const questionOptions = computed(() => {
  if (!currentQuestion.value) return []

  return [
    currentQuestion.value.selectA,
    currentQuestion.value.selectB,
    currentQuestion.value.selectC,
    currentQuestion.value.selectD
  ].filter(option => option) // 过滤掉空选项
})

// 判断是否为选择题
const isChoiceQuestion = () => {
  return currentQuestion.value &&
      (currentQuestion.value.selectA ||
          currentQuestion.value.selectB ||
          currentQuestion.value.selectC ||
          currentQuestion.value.selectD)
}

// 获取当前题目类型
const getCurrentQuestionType = () => {
  if (!currentQuestion.value) return '未知题型'
  return isChoiceQuestion() ? '选择题' : '简答题'
}

// 选项相关
const getOptionLetter = (index) => {
  return String.fromCharCode(65 + index) // A, B, C, D...
}

const saveAnswers = () => {
  try {
    // 保存用户答案到 localStorage
    localStorage.setItem(`exam_${examId.value}_answers`, JSON.stringify(userAnswers.value));
    // 保存标记状态到 localStorage
    localStorage.setItem(`exam_${examId.value}_marked`, JSON.stringify(markedQuestions));
    console.log('答案已保存');
  } catch (error) {
    console.error('保存答案失败:', error);
  }
};

// 选择选项
const selectOption = (option) => {
  // 更新当前题目的学生答案
  currentQuestion.value.studentAnswer = option;

  // 更新用户答案数组
  if (!userAnswers.value[currentIndex.value]) {
    userAnswers.value[currentIndex.value] = {
      questionId: currentQuestion.value.id,
      answer: option,
      index: currentIndex.value
    }
  } else {
    userAnswers.value[currentIndex.value].answer = option;
  }

  // 标记题目已回答
  answeredQuestions[currentIndex.value] = true;

  // 自动保存
  saveAnswers();
}

// 更新简答题答案
const updateAnswer = (text) => {
  if (!userAnswers.value[currentIndex.value]) {
    userAnswers.value[currentIndex.value] = {
      questionId: currentQuestion.value.id,
      answer: '',
      index: currentIndex.value
    }
  }

  userAnswers.value[currentIndex.value].answer = text
  answeredQuestions[currentIndex.value] = !!text.trim()
}

// 导航
const navigateToQuestion = (index) => {
  if (index >= 0 && index < questions.value.length) {
    currentIndex.value = index
  }
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
  }
}

const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// 标记题目
const toggleMark = () => {
  if (!currentQuestion.value) return;
  currentQuestion.value.marked = !currentQuestion.value.marked;
  markedQuestions[currentIndex.value] = currentQuestion.value.marked;
  saveAnswers(); // 保存标记状态
}
// 保存答案
const saveAnswer = () => {
  if (!userAnswers.value[currentIndex.value]) {
    userAnswers.value[currentIndex.value] = {
      questionId: currentQuestion.value.id,
      answer: '',
      index: currentIndex.value
    }
  }

  // 对于选择题，从选择项更新答案
  if (currentQuestion.value.type === 'select') {
    userAnswers.value[currentIndex.value].answer = currentQuestion.value.studentAnswer || '';
  }
  // 对于简答题，从文本框更新答案
  else if (currentQuestion.value.type === 'essay') {
    userAnswers.value[currentIndex.value].answer = currentQuestion.value.studentAnswer || '';
  }

  // 标记题目已回答（如果有内容）
  answeredQuestions[currentIndex.value] = !!userAnswers.value[currentIndex.value].answer.trim();

  // 保存到本地存储
  saveAnswers();
}

// 加载本地保存的答案
const loadSavedAnswers = () => {
  try {
    const savedAnswers = localStorage.getItem(`exam_${examId.value}_answers`)
    const savedMarked = localStorage.getItem(`exam_${examId.value}_marked`)

    if (savedAnswers) {
      const parsedAnswers = JSON.parse(savedAnswers)
      userAnswers.value = parsedAnswers

      // 更新已答题状态
      parsedAnswers.forEach(answer => {
        if (answer && answer.answer && answer.index !== undefined) {
          answeredQuestions[answer.index] = !!answer.answer.trim()
        }
      })
    }

    if (savedMarked) {
      const parsedMarked = JSON.parse(savedMarked)
      Object.keys(parsedMarked).forEach(key => {
        markedQuestions[key] = parsedMarked[key]
      })
    }
  } catch (error) {
    console.error('加载保存的答案失败:', error)
  }
  answeredQuestions[currentIndex.value] = !!text.trim();
}

// 确认提交
const confirmSubmit = () => {
  submitDialogVisible.value = true
}

// 提交试卷
// 提交试卷并在前端进行判分
// 提交试卷并在前端进行判分
// 提交试卷并在前端进行判分
// 提交试卷并保存成绩
// 提交试卷并保存结果到 localStorage，然后跳转到结果页
const submitExam = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    let totalScore = 0
    let totalPossibleScore = 0

    // 1. 保存选择题、累加分数，并写入 q.pointsEarned
    for (const q of questions.value) {
      totalPossibleScore += q.score || 0
      if (q.type === 'select') {
        const correct = q.studentAnswer === q.answer
        q.pointsEarned = correct ? q.score : 0
        totalScore += q.pointsEarned
        await request.post('/studentAnswer/add', {
          studentId,
          questionId: q.id,
          examId: exam.value.id,
          answer: q.studentAnswer,
          getscore: q.pointsEarned
        })
      } else {
        // 先保存空占位，AI 评分后再填
        await request.post('/studentAnswer/add', {
          studentId,
          questionId: q.id,
          examId: exam.value.id,
          answer: q.studentAnswer,
          getscore: 0
        })
      }
    }

    // 2. 对简答题调用 AI 接口打分，写入 q.pointsEarned 并累加
    for (const q of questions.value) {
      if (q.type === 'essay' && q.studentAnswer) {
        const res = await request.post('/dify/questionScore', {
          theQuestion: {
            id: q.id,
            description: q.description,
            answer: q.answer,
            score: q.score
          },
          exam: {
            id: exam.value.id,
            title: exam.value.title
          },
          studentId,
          solution: q.studentAnswer
        })
        const aiScore = res.code === '200' && !isNaN(+res.data)
            ? +parseFloat(res.data)
            : 0
        q.pointsEarned = aiScore    // 写回每题
        totalScore += aiScore
      }
    }

    // 3. 保存总成绩到后端
    const gradeRes = await request.post('/examgrade/add', {
      studentId,
      examId: exam.value.id,
      score: totalScore,
      gradedate: dayjs().format('YYYY-MM-DD')
    })
    if (gradeRes.code !== '200') {
      throw new Error('保存考试成绩失败')
    }

    // 4. 构造并保存 resultData
    const correctCount = questions.value.filter(q => q.type === 'select' && q.pointsEarned === q.score).length

    const resultDataToSave = {
      examTitle: exam.value.title,
      totalScore,
      totalPossibleScore,
      totalQuestions: questions.value.length,
      correctCount,
      scorePercentage: totalPossibleScore > 0 ? totalScore / totalPossibleScore * 100 : 0,
      submittedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      scoreDetails: questions.value.map((q, idx) => ({
        questionTitle: q.description,
        userAnswer: q.studentAnswer || '',
        correctAnswer: q.answer || '',
        isCorrect: q.type === 'select' ? (q.pointsEarned === q.score) : null,
        pointsEarned: q.pointsEarned ?? 0,
        score: q.score
      }))
    }

    localStorage.setItem(
        `exam_${exam.value.id}_result`,
        JSON.stringify(resultDataToSave)
    )

    ElMessage.success('试卷提交成功，正在跳转...')
    router.push({ path: '/exam-result', query: { examId: exam.value.id } })

  } catch (err) {
    console.error(err)
    ElMessage.error(err.message || '提交失败')
  } finally {
    isSubmitting.value = false
  }
}





// 重试加载
const retryLoading = () => {
  error.value = false;
  loading.value = true;
  fetchExamData();
};

// 键盘快捷键
const handleKeyDown = (e) => {
  if (e.key === 'ArrowLeft') {
    prevQuestion()
  } else if (e.key === 'ArrowRight') {
    nextQuestion()
  } else if (e.key === ' ') {
    e.preventDefault() // 防止页面滚动
    toggleMarkQuestion()
  }
}

// 格式化日期时间
function formatDateTime(dateStr) {
  if (!dateStr) return '';
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss');
}

// 判断题目是否已答
function isAnswered(question) {
  if (!question || !question.id) return false;
  const index = questions.value.findIndex(q => q.id === question.id);
  return index >= 0 && answeredQuestions[index] === true;
}

// 生命周期
onMounted(() => {
  const el = document.querySelector('#some-component');

  if (!el) {
    console.error('未找到 ID 为 #some-component 的元素');
    return;
  }

  if (!el.shadowRoot) {
    console.error('#some-component 没有 shadowRoot');
    return;
  }

  const inner = el.shadowRoot.querySelector('.inner');

  if (!inner) {
    console.error('在 shadowRoot 中未找到 .inner 元素');
    return;
  }

  // 如果你想对 inner 做操作，比如修改内容或样式
  // inner.textContent = '已找到';
  console.log('成功找到 .inner 元素', inner);
});


  fetchExamData()

  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeyDown)

  // 定时自动保存
  const autoSaveInterval = setInterval(() => {
    saveAnswers()
  }, 30000) // 每30秒自动保存一次

  const goToQuestion = (index) => {
    currentIndex.value = index;
  }

  // 清理函数
  onMounted(() => {
    if (!examId.value) {
      ElMessage.error('考试ID不存在')
      router.push('/exams')
      return
    }
    fetchExamData()
    window.addEventListener('keydown', handleKeyDown)
    const autoSaveInterval = setInterval(() => {
      saveAnswers()
    }, 30000)
    onUnmounted(() => {
      window.removeEventListener('keydown', handleKeyDown)
      clearInterval(timerId.value)
      clearInterval(autoSaveInterval)
    })

});
</script>

<style scoped>
.score-result {
  text-align: center;
}

.score-result h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #1a73e8;
}

.score-summary {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.score-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: #f8fafc;
  border-radius: 6px;
}

.score-item span:first-child {
  font-weight: 500;
  color: #555;
}

.score-item span:last-child {
  font-weight: 600;
  color: #1a73e8;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f5f7fa;
  color: #333;
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
}

.exam-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.exam-header {
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  color: white;
  padding: 25px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.exam-title {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 15px;
  letter-spacing: 0.5px;
}

.exam-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.exam-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.exam-info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 12px;
  border-radius: 20px;
}

.countdown {
  font-size: 24px;
  font-weight: 700;
  padding: 8px 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 130px;
}

.countdown.normal {
  background: rgba(76, 175, 80, 0.3);
}

.countdown.warning {
  background: rgba(255, 152, 0, 0.3);
  animation: pulse 2s infinite;
}

.countdown.danger {
  background: rgba(244, 67, 54, 0.3);
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

.exam-content {
  display: flex;
  min-height: 600px;
}

.question-nav {
  width: 260px;
  background: #f8fafc;
  border-right: 1px solid #eaeef5;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeef5;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a73e8;
}

.question-buttons {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  margin-bottom: 30px;
}

.q-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #f1f5f9;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid #e2e8f0;
}

.q-btn:hover {
  background: #e3f2fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.q-btn.current {
  background: #1a73e8;
  color: white;
  border-color: #1a73e8;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}

.q-btn.answered {
  background: #e8f5e9;
  color: #43a047;
  border-color: #c8e6c9;
}

.q-btn.marked {
  background: #fff8e1;
  color: #ff8f00;
  border-color: #ffecb3;
}

.q-btn.marked .el-icon {
  position: absolute;
  top: -5px;
  right: -5px;
  font-size: 12px;
  background: #ff8f00;
  color: white;
  border-radius: 50%;
  padding: 2px;
}

.question-type-section {
  margin-top: auto;
  background: white;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.type-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #1a73e8;
}

.type-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.type-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.type-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.question-content {
  flex: 1;
  padding: 25px 30px;
  display: flex;
  flex-direction: column;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeef5;
}

.question-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a73e8;
}

.question-meta {
  display: flex;
  gap: 15px;
}

.question-type, .question-score {
  background: #e3f2fd;
  color: #1a73e8;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.question-score {
  background: #e8f5e9;
  color: #43a047;
}

.question-body {
  flex: 1;
  padding: 10px 0;
}

.question-text {
  font-size: 17px;
  line-height: 1.7;
  margin-bottom: 30px;
  padding: 15px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 4px solid #1a73e8;
}

.options-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.option-item:hover {
  background: #e3f2fd;
  border-color: #bbdefb;
  transform: translateY(-2px);
}

.option-item.selected {
  background: #e3f2fd;
  border-color: #1a73e8;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.15);
}

.option-letter {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #e3f2fd;
  color: #1a73e8;
  font-weight: 600;
  margin-right: 15px;
  flex-shrink: 0;
}

.option-item.selected .option-letter {
  background: #1a73e8;
  color: white;
}

.answer-area {
  margin-top: 20px;
}

.answer-label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #1a73e8;
}

.answer-textarea {
  width: 100%;
  min-height: 200px;
  padding: 15px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.6;
  resize: vertical;
  transition: border-color 0.3s;
}

.answer-textarea:focus {
  outline: none;
  border-color: #1a73e8;
  box-shadow: 0 0 0 2px rgba(26, 115, 232, 0.2);
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eaeef5;
}

.nav-buttons, .submit-section {
  display: flex;
  gap: 12px;
}

.el-button {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.mark-button {
  background: #fff8e1;
  border-color: #ffecb3;
  color: #ff8f00;
}

.mark-button.marked {
  background: #ffecb3;
  color: #ff6f00;
}

.save-button {
  background: #e8f5e9;
  border-color: #c8e6c9;
  color: #43a047;
}

.submit-button {
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  border: none;
  color: white;
  padding: 10px 25px;
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.footer-note {
  text-align: center;
  padding: 20px;
  background: #fff8e1;
  color: #ff8f00;
  font-size: 14px;
  border-top: 1px solid #ffecb3;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.error-message {
  text-align: center;
  padding: 40px;
  color: #f56c6c;
}

.error-message h2 {
  margin-bottom: 20px;
}

@media (max-width: 900px) {
  .exam-content {
    flex-direction: column;
  }

  .question-nav {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #eaeef5;
  }

  .question-buttons {
    grid-template-columns: repeat(10, 1fr);
  }
}

@media (max-width: 600px) {
  .action-buttons {
    flex-direction: column;
    gap: 15px;
  }

  .nav-buttons, .submit-section {
    width: 100%;
  }
}


/* 新增提交等待遮罩样式 */
.submitting-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  z-index: 1000;
}
.submitting-overlay .is-loading {
  color: white;
}

</style>