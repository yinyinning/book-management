import type { SystemStatsResult } from "@/models/stats";
import request from "./request";

//管理员获取系统统计图表： GET /api/system/stats
export function getSystemStats(): Promise<SystemStatsResult> {
    return request.get('/system/stats')
}