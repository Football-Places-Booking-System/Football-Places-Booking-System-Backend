package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;

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
    public TeamResponse createTeam(TeamCreationRequest teamCreationRequest, Long creatorid) {
        Team team = new Team();
        team.setName(teamCreationRequest.name());
        team.setDescription(teamCreationRequest.description());
        team = teamRepository.save(team);
        User creatorUser = userRepository.findById(creatorid).orElseThrow();

        TeamMember organizerMember = new TeamMember();
        organizerMember.setUser(creatorUser);
        organizerMember.setTeam(team);
        organizerMember.setRole(TeamRole.ORGANIZER);
        organizerMember.setStatus(TeamStatus.APPROVED);
        teamMemberRepository.save(organizerMember);

        team.setCreator(organizerMember);
        team = teamRepository.save(team);

        return mapToTeamResponse(team);
    }

    @Override
    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        return mapToTeamResponse(team);
    }

    @Override
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapToTeamResponse)
                .toList();
    }

    @Override
    public TeamMemberResponse invitePlayer(Long teamid, String email, User inviter) {
        Team team = teamRepository.getById(teamid);
        User invitee = userRepository.findByEmail(email);
        if (invitee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        validateOrganizerRole(team, inviter);
        TeamMember invitation = createInvitation(team, invitee, inviter);
        return mapToTeamMemberResponse(invitation);
    }

    @Override
    public TeamResponse updateTeam(Long id, TeamCreationRequest teamCreationRequest) {
        Team team = teamRepository.findById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

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
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        teamMemberRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
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
                teamMember.getStatus()
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
                .role(TeamRole.MEMBER)
                .status(TeamStatus.PENDING)
                .invitedBy(inviter)
                .build();
        return teamMemberRepository.save(invitation);
    }
    private void validateOrganizerRole(Team team, User user) {
        teamMemberRepository.findByTeamAndUser(team, user)
                .filter(member -> member.getRole().equals(TeamRole.ORGANIZER))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,"Only team organizers can update team"));
    }
}
