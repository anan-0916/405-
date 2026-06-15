import request from './request'

export const borrowBook = (bookId, readerId) => request.post('/borrow', { bookId, readerId })
export const returnBook = (id) => request.post(`/borrow/return/${id}`)
export const getBorrowRecordsByReader = (readerId) => request.get(`/borrow/reader/${readerId}`)
export const getAllBorrowRecords = () => request.get('/borrow/all')

