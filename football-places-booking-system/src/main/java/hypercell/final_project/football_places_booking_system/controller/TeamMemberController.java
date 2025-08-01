package hypercell.final_project.football_places_booking_system.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamInvitationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberInviteResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@AllArgsConstructor
@Data
@RequestMapping("/api/team-members")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    // @PreAuthorize("@authService.hasTeamRole(#teamId, 'ORGANIZER')")
    // add teamId as a path variable
    @PostMapping
    public ResponseEntity<TeamMemberResponse> createTeamMember(
            @Valid @RequestBody TeamMemberCreationRequest request) throws AppException {
        TeamMemberResponse response = teamMemberService.createTeamMember(request, request.userId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberResponse> getTeamMemberById(
            @PathVariable UUID id) {
        TeamMemberResponse response = teamMemberService.getTeamMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/{teamId}")
    // Retrieves all team members by team ID
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembersByTeam(
            @PathVariable UUID teamId) {
        List<TeamMemberResponse> responses = teamMemberService.getTeamMembersByTeam(teamId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembersByUser(
            @PathVariable UUID userId) {
        List<TeamMemberResponse> responses = teamMemberService.getTeamMembersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping
    public ResponseEntity<TeamMemberResponse> updateTeamMember(
            @Valid @RequestBody TeamMemberUpdateRequest request) {
        TeamMemberResponse response = teamMemberService.updateTeamMember(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@authService.hasTeamRole(#teamId, 'ORGANIZER')")
    @PostMapping("/invite/{teamId}")
    public ResponseEntity<TeamMemberResponse> inviteByEmail(
            @PathVariable UUID teamId,
            @Valid @RequestBody TeamInvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        TeamMemberResponse response = teamMemberService.inviteByEmail(request.email(), teamId, (User) userDetails);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint to accept or reject an invitation
    @GetMapping("/invitation/{teamMemberId}")
    public ResponseEntity<?> respondToInvitation(
            @PathVariable UUID teamMemberId,
            @RequestParam("status") TeamStatus request,
            @RequestParam(value = "redirect", defaultValue = "false") boolean shouldRedirect
            ) throws AppException {

        System.out.println("Responding to invitation for team member ID: " + teamMemberId + " with request: " + request);

        TeamMemberInviteResponse response = teamMemberService.respondToInvitation(teamMemberId, request);

        if (shouldRedirect) {
            // For email clicks - redirect to Angular frontend
            String redirectUrl = "http://localhost:4200/invitation-response?status=" + request.name().toLowerCase();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } else {
            // For frontend API calls - return JSON response
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/join-request/{teamId}")
    public ResponseEntity<TeamMemberResponse> requestToJoinTeam(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        TeamMemberResponse response = teamMemberService.requestToJoinTeam(teamId, (User) userDetails);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/join-request/respond/{teamMemberId}")  
    public ResponseEntity<TeamMemberResponse> respondToJoinRequest(
            @PathVariable UUID teamMemberId,
            @RequestParam TeamStatus status,
            @AuthenticationPrincipal UserDetails principal) throws AppException {
        return ResponseEntity.ok(
            teamMemberService.respondToJoinRequest(teamMemberId, status, (User) principal));
    }

    @GetMapping("/join-requests/{teamId}")
    public ResponseEntity<List<TeamMemberResponse>> listPendingRequests(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails principal) throws AppException {
        User organiser = (User) principal;                     // make sure caller is organizer
        if (!teamMemberService.isOrganizer(organiser.getId(), teamId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(teamMemberService.getPendingJoinRequests(teamId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User requester = (User) userDetails;
        teamMemberService.deleteTeamMember(id, requester.getId());
        return ResponseEntity.noContent().build();
    }
}