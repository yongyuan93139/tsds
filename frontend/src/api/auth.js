import axios from '@/utils/request'

export const login = (data) => {
  const params = new URLSearchParams()
  params.append('username', data.username)
  params.append('password', data.password)
  
  return axios({
    method: 'post',
    url: '/auth/login',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    transformResponse: [(data) => {
      try {
        return JSON.parse(data)
      } catch (e) {
        return data
      }
    }]
  })
}

export const logout = () => {
  return axios.post('/auth/logout')
}

export const getCurrentUser = () => {
  return axios.get('/auth/current')
}