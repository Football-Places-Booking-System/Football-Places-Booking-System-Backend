package hypercell.final_project.football_places_booking_system.model.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // ===== User Errors =====
    INVALID_USERNAME(200, "Username is either empty or null"),
    INVALID_EMAIL(201, "Email is either empty or null"),
    EMAIL_ALREADY_EXISTS(202, "Email already exists"),
    INVALID_PASSWORD(203, "Password is either empty or null"),
    INVALID_USER_ROLE(204, "User role is invalid"),
    INVALID_USER_STATUS(205, "User status is invalid"),
    USER_ALREADY_EXISTS(206, "User already exists"),
    USER_NOT_FOUND(207, "User not found"),

    // ===== Team Errors =====
    INVALID_TEAM_NAME(300, "Team name is either empty or null"),
    INVALID_TEAM_DESCRIPTION(301, "Team description is either empty or null"),
    TEAM_NOT_FOUND(302, "Team not found"),
    TEAM_ALREADY_EXISTS(303, "Team already exists"),

    // ===== Team Member Errors =====
    INVALID_TEAM_MEMBER_ROLE(400, "Team member role is invalid"),
    INVALID_TEAM_MEMBER_STATUS(401, "Team member status is invalid"),
    TEAM_MEMBER_ALREADY_EXISTS(402, "User is already a team member"),
    TEAM_MEMBER_NOT_FOUND(403, "Team member not found"),
    INVALID_TEAM_STATUS(404, "Team status is invalid"),
    TEAM_MEMBER_ALREADY_INVITED(405, "User is already invited to the team"),
    TEAM_MEMBER_RESPONSE_ALREADY_EXISTS(406, "Team member response already exists"),

    // ===== Place Errors =====
    INVALID_PLACE_NAME(500, "Place name is either empty or null"),
    INVALID_PLACE_LOCATION(501, "Place location is either empty or null"),
    INVALID_PLACE_IMAGE_URL(502, "Place image URL is either empty or null"),
    INVALID_PLACE_TYPE(503, "Place type is invalid"),
    PLACE_NOT_FOUND(504, "Place not found"),

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
    INTERNAL_ERROR(905, "Internal server error"),
    INVALID_CREDENTIALS(906, "Invalid credentials provided");

    // ===== Email Errors =====
    // EMAIL_SEND_FAILURE(1000, "Failed to send email");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
