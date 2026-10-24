package com.teamdev.bookmanagement.dto.response;

import com.teamdev.bookmanagement.common.StatItem;
import com.teamdev.bookmanagement.common.TrendPoint;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserStatsResponse {
    //用户总数
    private Long totalUsers;

    //角色分布
    private List<StatItem> roleDistribution;

    //状态分布
    private List<StatItem> statusDistribution;

    //注册趋势
    private List<TrendPoint> registrationTrend;
}
