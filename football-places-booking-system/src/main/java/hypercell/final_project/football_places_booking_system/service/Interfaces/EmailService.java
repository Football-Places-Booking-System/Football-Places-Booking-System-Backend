package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

public interface EmailService {

    void sendInviteToJoinTeam(User invitedBy, User inviteeUser, String inviteToEmail, Team team, UUID teamMemberId) throws AppException;

    void sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus response);

    void sendRequestToJoinTeam(User user, Team team, UUID teamMemberId) throws AppException;

    void sendResponseToJoinRequest(TeamMember teamMember, TeamStatus response);

    void sendInvitationEmail(String email, MatchParticipant matchParticipant);

    void sendResponseToMatchParticipantInvitation(MatchParticipant matchParticipant, ParticipantStatus response);

}
