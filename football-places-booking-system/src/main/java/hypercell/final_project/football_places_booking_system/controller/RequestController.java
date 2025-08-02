package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.db.User;
import lombok.RequiredArgsConstructor;

import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestService requestService;
    
    @GetMapping("/received/{receiverId}")
    public List<Request> getReceivedRequests(@PathVariable UUID receiverId) throws AppException {
        return requestService.getRequestsByReceiver(receiverId);
    }

    @GetMapping("/received")
    public List<Request> getReceivedRequests(@AuthenticationPrincipal UserDetails userDetails) throws AppException {
        User user = (User) userDetails;
//        receiverId = user.getId()

        return requestService.getRequestsByReceiver(user.getId());
    }
}
