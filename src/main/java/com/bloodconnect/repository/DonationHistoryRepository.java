package com.bloodconnect.repository;

import com.bloodconnect.entity.DonationHistory;
import com.bloodconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {

    List<DonationHistory> findByDonorOrderByDonationDateDesc(User donor);

    List<DonationHistory> findByDonorIdOrderByDonationDateDesc(Long donorId);

    long countByDonor(User donor);
}
