package com.lari.finance.api.infrastructure.web.dto;

import java.util.List;

public record PagedResponse<T>(
    List<T> content,
    PageMetadata metadata
) {
    public record PageMetadata(
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean first,
        boolean last
    ) {}

    public static <T> PagedResponse<T> of(List<T> content, long totalElements, int totalPages, int page, int size) {
        return new PagedResponse<>(content, new PageMetadata(
            totalElements,
            totalPages,
            page,
            size,
            page == 0,
            totalPages == 0 || page >= totalPages - 1
        ));
    }
}
