package com.heng.cms.concessionservice.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
	
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    public <V> PageResponse(List<T> content, Page<V> page){
        this.content = content;
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }

}
