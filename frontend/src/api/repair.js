import axios from '@/utils/request'

// 获取维修工单列表
export const getRepairs = (params) => {
  return axios.get('/part-repairs', { params })
}

// 创建维修工单
export const createRepair = (data) => {
  return axios.post('/part-repairs', data)
}

// 更新维修工单
export const updateRepair = (id, data) => {
  return axios.put(`/part-repairs/${id}`, data)
}

// 获取工单详情
export const getById = (id) => {
  return axios.get(`/part-repairs/${id}`)
}

// 处理工单
export const processRepair = (id, params) => {
  return axios.put(`/part-repairs/${id}/process`, null, { params })
}

// 根据配件ID查询维修记录
export const listByPartId = (partId, params) => {
  return axios.get(`/part-repairs/by-part/${partId}`, { params })
}
