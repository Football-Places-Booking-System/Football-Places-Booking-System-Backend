package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

import java.util.UUID;

public interface EmailService {

    void sendRequestTOJoinTeam(UUID invitedById, UUID inviteToId, String inviteToEmail, UUID teamId);

    void sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus response);

}
