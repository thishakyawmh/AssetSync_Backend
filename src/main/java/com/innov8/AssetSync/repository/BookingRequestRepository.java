package com.innov8.AssetSync.repository;

import com.innov8.AssetSync.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
}
