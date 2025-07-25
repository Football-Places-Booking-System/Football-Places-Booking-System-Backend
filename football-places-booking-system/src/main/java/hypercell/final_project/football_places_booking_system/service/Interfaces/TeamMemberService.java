package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamMemberService {

    TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, Long creatorid);
    List<TeamMemberResponse>getTeamMembersByTeam(Long teamId);
    List<TeamMemberResponse> getTeamMembersByUserId(Long userId);
    TeamMemberResponse updateTeamMember(TeamMemberUpdateRequest teamMemberUpdateRequest);
    TeamMemberResponse getTeamMemberById(Long id);
    void deleteTeamMember(Long id);

}


