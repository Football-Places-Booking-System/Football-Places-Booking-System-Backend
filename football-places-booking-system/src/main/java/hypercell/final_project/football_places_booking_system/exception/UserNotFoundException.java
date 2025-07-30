package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

public class UserNotFoundException extends AppException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
