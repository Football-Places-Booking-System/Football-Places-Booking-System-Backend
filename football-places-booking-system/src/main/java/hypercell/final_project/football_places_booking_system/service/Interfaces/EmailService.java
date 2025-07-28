package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;

public interface EmailService {

    // String sendRequestEmail(String to, String subject, String body);
    
    // String sendRequestEmail(String emailTo, String teamName, String fromName);
    String sendRequestTOJoinTeam(UUID invitedById, UUID inviteToId, String inviteToEmail, UUID teamId);

    //        get team_member_id by user and team
//        send_email( email ,team_member_id)


    // String sendHtmlEmail(String to, String subject, String templateName, Object model);

//    String sendHtmlEmailWithInlineImage(String to, String subject, String templateName, Object model, String imagePath);
}
