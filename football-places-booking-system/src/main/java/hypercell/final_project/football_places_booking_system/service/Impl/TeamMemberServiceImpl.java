package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberInviteResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.repository.RequestRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamServiceImpl teamService;
    private final EmailServiceImpl emailService;
    private final RequestService requestService;
    private final RequestRepository requestRepository;


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
                invitedById, teamId, TeamRole.ORGANIZER)) {
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
        
        // 7. Get inviter user information for the message
        User inviterUser = userRepository.findById(invitedById)
                .orElseThrow(() -> {
                    log.warn("Inviter user not found with ID: {}", invitedById);
                    return new NotFoundException(ErrorCode.USER_NOT_FOUND);
                });
        
        // 8. Create Request entity for the invitation with meaningful message
        String invitationMessage = String.format("%s has invited you to join Team %s", 
                inviterUser.getUserName(), team.getName());
        requestService.createRequestWithMessage(invitedById, user.getId(), RequestType.JOIN_TEAM_INVITATION, invitationMessage);
        
        // 8. Send the invitation email
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
        
        // Save the team member
        teamMember = teamMemberRepository.save(teamMember);
        
        // Update the Request entity status with meaningful response message
        ResponseStatus responseStatus = request == TeamStatus.APPROVED ? ResponseStatus.ACCEPTED : ResponseStatus.REJECTED;
        
        // Find the request by sender (inviter) and receiver (team member) and type
        UUID inviterId = teamMember.getInvitedBy().getId();
        UUID receiverId = teamMember.getUser().getId();
        
        // Create meaningful response message
        String responseMessage = String.format("Your invitation to join Team %s has been %s by %s",
                teamMember.getTeam().getName(),
                (request == TeamStatus.APPROVED) ? "accepted" : "rejected",
                teamMember.getUser().getUserName());
        
        // Find and update the request with response message
        requestRepository.findBySenderIdAndReceiverIdAndRequestType(inviterId, receiverId, RequestType.JOIN_TEAM_INVITATION)
                .ifPresent(existingRequest -> {
                    try {
                        requestService.updateRequestStatusWithMessage(existingRequest.getId(), responseStatus, responseMessage);
                    } catch (AppException e) {
                        log.warn("Failed to update request entity with response message: {}", e.getMessage());
                        // Fallback to status-only update
                        try {
                            requestService.updateRequestStatus(existingRequest.getId(), responseStatus);
                        } catch (AppException fallbackException) {
                            throw new RuntimeException(fallbackException);
                        }
                    }
                });
        
        // Map to response
        TeamMemberInviteResponse response = new TeamMemberInviteResponse(
                teamMember.getId(),
                teamMember.getRole(),
                teamMember.getStatus(),
                teamMember.getUser().getId(),
                teamMember.getTeam().getId(),
                teamMember.getUser().getUserName(),
                teamMember.getTeam().getName()
        );

        // send an email notification to the organizer to tell him that the team member accepted or rejected the team invitation
        emailService.sendResponseToTeamMemberInvitation(teamMember, request);

        return response;
    }

    @Override
    public TeamMemberResponse requestToJoinTeam(UUID teamId, UUID userId) throws AppException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Check if already a member
        if (teamMemberRepository.existsByTeamAndUser(team, user)) {
            throw new ValidationException(ErrorCode.TEAM_MEMBER_ALREADY_EXISTS);
        }

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(TeamRole.PLAYER)
                .status(TeamStatus.PENDING)
                .build();

        teamMember = teamMemberRepository.save(teamMember);
        
        // Create Request entity for the join request with meaningful message
        String requestMessage = String.format("%s is asking to join %s", 
                user.getUserName(), team.getName());
        requestService.createRequestWithMessage(userId, team.getCreator().getId(), RequestType.JOIN_TEAM_REQUEST, requestMessage);
        
        return mapToTeamMemberResponse(teamMember);
    }

    @Override
    public List<TeamMemberResponse> getPendingJoinRequests(UUID teamId) throws AppException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        List<TeamMember> pendingMembers = teamMemberRepository.findByTeamAndStatus(team, TeamStatus.PENDING);
        return pendingMembers.stream()
                .map(this::mapToTeamMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamMemberResponse respondToJoinRequest(UUID teamMemberId, TeamStatus response, UUID organizerId) throws AppException {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_MEMBER_NOT_FOUND));

        Team team = teamMember.getTeam();

        // Only team creator (organizer) can respond
        if (!team.getCreator().getId().equals(organizerId)) {
            throw new ValidationException(ErrorCode.FORBIDDEN);
        }

        // Ensure request is still pending
        if (teamMember.getStatus() != TeamStatus.PENDING) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_STATUS);
        }

        // Accept/Reject
        if (response == TeamStatus.APPROVED || response == TeamStatus.REJECTED) {
            teamMember.setStatus(response);
            
            // Update request entity with response message
            try {
                User responderUser = userRepository.findById(organizerId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
                
                ResponseStatus requestStatus = (response == TeamStatus.APPROVED) ? 
                        ResponseStatus.ACCEPTED : ResponseStatus.REJECTED;
                String responseMessage = String.format("Your request to join Team %s has been %s by %s",
                        team.getName(), 
                        (response == TeamStatus.APPROVED) ? "accepted" : "rejected",
                        responderUser.getUserName());
                
                // Find the request by searching for requests from team member to organizer for team join request
                List<Request> senderRequests = requestService.getRequestsBySender(teamMember.getUser().getId());
                UUID organizerUserId = organizerId;
                
                for (Request request : senderRequests) {
                    if (request.getReceiver().getId().equals(organizerUserId) && 
                        request.getRequestType() == RequestType.JOIN_TEAM_REQUEST &&
                        request.getStatus() == ResponseStatus.PENDING) {
                        requestService.updateRequestStatusWithMessage(request.getId(), requestStatus, responseMessage);
                        break;
                    }
                }
            } catch (AppException e) {
                log.warn("Failed to update request entity for team join request response: {}", e.getMessage());
            }
        } else {
            throw new ValidationException(ErrorCode.INVALID_TEAM_STATUS);
        }

        teamMemberRepository.save(teamMember);
        return mapToTeamMemberResponse(teamMember);
    }
}