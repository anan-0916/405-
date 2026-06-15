import request from './request'

export const submitRequest = (data) => request.post('/borrow-request', data)
export const approveRequest = (id, adminId) => request.post(`/borrow-request/approve/${id}`, { adminId })
export const rejectRequest = (id, adminId, rejectReason) => request.post(`/borrow-request/reject/${id}`, { adminId, rejectReason })
export const cancelRequest = (id, readerId) => request.post(`/borrow-request/cancel/${id}`, { readerId })
export const getPendingRequests = () => request.get('/borrow-request/pending')
export const getAllRequests = () => request.get('/borrow-request/all')
export const getRequestsByReader = (readerId) => request.get(`/borrow-request/reader/${readerId}`)
export const getRequestById = (id) => request.get(`/borrow-request/${id}`)


