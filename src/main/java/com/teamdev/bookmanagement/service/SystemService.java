package com.teamdev.bookmanagement.service;

import com.teamdev.bookmanagement.dto.response.SystemStatsResponse;
import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

@Service
public class SystemService {
    public SystemStatsResponse getSystemStats() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return SystemStatsResponse.builder()
                .heapUsed(memoryMXBean.getHeapMemoryUsage().getUsed())
                .heapMax(memoryMXBean.getHeapMemoryUsage().getMax())
                .uptimeMillis(runtimeMXBean.getUptime())
                .threadCount(threadMXBean.getThreadCount())
                .cpuCores(Runtime.getRuntime().availableProcessors())
                .javaVersion(System.getProperty("java.version"))
                .osName(System.getProperty("os.name")).build();
    }
}
