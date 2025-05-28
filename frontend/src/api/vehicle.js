import axios from '@/utils/request'

export const getVehicles = (params) => {
  return axios.get('/vehicles', { params })
}

export const getVehicleById = (id) => {
  return axios.get(`/vehicles/${id}`)
}

export const createVehicle = (data) => {
  return axios.post('/vehicles', data)
}

export const updateVehicle = (data) => {
  return axios.put(`/vehicles/${data.id}`, data)
}

export const deleteVehicle = (id) => {
  return axios.delete(`/vehicles/${id}`)
}
