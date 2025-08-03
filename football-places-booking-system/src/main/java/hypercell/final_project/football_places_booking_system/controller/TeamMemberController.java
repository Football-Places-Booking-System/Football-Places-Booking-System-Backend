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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
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
        System.out.println("Inside get team members by team ID: " + teamId + " ...");
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
            @Valid @RequestBody InvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        TeamMemberResponse response = teamMemberService.inviteByEmail(request.email(), teamId, (User) userDetails);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint to accept or reject an invitation
    @PatchMapping("/respond/{teamMemberId}")
    public ResponseEntity<?> respondToInvitation(
            @PathVariable UUID teamMemberId,
            @RequestParam("status") TeamStatus request
    ) throws AppException {

        System.out.println("Responding to invitation for team member ID: " + teamMemberId + " with request: " + request);

        TeamMemberInviteResponse response = teamMemberService.respondToInvitation(teamMemberId, request);

        return ResponseEntity.ok(response);

    }

    // Endpoint to accept or reject an invitation
    @GetMapping("/respond-mail/{teamMemberId}")
    public ResponseEntity<Void> respondToInvitationMail(
            @PathVariable UUID teamMemberId,
            @RequestParam("status") TeamStatus request) throws AppException {


        teamMemberService.respondToInvitation(teamMemberId, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:4200/"))
                .build();
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
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User user = (User) userDetails;

        return ResponseEntity.ok(teamMemberService.respondToJoinRequest(teamMemberId, status, user.getId()));
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

    @GetMapping("/isOrganizer/{teamId}")
    public ResponseEntity<Boolean> getTeamMember(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User user = (User) userDetails;

        boolean isOrganizer = teamMemberService.isOrganizer(user.getId(), teamId);
        return ResponseEntity.ok(isOrganizer);
    }

}