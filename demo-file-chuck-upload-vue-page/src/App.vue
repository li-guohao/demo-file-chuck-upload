<template>
  <div class="root">
    <FileUpload ref="filePondUploadRef" :enableChunkUploads="true" :enableChunkForce="true" :chunkSize="50000000" :uploadHandler="uploadHandler" />
  </div>
</template>

<script>
import FileUpload from './components/FileUpload.vue'
import { upload } from './axios'

export default {
  name: 'App',
  components: { FileUpload },
  data () {
    return {
      uploadHandler: (file, onUploadProgress) => upload(file, onUploadProgress)
    }
  }
}
</script>

<style>
.root {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  /* margin-top: 60px; */
  margin: 60px 10%;
}
</style>
