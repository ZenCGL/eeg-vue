<script setup>
import { ref } from 'vue';
import { ElMessage, ElUpload, ElProgress } from 'element-plus';

const videoForm = ref({
  storageurl: ''
});

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
    <el-container>
  <el-header><div >放一些信息</div></el-header>
  <!-- <div class="upload-container"> -->
    <el-container>
    <el-main>
    <!-- 视频上传区域 -->
    <el-form-item class="video-upload-item" prop="storageurl" label-position="top">
      <el-upload

        class="avatar-uploader"
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
    <el-aside width="30%"><div class="left-info">这里放信息</div></el-aside>
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
}

.left-info {
  width: 200px; /* 左边信息区域的宽度 */
  background-color: #f0f0f0;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.video-upload-item {
  display: flex;
  justify-content: center;
  width: 100%;
}
.avatar-uploader {
  width: 100%; /* 根据需要设置宽度 */
  height: 100%; /* 自动调整高度 */
}
.avatar-uploader-icon {
  border: 1px dashed #d9d9d9 !important;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9 !important;
  border-radius: 6px !important;
  position: relative !important;
  overflow: hidden !important;
}

.avatar-uploader .el-upload:hover {
  border: 2px dashed #d9d9d9 !important;
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 300px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 178px;
  display: block;
}

.el-upload--drag {
  border: 1px dashed #d9d9d9 !important;
  border-radius: 6px !important;
  position: relative !important;
  overflow: hidden !important;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  height: 300px; 
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.video-text {
  margin-top: 0;
  text-align: center;
  color: #606266;
  font-size: 14px;
  line-height: 1.3;
  min-height: 40px;
}

.el-progress-circle {
  margin-top: 20px;
}
</style>