package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

public class ForbiddenActionException extends AppException {
    public ForbiddenActionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
