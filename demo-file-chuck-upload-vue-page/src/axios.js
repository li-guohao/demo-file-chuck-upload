import axios from 'axios'

const api = {
    fileUpload: '/file/data',
    fileUploadChunk: '/file/data/chunk'
}

const request = axios.create({
    // API 请求的默认前缀
    baseURL: process.env.VUE_APP_API_BASE_URL,
    timeout: 60000
})

export function upload (file, onUploadProgress) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
        url: api.fileUpload,
        method: 'put',
        data: formData,
        headers: {
        'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: onUploadProgress
    })
}