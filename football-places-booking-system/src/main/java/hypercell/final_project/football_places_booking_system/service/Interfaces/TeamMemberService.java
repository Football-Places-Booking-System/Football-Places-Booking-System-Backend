package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.*;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;


public interface TeamMemberService {



    TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, UUID creatorid) throws NotFoundException;
    List<TeamMemberResponse>getTeamMembersByTeam(UUID teamId);
    List<TeamMemberResponse> getTeamMembersByUserId(UUID userId);
    TeamMemberResponse updateTeamMember(TeamMemberUpdateRequest teamMemberUpdateRequest);
    TeamMemberResponse getTeamMemberById(UUID id);
    public void deleteTeamMember(UUID teamMemberId, UUID requesterId) throws NotFoundException, ValidationException;
    public boolean isOrganizer(UUID userId, UUID teamId) throws NotFoundException;
    public TeamMemberResponse inviteByEmail(String email, UUID teamId, UUID invitedById) throws NotFoundException;

    TeamMemberInvitationResponse respondToInvitation(UUID teamMemberId, TeamStatus request) throws AppException;
}


