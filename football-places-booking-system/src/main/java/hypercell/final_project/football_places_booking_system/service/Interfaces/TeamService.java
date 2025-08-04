package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;

public interface TeamService {
    TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID id) throws AppException;
    TeamResponse getTeamById(UUID id) throws NotFoundException;
    Page<TeamResponse> filterTeams(String name, String description, Pageable pageable) throws AppException;
    public List<TeamResponse> getAllTeams() throws AppException;
    TeamResponse updateTeam(UUID id, TeamCreationRequest teamCreationRequest) throws AppException;
    ResponseEntity<ResponseDTO> deleteTeam(UUID id) throws AppException;
    Page<TeamResponse> getTeamsByUser(UUID userId, int page, int size) throws AppException;
    Page<TeamResponse> getOtherTeams(UUID userId, int page, int size) throws AppException;
}
