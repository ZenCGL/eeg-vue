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
  if (
    [
      'video/mp4',
      'video/ogg',
      'video/flv',
      'video/avi',
      'video/wmv',
      'video/rmvb'
    ].indexOf(file.type) === -1
  ) {
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
  <el-form-item label="视频上传" prop="storageurl">
    <el-upload
      class="avatar-uploader el-upload--text"
      action="实际的上传视频的后台地址"
      :show-file-list="false"
      :on-success="handleVideoSuccess"
      :before-upload="beforeUploadVideo"
      :on-progress="uploadVideoProcess"
    >
      <video
        v-if="videoForm.storageurl && !videoFlag"
        :src="videoForm.storageurl"
        class="avatar"
        controls
      >
        您的浏览器不支持视频播放
      </video>
      <i
        v-else-if="!videoForm.storageurl && !videoFlag"
        class="el-icon-plus avatar-uploader-icon"
      ></i>
      <el-progress
        v-if="videoFlag"
        type="circle"
        :percentage="videoUploadPercent"
        style="margin-top:30px;"
      ></el-progress>
    </el-upload>
  </el-form-item>
</template>

<style scoped>
.avatar-uploader-icon {
  border: 1px dashed #d9d9d9 !important;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9 !important;
  border-radius: 6px !important;
  position: relative !important;
  overflow: hidden !important;
}
.avatar-uploader .el-upload:hover {
  border: 1px dashed #d9d9d9 !important;
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
  width: 300px;
  height: 178px;
  display: block;
}
</style>