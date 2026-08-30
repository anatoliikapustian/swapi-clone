package com.lk.swapiclone.common;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityIdResolverTest {

    @Test
    void resolveId_returnsNull_whenIdIsNull() {
        String result = EntityIdResolver.resolveId(null, id -> Optional.of("entity"), "Planet");

        assertThat(result).isNull();
    }

    @Test
    void resolveId_returnsEntity_whenFound() {
        String result = EntityIdResolver.resolveId(1L, id -> Optional.of("entity-" + id), "Planet");

        assertThat(result).isEqualTo("entity-1");
    }

    @Test
    void resolveId_throwsNotFound_whenEntityMissing() {
        assertThatThrownBy(() -> EntityIdResolver.resolveId(404L, id -> Optional.empty(), "Planet"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404")
            .hasMessageContaining("Planet not found: 404");
    }

    @Test
    void resolveIds_returnsEmptySet_whenIdsIsNull() {
        Set<String> result = EntityIdResolver.resolveIds(null, ids -> List.of(), s -> 0L, "Planet");

        assertThat(result).isEmpty();
    }

    @Test
    void resolveIds_returnsEmptySet_whenIdsIsEmpty() {
        Set<String> result = EntityIdResolver.resolveIds(List.of(), ids -> List.of(), s -> 0L, "Planet");

        assertThat(result).isEmpty();
    }

    @Test
    void resolveIds_returnsEntities_whenAllFound() {
        Set<String> result = EntityIdResolver.resolveIds(
            List.of(1L, 2L),
            ids -> ids.stream().map(id -> "entity-" + id).toList(),
            entity -> Long.valueOf(entity.substring("entity-".length())),
            "Planet");

        assertThat(result).containsExactlyInAnyOrder("entity-1", "entity-2");
    }

    @Test
    void resolveIds_deduplicatesRequestedIds() {
        Set<String> result = EntityIdResolver.resolveIds(
            List.of(1L, 1L),
            ids -> List.of("entity-1"),
            entity -> Long.valueOf(entity.substring("entity-".length())),
            "Planet");

        assertThat(result).containsExactly("entity-1");
    }

    @Test
    void resolveIds_throwsNotFound_listingMissingIds() {
        assertThatThrownBy(() -> EntityIdResolver.resolveIds(
            List.of(1L, 2L, 3L),
            ids -> List.of("entity-1"),
            entity -> Long.valueOf(entity.substring("entity-".length())),
            "Planet"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404")
            .hasMessageContaining("Planet ids not found")
            .hasMessageContaining("2")
            .hasMessageContaining("3");
    }
}
