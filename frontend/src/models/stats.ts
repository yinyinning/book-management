//饼图元素,后端StatItem
export interface StatItem {
    name: string
    value: number
}

//折线图节点,后端TrendPoint
export interface TrendPoint {
    date: string
    count: number
}

//用户统计图表,后端UserStatsResponse
export interface UserStatsResult {
    totalUsers: number
    roleDistribution: StatItem[]
    statusDistribution: StatItem[]
    registrationTrend: TrendPoint[]
}

//系统统计图表,后端SystemStatsResponse
export interface SystemStatsResult {
    //单位：字节
    heapUsed: number

    //单位：字节
    heapMax: number

    //单位：毫秒
    uptimeMillis: number

    threadCount: number
    cpuCores: number
    javaVersion: string
    osName: string
}