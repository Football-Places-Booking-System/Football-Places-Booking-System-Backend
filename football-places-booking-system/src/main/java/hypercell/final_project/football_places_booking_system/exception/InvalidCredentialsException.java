package hypercell.final_project.football_places_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends AppException {

    public InvalidCredentialsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
