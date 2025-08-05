package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.service.Interfaces.PlaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NoContentException;
import hypercell.final_project.football_places_booking_system.exception.NoDataException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.dto.PlaceDTO;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.PlaceType;
import hypercell.final_project.football_places_booking_system.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceDTO createPlace(PlaceDTO placeDto) throws AppException {
        if (placeDto.name() == null || placeDto.name().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_NAME);
        }

        if (placeDto.description() == null || placeDto.description().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_DESCRIPTION);
        }

        if (placeDto.location() == null || placeDto.location().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_LOCATION);
        }

        if (placeDto.imageUrl() == null || placeDto.imageUrl().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_IMAGE_URL);
        }

        if (placeDto.placeType() == null) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_TYPE);
        }

        Place place = new Place();
        place.setName(placeDto.name());
        place.setDescription(placeDto.description());
        place.setLocation(placeDto.location());
        place.setImageUrl(placeDto.imageUrl());
        place.setPlaceType(placeDto.placeType());

        Place newPlace = placeRepository.save(place);

        return new PlaceDTO(
                newPlace.getId(),
                newPlace.getName(),
                newPlace.getDescription(),
                newPlace.getLocation(),
                newPlace.getPlaceType(),
                newPlace.getImageUrl()
            );
    }


    public PlaceDTO getPlaceById(UUID id) throws AppException {
        Place place = placeRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        return new PlaceDTO(
            place.getId(),
            place.getName(),
            place.getDescription(),
            place.getLocation(),
            place.getPlaceType(),
            place.getImageUrl()
        );
    }

    public Page<PlaceDTO> filterPlaces(String name, String location, PlaceType placeType, String imageUrl, Pageable pageable) throws AppException {
        Specification<Place> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (location != null && !location.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
        }

        if (placeType != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("placeType"), placeType));
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("imageUrl")), "%" + imageUrl.toLowerCase() + "%"));
        }

        Page<Place> places = placeRepository.findAll(spec, pageable);

        if (places.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }

        return places.map(place -> new PlaceDTO(
            place.getId(),
            place.getName(),
            place.getDescription(),
            place.getLocation(),
            place.getPlaceType(),
            place.getImageUrl()
        ));
    }

    public ResponseEntity<ResponseDTO> updatePlace(UUID id, PlaceDTO updatedPlace) throws AppException {
        Place place = placeRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        if (updatedPlace.name() == null && updatedPlace.description() == null && updatedPlace.location() == null && updatedPlace.imageUrl() == null && updatedPlace.placeType() == null) {
            throw new NoDataException(ErrorCode.NO_DATA);
        }

        if (updatedPlace.name() != null && !updatedPlace.name().isEmpty()) {
            place.setName(updatedPlace.name());
        }

        if (updatedPlace.description() != null && !updatedPlace.description().isEmpty()) {
            place.setDescription(updatedPlace.description());
        }

        if (updatedPlace.location() != null && !updatedPlace.location().isEmpty()) {
            place.setLocation(updatedPlace.location());
        }

        if (updatedPlace.imageUrl() != null && !updatedPlace.imageUrl().isEmpty()) {
            place.setImageUrl(updatedPlace.imageUrl());
        }

        if (updatedPlace.placeType() != null) {
            place.setPlaceType(updatedPlace.placeType());
        }

        placeRepository.save(place);

        return ResponseEntity.ok(new ResponseDTO(id, "Place updated successfully"));
    }


    public ResponseEntity<ResponseDTO> deletePlace(UUID id) throws AppException {
        if (!placeRepository.existsById(id)) {
            throw new NotFoundException(ErrorCode.PLACE_NOT_FOUND);
        }

        placeRepository.deleteById(id);

        return ResponseEntity.ok(new ResponseDTO(id, "Place deleted successfully"));
    }
}
