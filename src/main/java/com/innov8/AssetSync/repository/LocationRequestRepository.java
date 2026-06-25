package com.innov8.AssetSync.repository;

import com.innov8.AssetSync.model.LocationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRequestRepository extends JpaRepository<LocationRequest, Long> {
    List<LocationRequest> findByStatusOrderByPriorityAsc(String status);
}
