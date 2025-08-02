package hypercell.final_project.football_places_booking_system.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AlreadyExistsException;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NoDataException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.exception.NoContentException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import hypercell.final_project.football_places_booking_system.model.dto.PasswordDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BooleanResponseDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserDTO userDTO) throws AppException {
        if (userDTO.username() == null || userDTO.username().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_USERNAME);
        }

        if (userDTO.email() == null || userDTO.email().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_EMAIL);
        }

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new AlreadyExistsException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userDTO.password() == null || userDTO.password().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PASSWORD);
        }

        User user = new User();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    public UserDTO getUserById(UUID id) throws AppException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        
        return new UserDTO(
            user.getId(),
            user.getUserName(),
            user.getEmail(),
            null,
            user.getRole(),
            user.getStatus()
        );
    }

    public Page<UserDTO> filterUsers(String email, UserRole role, UserStatus status, String username, Pageable pageable) throws AppException {
        Specification<User> spec = (root, query, cb) -> cb.conjunction(); 

        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        if (role != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("role"), role));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("status"), status));
        }

        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }

        Page<User> users = userRepository.findAll(spec, pageable);

        if (users.isEmpty()) {
            throw new NoContentException(ErrorCode.NO_CONTENT);
        }

        return users.map(user -> new UserDTO(
            user.getId(),
            user.getUserName(),
            user.getEmail(),
            null,
            user.getRole(),
            user.getStatus()
        ));
    }

    public ResponseEntity<ResponseDTO> updateUser(UUID id, UserDTO userDTO) throws AppException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        if (userDTO.username() == null &&
            userDTO.password() == null && userDTO.role() == null && userDTO.status() == null) {
            throw new NoDataException(ErrorCode.NO_DATA);
        }

        if (userDTO.username() != null && !userDTO.username().isEmpty()) {
            user.setUsername(userDTO.username());
        }

        if (userDTO.password() != null && !userDTO.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.password()));
        }

        if (userDTO.role() != null) {
            user.setRole(userDTO.role());
        }

        if (userDTO.status() != null) {
            user.setStatus(userDTO.status());
        }

        userRepository.save(user);
        return ResponseEntity.ok(new ResponseDTO(id, "User updated successfully"));
    }

    public ResponseEntity<BooleanResponseDTO> checkPassword(User user, PasswordDTO password) {
        boolean isMatch = passwordEncoder.matches(password.password(), user.getPassword());
        return ResponseEntity.ok(new BooleanResponseDTO(isMatch));
    }

    public ResponseEntity<ResponseDTO> deleteUser(UUID id) throws AppException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.delete(user);
        
        return ResponseEntity.ok(new ResponseDTO(id, "User deleted successfully"));
    }
}
