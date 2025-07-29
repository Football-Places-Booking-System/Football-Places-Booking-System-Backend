package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.*;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTeamMember(
//            @PathVariable UUID id) {
//        teamMemberService.deleteTeamMember(id);
//        return ResponseEntity.noContent().build();
//    }
    @PostMapping("/invite/{teamId}")
    public ResponseEntity<TeamMemberResponse> inviteByEmail(
            @PathVariable UUID teamId,
            @Valid @RequestBody TeamInvitationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, ValidationException {
        User inviter = (User) userDetails;
        if (!teamMemberService.isOrganizer(inviter.getId(), teamId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        TeamMemberResponse response = teamMemberService.inviteByEmail(request.email(), teamId, inviter.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, ValidationException {
        User requester = (User) userDetails;
        teamMemberService.deleteTeamMember(id, requester.getId());
        return ResponseEntity.noContent().build();
    }

    // Endpoint to accept or reject an invitation
    @GetMapping("/invitation/{teamMemberId}")
    public ResponseEntity<TeamMemberInviteResponse> respondToInvitation(
            @PathVariable UUID teamMemberId,
            @RequestParam("status") TeamStatus request
            ) throws AppException {

        System.out.println("Responding to invitation for team member ID: " + teamMemberId + " with request: " + request);

        TeamMemberInviteResponse response = teamMemberService.respondToInvitation(teamMemberId, request);

        return ResponseEntity.ok(response);
    }
}