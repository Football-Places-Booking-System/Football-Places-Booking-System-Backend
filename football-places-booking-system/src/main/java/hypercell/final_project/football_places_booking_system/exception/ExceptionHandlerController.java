package hypercell.final_project.football_places_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<AppException.Data> handleAppException(ValidationException e) {
        return new ResponseEntity<>(e.getData(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<AppException.Data> handleAppException() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AppException.Data> handleAppException(NotFoundException e) {
        return new ResponseEntity<>(e.getData(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<AppException.Data> handleAppException(NoDataException e) {
        return new ResponseEntity<>(e.getData(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<AppException.Data> handleAppException(AlreadyExistsException e) {
        return new ResponseEntity<>(e.getData(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AppException.Data> handleAppException(InvalidCredentialsException e) {
        return new ResponseEntity<>(e.getData(), HttpStatus.UNAUTHORIZED);
    }
}
