package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

public interface EmailService {

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendInviteToJoinTeam(User invitedBy, User inviteeUser, String inviteToEmail, Team team, UUID teamMemberId) throws AppException;

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus response);

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendRequestToJoinTeam(User user, Team team, UUID teamMemberId) throws AppException;

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendResponseToJoinRequest(TeamMember teamMember, TeamStatus response);

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendInvitationEmail(String email, MatchParticipant matchParticipant);

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendResponseToMatchParticipantInvitation(MatchParticipant matchParticipant, ParticipantStatus response);

    @Async("emailTaskExecutor")
    CompletableFuture<Void> sendHtmlTeamInviteEmail(String organizerName, String teamName, String teamDescription, 
                                                   String recipientEmail, String recipientName, UUID teamMemberId) throws AppException;
}
