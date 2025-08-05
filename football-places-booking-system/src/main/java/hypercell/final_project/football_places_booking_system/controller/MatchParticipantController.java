package hypercell.final_project.football_places_booking_system.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDetailRespDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.UserMatchResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.ForbiddenActionException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDetailRespDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartMapper;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.service.Impl.BookingMatchServiceImpl;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import hypercell.final_project.football_places_booking_system.service.Impl.MatchParticipantServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/match-participants")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantServiceImpl matchParticipantService;
    private final TeamMemberService teamMemberService;
    private final BookingMatchServiceImpl bookingMatchService;

    /**
     * Invite a user to participate in a match.
     * Only ORGANIZERs in the team can send invitations.
     */
    @PostMapping("/invite/{bookingMatchId}")
    public ResponseEntity<MatchPartResponseDTO> invite(
            @PathVariable UUID bookingMatchId,
            @RequestBody InvitationRequest dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {

        User inviter = (User) userDetails;

        // Fetch booking match via BookingMatchService
        var bookingMatch = bookingMatchService.getById(bookingMatchId);
        UUID teamId = bookingMatch.getTeam().getId();

        // Check if inviter is organizer
        if (!teamMemberService.isOrganizer(inviter.getId(), teamId)) {
            throw new ForbiddenActionException(ErrorCode.FORBIDDEN);
        }

        var participant = matchParticipantService.inviteParticipant(dto, bookingMatchId);
        return new ResponseEntity<>(MatchPartMapper.toResponseDTO(participant), HttpStatus.CREATED);
    }

    // Endpoint to accept or reject invitation via frontend
    @GetMapping("/respond/{matchParticipantId}")
    public void respond(
            @PathVariable UUID matchParticipantId,
            @RequestParam ParticipantStatus status
    ) throws AppException {
        matchParticipantService.respondToInvitation(matchParticipantId, status);
    }

    // Endpoint to accept or reject invitation via mail
    @GetMapping("/respond-mail/{id}")
    public ResponseEntity<Void> respondMail(
            @PathVariable UUID id,
            @RequestParam ParticipantStatus status
    ) throws AppException {
        matchParticipantService.respondToInvitation(id, status);
        return ResponseEntity.status(HttpStatus.FOUND)
                // change url
                .location(URI.create("http://localhost:4200/dashboard/matches"))
                .build();
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

    @PostMapping("/join-as-organizer/{matchId}")
    public ResponseEntity<MatchPartResponseDTO> joinAsOrganizer(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User organizer = (User) userDetails;

        var participant = matchParticipantService.joinMatchAsOrganizer(matchId, organizer.getId());

        return new ResponseEntity<>(
                MatchPartMapper.toResponseDTO(participant),
                HttpStatus.CREATED
        );
    }


    @GetMapping("/user/matches")
    public ResponseEntity<List<UserMatchResponseDTO>> getMatchesByUserDetails(
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;

        var matches = matchParticipantService.getUserParticipatedMatches(currentUser.getId());

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/user/matches/details")
    public ResponseEntity<List<BookingDetailRespDTO>> getUserMatchesDetailed(
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;
        var matches = matchParticipantService.getUserParticipatedMatchesDetailed(currentUser.getId());
        return ResponseEntity.ok(matches);
    }
}