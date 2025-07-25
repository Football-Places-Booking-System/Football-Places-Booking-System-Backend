package hypercell.final_project.football_places_booking_system.model.dto;

import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.enums.PlaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto{


    @NotBlank(message = "Place name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Place type is required")
    private PlaceType placeType;

    private String imageUrl;

}
