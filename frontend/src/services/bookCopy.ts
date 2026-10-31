import request from './request'
import type { BookCopy } from '@/models/bookCopy'

//查询全部图书副本： GET /api/book-copy/list
export function listBookCopies(): Promise<BookCopy[]>{
  return request.get('/book-copy/list') as any
}

//根据图书ID查询副本列表： GET /api/book-copy/book/{bookId}
export function listByBookId(bookId: number): Promise<BookCopy[]> {
  return request.get(`/book-copy/book/${bookId}`)
}

// 新增图书副本：POST /api/book-copy
export function addBookCopy(data: Omit<BookCopy, 'id' | 'status'>): Promise<boolean> {
  return request.post('/book-copy', data) as any
}

// 修改图书副本：PUT /api/book-copy
export function updateBookCopy(data: Partial<Omit<BookCopy,'id'>>&Pick<BookCopy,'id'>): Promise<boolean> {
  return request.put('/book-copy', data) as any
}

//删除图书副本：DELETE /api/book-copy/{id}
export function deleteBookCopy(id: number): Promise<boolean> {
  return request.delete(`/book-copy/${id}`) as any
}