// 借阅记录（对应后端 BorrowRecordResponse）
export interface BorrowRecord{
    id:number
    username:string
    barcode:string
    title:string
    borrowTime:string
    dueTime:string
    returnTime:string
    statusText:string
}