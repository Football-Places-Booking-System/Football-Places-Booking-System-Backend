package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import hypercell.final_project.football_places_booking_system.service.UserService;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/get/{id}")
    public UserDTO getUserById(@PathVariable Long id) throws AppException {
        return userService.getUserById(id);
    }

    @GetMapping("/filter")
    public List<UserDTO> filterUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String username
    ) {
        return userService.filterUsers(email, role, status, username);
    }

    @GetMapping("/get")
    public Page<UserDTO> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws AppException {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllUsersWithPagination(pageable);
    }
    
    @GetMapping("/all")
    public List<UserDTO> getAllUsers() throws AppException {
        return userService.getAllUsers();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) throws AppException {
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable Long id) throws AppException {
        return userService.deleteUser(id);
    }
}
