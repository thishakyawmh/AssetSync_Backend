package com.innov8.AssetSync.repository;

import com.innov8.AssetSync.model.LocationRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRequestRepository extends JpaRepository<LocationRequest, Long> {
    List<LocationRequest> findByStatusOrderByPriorityAsc(String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM LocationRequest r WHERE r.id = :requestId")
    Optional<LocationRequest> findByIdForUpdate(@Param("requestId") Long requestId);
}
