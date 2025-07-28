package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
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

    @Override
    public TeamResponse createTeam(TeamCreationRequest teamCreationRequest, UUID creatorid) throws AppException {
        Team team = new Team();
        if (teamRepository.existsByNameIgnoreCase(teamCreationRequest.name())) {
            throw new AlreadyExistsException(ErrorCode.TEAM_ALREADY_EXISTS);
        }
        team.setName(teamCreationRequest.name());
        team.setDescription(teamCreationRequest.description());
        team = teamRepository.save(team);
        User creatorUser = userRepository.findById(creatorid).orElseThrow();

        TeamMember organizerMember = new TeamMember();
        organizerMember.setUser(creatorUser);
        organizerMember.setTeam(team);
        organizerMember.setRole(TeamRole.ORGANIZER);
        //user is automatically organizer when creating a team
        organizerMember.setStatus(TeamStatus.APPROVED);
        teamMemberRepository.save(organizerMember);

        team.setCreator(creatorUser);
        team = teamRepository.save(team);

        return mapToTeamResponse(team);
    }
    //The team creation method creates a team by description and name and automatically adds the creator as an organizer.
    //also doesnt allow for duplicate team names

    @Override
    public TeamResponse getTeamById(UUID id) throws NotFoundException {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return mapToTeamResponse(team);
    }

    @Override
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapToTeamResponse)
                .toList();
    }

    @Override
    public TeamMemberResponse invitePlayer(UUID teamid, String email, User inviter) throws NotFoundException, AlreadyExistsException, ValidationException {
        Team team = teamRepository.getById(teamid);
        User invitee = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if (teamMemberRepository.existsByTeamAndUser(team, invitee)) {
            throw new AlreadyExistsException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
        }
        //    User invitee = userRepository.findByEmail(email);
        // if (invitee == null) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        // }

        validateOrganizerRole(team, inviter);
        TeamMember invitation = createInvitation(team, invitee, inviter);
        return mapToTeamMemberResponse(invitation);
    }

    @Override
    public TeamResponse updateTeam(UUID id, TeamCreationRequest teamCreationRequest, UUID userId) throws NotFoundException {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        if (teamCreationRequest.name() != null) {
            team.setName(teamCreationRequest.name());
        }
        if (teamCreationRequest.description() != null) {
            team.setDescription(teamCreationRequest.description());
        }

        Team updatedTeam = teamRepository.save(team);
        return mapToTeamResponse(updatedTeam);
    }

    @Override
    public void deleteTeam(UUID teamId, UUID userId) throws NotFoundException, ValidationException {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        if (!team.getCreator().getId().equals(userId)) {
            throw new ValidationException(ErrorCode.FORBIDDEN);
        }

        teamMemberRepository.deleteAllByTeamId(teamId);


        teamRepository.delete(team);
    }

    @Override
    public List<TeamResponse> getTeamsByUser(UUID userId) {
        List<TeamMember> memberships = teamMemberRepository.findByUserId(userId);
        List<UUID> teamIds = memberships.stream()
                .map(member -> member.getTeam().getId())
                .toList();
        List<Team> teams = teamRepository.findAllById(teamIds);
        return teams.stream()
                .map(this::mapToTeamResponse).toList();
    }



    private TeamResponse mapToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdBy(team.getCreator().getId())
                .createdAt(team.getCreatedAt())
                .members(mapTeamMembers(team.getTeamMembers()))
                .build();
    }
    private TeamMemberResponse mapToTeamMemberResponse(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getUser().getId(),
                teamMember.getUser().getUsername(),
                teamMember.getRole(),
                teamMember.getStatus(),
                teamMember.getTeam().getId()
        );
    }

    private List<TeamMemberResponse> mapTeamMembers(List<TeamMember> members) {
        return members.stream()
                .map(member -> TeamMemberResponse.builder()
                        .userId(member.getUser().getId())
                        .userName(member.getUser().getUsername())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .build())
                .toList();
    }

    private TeamMember createInvitation(Team team, User invitee, User inviter) {
        TeamMember invitation = TeamMember.builder()
                .team(team)
                .user(invitee)
                .role(TeamRole.PLAYER)
                .status(TeamStatus.PENDING)
                .invitedBy(inviter)
                .build();
        return teamMemberRepository.save(invitation);
    }
    private void validateOrganizerRole(Team team, User user) throws ValidationException {
        teamMemberRepository.findByTeamAndUser(team, user)
                .filter(member -> member.getRole().equals(TeamRole.ORGANIZER))
                .orElseThrow(() -> new ValidationException(ErrorCode.FORBIDDEN));
    }
}
