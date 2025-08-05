package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    @PreAuthorize("@authService.is('ACTIVE')")
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @RequestBody TeamCreationRequest request,
            @AuthenticationPrincipal User user) throws AppException {
        TeamResponse response = teamService.createTeam(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/{id}")
    public TeamResponse getTeam(@PathVariable UUID id) throws AppException {
        return teamService.getTeamById(id);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all-filtered")
    public Page<TeamResponse> filterTeams(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws AppException {
        Pageable pageable = PageRequest.of(page, size);
        return teamService.filterTeams(name, description, pageable);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all")
    public List<TeamResponse> getAllTeams() throws AppException {
        return teamService.getAllTeams();
    }
    
    @PreAuthorize("@authService.is('ACTIVE') and @authService.hasTeamRole(#id, 'ORGANIZER')")
    @PatchMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam( 
            @PathVariable UUID id,
            @RequestBody TeamCreationRequest request) throws AppException {
        TeamResponse response = teamService.updateTeam(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@authService.is('ACTIVE') and @authService.hasTeamRole(#id, 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteTeam(
            @PathVariable UUID id) throws AppException {
        return teamService.deleteTeam(id);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/my-teams")
    public ResponseEntity<Page<TeamResponse>> getUserTeams(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AppException {

        User user = (User) userDetails;
        return ResponseEntity.ok(teamService.getTeamsByUser(user.getId(), page, size));
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/other-teams")
    public ResponseEntity<Page<TeamResponse>> getOtherTeams(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AppException {

        User user = (User) userDetails;
        return ResponseEntity.ok(teamService.getOtherTeams(user.getId(), page, size));
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/isOrganizer/{teamId}")
    public ResponseEntity<Boolean> getTeamMember(
            @PathVariable UUID teamId,
            @AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User user = (User) userDetails;

        boolean isOrganizer = teamMemberService.isOrganizer(user.getId(), teamId);
        return ResponseEntity.ok(isOrganizer);
    }
}
