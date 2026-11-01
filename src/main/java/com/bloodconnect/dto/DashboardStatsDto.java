package com.bloodconnect.dto;

public class DashboardStatsDto {

    private long totalUsers;
    private long totalDonors;
    private long totalRequesters;
    private long totalRequests;
    private long pendingRequests;
    private long fulfilledRequests;
    private long acceptedRequests;
    private long cancelledRequests;
    private long availableDonors;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalUsers, long totalDonors, long totalRequesters,
                             long totalRequests, long pendingRequests, long fulfilledRequests,
                             long acceptedRequests, long cancelledRequests, long availableDonors) {
        this.totalUsers = totalUsers;
        this.totalDonors = totalDonors;
        this.totalRequesters = totalRequesters;
        this.totalRequests = totalRequests;
        this.pendingRequests = pendingRequests;
        this.fulfilledRequests = fulfilledRequests;
        this.acceptedRequests = acceptedRequests;
        this.cancelledRequests = cancelledRequests;
        this.availableDonors = availableDonors;
    }

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public long getTotalRequesters() {
        return totalRequesters;
    }

    public void setTotalRequesters(long totalRequesters) {
        this.totalRequesters = totalRequesters;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getFulfilledRequests() {
        return fulfilledRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public long getAcceptedRequests() {
        return acceptedRequests;
    }

    public void setAcceptedRequests(long acceptedRequests) {
        this.acceptedRequests = acceptedRequests;
    }

    public long getCancelledRequests() {
        return cancelledRequests;
    }

    public void setCancelledRequests(long cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    public long getAvailableDonors() {
        return availableDonors;
    }

    public void setAvailableDonors(long availableDonors) {
        this.availableDonors = availableDonors;
    }
}
