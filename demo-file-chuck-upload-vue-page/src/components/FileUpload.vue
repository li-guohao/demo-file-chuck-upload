<template>
    <div>
      <file-pond
        ref="pond"
        :accepted-file-types="accepts"
        :allow-multiple="multiple"
        :allowImagePreview="true"
        :allowRevert="false"
        :files="fileList"
        :label-idle="label"
        :maxFiles="20"
        :maxParallelUploads="5"
        :name="name"
        :server="server"
        :chunkUploads="enableChunkUploads"
        :chunkSize="chunkSize"
        :chunkForce="enableChunkForce"
        fileValidateTypeLabelExpectedTypes="请选择 {lastType} 格式的文件"
        labelFileProcessing="上传中"
        labelFileProcessingAborted="取消上传"
        labelFileProcessingComplete="上传完成"
        labelFileProcessingError="上传错误"
        labelFileTypeNotAllowed="不支持当前文件格式"
        labelTapToCancel="点击取消"
        labelTapToRetry="点击重试"
        @init="handleFilePondInit" />
    </div>
  </template>
  <script>
  import vueFilePond from 'vue-filepond'
  import 'filepond/dist/filepond.min.css'
  
  // Plugins
  import FilePondPluginImageExifOrientation from 'filepond-plugin-image-exif-orientation'
  import FilePondPluginImagePreview from 'filepond-plugin-image-preview'
  import 'filepond-plugin-image-preview/dist/filepond-plugin-image-preview.min.css'
  import FilePondPluginFileValidateType from 'filepond-plugin-file-validate-type'
  
  // Create component and register plugins
  const FilePond = vueFilePond(
    FilePondPluginImageExifOrientation,
    FilePondPluginImagePreview,
    FilePondPluginFileValidateType)
  export default {
    name: 'FilePondUpload',
    components: {
      FilePond
    },
    props: {
      name: {
        type: String,
        required: false,
        default: 'file'
      },
      field: {
        type: String,
        required: false,
        default: ''
      },
      multiple: {
        type: Boolean,
        required: false,
        default: true
      },
      accepts: {
        type: Array,
        required: false,
        default: () => {
          return null
        }
      },
      label: {
        type: String,
        required: false,
        default: '点击选择文件或将文件拖拽到此处'
      },
      uploadHandler: {
        type: Function,
        required: true
      },
      // 是否开启分片上传
      enableChunkUploads: {
        type: Boolean,
        required: false,
        default: false
      },
      // 是否强制分片上传
      enableChunkForce: {
        type: Boolean,
        required: false,
        default: false
      },
      // 分片上传单片字节大小，默认 50MB
      chunkSize: {
        type: Number,
        required: false,
        default: 50000000
      }

    },
    data: function () {
      return {
        server: {
        //   process: (fieldName, file, metadata, load, error, progress, abort, transfer, options) => {
        //     console.log('fieldName', fieldName)
        //     console.log('file', file)
        //     console.log('metadata', metadata)
        //     console.log('load', load)
        //     console.log('error', error)
        //     console.log('progress', progress)
        //     console.log('abort', abort)
        //     console.log('transfer', transfer)
        //     console.log('options', options)
        //     this.uploadHandler(
        //       file,
        //       {
        //         onUploadProgress: progressEvent => {
        //           if (progressEvent.total > 0) {
        //             progress(progressEvent.lengthComputable, progressEvent.loaded, progressEvent.total)
        //           }
        //         }
        //       }
        //     )
        //     .then(response => {
        //         load(response)
        //         console.log('[succss] Uploaded successfully, info: ', response)
        //         this.$emit('success', response, file)
        //         })
        //         .catch(failure => {
        //         console.log('[error] Failed to upload file, info: ', failure)
        //         this.$emit('failure', failure, file)
        //         error()
        //     })
        //     return {
        //       abort: () => {
        //         abort()
        //         console.log('[succss] Upload operation aborted by the user')
        //         // source.cancel('Upload operation canceled by the user.')
        //       }
        //     }
        //   }
            url: 'http://localhost:9090/',
            process: './file/process',
            revert: './file/revert',
            restore: './file/restore/',
            load: './file/load/',
            fetch: './file/fetch/',
            patch: './file/patch/'
        },
        fileList: []
      }
    },
    methods: {
      handleFilePondInit () {
        // console.log('FilePond has initialized')
      },
      handleClearFileList () {
        this.$refs.pond.removeFiles()
      }
    }
  }
  </script>
  