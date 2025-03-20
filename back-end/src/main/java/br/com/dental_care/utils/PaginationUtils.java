package br.com.dental_care.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

    public static Pageable buildPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortParams[0])));

        if (sortParams.length > 1 && sortParams[1].equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortParams[0])));
        }

        return pageable;
    }
}
