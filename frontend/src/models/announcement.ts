// 一条通告（对应后端 Announcement 实体）
  export interface Announcement {
      id: number
      title: string
      content: string
      // 类型：0=公告，1=申诉
      type: number
      publisherId: number
      publishTime: string
      publisherName: string
  }