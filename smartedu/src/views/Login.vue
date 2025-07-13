<template>
  <div class="container" ref="container">
    <div class="panels-container">
      <div class="panel left-panel">
        <div class="panel-content">
          <h3>还没有加入Smartedu？</h3>
          <p>注册你自己的Smartedu账号</p>
          <button class="panel-btn" @click="toSignUp">注册</button>
        </div>
        <img class="panel-img" src="@/assets/adminLog.png" alt="" />
      </div>
      <div class="panel right-panel">
        <div class="panel-content">
          <h3>已经有Smartedu账号？</h3>
          <p>登录Smartedu</p>
          <button class="panel-btn" @click="toSignIn">登录</button>
        </div>
        <img class="panel-img" src="@/assets/adminRegister.png" alt="" />
      </div>
    </div>

    <div class="forms-container">
      <div class="signin-signup">
        <el-form ref="loginFormRef" :rules="loginData.rules" :model="loginData.form" class="sign-in-form">
          <h2 class="form-title">登录</h2>
          <el-form-item class="form-input" prop="username">
            <el-input v-model="loginData.form.username" type="text" placeholder="账号" prefix-icon="User"></el-input>
          </el-form-item>
          <el-form-item class="form-input" prop="password">
            <el-input v-model="loginData.form.password" type="text" placeholder="密码" show-password prefix-icon="Lock"></el-input>
          </el-form-item>
          <el-form-item class="form-input" prop="role">
            <el-input 
              v-model="loginData.form.role" 
              type="text" 
              placeholder="请输入登录角色(学生/教师)" 
              prefix-icon="UserFilled"
            ></el-input>
          </el-form-item>
          <el-button class="form-btn" type="primary" @click="login">登录</el-button>
        </el-form>
        <el-form ref="registerFormRef" :rules="registerData.rules" :model="registerData.form" class="sign-up-form">
          <h2 class="form-title">注册</h2>
          <el-form-item class="form-input" prop="username">
            <el-input v-model="registerData.form.username" type="text" placeholder="请输入账号" prefix-icon="User"></el-input>
          </el-form-item>
          <el-form-item class="form-input" prop="password">
            <el-input v-model="registerData.form.password" type="text" placeholder="请输入密码" show-password prefix-icon="Lock"></el-input>
          </el-form-item>
          <el-form-item class="form-input" prop="confirmPassword">
            <el-input v-model="registerData.form.confirmPassword" type="text" placeholder="请确认密码" show-password prefix-icon="Lock"></el-input>
          </el-form-item>
          <el-form-item class="form-input" prop="name">
            <el-input v-model="registerData.form.name" type="text" placeholder="请输入名称" prefix-icon="Edit"></el-input>
          </el-form-item>
          <el-button class="form-btn" type="primary" @click="register">注册</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import request from '@/utils/request.ts';
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次确认密码'))
  } else if (value !== registerData.form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateRole = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入登录角色'));
  } else if (!['学生', '教师'].includes(value)) {
    callback(new Error('请输入"学生"或"教师"'));
  } else {
    callback();
  }
};

const loginData = reactive({
    form: {},
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur'}
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur'}
        ],
        role: [
            { validator: validateRole, trigger: 'blur' }
        ]
    }
})

const registerData = reactive({
    form: {},
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur'}
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur'}
        ],
        confirmPassword: [
            { validator: validatePass, trigger: 'blur'}
        ],
        name: [
            { required: true, message: '请输入名称', trigger: 'blur'}
        ]
    }
})

const loginFormRef = ref();
const registerFormRef = ref();

const login = () => {
    loginFormRef.value.validate((valid) => {
        if (valid) {
          request.post('/login', loginData.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem('db-user', JSON.stringify(res.data));
              ElMessage.success('登录成功')
              
              const role = loginData.form.role;
              const redirectUrl = role === '学生' 
                ? '/manager/StudentHome' 
                : '/manager/TeacherHome';
              
              setTimeout( () => {
                location.href = redirectUrl;
              }, 500)
            } else {
              ElMessage.error(res.msg)
            }
          })
        }
    })
}

const register = () => {
    registerFormRef.value.validate((valid) => {
        if (valid) {
          request.post('/teacherRegister', registerData.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem('db-user', JSON.stringify(res.data));
              ElMessage.success('注册成功，正在登录系统')
              setTimeout( () => {
                location.href = '/manager/TeacherHome'
              }, 500)
            } else {
              ElMessage.error(res.msg)
            }
          })
        }
    })
}

const container = ref(null);

const toSignUp = () => {
  container.value.classList.add('sign-up-mode');
  if (loginFormRef.value) {
    loginFormRef.value.resetFields();
  }
};

const toSignIn = () => {
  container.value.classList.remove('sign-up-mode');
  if (registerFormRef.value) {
    registerFormRef.value.resetFields();
  }
};

</script>

<style lang="less" scoped>
/* 保持原有样式不变 */
.container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  font-size: 14px;
  font-family: sans-serif;
  background-color: #fff;
  overflow: hidden;
}

.container::before {
  content: '';
  position: absolute;
  top: -10%;
  right: 48%;
  transform: translateY(-50%);
  transition: 1.8s ease-in-out;
  width: 2000px;
  height: 2000px;
  background-image: linear-gradient(-45deg, rgb(52, 52, 169) 0%, blue 100%);
  border-radius: 50%;
  z-index: 3;
}

.panels-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}

.panel {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: flex-end;
  text-align: center;
  z-index: 9;

  .panel-content {
    color: #fff;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;

    h3 {
      font-size: 22px;
      font-weight: 600;
      line-height: 1;
    }

    p {
      padding: 10px 0;
    }

    .panel-btn {
      width: 130px;
      height: 41px;
      outline: none;
      border: 2px solid #fff;
      border-radius: 49px;
      background: none;
      color: #fff;
      font-weight: 600;
      text-transform: uppercase;
      cursor: pointer;
      transition: 0.5s;
    }
  }

  .panel-img {
    width: 100%;
    transition: transform 1.1s ease-in-out;
    transition-delay: 0.4s;
  }
}

.left-panel {
  pointer-events: all;
  padding: 42px 17% 28px 12%;
}

.right-panel {
  pointer-events: none;
  padding: 42px 12% 28px 17%;
  .panel-content,
  .panel-img {
    transform: translateX(800px);
  }
}

.container.sign-up-mode::before {
  right: 52%;
  transform: translate(100%, -50%);
}

.container.sign-up-mode {
  .left-panel {
    pointer-events: none;
    .panel-content,
    .panel-img {
      transform: translateX(-800px);
    }
  }

  .right-panel {
    pointer-events: all;
    .panel-content,
    .panel-img {
      transform: translateX(0);
    }
  }

  .signin-signup {
    left: 25%;
  }

  .sign-up-form {
    z-index: 2;
    opacity: 1;
  }

  .sign-in-form {
    z-index: 1;
    opacity: 0;
  }
}

.forms-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.signin-signup {
  position: absolute;
  top: 50%;
  left: 75%;
  transform: translate(-50%, -50%);
  transition: 1s, 0.7s ease-in-out;
  width: 50%;
  display: grid;
  grid-template-columns: 1fr;
}

.sign-in-form,
.sign-up-form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  grid-column: 1 / span 1;
  grid-row: 1 / span 1;
  transition: all 0.2s, 0.7s;
}

.sign-up-form {
  z-index: 1;
  opacity: 0;
}

.sign-in-form {
  z-index: 2;
}

.form-title {
  font-size: 30px;
  color: #444;
  margin-bottom: 10px;
}

.form-input {
  width: 100%;
  max-width: 380px;
  height: 55px;
  border-radius: 55px;
  padding: 0 20px;
  background-color: #f0f0f0;
  margin: 10px 0;
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

.form-btn {
  width: 150px;
  height: 49px;
  margin: 10px 0;
  outline: none;
  border: none;
  border-radius: 49px;
  color: #fff;
  font-weight: 600;
  text-transform: uppercase;
  background-color: blue;
  cursor: pointer;
}

.form-btn:hover {
  background-color: blue;
}

.social-text {
  padding: 10px 0;
}

.social-media {
  display: flex;
  justify-content: center;
}

.social-link {
  margin: 0 4px;
  color: #333;
  text-decoration: none;
  transition: 0.3s;
}

.social-link::hover {
  color: blue;
}

.social-icon {
  fill: currentColor;
}
</style>