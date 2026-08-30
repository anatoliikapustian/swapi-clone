package com.lk.swapiclone.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Resolves foreign-key IDs supplied in create requests to their referenced entities,
 * raising a 404 {@link ResponseStatusException} when a reference cannot be found.
 */
public class EntityIdResolver {

    private EntityIdResolver() {
    }

    /**
     * Resolves a single optional ID to its entity.
     *
     * @param id           the ID to resolve, or {@code null} if the reference is absent
     * @param finder       looks up an entity by ID
     * @param resourceName name of the referenced resource, used in the 404 error message
     * @return the resolved entity, or {@code null} if {@code id} was {@code null}
     * @throws ResponseStatusException with status 404 if no entity is found for {@code id}
     */
    public static <T> T resolveId(Long id, Function<Long, Optional<T>> finder, String resourceName) {
        if (id == null) {
            return null;
        }
        return finder.apply(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, resourceName + " not found: " + id));
    }

    /**
     * Resolves a list of IDs to their entities, requiring every ID to match an existing entity.
     *
     * @param ids          the IDs to resolve; {@code null} or empty yields an empty set
     * @param finder       looks up entities for a batch of IDs
     * @param idExtractor  extracts the ID from a found entity, used to detect missing references
     * @param resourceName name of the referenced resource, used in the 404 error message
     * @return the resolved entities
     * @throws ResponseStatusException with status 404 listing any IDs that could not be found
     */
    public static <T> Set<T> resolveIds(List<Long> ids, Function<List<Long>, List<T>> finder,
                                         Function<T, Long> idExtractor, String resourceName) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> requested = new HashSet<>(ids);
        List<T> found = finder.apply(ids);
        if (found.size() != requested.size()) {
            Set<Long> foundIds = new HashSet<>();
            for (T entity : found) {
                foundIds.add(idExtractor.apply(entity));
            }
            requested.removeAll(foundIds);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                resourceName + " ids not found: " + requested);
        }
        return new HashSet<>(found);
    }
}
