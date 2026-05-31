package com.fasttravel.backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignRequest {
    private UUID vehicleId;
    private UUID driverId;
}