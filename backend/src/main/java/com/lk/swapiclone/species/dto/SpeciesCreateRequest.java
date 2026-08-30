package com.lk.swapiclone.species.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SpeciesCreateRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 255) String classification,
    @NotBlank @Size(max = 255) String designation,
    @NotBlank @Size(max = 255) String averageHeight,
    @NotBlank @Size(max = 255) String averageLifespan,
    @NotBlank @Size(max = 255) String eyeColors,
    @NotBlank @Size(max = 255) String hairColors,
    @NotBlank @Size(max = 255) String skinColors,
    @NotBlank @Size(max = 255) String language,
    @Positive Long homeworld
) {}
