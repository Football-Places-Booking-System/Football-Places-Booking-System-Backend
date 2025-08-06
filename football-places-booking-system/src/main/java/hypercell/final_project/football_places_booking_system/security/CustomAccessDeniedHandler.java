package hypercell.final_project.football_places_booking_system.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String reason = (String) request.getAttribute("ERROR");

        if (reason == null) {
                response.getWriter().write(
                    String.format("{\"code\":%d,\"msg\":\"%s\"}",
                        ErrorCode.FORBIDDEN.getCode(),
                        ErrorCode.FORBIDDEN.getMsg())
                );
        }
        else {
            switch (reason) {
                case "NOT_ORGANIZER" -> response.getWriter().write(
                            String.format("{\"code\":%d,\"msg\":\"%s\"}",
                                    ErrorCode.FORBIDDEN_ROLE.getCode(),
                                    ErrorCode.FORBIDDEN_ROLE.getMsg())
                    );
                case "NOT_ACTIVE" -> response.getWriter().write(
                            String.format("{\"code\":%d,\"msg\":\"%s\"}",
                                    ErrorCode.FORBIDDEN_STATUS.getCode(),
                                    ErrorCode.FORBIDDEN_STATUS.getMsg())
                    );
                default -> response.getWriter().write(
                            String.format("{\"code\":%d,\"msg\":\"%s\"}",
                                    ErrorCode.FORBIDDEN.getCode(),
                                    ErrorCode.FORBIDDEN.getMsg())
                    );
            }
        }
    }
}
