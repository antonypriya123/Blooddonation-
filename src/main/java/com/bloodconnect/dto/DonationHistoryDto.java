package com.bloodconnect.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DonationHistoryDto {

    private Long id;
    private Long donorId;
    private String donorName;
    private Long bloodRequestId;
    private LocalDate donationDate;
    private int unitsDonated;
    private String hospitalName;
    private String remarks;
    private LocalDateTime createdAt;

    public DonationHistoryDto() {}

    public DonationHistoryDto(Long id, Long donorId, String donorName, Long bloodRequestId,
                              LocalDate donationDate, int unitsDonated, String hospitalName,
                              String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodRequestId = bloodRequestId;
        this.donationDate = donationDate;
        this.unitsDonated = unitsDonated;
        this.hospitalName = hospitalName;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public Long getBloodRequestId() {
        return bloodRequestId;
    }

    public void setBloodRequestId(Long bloodRequestId) {
        this.bloodRequestId = bloodRequestId;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public int getUnitsDonated() {
        return unitsDonated;
    }

    public void setUnitsDonated(int unitsDonated) {
        this.unitsDonated = unitsDonated;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
