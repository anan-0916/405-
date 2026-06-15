import request from './request'

export const getReaders = () => request.get('/readers')
export const getReaderById = (id) => request.get(`/readers/${id}`)
export const getReaderByReaderId = (readerId) => request.get(`/readers/readerId/${readerId}`)
export const createReader = (data) => request.post('/readers', data)
export const updateReader = (id, data) => request.put(`/readers/${id}`, data)
export const deleteReader = (id) => request.delete(`/readers/${id}`)

