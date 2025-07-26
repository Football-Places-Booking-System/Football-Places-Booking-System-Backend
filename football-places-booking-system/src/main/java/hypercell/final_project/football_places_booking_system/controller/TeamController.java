package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;

import hypercell.final_project.football_places_booking_system.model.db.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamCreationRequest;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.TeamResponse;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    @PostMapping
//    creates a new team and puts creator as organzier and registers it in teamMember
    public ResponseEntity<TeamResponse> createTeam(
            @RequestBody @Valid TeamCreationRequest request,
            @AuthenticationPrincipal User user) {
       // Long creatorId = Long.parseLong(user.getUsername());
        TeamResponse response = teamService.createTeam(request, user.getId());
        return ResponseEntity.ok(response);
    }
    //getTema by id
    @GetMapping("/{id}")
    public TeamResponse getTeam(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @GetMapping
    //gets all teams in db
    public List<TeamResponse> getAllTeams() {
        return teamService.getAllTeams();
    }

    @PutMapping("/{id}")
    //updates team by id, only organizer can update
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @RequestBody @Valid TeamCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        TeamResponse response = teamService.updateTeam(id, request, user.getId());
        return ResponseEntity.ok(response);
    }
    //TODO: Fix 403 Forbidden bug when tryint to delete team by id.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        teamService.deleteTeam(id, user.getId());
        return ResponseEntity.noContent().build();
    }
    //gets all teams by user signed in
    @GetMapping("/my-teams")
    public ResponseEntity<List<TeamResponse>> getUserTeams(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(teamService.getTeamsByUser(user.getId()));
    }
}
