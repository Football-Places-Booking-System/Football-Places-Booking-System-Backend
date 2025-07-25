package hypercell.final_project.football_places_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends AppException {
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
