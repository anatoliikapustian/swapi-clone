package com.lk.swapiclone.starship.dto;

import java.util.List;

public record StarshipResponse(
    Long id,
    String name,
    String model,
    String starshipClass,
    String manufacturer,
    String costInCredits,
    String length,
    String crew,
    String passengers,
    String maxAtmospheringSpeed,
    String hyperdriveRating,
    String megalightPerHour,
    String cargoCapacity,
    String consumables,
    List<String> pilots,
    List<String> films,
    String url,
    String created,
    String edited
) {}
