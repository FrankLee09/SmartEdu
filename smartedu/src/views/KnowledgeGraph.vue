<template>
  <div class="knowledge-graph">
    <el-card class="graph-card" v-loading="loading">
      <template #header>
        <div class="graph-header">
          <h2>知识图谱</h2>
          <div class="subject-buttons">
            <el-button-group>
              <el-button
                  v-for="subject in subjects"
                  :key="subject.id"
                  type="primary"
                  :plain="selectedSubject !== subject.id"
                  @click="handleSubjectClick(subject.id)"
                  :loading="loading && selectedSubject === subject.id"
              >
                {{ subject.name }}
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <div v-if="error" class="error-message">
        <el-alert :title="error" type="error" :closable="false" />
      </div>

      <knowledge-graph-viewer
          v-if="graphData.nodes?.length"
          :graph-data="graphData"
      />
      <div v-else-if="!loading && !error" class="empty-placeholder">
        请选择课程以生成知识图谱
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import KnowledgeGraphViewer from '@/components/KnowledgeGraphViewer.vue'

const loading = ref(false)
const error = ref('')
const graphData = ref<{
  nodes: Array<{
    id: string;
    text: string;
    color?: string;
    borderColor?: string;
    fontColor?: string;
    width?: number;
    height?: number;
  }>;
  lines: Array<{
    from: string;
    to: string;
    text?: string;
  }>;
  rootId: string;
}>({
  nodes: [],
  lines: [],
  rootId: ''
})

const selectedSubject = ref<number | null>(null)

const subjects = [
  { id: 1, name: '概率论' },
  { id: 2, name: '英语' },
  { id: 3, name: '嵌入式系统' }
]

const handleSubjectClick = (courseId: number) => {
  if (loading.value) return
  selectedSubject.value = courseId
  error.value = ''
  selectSubject(courseId)
}

const selectSubject = async (courseId: number) => {
  loading.value = true
  graphData.value = { nodes: [], lines: [], rootId: '' }

  try {
    // 1. 获取课程相关的所有题目
    const questionsRes = await request.get('/question/getAll')
    if (questionsRes.code !== '200' || !Array.isArray(questionsRes.data)) {
      throw new Error(questionsRes.msg || '获取题目数据失败')
    }

    // 2. 过滤出当前课程的知识点
    const filteredQuestions = questionsRes.data.filter((q: any) =>
        q?.courseId && q?.kgPoint && Number(q.courseId) === Number(courseId)
    )

    if (filteredQuestions.length === 0) {
      throw new Error(`未找到课程ID ${courseId} 的相关知识点`)
    }

    // 3. 提取唯一的知识点
    const points = new Set(
        filteredQuestions
            .map((q: any) => q.kgPoint)
            .filter(Boolean)
            .map((point: string) => point.trim())
    )

    const kgPointsArray = Array.from(points)
    if (kgPointsArray.length === 0) {
      throw new Error('未找到有效的知识点')
    }

    // 4. 调用知识图谱生成接口
    const graphRes = await request.post('/dify/knowledgeGraph', {
      knowledge: kgPointsArray.join(',')
    })

    if (graphRes.code !== '200' || !graphRes.data) {
      throw new Error(graphRes.msg || '生成知识图谱失败')
    }

    // 5. 更新图谱数据
    graphData.value = graphRes.data
    console.log('生成的知识图谱数据:', graphData.value)

  } catch (err: any) {
    console.error('知识图谱生成失败:', err)
    error.value = err.message || '请求失败，请检查网络连接'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.knowledge-graph {
  padding: 20px;
}

.graph-card {
  background: #fff;
  min-height: 800px;
}

.graph-header {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.graph-header h2 {
  margin: 0;
  color: #1a1a1a;
}

.subject-buttons {
  display: flex;
  justify-content: center;
  margin: 10px 0;
}

.error-message {
  margin: 20px 0;
}

.empty-placeholder {
  height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 16px;
}
</style>