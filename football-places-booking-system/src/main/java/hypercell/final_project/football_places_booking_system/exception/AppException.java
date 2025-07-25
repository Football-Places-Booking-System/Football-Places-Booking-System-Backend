package hypercell.final_project.football_places_booking_system.exception;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AppException extends Exception {
    private final Data data;

    protected AppException() {
        super();
        data = null;
    }

    protected AppException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.data = Data.fromErrorCode(errorCode);
    }

    public record Data(Integer code, String msg) {
        public static Data fromErrorCode(ErrorCode errorCode) {
            return new Data(errorCode.getCode(), errorCode.getMsg());
        }
    }
}
