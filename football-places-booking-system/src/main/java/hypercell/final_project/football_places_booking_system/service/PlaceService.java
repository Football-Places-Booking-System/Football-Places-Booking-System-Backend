package hypercell.final_project.football_places_booking_system.service;

import hypercell.final_project.football_places_booking_system.exception.ResourceNotFoundException;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.dto.PlaceDto;
import hypercell.final_project.football_places_booking_system.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;


    public Place createPlace(PlaceDto placeDto) {
        Place place = new Place();
        place.setName(placeDto.getName());
        place.setLocation(placeDto.getLocation());
        place.setImageUrl(placeDto.getImageUrl());
        place.setPlaceType(placeDto.getPlaceType());
        return placeRepository.save(place);
    }


    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found with id: " + id));
    }


    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }


    public Place updatePlace(Long id, Place updatedPlace) {
        Place existingPlace = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + id));
        existingPlace.setName(updatedPlace.getName());
        existingPlace.setLocation(updatedPlace.getLocation());
        existingPlace.setPlaceType(updatedPlace.getPlaceType());
        existingPlace.setImageUrl(updatedPlace.getImageUrl());

        return placeRepository.save(existingPlace);
    }


    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}
