import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router/index.ts";

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 300000
})

request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        if (error.response) {
            if (error.response.status === 404) {
                ElMessage.error('未找到请求接口')
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请查看后端控制台报错')
            } else {
                ElMessage.error(error.response.data?.msg || '请求失败')
            }
        } else if (error.request) {
            ElMessage.error('服务器无响应，请检查网络连接')
        } else {
            ElMessage.error('请求配置错误：' + error.message)
        }
        return Promise.reject(error)
    }
)

export default request