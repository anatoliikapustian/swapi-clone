package com.lk.swapiclone.species.dto;

import java.util.List;

public record SpeciesResponse(
    Long id,
    String name,
    String classification,
    String designation,
    String averageHeight,
    String averageLifespan,
    String eyeColors,
    String hairColors,
    String skinColors,
    String language,
    String homeworld,
    List<String> people,
    List<String> films,
    String url,
    String created,
    String edited
) {}
