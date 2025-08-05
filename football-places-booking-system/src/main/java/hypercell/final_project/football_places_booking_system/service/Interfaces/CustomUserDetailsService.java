package hypercell.final_project.football_places_booking_system.service.Interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService {
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

}
