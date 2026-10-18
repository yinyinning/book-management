import request from './request'
import type{ Book,PageResult } from '@/models/book'

//查询全部图书： GET /api/book/list
export function listBooks(): Promise<Book[]>{
  return request.get('/book/list') as any
}

// 分页模糊搜索：GET /api/book/search?keyword=&pageNum=&pageSize=
export function searchBooks(keyword: string, pageNum: number, pageSize: number): Promise<PageResult<Book>> {
   return request.get('/book/search', { params: { keyword, pageNum, pageSize } }) as any
}

// 新增图书：POST /api/book
export function addBook(data: Omit<Book, 'id' | 'types'>): Promise<number> {
  return request.post('/book', data) as any
}

  // 修改图书：PUT /api/book
export function updateBook(data: Omit<Book, 'types'>): Promise<boolean> {
  return request.put('/book', data) as any
} 

//删除图书：DELETE /api/book/{id}
export function deleteBook(id: number): Promise<boolean> {
  return request.delete(`/book/${id}`) as any
}