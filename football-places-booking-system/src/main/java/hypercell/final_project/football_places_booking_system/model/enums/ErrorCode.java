package hypercell.final_project.football_places_booking_system.model.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // ===== User Errors =====
    INVALID_USERNAME(200, "Username is either empty or null"),
    USERNAME_ALREADY_EXISTS(201, "Username already exists"),
    INVALID_EMAIL(202, "Email is either empty or null"),
    EMAIL_ALREADY_EXISTS(203, "Email already exists"),
    INVALID_PASSWORD(204, "Password is either empty or null"),
    INVALID_USER_ROLE(205, "User role is invalid"),
    INVALID_USER_STATUS(206, "User status is invalid"),
    USER_ALREADY_EXISTS(207, "User already exists"),
    USER_NOT_FOUND(208, "User not found"),

    // ===== Team Errors =====
    INVALID_TEAM_NAME(300, "Team name is either empty or null"),
    INVALID_TEAM_DESCRIPTION(301, "Team description is either empty or null"),
    TEAM_NOT_FOUND(302, "Team not found"),

    // ===== Team Member Errors =====
    INVALID_TEAM_MEMBER_ROLE(400, "Team member role is invalid"),
    INVALID_TEAM_MEMBER_STATUS(401, "Team member status is invalid"),
    TEAM_MEMBER_ALREADY_EXISTS(402, "User is already a team member"),
    TEAM_MEMBER_NOT_FOUND(403, "Team member not found"),

    // ===== Place Errors =====
    INVALID_PLACE_NAME(500, "Place name is either empty or null"),
    INVALID_PLACE_LOCATION(501, "Place location is either empty or null"),
    INVALID_PLACE_TYPE(502, "Place type is invalid"),
    PLACE_NOT_FOUND(503, "Place not found"),

    // ===== Booking Match Errors =====
    INVALID_BOOKING_START_TIME(600, "Booking start time is invalid"),
    INVALID_BOOKING_END_TIME(601, "Booking end time is invalid"),
    INVALID_MATCH_STATUS(602, "Booking match status is invalid"),
    BOOKING_MATCH_NOT_FOUND(603, "Booking match not found"),

    // ===== Match Participant Errors =====
    INVALID_PARTICIPANT_STATUS(700, "Participant status is invalid"),
    MATCH_PARTICIPANT_NOT_FOUND(701, "Match participant not found"),

    // ===== Request Errors =====
    INVALID_REQUEST_TYPE(800, "Request type is invalid"),
    INVALID_REQUEST_STATUS(801, "Request status is invalid"),
    INVALID_REQUEST_MESSAGE(802, "Request message is either empty or null"),
    REQUEST_NOT_FOUND(803, "Request not found"),

    // ===== Generic Errors =====
    NO_CONTENT(900, "No content available"),
    NOT_FOUND(901, "Resource not found"),
    NO_DATA(902, "No data provided"),
    UNAUTHORIZED(903, "Unauthorized access"),
    FORBIDDEN(904, "Action is forbidden"),
    INTERNAL_ERROR(905, "Internal server error");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
