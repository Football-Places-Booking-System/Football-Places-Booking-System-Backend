package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;

public interface TeamService {
    TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID id);
    TeamResponse getTeamById(UUID id);
    List<TeamResponse> getAllTeams();
    TeamResponse updateTeam(UUID id, TeamCreationRequest teamCreationRequest, UUID userId);
    TeamMemberResponse invitePlayer(UUID teamid, String email, User inviter);
    void deleteTeam(UUID id, UUID userId);
    List<TeamResponse> getTeamsByUser(UUID userId);
}
