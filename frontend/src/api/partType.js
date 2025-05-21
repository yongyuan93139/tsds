import axios from '@/utils/request'

export const getPartTypes = (params) => {
  return axios.get('/part-types', { params })
}

export const createPartType = (data) => {
  return axios.post('/part-types', data)
}

export const updatePartType = (data) => {
  return axios.put(`/part-types/${data.id}`, data)
}

export const deletePartType = (id) => {
  return axios.delete(`/part-types/${id}`)
}