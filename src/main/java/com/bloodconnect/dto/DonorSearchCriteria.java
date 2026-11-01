package com.bloodconnect.dto;

public class DonorSearchCriteria {

    private String bloodGroup;
    private String city;
    private String state;
    private Boolean isAvailable;

    public DonorSearchCriteria() {}

    public DonorSearchCriteria(String bloodGroup, String city, String state, Boolean isAvailable) {
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.state = state;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
