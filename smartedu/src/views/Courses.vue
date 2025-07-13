<template>
  <div class="feeds-page">
    <div class="search-container">
      <el-form class="search-form" :model="searchData" @submit.prevent="handleSearch">
        <el-form-item class="form-input" prop="title">
          <el-input v-model="searchData.title" type="text" placeholder="搜索课程" prefix-icon="Search" @keyup.enter="handleSearch"/>
        </el-form-item>
      </el-form>
    </div>
    <div class="channel-container">
      <div class="scroll-container channel-scroll-container">
        <div class="content-container">
          <div class="channel" :class="{ active: currentChannel === '全部课程' }" @click="changeChannel('全部课程')">全部课程</div>
          <div class="channel" :class="{ active: currentChannel === '热门课程' }" @click="changeChannel('热门课程')">热门课程</div>
          <div class="channel" :class="{ active: currentChannel === '已报名课程' }" @click="changeChannel('已报名课程')">已报名课程</div>
        </div>
      </div>
    </div>
    <div class="loading-container"></div>
    <div class="feeds-container">
      <Waterfall :list="filteredArticleList" :width="240" :hasAroundGutter="false" style="max-width: 1260px">
        <template #item="{ item, url, index }">
          <div class="card">
            <LazyImg :url="item.imgs" style="border-radius: 8px" @click="toMain(item)" />
            <div class="footer">
              <a class="title"><span>{{ item.title }}</span></a>
              <div class="author-wrapper">
                <a class="author">
                  <img class="author-avatar" :src="item.posterAvatar" />
                  <span class="name">{{ item.posterName }}</span>
                </a>
                <span class="like-wrapper like-active">
                </span>
              </div>
            </div>
          </div>
        </template>
      </Waterfall>
    </div>
    <div class="feeds-loading" v-if="isLoading">加载中...</div>
    <Main 
      @main_close="closeMain" 
      v-if="mainShow" 
      :article="selectedArticle"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";
import request from '@/utils/request.ts';
import { Search } from "@element-plus/icons-vue";
import { LazyImg, Waterfall } from "vue-waterfall-plugin-next";
import "vue-waterfall-plugin-next/dist/style.css";
import Main from "@/views/CourseDetail.vue";

const articleList = ref<any[]>([]);
const filteredArticleList = ref<any[]>([]);
const isLoading = ref(false);
const currentChannel = ref('全部课程');
const mainShow = ref(false);
const selectedArticle = ref<any>({});
const searchData = ref({
  title: null
});

const toMain = (article: any) => {
  selectedArticle.value = article;
  mainShow.value = true;
};

const loadArticles = async () => {
  isLoading.value = true;
  try {
    const res = await request.get('/course/selectAll', {
      params: {
        title: null
      }
    });
    articleList.value = res.data.list;
    filteredArticleList.value = res.data.list;
  } catch (error) {
    console.error('加载课程数据失败:', error);
  } finally {
    isLoading.value = false;
  }
};

const changeChannel = (channel: string) => {
  currentChannel.value = channel;
  if (channel === '全部课程') {
    filteredArticleList.value = articleList.value;
  } else {
    filteredArticleList.value = articleList.value.filter((article) => article.tag === channel);
  }
};

const closeMain = () => {
  mainShow.value = false;
};

const handleSearch = async () => {
  isLoading.value = true;
  try {
    const res = await request.get('/course/selectAll', {
      params: {
        title: searchData.value.title || null
      }
    });
    articleList.value = res.data.list;
    filteredArticleList.value = res.data.list;
  } catch (error) {
    console.error('搜索课程数据失败:', error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  loadArticles();
});
</script>

<style lang="less" scoped>
.feeds-page {
  flex: 1;
  padding: 0 96px;
  padding-top: 24px;
  box-sizing: border-box;
  max-width: 100vw;
  overflow-x: hidden;

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
      transition: all 0.3s ease;
      cursor: pointer;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
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

      .author-wrapper {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 20px;
        color: rgba(51, 51, 51, 0.8);
        font-size: 12px;
        transition: all 0.3s ease;

        .author {
          display: flex;
          align-items: center;
          color: inherit;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-right: 12px;

          .author-avatar {
            margin-right: 6px;
            width: 20px;
            height: 20px;
            border-radius: 20px;
            border: 1px solid rgba(0, 0, 0, 0.08);
            flex-shrink: 0;
          }

          .name {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .like-wrapper {
          position: relative;
          cursor: pointer;
          display: flex;
          align-items: center;

          .count {
            margin-left: 2px;
          }
        }
      }
    }
  }
}
</style>