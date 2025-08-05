package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.RequestDTO;
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestService requestService;
    
    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/received")
    public List<RequestDTO> getReceivedRequests(@AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User user = (User) userDetails;
//        receiverId = user.getId()
        System.out.println("Get requests for user: " + user.getId() + "");

        return requestService.getRequestsByReceiver(user.getId());
    }
}
