// 图书分类（对应后端 BookType）
export interface BookType{
    id:number
    name:string
}
// 一本书（对应后端 BookResponse）
export interface Book{
    id:number
    isbn:string
    title:string
    author:string
    publisher:string
    types:BookType[]
}
// 分页结果（对应后端 PageResponse<T>）
export interface PageResult<T>{
    records:T[]
    total:number
    current:number
    size:number
}
