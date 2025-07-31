package hypercell.final_project.football_places_booking_system.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
                            
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String error = (String) request.getAttribute("auth_error");
        int code = -1;
        String msg = "This is a new error.. debug";

        if ("MISSING_TOKEN".equals(error)) {
            code = ErrorCode.UNAUTHORIZED.getCode();
            msg = ErrorCode.UNAUTHORIZED.getMsg();
        } else if ("INVALID_TOKEN".equals(error)) {
            code = ErrorCode.INVALID_TOKEN.getCode();
            msg = ErrorCode.INVALID_TOKEN.getMsg();
        }

        response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + msg + "\"}");
    }
}
