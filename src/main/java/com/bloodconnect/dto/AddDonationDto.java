package com.bloodconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AddDonationDto {

    @NotNull(message = "Donation date is required")
    private LocalDate donationDate;

    @NotNull(message = "Units donated is required")
    @Min(value = 1, message = "At least 1 unit must be donated")
    private Integer unitsDonated;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    private String remarks;

    private Long bloodRequestId;

    public AddDonationDto() {}

    // Getters and Setters
    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public Integer getUnitsDonated() {
        return unitsDonated;
    }

    public void setUnitsDonated(Integer unitsDonated) {
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

    public Long getBloodRequestId() {
        return bloodRequestId;
    }

    public void setBloodRequestId(Long bloodRequestId) {
        this.bloodRequestId = bloodRequestId;
    }
}
