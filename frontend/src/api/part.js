import axios from '@/utils/request'

// 添加根据编码获取配件的方法
export const getPartByCode = (partCode) => {
  return axios.get(`/parts/detail/${encodeURIComponent(partCode)}`)
}

export const getParts = (params) => {
  return axios.get('/parts', { params })
}

export const getPartsByTypes = (params) => {
  return axios.get('/parts/by-types', { params })
}

// 确保已正确定义getPartsByVehicleAndType
export const getPartsByVehicleAndType = (params) => {
  return axios.get('/parts/by-vehicle-and-type', { 
    params: {
      vehicleId: params.vehicleId || null,
      typeId: params.typeId || null
    }
  })
}

export const createPart = (data) => {
  return axios.post('/parts', data)
}

export const updatePart = (data) => {
  return axios.put(`/parts/${data.id}`, data)
}

export const deletePart = (id) => {
  return axios.delete(`/parts/${id}`)
}

export const batchAssociateVehicle = (data) => {
  return axios.post('/parts/batch-associate-vehicle', data)
}

export const replacePart = (data) => {
  return axios.post('/parts/replace', data)
}

// 解绑配件
export const disassociatePart = (id) => {
  return axios.put(`/parts/${id}/disassociate-vehicle`)
}

// 关联配件
export const associatePart = (id, vehicleId, parentId) => {
  return axios.put(`/parts/${id}/associate-vehicle`, null, {
    params: { vehicleId, parentId }
  })
}

// 报废配件
export const scrapPart = (id, remark) => {
  return axios.put(`/parts/${id}/scrap`, null, {
    params: { remark }
  })
}

// 获取配件操作历史
export const getPartHistory = (partId) => {
  return axios.get(`/parts/operations/${partId}/history`)
}