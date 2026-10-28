<template>
    <div>
        <div class="kpicard-container">
            <el-row :gutter="15">
                <el-col :span="6">
                    <el-card>
                        <el-statistic title="线程数" :value="systemStats?.threadCount" />
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <el-statistic title="CPU核数" :value="systemStats?.cpuCores" />
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <el-statistic title="堆内存" :value="systemStats?.heapMax" :formatter="formatHeap" />
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <el-statistic title="运行时长" :value="systemStats?.uptimeMillis" :formatter="formatUpdate" />
                    </el-card>
                </el-col>
            </el-row>
            <el-row :gutter="15">
                <el-col :span="6">
                    <el-card>
                        <el-statistic title="用户总数" :value="userStats?.totalUsers" />
                    </el-card>
                </el-col>
            </el-row>
        </div>
        <div class="userstats-container">
            <v-chart :option="roleOption" autoresize />
            <v-chart :option="statusOption" autoresize />
            <v-chart :option="registrationOption" autoresize />
        </div>
    </div>
</template>

<script setup lang="ts">
import type{ SystemStatsResult, UserStatsResult } from '@/models/stats';
import { getSystemStats } from '@/services/system';
import { getUserStats } from '@/services/user';
import { computed, onMounted, ref } from 'vue'

const systemStats=ref<SystemStatsResult | null>(null)
const userStats=ref<UserStatsResult | null>(null)
const roleOption=computed(()=>({
    title: { text: '角色分布' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
        type: 'pie',
        radius: '60%',
        data: userStats.value?.roleDistribution ?? [],
    }],
}))
const statusOption=computed(()=>({
    title: { text: '状态分布' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
        type: 'pie',
        radius: '60%',
        data: userStats.value?.statusDistribution ?? [],
    }]
}))
const trendDates=computed(()=>userStats.value?.registrationTrend.map(t=>t.date) ?? [])
const trendCounts=computed(()=>userStats.value?.registrationTrend.map(t=>t.count) ?? [])
const registrationOption=computed(()=>({
    title: { text: '近30天注册趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trendDates.value },
    yAxis: { type: 'value' },
    series: [{
        type: 'line',
        data: trendCounts.value,
        smooth: true,
    }]
}))

async function fetch() {
    systemStats.value=await getSystemStats()
    userStats.value=await getUserStats()
}

function formatUpdate(v: number): string {
    const m=Math.floor(v/1000/60%60)
    const h=Math.floor(v/1000/60/60%24)
    const d=Math.floor(v/1000/60/60/24)
    return `${d}天 ${h}时 ${m}分`
}

function formatHeap(v: number): string {
    const maxMb=Math.floor(v/1024/1024*100)/100
    const usedMb=Math.floor((systemStats?.value?.heapUsed??0)/1024/1024*100)/100
    return `${usedMb}MB / ${maxMb}MB`
}

onMounted(()=>{
    fetch()
})
</script>

<style scoped>
.kpicard-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.userstats-container {
    display: flex;
    height: 300px;
}
</style>