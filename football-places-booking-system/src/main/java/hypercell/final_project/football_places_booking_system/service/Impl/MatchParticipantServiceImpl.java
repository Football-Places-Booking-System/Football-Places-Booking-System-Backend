package hypercell.final_project.football_places_booking_system.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDetailRespDTO;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.UserMatchResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.service.Interfaces.MatchParticipantService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.EmailService;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.model.enums.PlaceType;
import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;
import hypercell.final_project.football_places_booking_system.repository.MatchParticipantRepository;
import hypercell.final_project.football_places_booking_system.repository.RequestRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantServiceImpl implements MatchParticipantService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final UserRepository userRepository;
    private final BookingMatchServiceImpl bookingMatchService;
    private final EmailService emailService;
    private final RequestService requestService;
    private final RequestRepository requestRepository;

    // Invite a user to a match
    public MatchParticipant inviteParticipant(InvitationRequest dto, UUID bookingMatchId) throws AppException {
        // Validate email
        if (dto.email() == null || dto.email().trim().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_EMAIL);
        }

        // Validate booking match ID
        if (bookingMatchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Use service instead of direct repository (this will validate booking match exists)
        BookingMatch match = bookingMatchService.getById(bookingMatchId);

        // Check if user is already a participant in this match
        boolean alreadyParticipant = matchParticipantRepository
                .findByBookingMatchIdAndUserId(match.getId(), user.getId())
                .isPresent();

        if (alreadyParticipant) {
            throw new ValidationException(ErrorCode.MATCH_PARTICIPANT_ALREADY_EXISTS);
        }

        MatchParticipant participant = MatchParticipant.builder()
                .bookingMatch(match)
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        MatchParticipant matchParticipant = matchParticipantRepository.save(participant);

        // Create Request entity for the invitation
        // The sender is the user who created the booking match, receiver is the invited user
        UUID senderId = match.getUser().getId(); // The booking match creator
        UUID receiverId = user.getId(); // The invited user
        
        // Create invitation message
        String senderName = match.getUser().getUserName();
        String placeName = match.getPlace().getName();
        String teamName = match.getTeam().getName();
        
        // Format date and time for better readability
        String matchDate = match.getStartTime().toLocalDate().toString();
        
        // Format start time
        int startHour = match.getStartTime().getHour();
        String startAmPm = startHour >= 12 ? "pm" : "am";
        int startDisplayHour = startHour == 0 ? 12 : (startHour > 12 ? startHour - 12 : startHour);
        String startTime = String.format("%d %s", startDisplayHour, startAmPm);
        
        // Format end time
        int endHour = match.getEndTime().getHour();
        String endAmPm = endHour >= 12 ? "pm" : "am";
        int endDisplayHour = endHour == 0 ? 12 : (endHour > 12 ? endHour - 12 : endHour);
        String endTime = String.format("%d %s", endDisplayHour, endAmPm);
        
        String invitationMessage = String.format("%s has invited you to join match at %s with team %s at %s from %s to %s", 
            senderName, placeName, teamName, matchDate, startTime, endTime);
        
        requestService.createRequestWithMessage(senderId, receiverId, RequestType.MATCH_INVITATION, invitationMessage, matchParticipant.getId());

        // Send invitation email
        emailService.sendInvitationEmail(dto.email(), matchParticipant);

        return matchParticipant;
    }

    // Respond to invitation
    public MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) throws AppException {
        // Validate participant ID
        if (participantId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate status
        if (status == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_STATUS);
        }

        MatchParticipant participant = matchParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATCH_PARTICIPANT_NOT_FOUND));

        // Check if the current status is INVITED (only INVITED participants can respond)
        if (participant.getStatus() != ParticipantStatus.INVITED) {
            throw new AlreadyExistsException(ErrorCode.MATCH_PARTICIPANT_ALREADY_RESPONDED);
        }

        // Validate the new status (should be ACCEPTED or DECLINED)
        if (status != ParticipantStatus.ACCEPTED && status != ParticipantStatus.DECLINED) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_STATUS);
        }

        // Check capacity only if the user is trying to accept the invitation
        if (status == ParticipantStatus.ACCEPTED) {
            // Get the place type and calculate maximum capacity
            BookingMatch bookingMatch = participant.getBookingMatch();
            PlaceType placeType = bookingMatch.getPlace().getPlaceType();
            
            // Calculate max capacity based on place type (double the number)
            int maxCapacity;
            switch (placeType) {
                case FIVE -> maxCapacity = 10;   // 5 * 2
                case SEVEN -> maxCapacity = 14;  // 7 * 2
                case ELEVEN -> maxCapacity = 22; // 11 * 2
                default -> throw new ValidationException(ErrorCode.INVALID_PLACE_TYPE);
            }
            
            // Count current accepted participants for this booking match
            long acceptedCount = matchParticipantRepository
                    .findByBookingMatchId(bookingMatch.getId())
                    .stream()
                    .filter(p -> p.getStatus() == ParticipantStatus.ACCEPTED)
                    .count();
            
            // Check if accepting this invitation would exceed capacity
            if (acceptedCount >= maxCapacity) {
                throw new ValidationException(ErrorCode.MATCH_CAPACITY_EXCEEDED);
            }
        }

        participant.setStatus(status);
        participant.setRespondedAt(LocalDateTime.now());

        // Update the Request entity status
        ResponseStatus responseStatus = status == ParticipantStatus.ACCEPTED ? ResponseStatus.ACCEPTED : ResponseStatus.REJECTED;

        // Find and update the request
        Request existingRequest = requestRepository.findByJokerId(participantId);

        try {
            // Create a response message
            String participantName = participant.getUser().getUserName();
            String responseText = status == ParticipantStatus.ACCEPTED ? "accepted" : "declined";
            String responseMessage = String.format("%s has %s the match invitation", participantName, responseText);

            requestService.updateRequestStatusWithMessage(existingRequest.getId(), responseStatus, responseMessage);
        } catch (AppException e) {
            // Log the error but don't fail the main operation
            throw new RuntimeException("Failed to update request status: " + e.getMessage(), e);
        }

        // Send a response notification email to the match organizer
        emailService.sendResponseToMatchParticipantInvitation(participant, status);

        return matchParticipantRepository.save(participant);
    }

    // Get all participants for a match
    public List<MatchParticipant> getByMatch(UUID matchId) throws AppException {
        // Validate match ID
        if (matchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        // Validate existence using service (this will throw proper exception if not found)
        bookingMatchService.getById(matchId);
        
        return matchParticipantRepository.findByBookingMatchId(matchId);
    }

    // Get all matches that a user has participated in
    public List<UserMatchResponseDTO> getUserParticipatedMatches(UUID userId) throws AppException {
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate user existence
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Map to booking matches
        return matchParticipantRepository.findByUserId(userId).stream()
                .map(mp -> UserMatchResponseDTO.builder()
                        .matchId(mp.getBookingMatch().getId())
                        .participantId(mp.getId())   // Participant primary key
                        .teamId(mp.getBookingMatch().getTeam().getId())
                        .teamName(mp.getBookingMatch().getTeam().getName())
                        .placeId(mp.getBookingMatch().getPlace().getId())
                        .placeName(mp.getBookingMatch().getPlace().getName())
                        .startTime(mp.getBookingMatch().getStartTime())
                        .endTime(mp.getBookingMatch().getEndTime())
                        .bookingStatus(mp.getBookingMatch().getStatus())
                        .invitationStatus(mp.getStatus())   // INVITED, ACCEPTED, DECLINED
                        .build())
                .toList();
    }

    public List<BookingDetailRespDTO> getUserParticipatedMatchesDetailed(UUID userId) throws AppException {
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Fetch all participant entries for this user
        List<MatchParticipant> participations = matchParticipantRepository.findByUserId(userId);

        // Map to detailed response DTO
        return participations.stream()
                .map(mp -> {
                    BookingMatch match = mp.getBookingMatch();
                    return BookingDetailRespDTO.builder()
                            .id(match.getId())
                            .startTime(match.getStartTime())
                            .endTime(match.getEndTime())
                            .status(match.getStatus())
                            .createdAt(match.getCreatedAt())

                            .placeId(match.getPlace().getId())
                            .placeName(match.getPlace().getName())

                            .teamId(match.getTeam().getId())
                            .teamName(match.getTeam().getName())

                            .userId(match.getUser().getId())
                            .userName(match.getUser().getUserName())
                            .build();
                })
                .distinct()
                .toList();
    }

}
