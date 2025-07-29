package hypercell.final_project.football_places_booking_system.service.Impl;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

//import javax.naming.Context;
import java.io.File;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;



    public void sendRequestTOJoinTeam(UUID invitedById, UUID inviteeUserId, String email, UUID teamId) {
        try {
            // Get the inviter's name
            String invitedByName = userRepository.findUsernameById(invitedById);
            
            // Get team details
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Team not found"));
                    
            String teamName = team.getName();
            String teamDescription = team.getDescription();
            
            // Get invitee's name
            String toName = userRepository.findUsernameById(inviteeUserId);
            
            // Get a team member record
            TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, inviteeUserId)
                    .orElseThrow(() -> new RuntimeException("Team member record not found"));
            
            // Send the email
            sendHtmlTeamRequestEmail(invitedByName, teamName, teamDescription, email, toName, teamMember.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e);
//            throw new RuntimeException(ErrorCode.EMAIL_SEND_FAILURE);
        }
    }



    public void sendHtmlTeamRequestEmail(String invitedByName, String teamName, String teamDescription, String email, String toName, UUID teamMemberId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(email);
            helper.setSubject("Request to Join Team " + teamName);

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("invitedByName", invitedByName);
            context.setVariable("teamName", teamName);
            context.setVariable("teamDescription", teamDescription);
            context.setVariable("toName", toName);
            context.setVariable("teamMemberId", teamMemberId);
            context.setVariable("invitationApi", "http://localhost:8080/api/team-members/invitation/" + teamMemberId);

            // Process the template
            String htmlContent = templateEngine.process("team-invitation-email-content", context);
            helper.setText(htmlContent, true);

//            helper.addInline("database.png", new File("E:\\HyperCell\\WorkSpace\\Try_Repos\\javamail\\src\\main\\resources\\static\\RE.png"));

            mailSender.send(message);

//            return "Email sent successfully!";
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e);
//            return e.getMessage();
        }

    }


    public void sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus request) {
        try {
            // Get team member details
            String teamMemberName = userRepository.findUsernameById(teamMember.getUser().getId());
            
            // Get team details
            Team team = teamRepository.findById(teamMember.getTeam().getId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            String teamName = team.getName();
            
            // Get organizer details (assuming organizer is the one who invited the team member)
            User organizer = userRepository.findById(teamMember.getInvitedBy().getId())
                    .orElseThrow(() -> new RuntimeException("Team organizer not found"));
            
            String organizerName = organizer.getUserName();
            String organizerEmail = organizer.getEmail();
            
            // Send the response email to the organizer who invited the team member
            sendHtmlTeamResponseEmail(teamMemberName, teamName, organizerName, organizerEmail, request);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send team response email: " + e.getMessage(), e);
        }
    }

    public void sendHtmlTeamResponseEmail(String teamMemberName, String teamName, String organizerName, String organizerEmail, TeamStatus responseStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(organizerEmail);
            
            String subject = "Team Invitation " + (responseStatus == TeamStatus.APPROVED ? "Accepted" : "Rejected") + " - " + teamName;
            helper.setSubject(subject);

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("teamMemberName", teamMemberName);
            context.setVariable("teamName", teamName);
            context.setVariable("organizerName", organizerName);
            context.setVariable("responseStatus", responseStatus);
            context.setVariable("isAccepted", responseStatus == TeamStatus.APPROVED);
            context.setVariable("statusText", responseStatus == TeamStatus.APPROVED ? "accepted" : "rejected");
            context.setVariable("statusColor", responseStatus == TeamStatus.APPROVED ? "#27ae60" : "#e74c3c");

            // Process the template
            String htmlContent = templateEngine.process("team-response-email-content", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send team response email: " + e.getMessage(), e);
        }
    }



}
