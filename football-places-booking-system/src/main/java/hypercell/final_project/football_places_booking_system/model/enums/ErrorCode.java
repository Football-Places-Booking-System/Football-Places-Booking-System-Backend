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
    FORBIDDEN_STATUS(208, "Inactive user"),

    // ===== Team Errors =====
    INVALID_TEAM_ID(300, "Team ID is either empty or null"),
    INVALID_TEAM_NAME(301, "Team name is either empty or null"),
    INVALID_TEAM_DESCRIPTION(302, "Team description is either empty or null"),
    TEAM_NOT_FOUND(303, "Team not found"),
    TEAM_ALREADY_EXISTS(304, "Team already exists"),
    FORBIDDEN_ROLE(305, "Must be an organizer"),

    // ===== Team Member Errors =====
    INVALID_TEAM_MEMBER_ROLE(400, "Team member role is invalid"),
    INVALID_TEAM_MEMBER_STATUS(401, "Team member status is invalid"),
    TEAM_MEMBER_ALREADY_EXISTS(402, "User is already a team member"),
    TEAM_MEMBER_NOT_FOUND(403, "Team member not found"),
    INVALID_TEAM_STATUS(404, "Team status is invalid"),
    TEAM_MEMBER_ALREADY_PENDING(405, "User is already pending to join this team"),
    TEAM_MEMBER_RESPONSE_ALREADY_EXISTS(406, "Team member response already exists"),

    // ===== Place Errors =====
    INVALID_PLACE_ID(500, "Place ID is either empty or null"),
    INVALID_PLACE_NAME(501, "Place name is either empty or null"),
    INVALID_PLACE_DESCRIPTION(502, "Place description is either empty or null"),
    INVALID_PLACE_IMAGE_URL(503, "Place image URL is either empty or null"),
    INVALID_PLACE_LOCATION(504, "Place location is either empty or null"),
    INVALID_PLACE_TYPE(505, "Place type is invalid"),
    PLACE_NOT_FOUND(506, "Place not found"),

    // ===== Booking Match Errors =====
    INVALID_BOOKING_MATCH_ID(600, "Booking match ID is either empty or null"),
    INVALID_BOOKING_START_TIME(601, "Booking start time is invalid"),
    INVALID_BOOKING_END_TIME(602, "Booking end time is invalid"),
    INVALID_MATCH_STATUS(603, "Booking match status is invalid"),
    BOOKING_MATCH_NOT_FOUND(604, "Booking match not found"),
    TIME_SLOT_UNAVAILABLE(605, "The selected time slot is already booked for this place"),
    UNAUTHORIZED_BOOKING_ACTION(606, "Only team organizers can perform this action"),
    MATCH_CANNOT_BE_CANCELLED_NOW(607, "Match Can not be cancelled now"),

    // ===== Match Participant Errors =====
    INVALID_PARTICIPANT_ID (700, "Participant ID is either empty or null"),
    INVALID_PARTICIPANT_STATUS(701, "Participant status is invalid"),
    MATCH_PARTICIPANT_NOT_FOUND(702, "Match participant not found"),
    MATCH_PARTICIPANT_ALREADY_EXISTS(703, "User is already a participant in this match"),
    INVALID_PARTICIPANT_EMAIL(704, "Participant email is either empty or null"),
    MATCH_PARTICIPANT_ALREADY_RESPONDED(705, "Participant has already responded to the invitation"),
    MATCH_CAPACITY_EXCEEDED(706, "Match capacity exceeded - invitation expired"),

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
    INVALID_CREDENTIALS(906, "Invalid credentials provided"),
    INVALID_TOKEN(907, "Token is invalid or expired");

    // ===== Email Errors =====
    // EMAIL_SEND_FAILURE(1000, "Failed to send email");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
