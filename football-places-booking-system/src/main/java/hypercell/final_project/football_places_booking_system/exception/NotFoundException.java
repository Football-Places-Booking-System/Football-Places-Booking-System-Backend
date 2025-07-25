package hypercell.final_project.football_places_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends AppException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
