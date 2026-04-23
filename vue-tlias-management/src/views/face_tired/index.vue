<script setup>
import { ref , onMounted,onBeforeUnmount} from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios'; 
import SockJS from 'sockjs-client/dist/sockjs.min.js';
import * as Stomp from 'stompjs';
import * as echarts from 'echarts';

const fatigueData = ref([]);
const chartInstance = ref(null); // 存储ECharts实例
const initChart = () => {
  const chartDom = document.getElementById('fatigue-chart');
  if (!chartDom) return;
  
  // 销毁旧实例（如果存在）
  if (chartInstance.value) {
    chartInstance.value.dispose();
  }
  
  // 初始化新实例
  chartInstance.value = echarts.init(chartDom);
  
  // 初始配置
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    axisPointer: {
            animation: false,
          },
    xAxis: {
      type: 'category',
      data: [],
      boundaryGap: false,
      splitLine: {
            show: false,
          },
          triggerEvent: true,
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 5
    },
    series: [{
      name: '疲劳等级',
      type: 'line',
      smooth: true,
      data: [],
      itemStyle: {
        color: '#409EFF' // Element Plus主题色
      }
    }]
  };
  chartInstance.value.setOption(option);
};
// 更新图表数据
const updateChart = (newValue) => {
  // 保持最多20个数据点
  if (fatigueData.value.length >= 100) {
    fatigueData.value.shift();
  }
  fatigueData.value.push(newValue);

  // 生成x轴标签
  const xData = fatigueData.value.map((_, index) => `${index + 1}`);

  // 更新图表
  if (chartInstance.value) {
    chartInstance.value.setOption({
      xAxis: {
        data: xData
      },
      series: [{
        data: fatigueData.value
      }]
    });
  }
};

const currentTime = ref('');
const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString(); // 本地化的时间字符串
};
// 定义 stompClient
const stompClient = ref(null);
var userId = '12345';
const fatigueRank = ref(null);
// 连接 WebSocket
const connect = () => {
  const socket = new SockJS('http://localhost:8081/ws'); // 对应后端的 "/ws" 端点
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
    console.log('Connected to WebSocket');

    // 订阅 "/topic" 目标
    stompClient.value.subscribe(`/topic/face_fatigue/${userId}`, (message) => {
    if (message.body) {
        try {
            const data = JSON.parse(message.body);
            console.log('Received message:', data);
            const rank = parseInt(data.fatigueRank);
            fatigueRank.value = rank; 
            updateChart(rank); // 新增：更新图表
            // console.log(fatigueRank.value)
        } catch (error) {
            console.error('Failed to parse message:', error);
            alert('Received invalid message format.');
        }
    }
});

    // 订阅 "/user/queue/greetings" 目标（用户特定消息）
    stompClient.value.subscribe('/user/queue/greetings', (message) => {
      if (message.body) {
        const data = JSON.parse(message.body);
        console.log('Received user-specific message:', data);
        // 在这里处理用户特定的消息
      }
    });

//     const resultSubscription = stompClient.value.subscribe('/topic/fatigueResults', (message) => {
//     const data = JSON.parse(message.body);
//     console.log('收到疲劳分析结果:', data);
// });

  });
};

// 断开连接
const disconnect = () => {
  if (stompClient.value) {
    stompClient.value.disconnect();
  }
  console.log('Disconnected from WebSocket');
};



// 生命周期钩子：组件挂载时连接 WebSocket
onMounted(() => {
  initChart();
  connect();
});


setInterval(updateTime, 1000);
// // 订阅错误信息
// const errorSubscription = stompClient.subscribe('/queue/errors', (error) => {
//     console.error('系统错误:', error.body);
// });
onBeforeUnmount(() => {
   // 销毁图表实例
   if (chartInstance.value) {
    chartInstance.value.dispose();
  }
  if (localVideoUrl.value) {
    URL.revokeObjectURL(localVideoUrl.value);
  }
});
const videoForm = ref({
  storageurl: ''
});
const localVideoUrl = ref('');
const handleFileChange = (file) => {
  if (file) {
    // 生成本地临时 URL
    localVideoUrl.value = URL.createObjectURL(file.raw);
  }
};
const videoFlag = ref(false);
const videoUploadPercent = ref(0);
const uploadData = ref(new FormData());
// 验证视频格式
const beforeUploadVideo = (file) => {
  const allowedTypes = [
    'video/mp4',
    'video/ogg',
    'video/flv',
    'video/avi',
    'video/wmv',
    'video/rmvb'
  ];
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('请上传正确的视频格式');
    return false;
  }
  // 清空旧的 FormData
  uploadData.value = new FormData();
  // 添加参数
  uploadData.value.append('userId', '12345'); // 替换为动态值
  uploadData.value.append('file', file);
  return true;
};
const customUpload = async (options) => {
  const formData = new FormData();
  formData.append('file', options.file); // 参数名必须与后端 @RequestParam("file") 一致
  formData.append('userId', '12345'); // 参数名必须与后端 @RequestParam("userId") 一致

  try {
    const response = await axios.post(options.action, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (progressEvent) => {
        const percent = Math.floor((progressEvent.loaded / progressEvent.total) * 100);
        options.onProgress({ percent }); // 触发进度更新
      },
    });
    return response
    // options.onSuccess(response,options.file); // 触发上传成功回调
  } catch (error) {
    // options.onError(error); // 触发上传失败回调
  }
};
// 上传进度显示
const uploadVideoProcess = (event, file, fileList) => {
  // console.log(event.percent, file, fileList);
  videoFlag.value = true;
  videoUploadPercent.value = Math.floor(event.percent);
};

// 获取上传图片地址
const handleVideoSuccess = (res, file) => {
  videoFlag.value = false;
  videoUploadPercent.value = 0;

  if (res && res.status === 200) {
    // videoForm.value.storageurl = res.data;
    ElMessage.success('上传成功！');
  } else {
    const errorMsg = res?.data?.message || '视频上传失败，请重新上传！';
    ElMessage.error(errorMsg);
  }
};
</script>

<template>
  <div class="common-layout">
    <el-container class="outside-container">
  <el-header><div >
  <el-descriptions 
  class="margin-top"
  title="所查看用户的面部检测"  
  :column="6"
  :size="small"
  border
  >
    <template #extra>
      <el-button type="primary">重新选择</el-button>
    </template>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon :style="iconStyle">
            <postcard />
          </el-icon>
          用户工号
        </div>
      </template>
      {{ userId }}
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon :style="iconStyle">
            <user />
          </el-icon>
          用户姓名
        </div>
      </template>
      张三
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon><OfficeBuilding /></el-icon>
          部门
        </div>
      </template>
      变电站
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon><Avatar /></el-icon>
          职务
        </div>
      </template>
      值班员
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon><Watch /></el-icon>
          年龄
        </div>
      </template>
      35
    </el-descriptions-item>
    <el-descriptions-item>
      <template #label>
        <div class="cell-item">
          <el-icon><UserFilled /></el-icon>
          性别
        </div>
      </template>
      男
    </el-descriptions-item>
  </el-descriptions>
</div>
<el-divider />
</el-header>

    <el-container class="main-container">
      <!-- 左侧 -->
      
    <el-main>
    <!-- 视频上传区域 -->
    <el-form-item class="video-upload-item" prop="storageurl">
      <el-upload
      class="el-upload"

        action="http://localhost:8081/faceDetectService/video_upload"
        :on-change="handleFileChange"
        :show-file-list="false"
        :on-success="handleVideoSuccess"
        :before-upload="beforeUploadVideo"
        :on-progress="uploadVideoProcess"
        :accept="'video/mp4,video/ogg,video/flv,video/avi,video/wmv,video/rmvb'"
        :http-request="customUpload"
        name="file" 
        drag
      >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
    <div class="el-upload__text">
      将文件拖到此处，或<em>点击上传</em>
    </div>
    <template #tip>
      <div class="el-upload__tip">
        请上传要检测疲劳等级的视频文件
      </div>
    </template>
        <div class="upload-content" v-if="localVideoUrl && !videoFlag">
          <video
            v-if="localVideoUrl"
            :src="localVideoUrl"
            class="preview-video"
            controls
          >
            您的浏览器不支持视频播放
          </video>
          <!-- <i
            v-else-if="videoForm.storageurl == '' && videoFlag == false"
            class="el-icon-plus avatar-uploader-icon"
          ></i> -->
          <el-progress
            v-if="videoFlag"
            type="circle"
            :percentage="videoUploadPercent"
            style="margin-top:30px;"
          ></el-progress>
        </div>
      </el-upload>
    </el-form-item>
    
  </el-main>
  <el-aside >
        <div id="fatigue-chart" style="width: 100%; height: 100%;"></div>
    </el-aside>
  <!-- <el-divider direction="vertical" /> -->
    <el-aside >
      <el-descriptions
    title="视频监测信息"
    :column="1"

    class="asideDes"
    :content-style="{
    'text-align': 'center',
    'min-width': '250px',
    'word-break': 'break-all'
  }"
    
  >
    <el-descriptions-item label="时间" :label-class-name="label-style">{{ currentTime }}</el-descriptions-item>
    <el-descriptions-item label="帧宽度"  >1920</el-descriptions-item>
    <el-descriptions-item label="帧高度" >1080</el-descriptions-item>
    <el-descriptions-item label="帧速率" >23.98fps</el-descriptions-item>
    <el-descriptions-item label="面部危险等级"
    ><span >{{ fatigueRank === null ? '无' : fatigueRank }}</span></el-descriptions-item>
    <el-descriptions-item label="是否需要停工" ><el-text class="mx-1" type="danger">{{ fatigueRank === null ? '无' : (fatigueRank > 2 ?'是':'否') }}</el-text></el-descriptions-item>
  </el-descriptions>
    </el-aside>
    <!-- 左边放置信息的区域 -->
  </el-container>
  </el-container>
  </div>
</template>

<style scoped>
#fatigue-chart {
  background: #fff;

  height: 100%;
  width: 100%;


}

:deep(.el-upload__tip){
  height: 10%;
  width: 100%;
}


.video-upload-item {
  display: flex;
  width: 100%;
  height: 90%;

}

:deep(.el-upload) {
  width: 100%; /* 根据需要设置宽度 */
  height: 90%;
  display: flex;
  flex-direction: column;
}
:deep(.el-upload .el-upload-dragger){
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;

}

.el-progress-circle {
  margin-top: 20px;
}
.main-container{
    height: 100%;
    width: 100%;
    }
.outside-container{
        height: 100%;

    }
.el-main{
      align-items: stretch;
      height: 100%;
  }
.common-layout{
  height: 100%;
}
.el-header{
  height: 20%;
}
.el-aside{
  align-items:center;
  display: flex;
  flex-direction: column;
  align-content: space-between;
  align-items: center;
  justify-content: center;
  height: 85%;
  width: 30%;
}
.asideDes {
  width: 100%;
  height: 80%;
}
/* 确保描述项容器填充高度 */
:deep(.el-descriptions__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  width: 100%;
}
:deep(.asideDes .el-descriptions__label){
  width: 100%;
}
:deep(.asideDes .el-descriptions__content){
  width: 100%;
}
/* 使描述项容器内容自动填充 */
:deep(.el-descriptions__table) {
  flex: 1;
}
.content-cell {
  text-align: right !important;
  width: 50%; /* 可根据需要调整内容区域宽度 */
  background-color: black;
  color: blue;
}
.label-style{
    color: red;
    text-align: center;
    font-weight: 600;
    height: 40px;
    min-width: 110px;
    word-break: keep-all
  }
.preview-video {
  width: 100%;
  height: 100%;
  max-width: 600px;
  max-height: 400px; /* 新增最大高度限制 */
  object-fit: contain; /* 保持比例填充 */
  margin-top: 20px;
}
</style>