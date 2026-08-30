package com.lk.swapiclone.validation;

import com.lk.swapiclone.exception.BadRequestException;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortValidator {

    private SortValidator() {
    }

    public static void validate(Sort sort, Set<String> allowedProperties) {
        for (Sort.Order order : sort) {
            if (!allowedProperties.contains(order.getProperty())) {
                throw new BadRequestException("Cannot sort by '" + order.getProperty() + "'");
            }
        }
    }
}
