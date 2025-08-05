package hypercell.final_project.football_places_booking_system.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import hypercell.final_project.football_places_booking_system.model.dto.PasswordDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BooleanResponseDTO;
import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import hypercell.final_project.football_places_booking_system.service.Impl.UserServiceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService;

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) throws AppException {
        return userService.getUserById(id);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all")
    public Page<UserDTO> filterUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws AppException {
        Pageable pageable = PageRequest.of(page, size);
        return userService.filterUsers(email, role, status, username, pageable);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all-sorted")
    public Page<UserDTO> filterUsersSorted(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) throws AppException {
        return userService.filterUsersSorted(email, role, status, username, page, size, sortBy, sortDirection);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) throws AppException {
        return userService.updateUser(id, userDTO);
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @PostMapping("/check-password")
    public ResponseEntity<BooleanResponseDTO> checkPassword(@AuthenticationPrincipal UserDetails user, @RequestBody PasswordDTO password) {
        return userService.checkPassword((User) user, password);
    }
    
    @PreAuthorize("@authService.is('ACTIVE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable UUID id) throws AppException {
        return userService.deleteUser(id);
    }
}
