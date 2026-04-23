<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import {
  ElDivider,
  ElCard,
  ElDescriptions,
  ElButton,
  ElContainer,
  ElHeader,
  ElMain,
  ElTabs,
  ElTabPane,
  ElTable,
  ElTableColumn,
  ElTag,
  ElAlert,
  ElUpload,
  ElMessage
} from 'element-plus'
import {
  Postcard,
  User,
  OfficeBuilding,
  Avatar,
  Watch,
  UserFilled
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {getEegData} from '@/api/fatigue'
import { useRoute, useRouter } from 'vue-router'
// 从路由拿参数
const route    = useRoute()
const router   = useRouter()
const mode = 'realtime'
const workerId = Number(route.params.workerId ?? 30)

// build SSE URL
const uploadRecordId = 1
// ==== 1. 准备图表容器引用和实例 ====
const lineChartRef = ref(null)
const gaugeChartRef = ref(null)
const roseChartRef = ref(null)

let lineChartInstance = null
let gaugeChartInstance = null
let roseChartInstance = null

// ==== 2. 响应式数据 ====
const rawWaveData = ref(Array(128).fill(0))             // 折线图数据：{ time, value }
const realTimeFatigueValue = ref(0)       // 仪表盘和折线图最新值
const waveDistributionData = ref([])      // 玫瑰图数据：[{ name, value }]
const fatigueLevel = ref('')              // 文本等级
const alertMessage = ref('')              // 预警信息
// 滑动窗口最大点数
const MAX_RAW_POINTS = 512
const rawWaveXAxis = ref([])      // x 轴标签
const TARGET_FS = 128
// ==== 3. 图表初始化函数 ====
function initLineChart() {
  if (!lineChartRef.value) return
  lineChartInstance = echarts.init(lineChartRef.value)
  lineChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { 
      type: 'category', 
      data: rawWaveXAxis.value,
      boundaryGap: false 
    },
    yAxis: { 
      type: 'value',
      min: 'dataMin',
      max: 'dataMax'},
    series: [{
      name: '脑电波指数',
      type: 'line',
      data:rawWaveXAxis.value,
      showSymbol: false,
      sampling: 'lttb',       // 大数据抽样算法
      lineStyle: { width: 1 },
      animation: false,
      animationDurationUpdate: 0,
      animationEasing: 'linear',
    }],
    grid: { left: '5%', right: '5%', right: '10%', bottom: '10%' }
  })
}

function updateLineChart(newSamples) {
  if (!lineChartInstance) return

  // 1) 推入新样本（可能是单个，也可能是数组）
  if (Array.isArray(newSamples)) {
    newSamples.forEach(v => rawWaveData.value.push(v));
  } else {
    rawWaveData.value.push(newSamples);
  }

  // 2) 超长则左移
  if (rawWaveData.value.length > MAX_RAW_POINTS) {
    rawWaveData.value.splice(0, rawWaveData.value.length - MAX_RAW_POINTS);
  }

  // 3) 生成 x 轴索引
  rawWaveXAxis.value = rawWaveData.value.map((_, i) => i);

  // 4) 更新图表，关闭动画以保证无卡顿
  lineChartInstance.setOption({
    animation: false,
    xAxis: { data: rawWaveXAxis.value },
    series: [{ data: rawWaveData.value }]
  }, false, true);
}

function initGaugeChart() {
  if (!gaugeChartRef.value) return
  gaugeChartInstance = echarts.init(gaugeChartRef.value)
  gaugeChartInstance.setOption({
    tooltip: { formatter: '{a} <br/>{b} : {c}%' },
    series: [{
      name: '疲劳指数',
      type: 'gauge',
      radius: '90%',
      center: ['50%', '55%'],
      detail: { formatter: '{value}%', fontSize: 16 },
      data: [{ value: 0 }],
      axisLine: {
        lineStyle: {
          color: [
            [0.6, '#67e0e3'],
            [0.8, '#ffdb5c'],
            [1.0, '#ff9f7f']
          ],
          width: 15
        }
      }
    }]
  })
}

function updateGaugeChart() {
  if (!gaugeChartInstance) return
  gaugeChartInstance.setOption({
    series: [{ data: [{ value: realTimeFatigueValue.value }] }]
  })
}

function initRoseChart() {
  if (!roseChartRef.value) return
  roseChartInstance = echarts.init(roseChartRef.value)
  roseChartInstance.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    series: [{
      name: 'Frequency Distribution',
      type: 'pie',
      radius: [20, 80],
      center: ['50%', '40%'],
      roseType: 'radius',
      label: { show: true },
      data: []
    }]
  })
}

function updateRoseChart() {
  if (!roseChartInstance) return
  roseChartInstance.setOption({
    series: [{
      data: waveDistributionData.value.map(item => ({
        name: item.name,
        value: item.value
      }))
    }]
  })
}

// ==== 4. 建立 SSE 连接，并在收到数据时更新各项 ====
function startSse() {

  // 构造带 access_token 的 URL
  const url = `/api/eeg/stream`
    + `?mode=${mode}`
    + `&workerId=${workerId}`
    + `&uploadRecordId=${uploadRecordId}`

  const evt = new EventSource(url)

  evt.onmessage = e => {
    console.log('[SSE] got data:', e.data)
    let data
    try {
      data = JSON.parse(e.data)
    } catch {
      return
    }
   const samples = data.rawWave;
   const interval = 1000 / TARGET_FS;      // 1000ms / 128 ≈ 7.8ms

   // 按采样率分批追加
   samples.forEach((v, idx) => {
    setTimeout(() => {
      updateLineChart(v);
    }, idx * interval);
  });
    
    // 3) 仪表盘
    realTimeFatigueValue.value = data.fatigueIndex
    // 4) 玫瑰图：四个波段转百分比
    waveDistributionData.value = [
      { name: 'Delta', value: data.deltaPower * 100 },
      { name: 'Theta', value: data.thetaPower * 100 },
      { name: 'Alpha', value: data.alphaPower * 100 },
      { name: 'Beta',  value: data.betaPower  * 100 }
    ]
    // 5) 文本信息
    fatigueLevel.value = data.fatigueLevel
    alertMessage.value = data.alertMessage

    // 6) 更新图表
    updateGaugeChart()
    updateRoseChart()
  }

  evt.onerror = err => {
    console.error('SSE 连接错误:', err)
    // 可选：重连或提示用户
  }


}

function isFatigued(){
      return this.realTimeFatigueValue >= 40;
    }

// ==== 5. 页面挂载时初始化并启动 SSE ====
onMounted(() => {
  initLineChart()
  initGaugeChart()
  initRoseChart()
  startSse()
  // 响应式自适应
  window.addEventListener('resize', () => {
    lineChartInstance?.resize()
    gaugeChartInstance?.resize()
    roseChartInstance?.resize()
  })
})
</script>

<template>
  <div class="page-container">
    <el-container class="outside-container">
      <el-header>
        <div style="display:flex; justify-content: space-between; align-items: center;">
          <el-descriptions
            title="脑电波检测"
            :column="3"
            size="small"
            border
          >
            <el-descriptions-item label="Worker ID">{{ workerId }}</el-descriptions-item>
            <el-descriptions-item label="Upload ID">{{ uploadRecordId }}</el-descriptions-item>
            <el-descriptions-item label="模式">{{ mode }}</el-descriptions-item>
          </el-descriptions>
          <el-button type="primary" @click="router.push({ name: 'EmployeeList' })">
            重新选择
          </el-button>
        </div>
        <el-divider />
      </el-header>

      <!-- 主内容：左侧折线图 + 右侧（仪表盘 + 玫瑰图） -->
      <el-main>
        <div class="top-row">
          <!-- 左侧折线图 -->
          <el-card class="line-card" shadow="hover">
            <div class="chart-title">实时脑电波监测曲线</div>
            <div class="line-chart" ref="lineChartRef"></div>
          </el-card>

          <!-- 右侧：仪表盘和玫瑰图 -->
          <div class="right-col">
            <!-- 仪表盘：疲劳指数 -->
            <el-card class="gauge-card" shadow="hover">
              <div class="chart-title">疲劳指数</div>
              <el-alert
                v-if="realTimeFatigueValue>40"
                :title="`预警：${alertMessage}`"
                type="error"
                show-icon
                class="fatigue-alert"
              />
              <div class="gauge-box" ref="gaugeChartRef"></div>
              <div class="recommendation">
                <h4>当前疲劳指数：<span style="color: #f56c6c;">{{ realTimeFatigueValue }}</span></h4>
                <el-tag
                  :type="realTimeFatigueValue >= 80
                    ? 'danger'
                    : realTimeFatigueValue >= 60
                    ? 'warning'
                    : realTimeFatigueValue >= 40
                    ? 'warning'
                    : 'success'"
                  effect="dark"
                >
                </el-tag>
              </div>
            </el-card>

            <!-- 玫瑰图：脑电波分布 -->
            <el-card class="wave-card" shadow="hover">
              <div class="chart-title">脑电波分布</div>
              <div class="rose-chart" ref="roseChartRef"></div>
            </el-card>
          </div>
        </div>


        <!-- 下方：展开/收起更多指标 -->
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.page-container {
  width: 100%;
  height: 100%;
}
.outside-container {
  height: 100%;
}
.el-header {
  height: auto;
}
/* 文件上传卡片 */
.upload-card {
  margin: 20px;
}
/* 第一行：左侧折线图 + 右侧（仪表盘 + 玫瑰图） */
.top-row {
  display: flex;
  gap: 20px;
  align-items: stretch;
  margin-bottom: 20px;
}
.line-card {
  flex: 3;
  min-height: 500px;
  display: flex;
  flex-direction: column;
}
.line-chart {
  flex: 1;
  width: 100%;
  height: 400px;
}
.right-col {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 500px;
}
.gauge-card,
.wave-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 50px;
}
.gauge-box {
  flex: 1;
  width: 100%;
  height: 200px;
}
.rose-chart {
  flex: 1;
  width: 100%;
  height: 200px;
}
.chart-title {
  text-align: center;
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 16px;
}
.fatigue-alert {
  margin-bottom: 10px;
}
.recommendation {
  text-align: center;
  margin-top: 10px;
  margin-bottom: 20px;
}
.bottom-row {
  margin-top: 20px;
}
.more-tabs {
  margin-top: 10px;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
