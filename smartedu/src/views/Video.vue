<template>
  <div class="right-container">
    <div class="back-button" @click="goBack">
      <el-icon><ArrowLeft /></el-icon>
    </div>
    <div class="card">
      <div class="video-wrapper">
        <div class="circular-progress-container">
          <div class="progress-group">
            <div class="completion-rate-display">完播率:</div>
            <div class=" circular-progress">
              <svg viewBox="0 0 100 100">
                <circle class=" circular-progress-circle circular-progress-circle-background" cx="50" cy="50" r="36" />
                <circle class=" circular-progress-circle circular-progress-circle-fill" 
                        cx="50" cy="50" r="36" 
                        :style="{ strokeDashoffset: circleDashoffset }" />
              </svg>
              <div class=" circular-progress-text">{{ formattedCompletionRate }}%</div>
            </div>
          </div>
        </div>

        <div v-if="videoUrl" ref="videoContainer" class="video-container">
          <video ref="videoRef" class="video-js vjs-default-skin vjs-big-play-centered" controls preload="auto">
            <source :src="videoUrl" type="video/mp4" />
            您的浏览器不支持HTML5视频播放
          </video>
        </div>
        <div v-else class="loading-message">加载视频中...</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch, nextTick } from 'vue';
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import request from '@/utils/request.ts';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const videoUrl = ref('');
const videoRef = ref<HTMLVideoElement | null>(null);
const videoContainer = ref<HTMLDivElement | null>(null);
const player = ref<videojs.Player | null>(null);
const isLoading = ref(true);

const INTERVAL_COUNT = 100;

const videoDuration = ref(0); // 视频总时长
const intervalCount = ref(INTERVAL_COUNT); // 总区间数
const viewedIntervals = ref(new Set<number>()); // 已观看的区间索引
const isFirstPlay = ref(true); // 标记是否为首次播放

const data = ref({
  file: null,
});

const viewedIntervalCount = computed(() => viewedIntervals.value.size);
const completionRate = computed(() => {
  if (intervalCount.value === 0) return 0;
  return Math.round((viewedIntervalCount.value / intervalCount.value) * 100);
});

const formattedCompletionRate = computed(() => completionRate.value.toString());

const CIRCUMFERENCE = 226;
const circleDashoffset = computed(() => {
  const percent = completionRate.value / 100;
  return CIRCUMFERENCE - (CIRCUMFERENCE * percent);
});

// 存储的完播率
const storedCompletionRate = ref(0);

const handleKeydown = (e: KeyboardEvent) => {
  if (!player.value) return;
  
  if (e.key === 'ArrowLeft') {
    e.preventDefault();
    player.value.currentTime(Math.max(0, player.value.currentTime() - 5));
  }
  
  if (e.key === 'ArrowRight') {
    e.preventDefault();
    player.value.currentTime(Math.min(player.value.duration(), player.value.currentTime() + 5));
  }
  
  if (e.code === 'Space') {
    e.preventDefault();
    if (player.value.paused()) {
      player.value.play();
    } else {
      player.value.pause();
    }
  }
};

const recordIntervalView = (time: number) => {
  if (videoDuration.value <= 0) return;
  
  const intervalIndex = Math.min(
    Math.floor((time / videoDuration.value) * intervalCount.value),
    intervalCount.value - 1
  );
  
  viewedIntervals.value.add(intervalIndex);
};

const ensureFinalInterval = () => {
  if (videoDuration.value <= 0 || intervalCount.value <= 0) return;
  
  const finalInterval = intervalCount.value - 1;
  viewedIntervals.value.add(finalInterval);
};

const updateCompletionRate = async () => {
  if (completionRate.value > storedCompletionRate.value) {
    try {
      const fileId = route.params.fileId;
      const res = await request.get(`/files/selectById/${fileId}`);
      const file = res.data;
      file.finished = completionRate.value / 100;
      await request.put(`/files/update`, file);
      storedCompletionRate.value = completionRate.value;
    } catch (error) {
      console.error('更新完播率失败:', error);
      alert('更新完播率失败: ' + (error as Error).message);
    }
  }
};

// 初始化播放器
const initPlayer = async () => {
  if (!videoRef.value || !videoUrl.value) return;
  
  if (player.value) {
    player.value.dispose();
    player.value = null;
  }
  
  await nextTick();
  
  try {
    const vjsPlayer = videojs(videoRef.value, {
      fluid: true,
      aspectRatio: '16:9',
      playbackRates: [0.5, 1.0, 1.5, 2.0]
    });
    
    player.value = vjsPlayer;
    isLoading.value = false;
    
    console.log('播放器初始化成功');
    
    vjsPlayer.on('loadedmetadata', () => {
      videoDuration.value = vjsPlayer.duration();
      console.log('视频加载完成，时长:', videoDuration.value, '秒');
    });
    
    vjsPlayer.on('error', (error: any) => {
      console.error('视频加载错误:', error);
      alert('视频播放出错: ' + (vjsPlayer.error()?.message || '未知错误'));
    });
    
    let lastRecordTime = 0;
    
    vjsPlayer.on('play', () => {
      if (vjsPlayer.currentTime() === 0) {
        isFirstPlay.value = false;
        lastRecordTime = 0;
      }
    });
    
    vjsPlayer.on('timeupdate', () => {
      if (vjsPlayer.paused() || vjsPlayer.ended()) return;
      
      const currentTime = vjsPlayer.currentTime();
      
      if (currentTime - lastRecordTime >= 0.01) {
        recordIntervalView(currentTime);
        lastRecordTime = currentTime;
      }
    });
    
    vjsPlayer.on('seeked', () => {
      recordIntervalView(vjsPlayer.currentTime());
    });
    
    vjsPlayer.on('ended', () => {
      ensureFinalInterval();
      updateCompletionRate();
    });

    watch(completionRate, () => {
      updateCompletionRate();
    });
  } catch (error) {
    console.error('初始化播放器失败:', error);
    alert('初始化播放器失败: ' + (error as Error).message);
  }
};

onMounted(async () => {
  document.addEventListener('keydown', handleKeydown);
  
  const fileId = route.params.fileId;
  if (!fileId) {
    console.error('未提供fileId参数');
    alert('缺少视频ID参数');
    return;
  }
  
  try {
    const res = await request.get(`/files/selectById/${fileId}`);
    if (res.data && res.data.fileUrl) {
      data.value = res.data;
      videoUrl.value = res.data.fileUrl;
      console.log('视频URL:', videoUrl.value);

      if (res.data.finished) {
        storedCompletionRate.value = res.data.finished * 100;
        const initialViewedIntervals = Math.round((storedCompletionRate.value / 100) * INTERVAL_COUNT);
        for (let i = 0; i < initialViewedIntervals; i++) {
          viewedIntervals.value.add(i);
        }
      }
      
      await nextTick();
      initPlayer();
    } else {
      console.error('未找到有效的 fileUrl:', res.data);
      alert('无法获取视频信息，请稍后重试');
    }
  } catch (error) {
    console.error('加载文件数据失败:', error);
    alert('加载视频失败: ' + (error as Error).message);
  }
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown);
  
  if (player.value) {
    player.value.dispose();
    player.value = null;
  }
});

watch(videoUrl, (newVal) => {
  if (newVal) {
    initPlayer();
  }
});

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.right-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 24px;
  cursor: pointer;
}

.video-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.video-container {
  width: 100%;
  max-width: 1280px;
  margin-top: 20px;
}

.video-js {
  width: 100% !important;
  height: auto !important;
}

.card {
  background-color: white;
  border: 2.5px solid rgb(177, 177, 177);
  border-radius: 20px;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.08), 
    0 6px 20px rgba(0, 0, 0, 0.12); 
  transition: box-shadow 0.3s ease;
  transition: all 0.3s ease;
  overflow: hidden;
  width: 80%;
  max-width: 1440px;
  padding: 40px 20px;
}

.circular-progress-container {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin: 5px 0 5px;
  width: 90%;
}

.progress-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.circular-progress {
  position: relative;
  width: 70px;
  height: 70px;
}

.circular-progress svg {
  transform: rotate(-90deg);
  width: 100%;
  height: 100%;
}

.circular-progress-circle {
  fill: none;
  stroke-width: 5;
  stroke-linecap: round;
}

.circular-progress-circle-background {
  stroke: #e0e0e0;
}

.circular-progress-circle-fill {
  stroke: #4285f4;
  stroke-dasharray: 226;
  transition: stroke-dashoffset 0.3s ease;
}

.circular-progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  font-weight: bold;
  color: #333;
}

.completion-rate-display {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.loading-message {
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}
</style>