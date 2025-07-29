package hypercell.final_project.football_places_booking_system.service.Impl;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
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
            
            // Get or create team member record
            TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, inviteeUserId)
                    .orElseThrow(() -> new RuntimeException("Team member record not found"));
            
            // Send the email
            sendHtmlTeamRequestEmail(invitedByName, teamName, teamDescription, email, toName, teamMember.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e);
        }
    }

//    public void sendHtmlTeamRequestEmail(String invitedByName, String teamName, String teamDescription, String email, String toName, UUID teamMemberId) {
    public String sendHtmlTeamRequestEmail(String invitedByName, String teamName, String teamDescription, String email, String toName, UUID teamMemberId) {
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

            return "Email sent successfully!";
        }
        catch (Exception e) {
            return e.getMessage();
        }

    }


    //    @RequestMapping("/send-html-email-test-api")
    public String sendHtmlTeamRequestEmail() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("omarsaadelgharbawy@gmail.com");
            helper.setTo("omar.saad@ieeecusb.org");
            helper.setSubject("Java Test Email with Test API From Omar");

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("name", "User");
            context.setVariable("confirmationLink", "http://localhost:8080/test");

            // Process the template
            String htmlContent = templateEngine.process("email-content", context);
            helper.setText(htmlContent, true);

//            helper.addInline("database.png", new File("E:\\HyperCell\\WorkSpace\\Try_Repos\\javamail\\src\\main\\resources\\static\\RE.png"));

            mailSender.send(message);

            return "success!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
