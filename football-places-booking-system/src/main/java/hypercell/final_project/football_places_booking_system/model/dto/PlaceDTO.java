package hypercell.final_project.football_places_booking_system.model.dto;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.PlaceType;

public record PlaceDTO (UUID id, String name, String description, String location, PlaceType placeType, String imageUrl) {}
