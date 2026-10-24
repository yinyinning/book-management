package com.teamdev.bookmanagement.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendPoint {
    //折线图节点

    //日期
    private String date;

    //数量
    private Long count;
}
