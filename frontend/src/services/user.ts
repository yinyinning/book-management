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