package com.bloodconnect.dto;

import com.bloodconnect.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusDto {

    @NotNull(message = "Status is required")
    private RequestStatus status;

    private Long assignedDonorId;

    public UpdateStatusDto() {}

    public UpdateStatusDto(RequestStatus status) {
        this.status = status;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getAssignedDonorId() {
        return assignedDonorId;
    }

    public void setAssignedDonorId(Long assignedDonorId) {
        this.assignedDonorId = assignedDonorId;
    }
}
