package com.teamdev.bookmanagement.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatItem {
    //饼图元素

    //名称
    private String name;

    //数值
    private Long value;
}
