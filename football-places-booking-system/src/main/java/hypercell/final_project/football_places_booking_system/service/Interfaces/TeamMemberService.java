package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberInviteResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;


public interface TeamMemberService {
    void realTimeNotify(UUID receiverId);
    TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, UUID creatorid) throws NotFoundException;
    List<TeamMemberResponse>getTeamMembersByTeam(UUID teamId);
    List<TeamMemberResponse> getTeamMembersByUserId(UUID userId);
    TeamMemberResponse updateTeamMember(TeamMemberUpdateRequest teamMemberUpdateRequest);
    TeamMemberResponse getTeamMemberById(UUID id);
    public void deleteTeamMember(UUID teamMemberId, UUID requesterId) throws NotFoundException, ValidationException;
    public boolean isOrganizer(UUID userId, UUID teamId) throws NotFoundException;
    public TeamMemberResponse inviteByEmail(String email, UUID teamId, User inviterUser) throws AppException;
    TeamMemberInviteResponse respondToInvitation(UUID teamMemberId, TeamStatus request) throws AppException;
    TeamMemberResponse requestToJoinTeam(UUID teamId, User user) throws AppException;
    List<TeamMemberResponse> getPendingJoinRequests(UUID teamId) throws AppException;
    TeamMemberResponse respondToJoinRequest(UUID teamMemberId, TeamStatus response, UUID organizerId) throws AppException;
}
