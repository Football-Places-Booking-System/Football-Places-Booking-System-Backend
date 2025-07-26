package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;

import java.util.List;

public interface TeamService {
    TeamResponse createTeam(TeamCreationRequest teamCreationRequest, Long id);
    TeamResponse getTeamById(Long id);
    List<TeamResponse> getAllTeams();
    TeamResponse updateTeam(Long id, TeamCreationRequest teamCreationRequest, Long userId);
    TeamMemberResponse invitePlayer(Long teamid, String email, User inviter);
    void deleteTeam(Long id, Long userId);
    List<TeamResponse> getTeamsByUser(Long userId);
}
