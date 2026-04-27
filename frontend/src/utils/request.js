import axios from 'axios'

const request = axios.create({
  baseURL: 'http://127.0.0.1:8080/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      console.error(res.msg)
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    console.error('网络错误:', error.message)
    return Promise.reject(error)
  }
)

export default request