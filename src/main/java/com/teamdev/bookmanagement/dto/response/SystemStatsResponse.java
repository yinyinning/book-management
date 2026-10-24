package com.teamdev.bookmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatsResponse {
    //单位：字节
    private Long heapUsed;

    //单位：字节
    private Long heapMax;

    //单位：毫秒
    private Long uptimeMillis;

    private Integer threadCount;
    private Integer cpuCores;
    private String javaVersion;
    private String osName;
}
