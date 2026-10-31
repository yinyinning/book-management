import request from './request'
import type{ Book,BookType,PageResult } from '@/models/book'

//按ID查询图书： GET /api/book/{id}
export function getById(id: number): Promise<Book> {
  return request.get(`/book/${id}`)
}

//查询全部图书： GET /api/book/list
export function listBooks(): Promise<Book[]> {
  return request.get('/book/list') as any
}

//查询最热图书： GET /api/book/hot/{limit}
export function listHotBooks(limit: number): Promise<Book[]> {
  return request.get(`/book/hot/${limit}`)
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

//获取所有图书分类：GET /api/book-type/list
export function getAllTypes(): Promise<BookType[]> {
  return request.get('/book-type/list');
}

//列出某分类下的所有图书：GET /api/book-type/{typeId}/books
export function getBooksByType(typeId: number): Promise<Book[]> {
  return request.get(`/book-type/${typeId}/books`);
}


//列出某图书的所有分类：GET /api/book/{bookId}/types
export function getTypesByBook(bookId: number): Promise<BookType[]> {
  return request.get(`/book/${bookId}/types`);
}

//新增分类：POST /api/book-type/add
export function addBookType(name: string): Promise<boolean> {
  return request.post('/book-type/add', {name})
}

//更改分类名：PUT /api/book-type/update
export function updateBookType(id: number, name: string): Promise<boolean> {
  return request.put('/book-type/update',{ id, name })
}

//删除分类：DELETE /api/book-type/delete/{id}
export function deleteBookType(id: number): Promise<boolean> {
  return request.delete(`/book-type/delete/${id}`)
}

//为某本书额外增加分类：PUT /api/book/add/{bookId}/types
export function addBookTypes(bookId: number,typeIds: number[]): Promise<boolean> {
  return request.put(`/book/add/${bookId}/types`, {typeIds})
}

//为某本书重新设置分类：PUT /api/book/reset/{bookId}/types
export function resetBookTypes(bookId: number,typeIds: number[]): Promise<boolean> {
  return request.put(`/book/reset/${bookId}/types`, {typeIds})
}

//清空某本书的分类： DELETE /api/book/clear/{bookId}/types
export function clearBookTypes(bookId: number): Promise<boolean> {
  return request.delete(`/book/clear/${bookId}/types`)
}