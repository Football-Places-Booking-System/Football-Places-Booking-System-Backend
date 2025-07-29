package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.*;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Creating team member for user: {} in team: {}", request.userId(), request.teamId());

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
    public TeamMemberResponse inviteByEmail(String email, UUID teamId, UUID invitedById) throws AppException {
        log.info("Inviting user with email: {} to team: {} by user: {}", email, teamId, invitedById);
        log.debug("Inviting user with email: {} to team: {} by user: {}", email, teamId, invitedById);

        // 1. Find the user by email
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new NotFoundException(ErrorCode.USER_NOT_FOUND);
                });

        // 2. Get the team and verify the inviter is the creator or has organizer role
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.warn("Team not found with ID: {}", teamId);
                    return new NotFoundException(ErrorCode.TEAM_NOT_FOUND);
                });
                
        // 3. Check if the inviter is the team creator or an organizer
        if (!team.getCreator().getId().equals(invitedById) && 
            !teamMemberRepository.existsByUserIdAndTeamIdAndRole(
                invitedById, teamId, TeamRole.ORGANIZER.name())) {
            log.warn("User {} is not authorized to invite members to team {}", invitedById, teamId);
            throw new ValidationException(ErrorCode.FORBIDDEN);
        }

        // 4. Check if the user is already a member of the team or has been invited (Validation)
        if (teamMemberRepository.findByTeamAndUser(team, user).isPresent()) {
            log.warn("User {} is already a member of team {}", user.getId(), teamId);
            throw new AlreadyExistsException(ErrorCode.TEAM_MEMBER_ALREADY_INVITED);
        }
        
        // 5. Build the TeamMemberCreationRequest
        TeamMemberCreationRequest req = new TeamMemberCreationRequest(
                user.getId(), teamId, TeamRole.PLAYER, invitedById
        );


        // 6. Create team member and save the result
        TeamMemberResponse teamMemberResponse = createTeamMember(req, invitedById);
        
        // 7. Send the invitation email
        log.info("Sending Team invitation email to: {}", email);
        emailService.sendRequestTOJoinTeam(invitedById, user.getId(), email, teamId);

        log.info("Successfully invited user {} to team {}", user.getId(), teamId);
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
    public TeamMemberInviteResponse respondToInvitation(UUID teamMemberId, TeamStatus request) throws AppException {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

        // Check if the current status is PENDING (only PENDING invitations can be updated)
        if (teamMember.getStatus() != TeamStatus.PENDING) {
            throw new AlreadyExistsException(ErrorCode.TEAM_MEMBER_RESPONSE_ALREADY_EXISTS);
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
        TeamMemberInviteResponse response = new TeamMemberInviteResponse(
                teamMember.getId(),
                teamMember.getRole(),
                teamMember.getStatus(),
                teamMember.getUser().getId(),
                teamMember.getTeam().getId(),
                teamMember.getUser().getUserName(),
                teamMember.getTeam().getName()
        );

        // send email notification to the organizer to tell him that the team member accepted or rejected the team invitation
        // in
        emailService.sendResponseToTeamMemberInvitation(teamMember, request);

        return response;
    }
}