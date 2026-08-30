package com.lk.swapiclone.planet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlanetCreateRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 255) String diameter,
    @NotBlank @Size(max = 255) String rotationPeriod,
    @NotBlank @Size(max = 255) String orbitalPeriod,
    @NotBlank @Size(max = 255) String gravity,
    @NotBlank @Size(max = 255) String population,
    @NotBlank @Size(max = 255) String climate,
    @NotBlank @Size(max = 255) String terrain,
    @NotBlank @Size(max = 255) String surfaceWater
) {}
