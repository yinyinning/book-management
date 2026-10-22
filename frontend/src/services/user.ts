import request from './request'

//登陆成功返回的结构，后端LoginResponse
export interface LoginResult {
  token: string
  id: number
  username: string
  role: number
}

//获取验证码返回的结构，后端CaptchaResponse
export interface CaptchaResult {
  captchaId: string
  image: string
}

//用户结果，后端UserResponse
export interface UserResult {
  id: number
  username: string
  role: number
  status: number
  createTime: string
  updateTime: string
  email: string
}

//获取验证码：GET /api/captcha
export function getCaptcha(): Promise<CaptchaResult> {
  return request.get('/captcha')
}

//登录：POST /api/user/login
export function login(username: string, password: string, captchaId: string, captchaCode: string): Promise<LoginResult> {
  return request.post('/user/login', { username, password, captchaId, captchaCode })
}

//注册：POST /api/user/register
export function register(username: string, password: string, captchaId: string, captchaCode: string, email: string): Promise<void> {
  return request.post('/user/register', { username, password, captchaId, captchaCode, email })
}

//登出：PUT /api/user/logout
export function logout(): Promise<void> {
  return request.put('/user/logout')
}

//更新用户名：PUT /api/user/name
export function updateUsername(username: string): Promise<boolean> {
  return request.put('/user/name', { username })
}

//更新密码：PUT /api/user/password
export function updatePassword(oldPassword: string, newPassword: string): Promise<boolean> {
  return request.put('/user/password', { oldPassword, newPassword })
}

//管理员按id查询用户： GET /api/user/{id}
export function getUserById(id: number): Promise<UserResult> {
  return request.get(`/user/${id}`)
}

//管理员按id删除用户： DELETE /api/user/{id}
export function deleteUserById(id: number): Promise<boolean> {
  return request.delete(`/user/${id}`)
}

//管理员按id冻结或解冻用户： PUT /api/user/{id}/status
export function updateUserStatus(id: number, status: number): Promise<boolean> {
  return request.put(`/user/${id}/status`, {status})
}

//管理员查询所有用户： GET /api/user/list
export function listUsers():Promise<UserResult[]> {
  return request.get('/user/list')
}

//管理员重置密码： PUT /api/user/help/password/{id}
export function resetUserPassword(id: number): Promise<boolean> {
  return request.put(`/user/help/password/${id}`)
}

//超级管理员新建管理员： POST /api/user/register/admin
export function registerAdmin(username: string, password: string, captchaId: string, captchaCode: string): Promise<void> {
  return request.post('/user/register/admin', {username, password, captchaId, captchaCode})
}

//请求向特定邮箱发送邮件： 