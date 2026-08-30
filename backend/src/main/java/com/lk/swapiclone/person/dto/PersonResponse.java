package com.lk.swapiclone.person.dto;

import java.util.List;

public record PersonResponse(
    Long id,
    String name,
    String birthYear,
    String eyeColor,
    String gender,
    String hairColor,
    String height,
    String mass,
    String skinColor,
    String homeworld,
    List<String> films,
    List<String> species,
    List<String> starships,
    List<String> vehicles,
    String url,
    String created,
    String edited
) {}
