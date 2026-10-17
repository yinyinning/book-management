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

//获取验证码：GET /api/captcha
export function getCaptcha(): Promise<CaptchaResult> {
  return request.get('/captcha')
}

//登录：POST /api/user/login
export function login(username: string, password: string, captchaId: string, captchaCode: string): Promise<LoginResult> {
  return request.post('/user/login', { username, password, captchaId, captchaCode })
}

//注册：POST /api/user/register
export function register(username: string, password: string, captchaId: string, captchaCode: string): Promise<void> {
  return request.post('/user/register', { username, password, captchaId, captchaCode })
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