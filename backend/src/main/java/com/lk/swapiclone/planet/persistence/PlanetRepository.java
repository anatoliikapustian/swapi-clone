package com.lk.swapiclone.planet.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long> {

    @Query("""
            SELECT p
            FROM Planet p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
            """)
    Page<Planet> search(@Param("search") String search, Pageable pageable);
}
