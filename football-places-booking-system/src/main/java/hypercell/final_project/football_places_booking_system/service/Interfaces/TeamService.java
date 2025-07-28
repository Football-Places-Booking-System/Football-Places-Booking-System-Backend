package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;

public interface TeamService {
    TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID id) throws AppException;
    TeamResponse getTeamById(UUID id) throws NotFoundException;
    List<TeamResponse> getAllTeams();
    TeamResponse updateTeam(UUID id, TeamCreationRequest teamCreationRequest, UUID userId) throws NotFoundException;
    TeamMemberResponse invitePlayer(UUID teamid, String email, User inviter) throws NotFoundException, AlreadyExistsException, ValidationException;
    void deleteTeam(UUID id, UUID userId) throws NotFoundException, ValidationException;
    List<TeamResponse> getTeamsByUser(UUID userId);
}
