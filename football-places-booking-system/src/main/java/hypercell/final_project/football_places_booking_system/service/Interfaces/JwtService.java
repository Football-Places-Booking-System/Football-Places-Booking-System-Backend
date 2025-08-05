package hypercell.final_project.football_places_booking_system.service.Interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    String generateToken(UserDetails userDetails);
    boolean isTokenExpired(String token);
    Claims extractAllClaims(String token);
}
