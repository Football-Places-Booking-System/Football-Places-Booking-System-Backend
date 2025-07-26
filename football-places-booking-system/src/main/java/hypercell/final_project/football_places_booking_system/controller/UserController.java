package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;

import hypercell.final_project.football_places_booking_system.service.UserService;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/get/{id}")
    public UserDTO getUserById(@PathVariable Long id) throws AppException {
        return userService.getUserById(id);
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
