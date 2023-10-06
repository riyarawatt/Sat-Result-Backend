package com.task.satresult.repository;

import com.task.satresult.model.SatResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SatResultRepository extends JpaRepository<SatResult, Long> {
    @Query("SELECT sr FROM SatResult sr WHERE sr.sat_score_percentage >= :sat_score_percentage")
    List<SatResult> findBySat_score_percentageGreaterThanEqual(int sat_score_percentage);
    SatResult findByName(String name);
}
