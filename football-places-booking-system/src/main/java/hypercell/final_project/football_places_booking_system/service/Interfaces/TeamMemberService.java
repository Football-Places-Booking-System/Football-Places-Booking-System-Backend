package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;

public interface TeamMemberService {

    TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, UUID creatorid);
    List<TeamMemberResponse>getTeamMembersByTeam(Long teamId);
    List<TeamMemberResponse> getTeamMembersByUserId(UUID userId);
    TeamMemberResponse updateTeamMember(TeamMemberUpdateRequest teamMemberUpdateRequest);
    TeamMemberResponse getTeamMemberById(Long id);
    void deleteTeamMember(Long id);
}


