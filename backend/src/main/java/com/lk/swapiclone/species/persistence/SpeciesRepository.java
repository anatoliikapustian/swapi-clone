package com.lk.swapiclone.species.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

    @Query("""
            SELECT s
            FROM Species s
            WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
            """)
    Page<Species> search(@Param("search") String search, Pageable pageable);
}
