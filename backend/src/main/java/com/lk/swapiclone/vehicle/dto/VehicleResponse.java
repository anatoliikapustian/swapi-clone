package com.lk.swapiclone.vehicle.dto;

import java.util.List;

public record VehicleResponse(
    Long id,
    String name,
    String model,
    String vehicleClass,
    String manufacturer,
    String costInCredits,
    String length,
    String crew,
    String passengers,
    String maxAtmospheringSpeed,
    String cargoCapacity,
    String consumables,
    List<String> pilots,
    List<String> films,
    String url,
    String created,
    String edited
) {}
