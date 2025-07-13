<template>
  <div>
    <div style="display: flex; grid-gap: 10px; justify-content: flex-start; margin-bottom: 15px;">
      <div class="card" style="padding: 20px; width: 100%; height: 100px; display: flex; gap: 15px;">
        <div style="width: fit-content; display: flex; align-items: center; padding-right: 10px;">
          <img :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="" style="width: 90px; height: 90px; border-radius: 25%;">
          <span style="display: flex; flex-direction: column; margin-left: 15px; color: black;">
                    {{ data.user?.name || '未知用户' }} 学习分析
                    <span style="font-size: 0.9em; color: #666;">{{ currentDate }}</span>
                    </span>
        </div>
      </div>
    </div>

    <div class="grid-container" style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 10px;">
      <div class="left-column" style="display: flex; flex-direction: column; gap: 10px;">
        <div class="card" style="padding: 20px; height: 400px;" id="bar"></div>
        <div class="card" style="padding: 20px; height: 400px;" id="line"></div>
      </div>
      <div class="right-column">
        <div class="card" style="padding: 20px; height: 95%;">
          <div class="recommendation-container">
            <div class="recommendation-header">
              <h3>AI学习分析</h3>
              <el-button
                  type="primary"
                  :loading="loading"
                  @click="getRecommendation"
                  :disabled="loading"
                  class="recommend-btn"
              >
                AI智能学习推荐
              </el-button>
            </div>
            <div v-if="recommendation" class="recommendation-content">
              <div class="recommendation-title">个性化学习建议</div>
              <div class="recommendation-text">{{ recommendation }}</div>
            </div>
            <div v-else-if="!loading" class="recommendation-empty">
              <el-icon><InfoFilled /></el-icon>
              <span>点击上方按钮获取AI智能学习推荐</span>
            </div>
            <div v-if="loading" class="recommendation-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>AI正在分析您的学习数据...</span>
            </div>
            <div class="bottom-image">
              <img src="@/assets/ai.png" alt="学习分析图片" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted} from 'vue';
import * as echarts from 'echarts';
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { InfoFilled, Loading } from '@element-plus/icons-vue';

const storedUser = localStorage.getItem('db-user');
const data = reactive({
  user: storedUser ? JSON.parse(storedUser) : {}
});

const currentDate = ref('');
const greeting = ref('');
const recommendation = ref('');
const loading = ref(false);

const getRecommendation = async () => {
  if (!data.user.id) {
    ElMessage.warning('用户信息不完整，无法获取推荐');
    return;
  }

  loading.value = true;
  try {
    const res = await request.get(`/dify/recommend/${data.user.id}`);
    if (res.code === '200' && res.data) {
      recommendation.value = res.data;
      ElMessage.success('获取AI学习推荐成功');
    } else {
      ElMessage.error(res.msg || '获取AI学习推荐失败');
    }
  } catch (error) {
    console.error('获取AI学习推荐出错:', error);
    ElMessage.error('获取AI学习推荐失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const getGreeting = () => {
  const now = new Date();
  const hours = now.getHours();

  if (hours >= 5 && hours < 12) {
    return '早上好';
  } else if (hours >= 12 && hours < 18) {
    return '下午好';
  } else if (hours >= 18 && hours < 24) {
    return '晚上好';
  } else {
    return '夜深了';
  }
};

const barOption = {
  title: {
    text: '各个任务的成绩'
  },
  tooltip: {},
  legend: {
    data: ['任务成绩']
  },
  xAxis: {
    data: []
  },
  yAxis: {},
  series: [
    {
      name: '任务数',
      type: 'bar',
      data: [],
      itemStyle: {
        normal: {
          color: function (params) {
            let colors = ['blue']
            return colors[params.dataIndex % colors.length]
          }
        },
      },
    }
  ]
};

const lineOption = {
  title: {
    text: '最近七次任务成绩变化'
  },
  tooltip: {},
  legend: {
    trigger: 'item'
  },
  xAxis: {
    data: []
  },
  yAxis: {},
  series: [
    {
      name: '成绩',
      type: 'line',
      data: [],
      smooth: true
    }
  ]
};

onMounted(() => {
  const timer = setInterval(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const date = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    currentDate.value = `${year}/${month}/${date} ${hours}:${minutes}:${seconds}`;
    greeting.value = getGreeting();
  }, 1000)

  greeting.value = getGreeting();

  const barChart = echarts.init(document.getElementById('bar'));
  const lineChart = echarts.init(document.getElementById('line'));

  const studentId = data.user.id;
  request.get(`/barData?studentId=${studentId}`).then(res => {
    barOption.xAxis.data = res.data.tag;
    barOption.series[0].data = res.data.count;
    barChart.setOption(barOption);
  });

  request.get(`/lineData?studentId=${studentId}`).then(res => {
    // 手动设置 x 轴数据为 1 到 7 的序列
    lineOption.xAxis.data = ['1', '2', '3', '4', '5', '6', '7'];
    lineOption.series[0].data = res.data.count;
    lineChart.setOption(lineOption);
  });

  const resizeHandler = () => {
    barChart.resize();
    lineChart.resize();
  };

  window.addEventListener('resize', resizeHandler);

  onUnmounted(() => {
    clearInterval(timer);
    window.removeEventListener('resize', resizeHandler);
    barChart.dispose();
    lineChart.dispose();
  })
})
</script>

<style scoped>
.card {
  background-color: white;
  border: 2.5px solid rgb(177, 177, 177);
  border-radius: 20px;
  box-shadow:
      0 2px 8px rgba(0, 0, 0, 0.08),
      0 6px 20px rgba(0, 0, 0, 0.12);
  transition: box-shadow 0.3s ease;

  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);

    .title span {
      color: #000;
      font-weight: 600;
    }

    .author-wrapper {
      color: #000;
    }
  }
}

.card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.grid-container {
  height: auto;
}

.left-column {
  height: 100%;
}

.right-column .card {
  min-height: calc(400px + 400px + 10px);
}

.recommendation-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.recommendation-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.recommend-btn {
  font-weight: bold;
  font-size: 16px;
}

.recommendation-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.recommendation-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #409EFF;
}

.recommendation-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-line;
  letter-spacing: 0.5px;
}

.recommendation-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  gap: 10px;
  font-size: 16px;
}

.recommendation-empty .el-icon {
  font-size: 48px;
}

.recommendation-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 150px;
  gap: 15px;
  color: #409EFF;
  font-size: 16px;
}

.recommendation-loading .el-icon {
  font-size: 32px;
}

/* 添加底部图片的样式 */
.bottom-image {
  margin-top: auto;
  display: flex;
  justify-content: center;
  padding-top: 30px; /* 增加上方间距 */
}

.bottom-image img {
  max-width: 90%; /* 略微减小宽度比例，避免溢出 */
  max-height: 350px; /* 增加最大高度，从原来的200px改为350px */
  border-radius: 8px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15); /* 略微增强阴影效果 */
}
</style>