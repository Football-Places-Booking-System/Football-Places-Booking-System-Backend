package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NoContentException;
import hypercell.final_project.football_places_booking_system.exception.NoDataException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.repository.RequestRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID creatorid) throws AppException {
        User creatorUser = userRepository.findById(creatorid).orElseThrow();

        if (creatorUser == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Team team = new Team();

        if (teamCreationRequest.name() == null || teamCreationRequest.name().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_NAME);
        }

        if (teamRepository.existsByNameIgnoreCase(teamCreationRequest.name())) {
            throw new AlreadyExistsException(ErrorCode.TEAM_ALREADY_EXISTS);
        }

        if (teamCreationRequest.description() == null || teamCreationRequest.description().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_DESCRIPTION);
        }

        team.setName(teamCreationRequest.name());
        team.setDescription(teamCreationRequest.description());
        team.setCreator(creatorUser);

        team = teamRepository.save(team);

        TeamMember organizerMember = new TeamMember();

        organizerMember.setRole(TeamRole.ORGANIZER);
        organizerMember.setStatus(TeamStatus.APPROVED);
        organizerMember.setUser(creatorUser);
        organizerMember.setTeam(team);

        teamMemberRepository.save(organizerMember);

        team.getTeamMembers().add(organizerMember);

        team = teamRepository.save(team);

        return mapToTeamResponse(team);
    }

    @Override
    public TeamResponse getTeamById(UUID id) throws NotFoundException {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return mapToTeamResponse(team);
    }

    @Override
    public Page<TeamResponse> filterTeams(String name, String description, Pageable pageable) throws AppException {
        Specification<Team> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (description != null && !description.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
        }

        Page<Team> teams = teamRepository.findAll(spec, pageable);

        if (teams.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }

        return teams.map(this::mapToTeamResponse);
    }

    @Override
    public TeamResponse updateTeam(UUID id, TeamCreationRequest teamCreationRequest) throws AppException {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        if (teamCreationRequest.name() == null && teamCreationRequest.description() == null) {
            throw new NoDataException(ErrorCode.NO_DATA);
        }

        if (teamCreationRequest.name() != null && !teamCreationRequest.name().isEmpty()) {
            team.setName(teamCreationRequest.name());
        }
        if (teamCreationRequest.description() != null && !teamCreationRequest.description().isEmpty()) {
            team.setDescription(teamCreationRequest.description());
        }

        Team updatedTeam = teamRepository.save(team);
        return mapToTeamResponse(updatedTeam);
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteTeam(UUID teamId) throws AppException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        // Get all team members for this team
        List<TeamMember> teamMembers = teamMemberRepository.getTeamMemberByTeam(team);
        
        // Extract team member IDs
        List<UUID> teamMemberIds = teamMembers.stream()
                .map(TeamMember::getId)
                .collect(Collectors.toList());
        
        // Delete all requests that have these team member IDs in the joker_id column
        if (!teamMemberIds.isEmpty()) {
            teamMemberIds.forEach(teamMemberId -> {
                // Find requests by joker_id (which stores team member ID)
                Request request = requestRepository.findByJokerId(teamMemberId);
                if (request != null) {
                    requestRepository.delete(request);
                }
            });
        }

        // Now delete the team (this will cascade delete team members due to relationship)
        teamRepository.delete(team);

        return ResponseEntity.ok(new ResponseDTO(teamId, "Team deleted successfully"));
    }

    @Override
    public Page<TeamResponse> getTeamsByUser(UUID userId, int page, int size) throws AppException {
        Pageable pageable = PageRequest.of(page, size);
        Page<TeamMember> pageOfMemberships = teamMemberRepository.findByUserIdAndStatus(userId, TeamStatus.APPROVED, pageable);

        if (pageOfMemberships.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }

        return pageOfMemberships
                .map(teamMember -> mapToTeamResponse(teamMember.getTeam()));
    }

    @Override
    public Page<TeamResponse> getOtherTeams(UUID userId, int page, int size) throws AppException {
        Pageable pageable = PageRequest.of(page, size);

        List<UUID> userTeamIds = teamMemberRepository.findByUserIdAndStatus(userId, TeamStatus.APPROVED)
                .stream()
                .map(teamMember -> teamMember.getTeam().getId())
                .collect(Collectors.toList());
        
        Page<Team> teams = teamRepository.findAll((root, query, cb) ->
                cb.not(root.get("id").in(userTeamIds))
        , pageable);

        if (teams.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }
        
        return teams.map(this::mapToTeamResponse);
    }

    @Override
    public List<TeamResponse> getAllTeams() throws AppException {
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }
        return teams.stream()
                .map(this::mapToTeamResponse)
                .collect(Collectors.toList());
    }

    private TeamMemberResponse mapToTeamMemberResponse(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getId(),
                teamMember.getUser().getId(),
                teamMember.getUser().getUserName(),
                teamMember.getUser().getEmail(),
                teamMember.getRole(),
                teamMember.getStatus(),
                teamMember.getTeam().getId()
        );
    }

    private List<TeamMemberResponse> mapTeamMembers(List<TeamMember> members) {
        return members.stream()
                .map(member -> mapToTeamMemberResponse(member))
                .toList();
    }

    private TeamResponse mapToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdBy(team.getCreator().getId())
                .createdAt(team.getCreatedAt())
                .createdByUsername(team.getCreator().getUserName())
                .members(mapTeamMembers(team.getTeamMembers()))
                .build();
    }
}
