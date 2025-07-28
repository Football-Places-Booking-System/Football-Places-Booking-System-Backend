package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import org.springframework.stereotype.Service;

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
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamServiceImpl teamService;


    @Override
    public TeamMemberResponse createTeamMember(TeamMemberCreationRequest request, UUID creatorid) throws NotFoundException {
        User user = userRepository.getById(request.userId());
        TeamResponse teamResponse = teamService.getTeamById(request.teamId());
        UUID teamId = teamResponse.id();
        Team team = teamRepository.findById(teamId).orElseThrow();
        UUID invitedBy = request.invitedById();
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
    public TeamMemberResponse getTeamMemberById(UUID id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow();
        return mapToTeamMemberResponse(teamMember);
    }

    @Override
    public List<TeamMemberResponse> getTeamMembersByTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        return teamMemberRepository.findByTeam(team).stream().map(this::mapToTeamMemberResponse).collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberResponse> getTeamMembersByUserId(UUID userId) {
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


    private TeamMemberResponse mapToTeamMemberResponse(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getUser().getId(),
                teamMember.getUser().getUsername(),
                teamMember.getRole(),
                teamMember.getStatus()
        );
    }
    public boolean isOrganizer(UUID userId, UUID teamId) throws NotFoundException {
        System.out.println("Checking organizer: user=" + userId + ", team=" + teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return teamMemberRepository.findByTeamAndUser(team, user)
                .map(tm -> tm.getRole() == TeamRole.ORGANIZER)
                .orElse(false);
    }
    public TeamMemberResponse inviteByEmail(String email, UUID teamId, UUID invitedById) throws NotFoundException {
        // 1. Find the user by email
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        // 2. Build the TeamMemberCreationRequest
        TeamMemberCreationRequest req = new TeamMemberCreationRequest(
                user.getId(), teamId, TeamRole.PLAYER, invitedById
        );
        return createTeamMember(req, invitedById);
    }
    @Override
    public void deleteTeamMember(UUID teamMemberId, UUID requesterId) throws NotFoundException, ValidationException {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).
                orElseThrow(()->
                new NotFoundException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

        // organizer can remove anyone but member can remove self
        if (!teamMember.getUser().getId().equals(requesterId)) {
            // Not deleting self, must be organizer!
            boolean isOrganizer = teamMemberRepository.existsByUserIdAndTeamIdAndRole(
                    requesterId,
                    teamMember.getTeam().getId(),
                    String.valueOf(TeamRole.ORGANIZER)
            );
            if (!isOrganizer) {
                throw new ValidationException(ErrorCode.FORBIDDEN);
            }
        }
        teamMemberRepository.delete(teamMember);
    }
}