<template>
  <div class="course-page">
    <div class="back-button" @click="goBack">
      <el-icon><ArrowLeft /></el-icon>
    </div>
    <div class="course">
      <div class="course-info">
        <div class="course-image">
          <div class="image-wrapper">
            <img
              :src="data.course.imgs || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
              class="course-image"
              style="border: 1px solid rgba(0, 0, 0, 0.08)"
            />
          </div>
        </div>
        <div class="info-part">
          <div class="info">
            <div class="basic-info">
              <div class="course-basic">
                <div class="course-name">
                  <div class="course-title">{{ data.course.title }}</div>
                </div>
                <div class="course-content">
                  <span class="course-teacher">授课教师: {{ getTeacherName(data.course.teacherId) }}</span>
                </div>
              </div>
            </div>
            <div class="course-desc">
              课程介绍:
              {{ data.course.description }}
            </div>
            <div class="course-tags">
            </div>
            <div class="data-info">
            </div>
          </div>
          <div class="enroll"></div>
        </div>
      </div>
    </div>
    <div class="reds-sticky-box course-page-sticky" style="--1ee3a37c: all 0.4s cubic-bezier(0.2, 0, 0.25, 1) 0s">
      <div class="reds-sticky" style="">
        <div class="tertiary center reds-tabs-list" style="padding: 0px 12px">
          <div class="reds-tab-item active" style="padding: 0px 16px; margin-right: 16px; font-size: 16px">
            <span>课程详情</span>
          </div>
        </div>
      </div>
    </div>
    <div class="feeds-tab-container" style="--1ee3a37c: all 0.4s cubic-bezier(0.2, 0, 0.25, 1) 0s">
      <div class="card" style="margin-bottom: 5px; margin-top: 50px;">
        <el-timeline>
          <el-timeline-item
            v-for="item in filesData.tableData"
            :key="item.id"
            placement="top"
          >
            <div class="file-card" @click="handleFileClick(item)">
              <div class="file-card-header">
                <span class="file-name">{{ item.tag }}: {{ item.filename }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <div style="margin-top: 15px;">
          <el-pagination
            @size-change="loadFiles"
            @current-change="loadFiles"
            v-model:current-page="filesData.pageNum"
            v-model:page-size="filesData.pageSize"
            :page-sizes="[5, 10, 15, 20]"
            background
            layout="total, size, prev, pager, next, jumper"
            :total="filesData.total"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import request from '@/utils/request.ts';
import { ArrowLeft, Edit, Delete, Search, Plus, Check } from '@element-plus/icons-vue';
import { reactive, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();

const data = reactive({
  course: {},
  teacherOptions: [],
});

const filesData = reactive({
  filename: null,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {
    uploadedFileName: '',
    fileUrl: '',
    filename: '',
    description: '',
    tag: ''
  },
  ids: [],
  isEdit: false,
  rules: {
    filename: [
      { required: true, message: '请输入文件名', trigger: 'blur'}
    ],
    tag: [
      { required: true, message: '请选择标签', trigger: 'change'}
    ]
  }
})

const handleFileClick = (item) => {
  if (item.tag === '视频') {
    router.push({ path: `/manager/video/${item.id}` });
  } else {
    if (item.fileUrl) {
      window.open(item.fileUrl, '_blank');
    } else {
      ElMessage.warning('文件链接不存在');
    }
  }
}

const loadFiles = () => {
  const courseId = route.params.id;
  request.get('/files/selectPage', {
    params: {
      pageNum: filesData.pageNum,
      pageSize: filesData.pageSize,
      filename: filesData.filename,
      courseId: courseId
    }
  }).then(res => {
    filesData.tableData = res.data.list.map(item => ({
      ...item,
      isLoading: false,
      originalFileUrl: item.fileUrl,
    }));
    filesData.total = res.data.total
  })
}

const loadTeachers = async () => {
  try {
    const res = await request.get('/teacher/getAll');
    data.teacherOptions = res.data || [];
  } catch (error) {
    console.error('加载教师列表失败:', error);
    ElMessage.error('教师列表加载失败');
  }
};

const getTeacherName = (teacherId) => {
  if (!teacherId) return '未分配';
  const teacher = data.teacherOptions.find((t) => t.id === teacherId);
  return teacher ? teacher.name : '未知教师';
};

const loadCourse = async () => {
  const courseId = route.params.id;
  if (courseId) {
    try {
      const res = await request.get('/course/selectById/' + courseId);
      data.course = res.data;
    } catch (error) {
      console.error('加载课程数据失败:', error);
    }
  }
};

onMounted(async () => {
  await loadCourse();
  await loadTeachers();
  loadFiles();
});

const goBack = () => {
  router.back();
};
</script>

<style lang="less" scoped>
.course-page {
  background: #fff;
  overflow-y: scroll;
  overflow-x: hidden;

  .back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    font-size: 24px;
    cursor: pointer;
  }

  .course {
    padding-top: 72px;
    display: flex;
    align-items: center;
    justify-content: center;
    .course-info {
      display: flex;
      justify-content: center;
      padding: 48px 0;

      .course-image {
        .image-wrapper {
          text-align: center;
          width: 550px;
          height: 325px;
          .course-image {
            border-radius: 20px;
            margin: 0 auto;
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }

      .info-part {
        position: relative;
        width: 100%;

        .info {
          @media screen and (min-width: 1728px) {
            width: 533.33333px;
          }
          margin-left: 32px;
          .basic-info {
            display: flex;
            align-items: center;
            .course-basic {
              width: 100%;
              .course-name {
                width: 100%;
                display: flex;
                align-items: center;
                max-width: calc(100% - 96px);
                .course-title {
                  width: 500px;
                  font-weight: 600;
                  font-size: 36px;
                  line-height: 120%;
                  color: #333;
                }
              }
              .course-content {
                width: 100%;
                font-size: 18px;
                line-height: 120%;
                color: rgba(51, 51, 51, 0.6);
                display: flex;
                margin-top: 8px;
                .course-teacher {
                  padding-right: 12px;
                }
              }
            }
          }
          .course-desc {
            width: 100%;
            font-size: 18px;
            line-height: 140%;
            color: #333;
            margin-top: 16px;
            white-space: pre-line;
          }
          .course-tags {
            height: 24px;
            margin-top: 16px;
            display: flex;
            align-items: center;
            font-size: 12px;
            color: #333;
            text-align: center;
            font-weight: 400;
            line-height: 120%;
            .tag-item :first-child {
              padding: 3px 6px;
            }
            .tag-item {
              display: flex;
              align-items: center;
              justify-content: center;
              padding: 4px 8px;
              grid-gap: 4px;
              gap: 4px;
              height: 18px;
              border-radius: 41px;
              background: rgba(0, 0, 0, 0.03);
              height: 24px;
              line-height: 24px;
              margin-right: 6px;
              color: rgba(51, 51, 51, 0.6);
            }
          }
          .data-info {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 20px;
            .course-interactions {
              width: 100%;
              display: flex;
              align-items: center;
              .count {
                font-weight: 500;
                font-size: 14px;
                margin-right: 4px;
              }
              .shows {
                color: rgba(51, 51, 51, 0.6);
                font-size: 14px;
                line-height: 120%;
              }
            }
            .course-interactions > div {
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              text-align: center;
              margin-right: 16px;
            }
          }
        }

        .enroll {
          position: absolute;
          margin-left: auto;
          display: block;
          right: 0;
          top: 0;
        }
      }
    }
  }

  .reds-sticky {
    padding: 16px 0;
    z-index: 5 !important;
    background: hsla(0, 0%, 100%, 0.98);
    border-bottom: 1.5px solid rgb(211, 211, 211);

    .reds-tabs-list {
      @media screen and (min-width: 1728px) {
        width: 1445.33333px;
      }
      display: flex;
      flex-wrap: nowrap;
      position: relative;
      font-size: 16px;
      justify-content: center;

      .reds-tab-item {
        padding: 0px 16px;
        margin-right: 0px;
        font-size: 16px;
        display: flex;
        align-items: center;
        box-sizing: border-box;
        height: 40px;
        cursor: pointer;
        color: rgba(51, 51, 51, 0.8);
        white-space: nowrap;
        transition: transform 0.3s cubic-bezier(0.2, 0, 0.25, 1);
        z-index: 1;
      }
      .reds-tab-item.active {
        background-color: rgba(0, 0, 0, 0.03);
        border-radius: 20px;
        font-weight: 600;
        color: rgba(51, 51, 51, 0.8);
      }
    }
  }

  .feeds-tab-container {
    padding-left: 2rem;
  }

  .file-uploader {
    border: 1px dashed var(--el-border-color);
    border-radius: 8px;
    width: 100%;
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .file-uploader:hover {
    border-color: var(--el-color-primary);
  }

  .uploader-icon {
    font-size: 28px;
    color: #8c939d;
  }

  .uploaded-file-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--el-color-success);
    font-size: 14px;
  }

  .uploaded-filename {
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .file-card {
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
    padding: 15px;
    margin-bottom: 15px;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
      
      .file-name {
        color: #000;
        font-weight: 600;
      }
      
      .file-card-content {
        color: #000;
      }
    }
  }

  .file-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }

  .file-name {
    font-size: 30px;
    font-weight: bold;
  }

  .file-card-content {
    margin-bottom: 10px;
  }

  .file-card-actions {
    display: flex;
    justify-content: flex-end;
  }
}
</style>