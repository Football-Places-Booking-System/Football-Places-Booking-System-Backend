package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.UUID;

public interface AuthService {
    public boolean hasTeamRole(UUID teamId, String expectedRole);

    }
