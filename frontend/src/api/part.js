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
