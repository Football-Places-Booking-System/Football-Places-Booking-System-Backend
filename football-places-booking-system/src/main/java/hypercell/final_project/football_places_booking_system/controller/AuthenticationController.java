package hypercell.final_project.football_places_booking_system.controller;

import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.InvalidCredentialsException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.AuthDTO;
import hypercell.final_project.football_places_booking_system.model.dto.LoginDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.service.Impl.JwtServiceImpl;
import hypercell.final_project.football_places_booking_system.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> register(@RequestBody UserDTO user) throws AppException {
        userService.register(user);

        Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.email(), user.password()));
        User authenticatedUser = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.ok(new AuthDTO(authenticatedUser.getId(), token, authenticatedUser.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody LoginDTO request) throws AppException {

        try {
            Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            User user = (User) auth.getPrincipal();

            // Get us
            if(user.getStatus() == UserStatus.INACTIVE){
                throw new InvalidCredentialsException(ErrorCode.FORBIDDEN_STATUS);
            }
            
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthDTO(user.getId(), token, user.getRole()));
        } 
        catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
