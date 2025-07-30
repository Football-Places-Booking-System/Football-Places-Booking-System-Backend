package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

public class BookingMatchNotFoundException extends AppException {
    public BookingMatchNotFoundException() {
        super(ErrorCode.BOOKING_MATCH_NOT_FOUND);
    }
}
