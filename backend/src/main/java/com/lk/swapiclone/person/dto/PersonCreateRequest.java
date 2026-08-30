package com.lk.swapiclone.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonCreateRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 255) String birthYear,
    @NotBlank @Size(max = 255) String eyeColor,
    @NotBlank @Size(max = 255) String gender,
    @NotBlank @Size(max = 255) String hairColor,
    @NotBlank @Size(max = 255) String height,
    @NotBlank @Size(max = 255) String mass,
    @NotBlank @Size(max = 255) String skinColor,
    @Positive Long homeworld,
    List<@NotNull @Positive Long> species,
    List<@NotNull @Positive Long> starships,
    List<@NotNull @Positive Long> vehicles
) {}
