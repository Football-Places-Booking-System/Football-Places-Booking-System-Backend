package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID id);
    TeamResponse getTeamById(Long id);
    List<TeamResponse> getAllTeams();
    TeamResponse updateTeam(Long id, TeamCreationRequest teamCreationRequest, UUID userId);
    TeamMemberResponse invitePlayer(Long teamid, String email, User inviter);
    void deleteTeam(Long id, UUID userId);
    List<TeamResponse> getTeamsByUser(UUID userId);
}
