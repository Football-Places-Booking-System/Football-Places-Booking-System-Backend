package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

public class InvalidParticipantStatusException extends AppException {
    public InvalidParticipantStatusException() {
        super(ErrorCode.INVALID_PARTICIPANT_STATUS);
    }
}
