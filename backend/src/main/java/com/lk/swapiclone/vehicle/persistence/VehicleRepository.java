package com.lk.swapiclone.vehicle.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("""
            SELECT v
            FROM Vehicle v
            WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
                OR LOWER(v.model) LIKE LOWER(CONCAT('%', :search, '%')) ESCAPE '\\'
            """)
    Page<Vehicle> search(@Param("search") String search, Pageable pageable);
}
