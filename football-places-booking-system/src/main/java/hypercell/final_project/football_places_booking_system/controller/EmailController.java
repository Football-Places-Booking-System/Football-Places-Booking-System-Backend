package hypercell.final_project.football_places_booking_system.controller;

import hypercell.final_project.football_places_booking_system.service.Impl.EmailServiceImpl;
import hypercell.final_project.football_places_booking_system.service.Interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
// Endpoints for testing email functionality.

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailServiceImpl emailService;

    @RequestMapping("api/send-team-invitation-email")
    public String MockSendHtmlTeamRequestEmail() {
        try {
            UUID teamMemberId = UUID.fromString("9dd246b0-861e-405d-a2fd-543b30b8154d");
            return emailService.sendHtmlTeamRequestEmail("Omar Organizer", "HyperCell", "A team for football enthusiasts",
                    "omar.saad@ieeecusb.org", "Omar Saad", teamMemberId);

        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }


}
