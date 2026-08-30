package com.lk.swapiclone.validation;

import com.lk.swapiclone.exception.BadRequestException;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortValidatorTest {

    @Test
    void validate_doesNotThrow_whenSortIsEmpty() {
        assertThatCode(() -> SortValidator.validate(Sort.unsorted(), Set.of("name")))
            .doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenAllPropertiesAreAllowed() {
        Sort sort = Sort.by("name").and(Sort.by("id"));

        assertThatCode(() -> SortValidator.validate(sort, Set.of("name", "id")))
            .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBadRequest_whenPropertyIsNotAllowed() {
        Sort sort = Sort.by("password");

        assertThatThrownBy(() -> SortValidator.validate(sort, Set.of("name")))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Cannot sort by 'password'");
    }

    @Test
    void validate_throwsBadRequest_onFirstDisallowedProperty_whenMixedWithAllowed() {
        Sort sort = Sort.by("name").and(Sort.by("password"));

        assertThatThrownBy(() -> SortValidator.validate(sort, Set.of("name")))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Cannot sort by 'password'");
    }
}
