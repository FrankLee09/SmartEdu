<template>
  <div class="feeds-page">
    <div class="top-layout">
      <div class="left-card" @click="goToTaskPage">
        <div class="timeline-outer-container">
          <div class="timeline-container">
            <h3 class="timeline-title">学习任务</h3>
            <div class="timeline-scroll-container" style="height: 280px; overflow-y: auto; padding-right: 10px;">
              <el-timeline>
                <el-timeline-item 
                  v-for="(item, index) in sortedTaskList" 
                  :key="index" 
                  :timestamp="formatTime(item.time)"
                  placement="top"
                >
                  <div class="timeline-content">
                    <h4>{{ item.title }}</h4>
                    <p>发布时间：{{ formatTime(item.time) }}</p>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
        </div>
      </div>
      <div class="banner-carousel-container">
        <el-carousel :interval="4000" type="card" height="400px">
          <el-carousel-item v-for="(item, index) in latestCourseList" :key="index">
            <div class="carousel-item" @click="toMain(item)">
              <img :src="item.imgs" alt="课程封面" class="carousel-img" />
              <h3 class="carousel-title">{{ item.title }}</h3>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="right-card" @click="goToPersonPage">
        <div style="display: flex; grid-gap: 10px; justify-content: flex-start; margin-bottom: 15px;">
          <div class="card" style="padding: 20px; width: 100%; display: flex; flex-direction: column; align-items: center;">
            <div style="width: fit-content; display: flex; flex-direction: column; align-items: center;">
              <img 
                :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
                alt="用户头像" 
                style="width: 90px; height: 90px; border-radius: 25%; margin-bottom: 15px;"
              >
              <span style="display: flex; flex-direction: column; align-items: center; color: black; text-align: center;">
                {{ greeting }}, {{ data.user?.name || '未知用户' }}!
                <span style="display: flex; flex-direction: column; align-items: center; color: black; text-align: center;">欢迎回到SmartEdu</span>
                <span style="font-size: 0.9em; color: #666; margin-top: 5px;">{{ currentDate }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="search-container">
      <el-form class="search-form" :model="searchData" @submit.prevent="handleSearch">
        <el-form-item class="form-input" prop="title">
          <el-input
            v-model="searchData.title"
            type="text"
            placeholder="搜索课程"
            prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
      </el-form>
    </div>
    <div class="channel-container">
      <div class="scroll-container channel-scroll-container">
        <div class="content-container">
          <div
            class="channel"
            :class="{ active: currentChannel === '全部课程' }"
            @click="changeChannel('全部课程')"
          >
            全部课程
          </div>
        </div>
      </div>
    </div>
    <div class="loading-container"></div>
    <div class="feeds-container">
      <Waterfall
        :list="filteredCourseList"
        :width="240"
        :hasAroundGutter="false"
        style="max-width: 1260px"
      >
        <template #item="{ item, url, index }">
          <div class="card">
            <LazyImg :url="item.imgs" @click="toMain(item)" />
            <div class="footer">
              <a class="title"><span>{{ item.title }}</span></a>
              <div class="like-wrapper like-active"></div>
            </div>
          </div>
        </template>
      </Waterfall>
    </div>
    <div class="feeds-loading" v-if="isLoading">加载中...</div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted, computed } from "vue";
import request from "@/utils/request.ts";
import { Search } from "@element-plus/icons-vue";
import { LazyImg, Waterfall } from "vue-waterfall-plugin-next";
import "vue-waterfall-plugin-next/dist/style.css";
import { useRouter } from 'vue-router';

const router = useRouter();
const allCourses = ref<any[]>([]);
const filteredCourseList = ref<any[]>([]);
const isLoading = ref(false);
const currentChannel = ref("全部课程");
const searchData = ref({
  title: null,
});

const latestCourseList = ref<any[]>([]);
const storedUser = localStorage.getItem("db-user");
const data = reactive({
  user: storedUser ? JSON.parse(storedUser) : {},
});
const currentDate = ref("");
const greeting = ref("");

const taskList = ref<any[]>([]);
const sortedTaskList = computed(() => {
  return [...taskList.value].sort((a, b) => {
    const timeA = a.time || 0;
    const timeB = b.time || 0;
    return new Date(timeB).getTime() - new Date(timeA).getTime();
  });
});

const toMain = (course: any) => {
  router.push({ path: `/manager/courseDetail/${course.id}` });
};

const loadCourses = async () => {
  isLoading.value = true;
  try {
    const res = await request.get("/course/selectPage", {
      params: {
        pageNum: 1,
        pageSize: 1000,
        title: null
      }
    });
    allCourses.value = res.data?.list || [];
    filteredCourseList.value = [...allCourses.value];
    loadLatestCourses();
    console.log('课程数据加载成功', allCourses.value);
  } catch (error) {
    console.error("加载课程数据失败:", error);
  } finally {
    isLoading.value = false;
  }
};

const loadTasks = async () => {
  try {
    const allTasksRes = await request.get('/task/selectPage', {
      params: {
        pageNum: 1,
        pageSize: 10000,
      }
    });
    taskList.value = allTasksRes.data?.list || [];
  } catch (error) {
    console.error('加载任务数据失败:', error);
  }
};

const loadLatestCourses = () => {
  if (allCourses.value.length > 0) {
    const sortedCourses = [...allCourses.value].sort((a, b) => {
      const timeA = a.createTime || a.time || 0;
      const timeB = b.createTime || b.time || 0;
      return new Date(timeB).getTime() - new Date(timeA).getTime();
    });
    latestCourseList.value = sortedCourses.slice(0, 5); 
  }
};

const formatTime = (time: string | number) => {
  if (!time) return "未知时间";
  
  try {
    const date = typeof time === 'number' ? new Date(time) : new Date(time);
    
    if (isNaN(date.getTime())) {
      console.warn('无效的日期格式:', time);
      return "未知时间";
    }
    
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  } catch (error) {
    console.error('日期格式化错误:', error, '原始值:', time);
    return "未知时间";
  }
};

const changeChannel = (channel: string) => {
  currentChannel.value = channel;
  filterCourses();
};

const goToTaskPage = () => {
  router.push({ path: '/manager/studentTask' });
};

const goToPersonPage = () => {
  router.push({ path: '/manager/studentPerson' });
};

const handleSearch = () => {
  filterCourses();
};

const filterCourses = () => {
  const searchTitle = searchData.value.title?.toLowerCase() || '';
  
  filteredCourseList.value = allCourses.value.filter(course => {
    const titleMatch = course.title?.toLowerCase().includes(searchTitle);
    
    if (currentChannel.value === '全部课程') {
      return titleMatch;
    } else if (currentChannel.value === '已报名课程') {
      return titleMatch && course.isEnrolled;
    }
    
    return false;
  });
};

const getGreeting = () => {
  const now = new Date();
  const hours = now.getHours();
  
  if (hours >= 5 && hours < 12) {
    return "早上好";
  } else if (hours >= 12 && hours < 18) {
    return "下午好";
  } else if (hours >= 18 && hours < 24) {
    return "晚上好";
  } else {
    return "夜深了";
  }
};

onMounted(() => {
  Promise.all([loadCourses(), loadTasks()]).then(() => {
    console.log('数据加载完成');
  });
  
  greeting.value = getGreeting();
  updateCurrentDate();
  const dateTimer = setInterval(updateCurrentDate, 1000);
  
  onUnmounted(() => {
    clearInterval(dateTimer);
  });
});

const updateCurrentDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const date = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  
  currentDate.value = `${year}/${month}/${date} ${hours}:${minutes}:${seconds}`;
  greeting.value = getGreeting();
};
</script>

<style lang="less" scoped>
.feeds-page {
  flex: 1;
  padding: 0 96px;
  padding-top: 24px;
  box-sizing: border-box;
  max-width: 100vw;
  overflow-x: hidden;

  .top-layout {
    display: flex;
    align-items: flex-start;
    justify-content: center; 
    margin: 0 auto 20px;
    max-width: 1260px;

    .left-card,
    .right-card {
      background-color: white;
      border: 2.5px solid rgb(177, 177, 177);
      border-radius: 20px;
      box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.08), 
        0 6px 20px rgba(0, 0, 0, 0.12); 
      transition: box-shadow 0.3s ease, transform 0.3s ease;
      cursor: pointer;
      overflow: hidden;
      width: 300px; 
      height: 350px;
      box-sizing: border-box;
      margin: 0 10px; 

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
      }
    }

    .left-card {
      .timeline-outer-container {
        display: flex;
        justify-content: flex-start;
        align-items: flex-start;
        width: 100%;
        height: 100%;
        padding-left: 10px;
      }
      
      .timeline-container {
        padding: 0;
        width: 100%;
        
        .timeline-title {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 10px;
          color: #333;
          border-bottom: 1px solid #eee;
          padding-bottom: 8px;
        }
        
        .timeline-scroll-container {
          -ms-overflow-style: none;
          scrollbar-width: none;
          
          &::-webkit-scrollbar {
            display: none;
          }
        }
        
        .timeline-content {
          padding: 12px;
          background: #f8f9fc;
          border-radius: 8px;
          margin-top: 8px;
          
          h4 {
            font-size: 14px;
            margin-bottom: 4px;
            color: #333;
          }
          
          p {
            font-size: 12px;
            color: #666;
            margin: 2px 0;
          }
        }
      }
    }

    .banner-carousel-container {
      flex: 0 0 750px; 
      overflow: hidden;
      margin: 0 10px; 
      border-radius: 20px; 

      .carousel-item {
        position: relative;
        height: 100%;
        cursor: pointer;
        border-radius: 20px;

        .carousel-img {
          width: 100%;
          height: 100%;
          object-fit: cover; 
          border-radius: 20px;
        }

        .carousel-title {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          padding: 10px 15px;
          background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
          color: white;
          font-size: 16px;
          font-weight: 500;
          margin: 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          border-radius: 0 0 20px 20px;
          padding-bottom: 20px;
        }
      }
    }

    .right-card {
      .card {
        height: auto;
      }
    }
  }

  .search-container {
    justify-content: flex-start;
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .search-form {
      display: flex;
      align-items: center;

      .form-input {
        width: 500px;
        height: 45px;
        border-radius: 55px;
        padding: 0 20px;
        background-color: #f0f0f0;
        margin: 0 350px;
        display: flex;
        align-items: center;

        :deep(.el-form-item__content) {
          display: flex;
          align-items: center;
          height: 100%;
          width: 100%;
          margin-left: 0 !important;
        }

        :deep(.el-input) {
          width: 100%;
          height: 100%;

          .el-input__wrapper {
            background: none;
            box-shadow: none;
            border: none;
            height: 100%;
            width: 100%;
            padding: 0;
          }

          .el-input__inner {
            height: 100%;
            font-size: 16px;
            font-weight: 600;
            color: #333;
            line-height: 1;

            &::placeholder {
              color: #aaa;
              font-weight: 500;
            }
          }
        }
      }
    }
  }

  .channel-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    user-select: none;
    -webkit-user-select: none;
    max-width: 100%;

    .channel-scroll-container {
      backdrop-filter: blur(20px);
      background-color: transparent;
      width: calc(100vw - 48px);
      max-width: 100%;

      position: relative;
      overflow: hidden;
      display: flex;
      user-select: none;
      -webkit-user-select: none;
      align-items: center;
      font-size: 16px;
      color: rgba(51, 51, 51, 0.8);
      height: 40px;
      white-space: nowrap;
      height: 72px;

      .content-container::-webkit-scrollbar {
        display: none;
      }

      .content-container {
        display: flex;
        overflow-x: auto;
        overflow-y: hidden;
        white-space: nowrap;
        color: rgba(51, 51, 51, 0.8);
        max-width: 100%;

        .active {
          font-weight: 600;
          background: rgba(0, 0, 0, 0.03);
          border-radius: 999px;
          color: #333;
        }

        .channel {
          height: 40px;
          display: flex;
          justify-content: center;
          align-items: center;
          padding: 0 16px;
          cursor: pointer;
          -webkit-user-select: none;
          user-select: none;
        }
      }
    }
  }

  .feeds-container {
    position: relative;
    transition: width 0.5s;
    margin: 0 auto;
    max-width: 100%;
    overflow: hidden;

    .card {
      background-color: white;
      border: 2.5px solid rgb(177, 177, 177);
      border-radius: 20px;
      box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.08), 
        0 6px 20px rgba(0, 0, 0, 0.12); 
      transition: box-shadow 0.3s ease, transform 0.3s ease;
      cursor: pointer;
      overflow: hidden;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);

        .title span {
          color: #000;
          font-weight: 600;
        }
      }
    }

    .footer {
      padding: 12px;
      background: #fff;
      .title {
        margin-bottom: 8px;
        word-break: break-all;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
        font-weight: 500;
        font-size: 14px;
        line-height: 140%;
        color: #333;
        transition: all 0.3s ease;
      }

      .like-wrapper {
        position: relative;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: flex-end;

        .count {
          margin-left: 2px;
        }
      }
    }
  }

  .feeds-loading {
    text-align: center;
    padding: 20px;
    font-size: 16px;
    color: #666;
  }
}
</style>