package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

public interface EmailService {

    void sendRequestTOJoinTeam(UUID invitedById, UUID inviteToId, String inviteToEmail, UUID teamId);

    void sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus response);

    void sendInvitationEmail(String email, MatchParticipant matchParticipant);

    void sendResponseToMatchParticipantInvitation(MatchParticipant matchParticipant, ParticipantStatus response);

}
