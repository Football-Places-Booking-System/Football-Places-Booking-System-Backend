package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.*;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.service.Interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
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
    private final EmailServiceImpl emailService;


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
        return TeamMemberResponse.builder()
                .userId(teamMember.getUser().getId())
                .userName(teamMember.getUser().getUsername())
                .role(teamMember.getRole())
                .status(teamMember.getStatus())
                .teamId(teamMember.getTeam().getId())
                .build();
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
    public TeamMemberResponse inviteByEmail(String email, UUID teamId, UUID invitedById) throws NotFoundException, ValidationException {
        // 1. Find the user by email
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
                
        // 2. Get the team and verify the inviter is the creator or has organizer role
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
                
        // 3. Check if the inviter is the team creator or an organizer
        if (!team.getCreator().getId().equals(invitedById) && 
            !teamMemberRepository.existsByUserIdAndTeamIdAndRole(
                invitedById, teamId, TeamRole.ORGANIZER.name())) {
            throw new ValidationException(ErrorCode.FORBIDDEN);
        }
        
        // 4. Build the TeamMemberCreationRequest
        TeamMemberCreationRequest req = new TeamMemberCreationRequest(
                user.getId(), teamId, TeamRole.PLAYER, invitedById
        );
        
        // 5. Create team member and save the result
        TeamMemberResponse teamMemberResponse = createTeamMember(req, invitedById);
        
        // 6. Send the invitation email
        emailService.sendRequestTOJoinTeam(invitedById, user.getId(), email, teamId);
        
        return teamMemberResponse;
    }


    @Override
    public void deleteTeamMember(UUID teamMemberId, UUID requesterId) throws NotFoundException, ValidationException {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(()->
                new NotFoundException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

        // organizer can remove anyone but member can remove self
        if (!teamMember.getUser().getId().equals(requesterId)) {
            // Not deleting self, must be organizer!
            if (!isOrganizer(requesterId, teamMember.getTeam().getId())) {
                throw new ValidationException(ErrorCode.FORBIDDEN);
            }
        }
        teamMemberRepository.delete(teamMember);
    }

    @Override
    public TeamMemberInvitationResponse respondToInvitation(UUID teamMemberId, TeamStatus request) throws AppException {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

        // Check if the current status is PENDING (only PENDING invitations can be updated)
        if (teamMember.getStatus() != TeamStatus.PENDING) {
            throw new ValidationException(ErrorCode.INVALID_REQUEST_TYPE);
        }
        
        // Set the new status based on the request
        if (request == TeamStatus.APPROVED || request == TeamStatus.REJECTED) {
            teamMember.setStatus(request);
        } else {
            throw new ValidationException(ErrorCode.INVALID_TEAM_STATUS);
        }
        // save the team member
        teamMember = teamMemberRepository.save(teamMember);
        // map to response
        TeamMemberInvitationResponse response = new TeamMemberInvitationResponse(
                teamMember.getId(),
                teamMember.getRole(),
                teamMember.getStatus(),
                teamMember.getUser().getId(),
                teamMember.getTeam().getId(),
                teamMember.getUser().getUserName(),
                teamMember.getTeam().getName()
        );

        return response;
    }
}