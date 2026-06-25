package com.innov8.AssetSync.repository;

import com.innov8.AssetSync.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    @Query("SELECT COUNT(a) FROM Allocation a WHERE a.resourceId = :resourceId AND ((a.startTime < :endTime) AND (a.endTime > :startTime))")
    int countOverlaps(@Param("resourceId") Long resourceId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
