package com.bloodconnect.dto;

import java.time.LocalDate;

public class DonorCardDto {

    private Long donorProfileId;
    private Long userId;
    private String fullName;
    private String bloodGroup;
    private String city;
    private String state;
    private boolean isAvailable;
    private LocalDate lastDonationDate;
    private int totalDonations;
    private String email;
    private String mobileNumber;
    private String address;

    public DonorCardDto() {}

    public DonorCardDto(Long donorProfileId, Long userId, String fullName, String bloodGroup,
                        String city, String state, boolean isAvailable, LocalDate lastDonationDate,
                        int totalDonations, String email, String mobileNumber, String address) {
        this.donorProfileId = donorProfileId;
        this.userId = userId;
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.state = state;
        this.isAvailable = isAvailable;
        this.lastDonationDate = lastDonationDate;
        this.totalDonations = totalDonations;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    // Getters and Setters
    public Long getDonorProfileId() {
        return donorProfileId;
    }

    public void setDonorProfileId(Long donorProfileId) {
        this.donorProfileId = donorProfileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
