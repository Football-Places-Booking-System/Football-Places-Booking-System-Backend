package hypercell.final_project.football_places_booking_system.controller;

import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberResponse;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamMemberUpdateRequest;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Data
@RequestMapping("/api/team-members")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity<TeamMemberResponse> createTeamMember(
            @Valid @RequestBody TeamMemberCreationRequest request) {
        TeamMemberResponse response = teamMemberService.createTeamMember(request, request.userId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberResponse> getTeamMemberById(
            @PathVariable Long id) {
        TeamMemberResponse response = teamMemberService.getTeamMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/{teamId}")
    // Retrieves all team members by team ID
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembersByTeam(
            @PathVariable Long teamId) {
        List<TeamMemberResponse> responses = teamMemberService.getTeamMembersByTeam(teamId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembersByUser(
            @PathVariable Long userId) {
        List<TeamMemberResponse> responses = teamMemberService.getTeamMembersByUserId(userId);
        return ResponseEntity.ok(responses);
    }
    @PutMapping
    public ResponseEntity<TeamMemberResponse> updateTeamMember(
            @Valid @RequestBody TeamMemberUpdateRequest request) {
        TeamMemberResponse response = teamMemberService.updateTeamMember(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(
            @PathVariable Long id) {
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }
}