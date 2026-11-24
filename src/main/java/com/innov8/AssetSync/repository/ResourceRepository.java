package com.innov8.AssetSync.repository;

import com.innov8.AssetSync.model.Resource;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByResourceType(Resource.ResourceType resourceType);

    List<Resource> findByResourceTypeAndCategoryAndCapacityGreaterThanEqual(Resource.ResourceType resourceType, String category, Integer capacity);

    List<Resource> findByResourceTypeAndCategoryAndQuantityGreaterThanEqual(Resource.ResourceType resourceType, String category, Integer quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Resource r WHERE r.id = :resourceId")
    Optional<Resource> findByIdForUpdate(@Param("resourceId") Long resourceId);
}
