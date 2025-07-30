package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.ForbiddenActionException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartMapper;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import hypercell.final_project.football_places_booking_system.service.MatchParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match-participants")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantService matchParticipantService;
    private final TeamMemberService teamMemberService;
    private final BookingMatchService bookingMatchService;

    /**
     * Invite a user to participate in a match.
     * Only ORGANIZERs in the team can send invitations.
     */
    @PostMapping("/invite")
    public ResponseEntity<MatchPartResponseDTO> invite(
            @RequestBody MatchPartDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {

        User inviter = (User) userDetails;

        // Fetch booking match via BookingMatchService
        var bookingMatch = bookingMatchService.getById(dto.bookingMatchId());
        UUID teamId = bookingMatch.getTeam().getId();

        // Check if inviter is organizer
        if (!teamMemberService.isOrganizer(inviter.getId(), teamId)) {
            throw new ForbiddenActionException();
        }

        var participant = matchParticipantService.inviteParticipant(dto);
        return new ResponseEntity<>(MatchPartMapper.toResponseDTO(participant), HttpStatus.CREATED);
    }

    /**
     * Respond to invitation (Accept or Decline).
     */
    @PutMapping("/{id}/respond")
    public ResponseEntity<MatchPartResponseDTO> respond(
            @PathVariable UUID id,
            @RequestParam ParticipantStatus status
    ) throws AppException {
        var participant = matchParticipantService.respondToInvitation(id, status);
        return ResponseEntity.ok(MatchPartMapper.toResponseDTO(participant));
    }

    /**
     *   Get participants for a match.
     * - If the requester is a PLAYER in the team -> show only ACCEPTED participants.
     * - If the requester is an ORGANIZER -> show all participants and statuses.
     */
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchPartResponseDTO>> getByMatch(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {

        User requester = (User) userDetails;

        // Fetch teamId from booking match
        var bookingMatch = bookingMatchService.getById(matchId);
        UUID teamId = bookingMatch.getTeam().getId();

        boolean isOrganizer = teamMemberService.isOrganizer(requester.getId(), teamId);

        var participants = matchParticipantService.getByMatch(matchId).stream()
                .filter(p -> isOrganizer || p.getStatus() == ParticipantStatus.ACCEPTED)
                .map(MatchPartMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(participants);
    }
}
