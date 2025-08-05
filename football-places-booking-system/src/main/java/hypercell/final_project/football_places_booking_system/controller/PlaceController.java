package hypercell.final_project.football_places_booking_system.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.dto.PlaceDTO;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.enums.PlaceType;
import hypercell.final_project.football_places_booking_system.service.Impl.PlaceServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/place")
public class PlaceController {

    private final PlaceServiceImpl placeService;
    
    @PreAuthorize("@authService.is('ACTIVE') and hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PlaceDTO> create(@RequestBody PlaceDTO placeDto) throws AppException {
        PlaceDTO placeDTO = placeService.createPlace(placeDto);
        
        return new ResponseEntity<>(placeDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable UUID id) throws AppException {
        PlaceDTO place = placeService.getPlaceById(id);
        return new ResponseEntity<>(place, HttpStatus.OK);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all")
    public Page<PlaceDTO> filterPlaces(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) PlaceType placeType,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws AppException {
        Pageable pageable = PageRequest.of(page, size);
        return placeService.filterPlaces(name, location, placeType, imageUrl, pageable);
    }

    @PreAuthorize("@authService.is('ACTIVE') and hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO> updatePlace(@PathVariable UUID id, @RequestBody PlaceDTO updatedPlace) throws AppException {
        return placeService.updatePlace(id, updatedPlace);
    }

    @PreAuthorize("@authService.is('ACTIVE') and hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletePlace(@PathVariable UUID id) throws AppException{
        return placeService.deletePlace(id);
    }
}
