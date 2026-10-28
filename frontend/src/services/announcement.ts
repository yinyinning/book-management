import request from './request'
  import type { Announcement } from '@/models/announcement'

  // 查通告列表：GET /api/announcement/list?type=0
  export function listAnnouncements(type: number): Promise<Announcement[]> {
    return request.get('/announcement/list', { params: { type } }) as any
  }

  // 发布公告/申诉：POST /api/announcement/add，body 传 {title, content, type}
  export function addAnnouncement(data: { title: string; content: string; type: number }): Promise<boolean> {
    return request.post('/announcement/add', data) as any
  }

  // 删除通告：DELETE /api/announcement/delete/{id}
  export function deleteAnnouncement(id: number): Promise<boolean> {
    return request.delete(`/announcement/delete/${id}`) as any
  }