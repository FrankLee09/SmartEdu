<template>
  <div>
    <div style="height:calc(100vh);">
      <RelationGraph ref="graphRef" :options="graphOptions" @node-click="onNodeClick" @line-click="onLineClick" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch, defineProps } from 'vue';
import RelationGraph from 'relation-graph-vue3';
import type { RGJsonData, RGNode, RGLine, RGLink, RGUserEvent, RGOptions, RelationGraphComponent } from 'relation-graph-vue3';

const props = defineProps<{
  graphData: {
    nodes: any[];
    lines: any[];
    rootId: string;
  }
}>();

const graphRef = ref<RelationGraphComponent | null>(null);

const graphOptions: RGOptions = {
  debug: false,
  defaultNodeBorderWidth: 0,
  allowSwitchLineShape: true,
  allowSwitchJunctionPoint: true,
  defaultLineShape: 1,
  layout: {
    layoutName: 'force',
    layoutClassName: 'ForceLayout',
    layoutSettings: {
      repulsion: 1000,
      attractive: 1,
      gravity: 0.1,
    }
  },
  defaultJunctionPoint: 'border'
};

onMounted(() => {
  showGraph();
});

// 监听 graphData 的变化
watch(() => props.graphData, (newData) => {
  if (newData?.nodes?.length) {
    showGraph();
  }
}, { deep: true });

const showGraph = async () => {
  // 转换数据格式以适配 relation-graph
  const __graph_json_data: RGJsonData = {
    rootId: props.graphData.rootId,
    nodes: props.graphData.nodes.map(node => ({
      id: node.id,
      text: node.text,
      color: node.color || '#4f81ff',
      borderColor: node.borderColor || '#4f81ff',
      fontColor: node.fontColor || '#000000',
      width: node.width || 120,
      height: node.height || 50,
    })),
    links: props.graphData.lines.map(line => ({
      from: line.from,
      to: line.to,
      text: line.text || '',
    }))
  };

  const graphInstance = graphRef.value?.getInstance();
  if (graphInstance) {
    await graphInstance.setJsonData(__graph_json_data);
    await graphInstance.moveToCenter();
    await graphInstance.setZoom(50);
  }
};

const onNodeClick = (nodeObject: RGNode, $event: RGUserEvent) => {
  console.log('节点点击:', nodeObject);
};

const onLineClick = (lineObject: RGLine, linkObject: RGLink, $event: RGUserEvent) => {
  console.log('连线点击:', lineObject);
};
</script>

<style lang="scss">
.relation-graph {
  width: 100%;
  height: 100%;
}
</style>

<style lang="scss" scoped>

</style>
