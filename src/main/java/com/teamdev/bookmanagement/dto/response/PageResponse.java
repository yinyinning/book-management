package com.teamdev.bookmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> records;
    private long total;
    private long current;
    private long size;
    public static <T> PageResponse<T> of(long total,long current,long size,List<T> records){
        return PageResponse.<T>builder().total(total).current(current).size(size).records(records).build();
    }
}
