package hypercell.final_project.football_places_booking_system.service.Impl;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamServiceImpl teamService;


    @Override
    public TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, Long creatorid) {
        User user = userRepository.getById(request.userId());
        TeamResponse teamResponse = teamService.getTeamById(request.teamId());
        Long teamId = teamResponse.id();
        Team team = teamRepository.findById(teamId).orElseThrow();
        Long invitedBy = request.invitedById();
        User invitedby = userRepository.getById(invitedBy);
        userRepository.getById(request.invitedById());

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .role(request.role())
                .invitedBy(invitedby)
                .status(TeamStatus.PENDING)
                .build();

        TeamMember savedMember = teamMemberRepository.save(teamMember);
        return mapToTeamMemberResponse(savedMember);
    }

    @Override
    public TeamMemberResponse getTeamMemberById(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow();
        return mapToTeamMemberResponse(teamMember);
    }

    @Override
    public List<TeamMemberResponse> getTeamMembersByTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        return teamMemberRepository.findByTeam(team).stream().map(this::mapToTeamMemberResponse).collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberResponse> getTeamMembersByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        return teamMemberRepository.findByUser(user).stream()
                .map(this::mapToTeamMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamMemberResponse updateTeamMember(TeamMemberUpdateRequest teamMemberUpdateRequest) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberUpdateRequest.id()).orElseThrow();

        if (teamMemberUpdateRequest.role() != null) teamMember.setRole(teamMemberUpdateRequest.role());
        if (teamMemberUpdateRequest.status() != null) teamMember.setStatus(teamMemberUpdateRequest.status());

        return mapToTeamMemberResponse(teamMemberRepository.save(teamMember));
    }

    @Override
    public void deleteTeamMember(Long id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow();
        teamMemberRepository.delete(teamMember);
    }



    private TeamMemberResponse mapToTeamMemberResponse(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getUser().getId(),
                teamMember.getUser().getUsername(),
                teamMember.getRole(),
                teamMember.getStatus()
        );
    }
}
