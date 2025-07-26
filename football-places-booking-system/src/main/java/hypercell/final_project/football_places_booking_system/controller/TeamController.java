package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;

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

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @RequestBody @Valid TeamCreationRequest request) {
        TeamMember member = new TeamMember();
        member.setId(1L);
        TeamResponse response = teamService.createTeam(request, member.getId());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public TeamResponse getTeam(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @GetMapping
    public List<TeamResponse> getAllTeams() {
        return teamService.getAllTeams();
    }
}
