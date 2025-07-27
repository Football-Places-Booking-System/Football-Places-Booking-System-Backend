package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.dto.PlaceDTO;
import hypercell.final_project.football_places_booking_system.service.PlaceService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable UUID id) {
        Place place = placeService.getPlaceById(id);
        return new ResponseEntity<>(place, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Place>> getAllPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return new ResponseEntity<>(places, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Place> create(@RequestBody PlaceDTO placeDto) {
        Place place = placeService.createPlace(placeDto);
        return new ResponseEntity<>(place, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable UUID id, @RequestBody Place updatedPlace) {
        Place place = placeService.updatePlace(id, updatedPlace);
        return new ResponseEntity<>(place, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable UUID id) {
        placeService.deletePlace(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
