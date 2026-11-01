package com.bloodconnect.repository;

import com.bloodconnect.entity.BloodRequest;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.UrgencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long>, JpaSpecificationExecutor<BloodRequest> {

    List<BloodRequest> findByRequesterOrderByCreatedAtDesc(User requester);

    List<BloodRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    List<BloodRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<BloodRequest> findAllByOrderByCreatedAtDesc();

    List<BloodRequest> findTop6ByStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    @Query("SELECT br FROM BloodRequest br " +
           "WHERE (:status IS NULL OR br.status = :status) " +
           "AND (:bloodGroup IS NULL OR :bloodGroup = '' OR br.bloodGroup = :bloodGroup) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(br.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:urgency IS NULL OR br.urgencyLevel = :urgency) " +
           "ORDER BY CASE br.urgencyLevel " +
           "  WHEN com.bloodconnect.enums.UrgencyLevel.CRITICAL THEN 1 " +
           "  WHEN com.bloodconnect.enums.UrgencyLevel.HIGH THEN 2 " +
           "  WHEN com.bloodconnect.enums.UrgencyLevel.MEDIUM THEN 3 " +
           "  WHEN com.bloodconnect.enums.UrgencyLevel.LOW THEN 4 ELSE 5 END, " +
           "br.createdAt DESC")
    List<BloodRequest> searchRequests(@Param("status") RequestStatus status,
                                     @Param("bloodGroup") String bloodGroup,
                                     @Param("city") String city,
                                     @Param("urgency") UrgencyLevel urgency);
}
