<template>
  <div class="root">
    <FileUpload ref="filePondUploadRef" :uploadHandler="uploadHandler" />
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
      uploadHandler: (file, options) => upload(file, options)
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
