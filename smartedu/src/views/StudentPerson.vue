<template>
  <div class="user-page">
    <div class="user">
      <div class="user-info">
        <div class="avatar">
          <div class="avatar-wrapper">
            <img
              :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
              class="user-image"
              style="border: 1px solid rgba(0, 0, 0, 0.08)"
            />
          </div>
        </div>
        <div class="info-part">
          <div class="info">
            <div class="basic-info">
              <div class="user-basic">
                <div class="user-nickname">
                  <div class="user-name">{{ data.user.name }}</div>
                </div>
                <div class="user-content">
                  <span class="user-redId">{{ data.user.role }}</span>
                </div>
              </div>
            </div>
            <div class="user-desc">
              学号:
              {{ data.user.id }}
            </div>
            <div class="user-desc">
              所属班级:
              {{ data.user.classId }}
            </div>
            <div class="user-actions">
              <div 
                class="gray-rounded-btn" 
                @click="data.formVisible = true"
              >
                <span>修改信息</span>
              </div>
            </div>
            <div class="user-tags">
            </div>
            <div class="data-info">
            </div>
          </div>
          <div class="follow"></div>
        </div>
      </div>
    </div>
    <div class="reds-sticky-box user-page-sticky" style="--1ee3a37c: all 0.4s cubic-bezier(0.2, 0, 0.25, 1) 0s">
      <div class="reds-sticky" style="">
        <div class="tertiary center reds-tabs-list" style="padding: 0px 12px">
          <div class="reds-tab-item active" style="padding: 0px 16px; margin-right: 16px; font-size: 16px">
            <span>全部课程</span>
          </div>
          <div class="active-tag" style="width: 64px; left: 627px"></div>
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

    <el-dialog title="学生信息" v-model="data.formVisible" width="50%" destroy-on-close>
      <div class="tab-container">
        <div class="tab-bar">
          <div 
            class="tab-item" 
            :class="{ active: !data.isPasswordTab }"
            @click="data.isPasswordTab = false"
          >
            基本信息修改
          </div>
          <div 
            class="tab-item" 
            :class="{ active: data.isPasswordTab }"
            @click="data.isPasswordTab = true"
          >
            修改密码
          </div>
          <div 
            class="tab-indicator"
            :style="{ transform: data.isPasswordTab ? 'translateX(100%)' : 'translateX(0)' }"
          ></div>
        </div>
        
        <div class="tab-content">
          <template v-if="!data.isPasswordTab">
            <el-form ref="formRef" :rules="data.rules" :model="data.form"  label-width="80px" style="padding-right: 40px; padding-top: 20px;">
              <div style="width: 100%; display: flex; justify-content: center; margin-bottom: 20px;">
                <el-upload
                  class="avatar-uploader"
                  action="http://localhost:8080/files/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                >
                  <img v-if="data.form.avatar" :src="data.form.avatar" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </div>
              <el-form-item label="学号" prop="id">
                <el-input disabled v-model="data.form.id" autocomplete="off" placeholder="请输入学号"/>
              </el-form-item>
              <el-form-item label="名称" prop="name">
                <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"/>
              </el-form-item>
              <el-form-item label="所属班级" prop="classId">
                <el-input disabled v-model="data.form.classId" autocomplete="off" placeholder="请输入所属班级"/>
              </el-form-item>
            </el-form>
          </template>
          <template v-else>
            <el-form ref="passwordFormRef" :rules="passwordData.rules" :model="passwordData.form"  label-width="100px" style="padding-right: 40px; padding-top: 20px;">
              <el-form-item label="原密码" prop="password">
                <el-input show-password v-model="passwordData.form.password" autocomplete="off" placeholder="请输入原密码"/>
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input show-password v-model="passwordData.form.newPassword" autocomplete="off" placeholder="请输入新密码"/>
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword" required>
                <el-input show-password v-model="passwordData.form.confirmPassword" autocomplete="off" placeholder="请再次确认新密码"/>
              </el-form-item>
            </el-form>
          </template>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="data.isPasswordTab ? updatePassword() : updateUser()">保存</el-button>
        </div>
      </template>
    </el-dialog>
    
  </div>
</template>
<script lang="ts" setup>
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { reactive, ref, watch, onMounted, computed } from 'vue';
import { Plus } from '@element-plus/icons-vue';
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

const formRef = ref()
const passwordFormRef = ref()

const data = reactive ({
  user: JSON.parse(localStorage.getItem('db-user') || '{}'),
  form: {},
  formVisible: false,
  isPasswordTab: false,
  rules: {
    name: [
      { required: true, message: '请输入名称', trigger: 'blur'}
    ],
    classId: [
      { required: true, message: '请输入班级', trigger: 'blur'}
    ],
    id: [
      { required: true, message: '请输入学号', trigger: 'blur'}
    ]
  }
})

watch(() => localStorage.getItem('db-user'), (newVal) => {
  if (newVal) {
    data.user = JSON.parse(newVal);
  }
})

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次确认新密码'))
  } else if (value !== passwordData.form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordData = reactive({
  form: {},
  rules: {
    password: [
      { required: true, message: '请输入原密码', trigger: 'blur'}
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur'}
    ],
    confirmPassword: [
      { validator: validatePass, trigger: 'blur'}
    ]
  }
})

const handleAvatarSuccess = (res) => {
  console.log(res.data)
  data.form.avatar = res.data
}

const emit = defineEmits(['updateUser'])

const loadUserInfo = () => {
  if (data.user.id) {
    request.get('/student/selectById/' + data.user.id).then(res => {
      data.form = JSON.parse(JSON.stringify(res.data));
      data.user = {...data.user, ...res.data};
      localStorage.setItem('db-user', JSON.stringify(data.user));
    }).catch(err => {
      ElMessage.error('加载用户信息失败')
      console.error(err)
    })
  }
}

loadUserInfo()

const updateUser = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.put('/student/update', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('更新成功')
          data.formVisible = false
          
          data.user = {...data.user, ...data.form};
          localStorage.setItem('db-user', JSON.stringify(data.user));
          
          emit('updateUser')
        } else {
          ElMessage.error(res.msg || '更新失败')
        }
      }).catch(err => {
        ElMessage.error('网络错误，更新失败')
        console.error(err)
      })
    }
  })
}

const updatePassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      passwordData.form.id = data.user.id
      passwordData.form.role = data.user.role
      request.put('/updatePassword', passwordData.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('修改成功，请重新登录')
          localStorage.removeItem('db-user')
          setTimeout(() => {
            location.href = '/login'
          }, 500)
        } else {
          ElMessage.error(res.msg || '修改失败')
        }
      }).catch(err => {
        ElMessage.error('网络错误，修改失败')
        console.error(err)
      })
    }
  })
}

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
    console.log('课程数据加载成功', allCourses.value);
  } catch (error) {
    console.error("加载课程数据失败:", error);
  } finally {
    isLoading.value = false;
  }
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

const changeChannel = (channel: string) => {
  currentChannel.value = channel;
  filterCourses();
};

onMounted(() => {
  loadCourses();
});
</script>

<style lang="less" scoped>
.user-page {
  background: #fff;
  overflow-y: scroll;
  overflow-x: hidden;

  .user {
    padding-top: 72px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1.5px solid rgb(211, 211, 211);
    .user-info {
      display: flex;
      justify-content: center;
      padding: 48px 0;

      .avatar {
        .avatar-wrapper {
          text-align: center;
          width: 250.66667px;
          height: 175.46667px;
          .user-image {
            border-radius: 50%;
            margin: 0 auto;
            width: 70%;
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
            .user-basic {
              width: 100%;
              .user-nickname {
                width: 100%;
                display: flex;
                align-items: center;
                max-width: calc(100% - 96px);
                .user-name {
                  width: 500px;
                  font-weight: 600;
                  font-size: 24px;
                  line-height: 120%;
                  color: #333;
                }
              }
              .user-content {
                width: 100%;
                font-size: 12px;
                line-height: 120%;
                color: rgba(51, 51, 51, 0.6);
                display: flex;
                margin-top: 8px;
                .user-redId {
                  padding-right: 12px;
                }
              }
            }
          }
          .user-desc {
            width: 100%;
            font-size: 14px;
            line-height: 140%;
            color: #333;
            margin-top: 16px;
            white-space: pre-line;
          }
          .user-actions {
            margin-top: 16px;
            
            .gray-rounded-btn {
              padding: 0px 16px;
              font-size: 16px;
              display: inline-flex;
              align-items: center;
              box-sizing: border-box;
              height: 40px;
              cursor: pointer;
              color: rgba(51, 51, 51, 0.8);
              white-space: nowrap;
              background-color: rgba(0, 0, 0, 0.05);
              border-radius: 20px;
              transition: all 0.3s cubic-bezier(0.2, 0, 0.25, 1);
              font-weight: 600;
              
              &:hover {
                background-color: rgba(0, 0, 0, 0.1);
              }
            }
          }
          .user-tags {
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
            .user-interactions {
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
            .user-interactions > div {
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              text-align: center;
              margin-right: 16px;
            }
          }
        }

        .follow {
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

    display: flex; 
    justify-content: center; 

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

  .feeds-tab-container {
    padding-left: 2rem;
  }
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
}

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
  line-height: 120px;
}

.tab-container {
  width: 100%;
  margin-bottom: 10px;
}

.tab-bar {
  position: relative;
  display: flex;
  width: 100%;
  height: 40px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 20px;
}

.tab-item {
  flex: 1;
  height: 100%;
  line-height: 40px;
  text-align: center;
  font-size: 15px;
  color: #6b7280;
  cursor: pointer;
  transition: color 0.3s ease;
  user-select: none;
}

.tab-item.active {
  color: #409eff;
  font-weight: 500;
}

.tab-indicator {
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 50%;
  height: 2px;
  background-color: #409eff;
  transition: transform 0.3s ease;
}

:deep(.el-form-item__label) {
  padding-right: 10px;
}

.el-button.is-active {
  background-color: #409eff;
  color: #fff;
}
</style>