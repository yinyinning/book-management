import request from './request'
//登陆成功返回的结构，后端LoginResponse
export interface LoginResult {
  token: string
  id: number
  username: string
  role: number
}

//登录：POST /api/user/login
export function login(username: string, password: string): Promise<LoginResult> {
  return request.post('/user/login', { username, password }) as any
}