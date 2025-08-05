package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;

import java.util.List;
import java.util.UUID;

public interface MatchParticipantService {

    MatchParticipant inviteParticipant(InvitationRequest dto, UUID bookingMatchId) throws AppException;

    MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) throws AppException;

    MatchParticipant joinMatchAsOrganizer(UUID bookingMatchId, UUID organizerId) throws AppException;

    List<MatchParticipant> getByMatch(UUID matchId) throws AppException;
}
