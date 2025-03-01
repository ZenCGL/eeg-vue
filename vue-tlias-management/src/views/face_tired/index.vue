<script setup>
import { ref } from 'vue';
import { ElMessage, ElUpload, ElProgress } from 'element-plus';
const videoForm = ref({
  storageurl: ''
});
// const CS = ref({
//       'text-align': 'center',  //文本居中
//       'min-width': '250px',   //最小宽度
//       'word-break': 'break-all'  //过长时自动换行
//     });
// const LS = ref( {
//       'color': '#000',
//       'text-align': 'center',
//       'font-weight': '600',
//       'height': '40px',
//       'background-color': 'rgba(255, 97, 2, 0.1)',
//       'min-width': '110px',
//       'word-break': 'keep-all'
//     });
const videoFlag = ref(false);
const videoUploadPercent = ref(0);

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
  return true;
};

// 上传进度显示
const uploadVideoProcess = (event, file, fileList) => {
  console.log(event.percent, file, fileList);
  videoFlag.value = true;
  videoUploadPercent.value = Math.floor(event.percent);
};

// 获取上传图片地址
const handleVideoSuccess = (res, file) => {
  videoFlag.value = false;
  videoUploadPercent.value = 0;
  if (res.status === 200) {
    console.log(res.data);
    videoForm.value.storageurl = res.data;
  } else {
    ElMessage.error('视频上传失败，请重新上传！');
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
      12345
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

    <el-container>
    <el-main>
    <!-- 视频上传区域 -->
    <el-form-item class="video-upload-item" prop="storageurl">
      <el-upload
      class="el-upload"
        action="实际的上传视频的后台地址"
        :show-file-list="false"
        :on-success="handleVideoSuccess"
        :before-upload="beforeUploadVideo"
        :on-progress="uploadVideoProcess"
        :accept="'video/mp4,video/ogg,video/flv,video/avi,video/wmv,video/rmvb'"
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
        <div class="upload-content" v-if="videoForm.storageurl && !videoFlag">
          <video
            v-if="videoForm.storageurl && !videoFlag"
            :src="videoForm.storageurl"
            class="avatar"
          >
            您的浏览器不支持视频播放
          </video>
          <i
            v-else-if="videoForm.storageurl == '' && videoFlag == false"
            class="el-icon-plus avatar-uploader-icon"
          ></i>
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
    <el-descriptions-item label="时间" :label-class-name="label-style">2024年12月21日 09:15:30:00</el-descriptions-item>
    <el-descriptions-item label="帧宽度"  >1920</el-descriptions-item>
    <el-descriptions-item label="帧高度" >1080</el-descriptions-item>
    <el-descriptions-item label="帧速率" >23.98fps</el-descriptions-item>
    <el-descriptions-item label="面部危险等级"
    ><span style="background-color: #E6E8EB">Hello</span></el-descriptions-item>
    <el-descriptions-item label="是否需要停工" ><el-text class="mx-1" type="danger">否</el-text></el-descriptions-item>
  </el-descriptions>
    </el-aside>
    <!-- 左边放置信息的区域 -->
  </el-container>
  </el-container>
  </div>
</template>

<style scoped>
.upload-container {
  display: flex;
  align-items: center;
  gap: 20px; /* 调整左右间距 */
  height: 100%;
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
  height: 15%;
}
.el-aside{
  align-items:center;
  display: flex;
  flex-direction: column;
  align-content: space-between;
  align-items: center;
  justify-content: center;
  height: 70%;
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
</style>