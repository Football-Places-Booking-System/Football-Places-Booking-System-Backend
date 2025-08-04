package hypercell.final_project.football_places_booking_system.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.service.Interfaces.MatchParticipantService;
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
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantServiceImpl implements MatchParticipantService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final UserRepository userRepository;
    private final BookingMatchServiceImpl bookingMatchService;
    private final EmailServiceImpl emailService;
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
        String startTime = match.getStartTime().toString();
        String endTime = match.getEndTime().toString();
        
        String invitationMessage = String.format("%s has invited you to join match at %s with team %s from %s to %s", 
            senderName, placeName, teamName, startTime, endTime);
        
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
        
        // Find the request by sender (match organizer) and receiver (participant) and type
        UUID senderId = participant.getBookingMatch().getUser().getId(); // The match organizer
        UUID receiverId = participant.getUser().getId(); // The participant
        
        // Find and update the request
        requestRepository.findBySenderIdAndReceiverIdAndRequestType(senderId, receiverId, RequestType.MATCH_INVITATION)
                .ifPresent(existingRequest -> {
                    try {
                        // Create response message
                        String participantName = participant.getUser().getUserName();
                        String responseText = status == ParticipantStatus.ACCEPTED ? "accepted" : "declined";
                        String responseMessage = String.format("%s has %s the match invitation", participantName, responseText);
                        
                        requestService.updateRequestStatusWithMessage(existingRequest.getId(), responseStatus, responseMessage);
                    } catch (AppException e) {
                        // Log the error but don't fail the main operation
                        throw new RuntimeException("Failed to update request status: " + e.getMessage(), e);
                    }
                });

        // Send response notification email to the match organizer
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
}
