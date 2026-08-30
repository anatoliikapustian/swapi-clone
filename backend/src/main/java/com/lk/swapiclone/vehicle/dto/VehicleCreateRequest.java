package com.lk.swapiclone.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehicleCreateRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 255) String model,
    @NotBlank @Size(max = 255) String vehicleClass,
    @NotBlank @Size(max = 255) String manufacturer,
    @NotBlank @Size(max = 255) String costInCredits,
    @NotBlank @Size(max = 255) String length,
    @NotBlank @Size(max = 255) String crew,
    @NotBlank @Size(max = 255) String passengers,
    @NotBlank @Size(max = 255) String maxAtmospheringSpeed,
    @NotBlank @Size(max = 255) String cargoCapacity,
    @NotBlank @Size(max = 255) String consumables
) {}
