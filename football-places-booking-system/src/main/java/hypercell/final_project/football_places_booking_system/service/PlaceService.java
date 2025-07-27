package hypercell.final_project.football_places_booking_system.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.ResourceNotFoundException;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.dto.PlaceDTO;
import hypercell.final_project.football_places_booking_system.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;


    public Place createPlace(PlaceDTO placeDto) {
        Place place = new Place();
        place.setName(placeDto.getName());
        place.setLocation(placeDto.getLocation());
        place.setImageUrl(placeDto.getImageUrl());
        place.setPlaceType(placeDto.getPlaceType());
        return placeRepository.save(place);
    }


    public Place getPlaceById(UUID id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found with id: " + id));
    }


    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }


    public Place updatePlace(UUID id, Place updatedPlace) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + id));
        existingPlace.setName(updatedPlace.getName());
        existingPlace.setLocation(updatedPlace.getLocation());
        existingPlace.setPlaceType(updatedPlace.getPlaceType());
        existingPlace.setImageUrl(updatedPlace.getImageUrl());

        return placeRepository.save(existingPlace);
    }


    public void deletePlace(UUID id) {
        placeRepository.deleteById(id);
    }
}
