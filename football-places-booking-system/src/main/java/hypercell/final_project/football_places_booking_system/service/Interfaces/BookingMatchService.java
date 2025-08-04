package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;

import java.util.List;
import java.util.UUID;

public interface BookingMatchService {
    public BookingMatch createBookingMatch(BookingDTO dto, UUID userId) throws AppException;
    public void cancelBooking(UUID matchId, UUID userId) throws AppException;
    public BookingMatch getById(UUID id) throws AppException;
    public List<BookingMatch> getByUser(UUID userId) throws AppException;
    public List<BookingMatch> getByTeam(UUID teamId) throws AppException;
    public List<BookingMatch> getByPlace(UUID placeId) throws AppException;
    public List<BookingMatch> getAll();
//    public List<BookingMatch> getMyMatchesAsPlayer(UUID userId) throws AppException;
    public List<BookingMatch> getMyMatchesAsOrganizer(UUID userId) throws AppException;
}
