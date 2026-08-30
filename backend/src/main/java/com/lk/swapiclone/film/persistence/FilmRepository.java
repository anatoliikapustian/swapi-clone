package com.lk.swapiclone.film.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    @Query("""
            SELECT f
            FROM Film f
            WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
            """)
    Page<Film> search(@Param("search") String search, Pageable pageable);
}
