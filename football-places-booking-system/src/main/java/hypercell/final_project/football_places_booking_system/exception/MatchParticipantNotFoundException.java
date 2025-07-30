package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

public class MatchParticipantNotFoundException extends AppException {
    public MatchParticipantNotFoundException() {
        super(ErrorCode.MATCH_PARTICIPANT_NOT_FOUND);
    }
}
