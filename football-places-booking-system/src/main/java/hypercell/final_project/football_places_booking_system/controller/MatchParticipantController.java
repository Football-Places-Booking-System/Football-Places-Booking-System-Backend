package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartMapper;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.service.MatchParticipantService;
import lombok.RequiredArgsConstructor;

// Controller for managing match participants (inviting, responding, listing).
@RestController
@RequestMapping("/api/match-participants")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantService service;

    // Invites a user to participate in a match.
    @PostMapping("/invite")
    public ResponseEntity<MatchPartResponseDTO> invite(@RequestBody MatchPartDTO dto) {
        var participant = service.inviteParticipant(dto);
        return ResponseEntity.ok(MatchPartMapper.toResponseDTO(participant));
    }

    // TODO: Use the token to check if the invitation sender is one of the organizers in the team.
    // TODO: Check it from the TeamMemberController.

    @PutMapping("/{id}/respond")
    public ResponseEntity<MatchPartResponseDTO> respond(
            @PathVariable UUID id,
            @RequestParam ParticipantStatus status
    ) {
        var participant = service.respondToInvitation(id, status);
        return ResponseEntity.ok(MatchPartMapper.toResponseDTO(participant));
    }


    // TODO: Check from the token to see if the request sender is a player in the match,
    //  if yes display accepted players only, if an organizer,
    //  display all participants with their invitation statuses.
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchPartResponseDTO>> getByMatch(@PathVariable UUID matchId) {
        var participants = service.getByMatch(matchId)
                .stream()
                .map(MatchPartMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(participants);
    }

}
