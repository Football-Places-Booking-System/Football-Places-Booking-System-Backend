package hypercell.final_project.football_places_booking_system.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        System.out.println("emaillllllllllllllllllllllllllllllllllllllllllllllllll" + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUUser not found with email: " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }
}
