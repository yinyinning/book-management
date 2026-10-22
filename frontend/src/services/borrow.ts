import request from './request'
import type{ BorrowRecord  } from '@/models/borrow'
// 借书：POST /api/borrow，body 传 {bookCopyId}
export function borrow(bookCopyId:number): Promise<void>{
  return request.post('/borrow',{ bookCopyId }) as any
}
// 还书：PUT /api/borrow/return，body 传 {bookCopyId}
export function returnBook(bookCopyId:number): Promise<void> {
   return request.put('/borrow/return',{ bookCopyId }) as any
}

// 管理员帮还：PUT /api/borrow/help/return，body 传 {bookCopyId}
export function helpReturn(bookCopyId:number): Promise<void> {
  return request.put('/borrow/help/return',{ bookCopyId }) as any
}

// 查询我的借阅记录：GET /api/borrow/my
export function listMyRecord(): Promise<BorrowRecord[]> {
  return request.get('/borrow/my') as any
} 
// 管理员查询某用户借阅记录：GET /api/borrow/{id}
export function listUserRecord(id:number): Promise<BorrowRecord[]> {
  return request.get(`/borrow/${id}`) as any
}
// 管理员查询所有借阅记录：GET /api/borrow/all
export function listAllRecord(): Promise<BorrowRecord[]> {
  return request.get('/borrow/all') as any
}