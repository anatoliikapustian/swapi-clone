package com.lk.swapiclone.starship.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StarshipRepository extends JpaRepository<Starship, Long> {

    @Query("""
            SELECT s
            FROM Starship s
            WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
                OR LOWER(s.model) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
            """)
    Page<Starship> search(@Param("search") String search, Pageable pageable);
}
