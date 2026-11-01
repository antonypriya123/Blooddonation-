package com.bloodconnect.repository;

import com.bloodconnect.entity.DonorProfile;
import com.bloodconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long>, JpaSpecificationExecutor<DonorProfile> {

    Optional<DonorProfile> findByUser(User user);

    Optional<DonorProfile> findByUserId(Long userId);

    @Query("SELECT dp FROM DonorProfile dp JOIN dp.user u " +
           "WHERE u.active = true " +
           "AND (:bloodGroup IS NULL OR :bloodGroup = '' OR dp.bloodGroup = :bloodGroup) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:state IS NULL OR :state = '' OR LOWER(u.state) LIKE LOWER(CONCAT('%', :state, '%'))) " +
           "AND (:isAvailable IS NULL OR dp.isAvailable = :isAvailable) " +
           "ORDER BY dp.isAvailable DESC, dp.totalDonations DESC, dp.updatedAt DESC")
    List<DonorProfile> searchDonors(@Param("bloodGroup") String bloodGroup,
                                    @Param("city") String city,
                                    @Param("state") String state,
                                    @Param("isAvailable") Boolean isAvailable);

    long countByIsAvailableTrue();
}
