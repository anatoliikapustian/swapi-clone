package com.lk.swapiclone.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikePatternEscaperTest {

    @Test
    void escape_returnsUnchanged_whenNoSpecialCharacters() {
        assertThat(LikePatternEscaper.escape("Tatooine")).isEqualTo("Tatooine");
    }

    @Test
    void escape_escapesPercent() {
        assertThat(LikePatternEscaper.escape("100%")).isEqualTo("100\\%");
    }

    @Test
    void escape_escapesUnderscore() {
        assertThat(LikePatternEscaper.escape("a_b")).isEqualTo("a\\_b");
    }

    @Test
    void escape_escapesBackslash() {
        assertThat(LikePatternEscaper.escape("a\\b")).isEqualTo("a\\\\b");
    }

    @Test
    void escape_escapesBackslashBeforeOtherCharacters_soEscapingIsNotDoubleApplied() {
        assertThat(LikePatternEscaper.escape("100\\%")).isEqualTo("100\\\\\\%");
    }

    @Test
    void escape_returnsEmptyString_whenInputIsEmpty() {
        assertThat(LikePatternEscaper.escape("")).isEqualTo("");
    }

    @Test
    void escape_escapesAllSpecialCharactersTogether() {
        assertThat(LikePatternEscaper.escape("50%_off\\sale")).isEqualTo("50\\%\\_off\\\\sale");
    }
}
