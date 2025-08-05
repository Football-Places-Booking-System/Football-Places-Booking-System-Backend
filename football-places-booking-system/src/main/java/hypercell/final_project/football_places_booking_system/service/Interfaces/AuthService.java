package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException; 

public interface AuthService {
    public boolean hasTeamRole(UUID teamId, String expectedRole) throws AppException;
    public boolean is(String status);
}
