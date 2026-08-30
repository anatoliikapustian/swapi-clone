package com.lk.swapiclone.planet.dto;

import java.util.List;

public record PlanetResponse(
    Long id,
    String name,
    String diameter,
    String rotationPeriod,
    String orbitalPeriod,
    String gravity,
    String population,
    String climate,
    String terrain,
    String surfaceWater,
    List<String> residents,
    List<String> films,
    String url,
    String created,
    String edited
) {}
