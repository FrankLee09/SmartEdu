import { createApp } from 'vue'
import { createPinia } from 'pinia'
import EmelentPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import Echarts from 'vue-echarts'
import 'echarts'

import App from './App.vue'
import router from './router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import VueVideoPlayer from '@videojs-player/vue'
import 'video.js/dist/video-js.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.component("Echarts",Echarts) 
app.use(createPinia())
app.use(router)
app.use(VueVideoPlayer)
app.use(EmelentPlus, {
    locale: zhCn
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')
