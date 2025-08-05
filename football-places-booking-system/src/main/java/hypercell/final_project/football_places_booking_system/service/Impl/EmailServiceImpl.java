package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public CompletableFuture<Void> sendInviteToJoinTeam(User invitedBy, User inviteeUser, String inviteToEmail, Team team, UUID teamMemberId) throws AppException {
        try {
            // Get the inviter's name
            String invitedByName = invitedBy.getUserName();

            String teamName = team.getName();
            String teamDescription = team.getDescription();

            // Get invitee's name
            String toName = inviteeUser.getUserName();
            
            // Send the email asynchronously
            return sendHtmlTeamInviteEmail(invitedByName, teamName, teamDescription, inviteToEmail, toName, teamMemberId);
            
            // Send the email asynchronously
            // return sendHtmlTeamInviteEmail(invitedByName, teamName, teamDescription, inviteToEmail, toName, teamMember.getId());


        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e));
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> sendHtmlTeamInviteEmail(String invitedByName, String teamName, String teamDescription, String inviteToEmail, String toName, UUID teamMemberId) throws AppException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(inviteToEmail);
            helper.setSubject("Invite to Join Team " + teamName);

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("invitedByName", invitedByName);
            context.setVariable("teamName", teamName);
            context.setVariable("teamDescription", teamDescription);
            context.setVariable("toName", toName);
            context.setVariable("invitationApi", "http://localhost:8080/api/team-members/respond-mail/" + teamMemberId);

            // Process the template
            String htmlContent = templateEngine.process("team-invitation-email-content", context);
            helper.setText(htmlContent, true);

//            helper.addInline("database.png", new File("E:\\HyperCell\\WorkSpace\\Try_Repos\\javamail\\src\\main\\resources\\static\\RE.png"));

            mailSender.send(message);
            return CompletableFuture.completedFuture(null);

//            return "Email sent successfully!";
        }
        catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e));
            return future;
//            return e.getMessage();
        }

    }

    @Override
    public CompletableFuture<Void> sendResponseToTeamMemberInvitation(TeamMember teamMember, TeamStatus response) {
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
            sendHtmlTeamResponseEmail(teamMemberName, teamName, organizerName, organizerEmail, response);
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send team response email: " + e.getMessage(), e));
            return future;
        }
    }

    private void sendHtmlTeamResponseEmail(String teamMemberName, String teamName, String organizerName, String organizerEmail, TeamStatus responseStatus) {
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

    @Override
    public CompletableFuture<Void> sendRequestToJoinTeam(User user, Team team, UUID teamMemberId) throws AppException {
        try {
            String userName = user.getUserName();

            sendHtmlTeamRequestEmail(userName, team, teamMemberId);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send team request email: " + e.getMessage(), e));
            return future;
        }
    }

    private void sendHtmlTeamRequestEmail(String name, Team team, UUID teamMemberId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(team.getCreator().getEmail());
            helper.setSubject("Request to Join Team " + team.getName());

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("teamName", team.getName());
            context.setVariable("teamDescription", team.getDescription());
            context.setVariable("api", "http://localhost:8080/api/team-members/join-request/respond-mail/" + teamMemberId + "/" + team.getCreator().getId());

            String htmlContent = templateEngine.process("team-request-email-content", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to send team invitation email: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<Void> sendResponseToJoinRequest(TeamMember teamMember, TeamStatus response) {
        try {
            Team team = teamRepository.findById(teamMember.getTeam().getId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            String teamName = team.getName();

            String email = teamMember.getUser().getEmail();
            String teamMemberName = teamMember.getUser().getUserName();

            sendHtmlJoinRequestResponseEmail(teamMemberName, teamName, email, response);
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send join request response email: " + e.getMessage(), e));
            return future;
        }
    }

    private void sendHtmlJoinRequestResponseEmail(String teamMemberName, String teamName, String email, TeamStatus responseStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(email);

            String subject = "Team Join Request " + (responseStatus == TeamStatus.APPROVED ? "Accepted" : "Rejected") + " - " + teamName;
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("teamMemberName", teamMemberName);
            context.setVariable("teamName", teamName);
            context.setVariable("responseStatus", responseStatus);
            context.setVariable("isAccepted", responseStatus == TeamStatus.APPROVED);
            context.setVariable("statusText", responseStatus == TeamStatus.APPROVED ? "accepted" : "rejected");
            context.setVariable("statusColor", responseStatus == TeamStatus.APPROVED ? "#27ae60" : "#e74c3c");

            String htmlContent = templateEngine.process("team-request-response", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send team response email: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<Void> sendInvitationEmail(String email, MatchParticipant matchParticipant) {
        try {
            // Get match details
            String placeName = matchParticipant.getBookingMatch().getPlace().getName();
            String placeImageUrl = matchParticipant.getBookingMatch().getPlace().getImageUrl();
            
            // Format date and time for better readability
            String matchDate = matchParticipant.getBookingMatch().getStartTime().toLocalDate().toString();
            String weekDay = matchParticipant.getBookingMatch().getStartTime().getDayOfWeek().toString();
            
            // Format start time
            int startHour = matchParticipant.getBookingMatch().getStartTime().getHour();
            String startAmPm = startHour >= 12 ? "PM" : "AM";
            int startDisplayHour = startHour == 0 ? 12 : (startHour > 12 ? startHour - 12 : startHour);
            String matchStartTime = String.format("%d %s", startDisplayHour, startAmPm);
            
            // Format end time
            int endHour = matchParticipant.getBookingMatch().getEndTime().getHour();
            String endAmPm = endHour >= 12 ? "PM" : "AM";
            int endDisplayHour = endHour == 0 ? 12 : (endHour > 12 ? endHour - 12 : endHour);
            String matchEndTime = String.format("%d %s", endDisplayHour, endAmPm);

            // Get participant's name
            String participantName = matchParticipant.getUser().getUserName();

            // Get match organizer (the user who created the booking match)
            String organizerName = matchParticipant.getBookingMatch().getUser().getUserName();
            
            // Send the email asynchronously
            sendHtmlMatchInvitationEmail(organizerName, placeName, placeImageUrl, matchDate, weekDay, matchStartTime, matchEndTime, email, participantName, matchParticipant.getId());
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send match invitation email: " + e.getMessage(), e));
            return future;
        }
    }

    private void sendHtmlMatchInvitationEmail(String organizerName, String placeName, String placeImageUrl, String matchDate, String weekDay, String matchStartTime, String matchEndTime, String email, String participantName, UUID participantId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(email);
            helper.setSubject("Match Invitation - " + placeName);

            // placeImageUrl = "https://drive.google.com/file/d/1SAgSaiE3llePtK7pyq7ER9zBxb5nJ7Yd/view?usp=sharing"; // Placeholder for the image URL
//            placeImageUrl = "https://tse2.mm.bing.net/th/id/OIP.8bPgs9SeX1oHf7Sxe7hQkwHaEK?rs=1&pid=ImgDetMain&o=7&rm=3"; // Placeholder for the image URL

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("organizerName", organizerName);
            context.setVariable("placeName", placeName);
            context.setVariable("placeImageUrl", placeImageUrl);
            context.setVariable("matchDate", matchDate);
            context.setVariable("weekDay", weekDay);
            context.setVariable("matchStartTime", matchStartTime);
            context.setVariable("matchEndTime", matchEndTime);
            context.setVariable("participantName", participantName);
            context.setVariable("participantId", participantId);
            context.setVariable("invitationApi", "http://localhost:8080/api/match-participants/respond-mail/" + participantId);

            // Process the template
            String htmlContent = templateEngine.process("match-invitation-email-content", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send match invitation email: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<Void> sendResponseToMatchParticipantInvitation(MatchParticipant matchParticipant, ParticipantStatus response) {
        try {
            // Get participant details
            String participantName = matchParticipant.getUser().getUserName();

            // Get match details
            String placeName = matchParticipant.getBookingMatch().getPlace().getName();
            
            // Format date and time for better readability
            String matchDate = matchParticipant.getBookingMatch().getStartTime().toLocalDate().toString();
            String weekDay = matchParticipant.getBookingMatch().getStartTime().getDayOfWeek().toString();
            
            // Format start time
            int startHour = matchParticipant.getBookingMatch().getStartTime().getHour();
            String startAmPm = startHour >= 12 ? "PM" : "AM";
            int startDisplayHour = startHour == 0 ? 12 : (startHour > 12 ? startHour - 12 : startHour);
            String matchStartTime = String.format("%d %s", startDisplayHour, startAmPm);

            // Get organizer details (the one who created the booking match)
            User organizer = matchParticipant.getBookingMatch().getUser();
            String organizerName = organizer.getUserName();
            String organizerEmail = organizer.getEmail();

            // Send the response email to the organizer who created the match
            sendHtmlMatchResponseEmail(participantName, placeName, matchDate, weekDay, matchStartTime, organizerName, organizerEmail, response);
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to send match response email: " + e.getMessage(), e));
            return future;
        }
    }

    private void sendHtmlMatchResponseEmail(String participantName, String placeName, String matchDate, String weekDay, String matchStartTime, String organizerName, String organizerEmail, ParticipantStatus responseStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("football.booking.system@gmail.com");
            helper.setTo(organizerEmail);

            String subject = "Match Invitation " + (responseStatus == ParticipantStatus.ACCEPTED ? "Accepted" : "Declined") + " - " + placeName;
            helper.setSubject(subject);

            // Create Thymeleaf context with variables
            Context context = new Context();
            context.setVariable("participantName", participantName);
            context.setVariable("placeName", placeName);
            context.setVariable("matchDate", matchDate);
            context.setVariable("weekDay", weekDay);
            context.setVariable("matchStartTime", matchStartTime);
            context.setVariable("organizerName", organizerName);
            context.setVariable("responseStatus", responseStatus);
            context.setVariable("isAccepted", responseStatus == ParticipantStatus.ACCEPTED);
            context.setVariable("statusText", responseStatus == ParticipantStatus.ACCEPTED ? "accepted" : "declined");
            context.setVariable("statusColor", responseStatus == ParticipantStatus.ACCEPTED ? "#27ae60" : "#e74c3c");

            // Process the template
            String htmlContent = templateEngine.process("match-response-email-content", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send match response email: " + e.getMessage(), e);
        }
    }


}