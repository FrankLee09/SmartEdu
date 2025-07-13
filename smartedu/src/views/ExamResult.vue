<template>
  <div class="result-container" v-if="resultData">
    <div class="result-header">
      <h1>{{ resultData.examTitle }} - 考试结果</h1>
      <div class="result-summary">
        <div class="result-item">
          <div class="result-label">总分</div>
          <div class="result-value">{{ resultData?.totalScore?.toFixed(1) || '0.0' }}/{{ resultData?.totalPossibleScore || '0' }}</div>
        </div>
        <div class="result-item">
          <div class="result-label">得分率</div>
          <div class="result-value">{{ resultData.scorePercentage.toFixed(1) }}%</div>
        </div>
        <div class="result-item">
          <div class="result-label">正确题数</div>
          <div class="result-value">{{ resultData.correctCount }}/{{ resultData.totalQuestions }}</div>
        </div>
        <div class="result-item">
          <div class="result-label">提交时间</div>
          <div class="result-value">{{ formatDateTime(resultData.submittedAt) }}</div>
        </div>
      </div>
    </div>

    <div class="details-container">
      <h2>答题详情</h2>
      <div class="question-details" v-for="(detail, index) in resultData.scoreDetails" :key="index">
        <div class="question-header">
          <div class="question-number">题目 {{ index + 1 }}</div>
          <div :class="['question-result', detail.isCorrect === true ? 'correct' : (detail.isCorrect === false ? 'incorrect' : '')]">
            <template v-if="detail.isCorrect === true">正确</template>
            <template v-else-if="detail.isCorrect === false">错误</template>
            <template v-else>得分</template>
            <span class="question-score">{{ (detail.pointsEarned ?? 0).toFixed(1) }}/{{ detail.score }}</span>
          </div>

        </div>
        <div class="question-title">{{ detail.questionTitle }}</div>
        <div class="answer-section">
          <div class="answer-item">
            <div class="answer-label">您的答案：</div>
            <div class="answer-content">{{ detail.userAnswer || '(未作答)' }}</div>
          </div>
          <div class="answer-item">
            <div class="answer-label">正确答案：</div>
            <div class="answer-content correct">{{ detail.correctAnswer }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="actions">
      <el-button type="primary" @click="backToExams">返回考试列表</el-button>
    </div>
  </div>
  <div v-else class="no-result">
    <h2>未找到考试结果</h2>
    <el-button type="primary" @click="backToExams">返回考试列表</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import dayjs from 'dayjs';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const examId = ref(route.query.examId);
const resultData = ref(null);

onMounted(() => {
  document.body.style.overflow = 'auto';
  if (!examId.value) {
    ElMessage.error('考试ID不存在');
    router.push('/manager/exam');
    return;
  }

  // 从本地存储获取结果数据
  const savedResult = localStorage.getItem(`exam_${examId.value}_result`);
  if (savedResult) {
    resultData.value = JSON.parse(savedResult);
  }
});

// 格式化日期时间
function formatDateTime(dateStr) {
  if (!dateStr) return '';
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss');
}

// 返回考试列表
function backToExams() {
  router.push('/manager/examList');
}
</script>

<style scoped>
.result-container {
  max-width: 1000px;
  margin: 30px auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.result-header {
  border-bottom: 1px solid #eaeef5;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.result-header h1 {
  font-size: 24px;
  color: #1a73e8;
  margin-bottom: 20px;
}

.result-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.result-item {
  background: #f8fafc;
  border-radius: 8px;
  padding: 15px;
  flex: 1;
  min-width: 150px;
  text-align: center;
}

.result-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.result-value {
  font-size: 22px;
  font-weight: 600;
  color: #1a73e8;
}

.details-container {
  margin-bottom: 30px;
}

.details-container h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.question-details {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  border-left: 4px solid #e0e0e0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-number {
  font-weight: 600;
  color: #555;
}

.question-result {
  display: flex;
  align-items: center;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.question-result.correct {
  background: #e8f5e9;
  color: #43a047;
}

.question-result.incorrect {
  background: #ffebee;
  color: #e53935;
}

.question-score {
  margin-left: 8px;
  background: rgba(255, 255, 255, 0.5);
  padding: 2px 6px;
  border-radius: 10px;
}

.question-title {
  font-size: 16px;
  margin-bottom: 15px;
  padding: 10px;
  background: white;
  border-radius: 6px;
  border-left: 3px solid #1a73e8;
}

.answer-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.answer-item {
  display: flex;
  padding: 10px;
  background: white;
  border-radius: 6px;
}

.answer-label {
  width: 100px;
  font-weight: 500;
  color: #555;
}

.answer-content {
  flex: 1;
}

.answer-content.correct {
  color: #43a047;
  font-weight: 500;
}

.actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.no-result {
  text-align: center;
  padding: 50px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  margin: 100px auto;
}

.no-result h2 {
  margin-bottom: 20px;
  color: #666;
}
</style>