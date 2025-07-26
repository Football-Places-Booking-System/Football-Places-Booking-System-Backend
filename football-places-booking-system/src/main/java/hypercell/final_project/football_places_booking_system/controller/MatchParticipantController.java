package hypercell.final_project.football_places_booking_system.controller;

import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.dto.MatchParticipantDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.service.MatchParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for managing match participants (inviting, responding, listing).
@RestController
@RequestMapping("/api/match-participants")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantService service;

    // Invites a user to participate in a match.
    @PostMapping("/invite")
    public ResponseEntity<MatchParticipant> invite(@RequestBody MatchParticipantDTO dto) {
        return ResponseEntity.ok(service.inviteParticipant(dto));
    }

    // Updates the participant's response to an invitation (accept/decline).
    @PutMapping("/{id}/respond")
    public ResponseEntity<MatchParticipant> respond(
            @PathVariable Long id,
            @RequestParam ParticipantStatus status
    ) {
        return ResponseEntity.ok(service.respondToInvitation(id, status));
    }

    // Lists all participants for a specific match.
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchParticipant>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(service.getByMatch(matchId));
    }
}
