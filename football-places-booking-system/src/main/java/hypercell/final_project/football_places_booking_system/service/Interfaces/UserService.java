package hypercell.final_project.football_places_booking_system.service.Interfaces;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.dto.ResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.UserDTO;
import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    public void register(UserDTO userDTO) throws AppException;
    public UserDTO getUserById(UUID id) throws AppException;
    public Page<UserDTO> filterUsers(String email, UserRole role, UserStatus status, String username, Pageable pageable) throws AppException;
    public Page<UserDTO> filterUsersSorted(String email, UserRole role, UserStatus status, String username, int page, int size, String sortBy, String sortDirection) throws AppException;
    public ResponseEntity<ResponseDTO> updateUser(UUID id, UserDTO userDTO) throws AppException;
    public ResponseEntity<ResponseDTO> deleteUser(UUID id) throws AppException;
}
