package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    Optional<TeamMember> findByTeam(Team team);
    Optional<TeamMember> findByUser(User user);
    Optional<TeamMember> deleteAllByTeam(Team team);
    boolean existsByTeamAndUser(Team team, User user);
    List<TeamMember> getTeamMemberByTeam(Team team);
    List<TeamMember> findByUserId(UUID userId);
    void deleteAllByTeamId(UUID id);
    boolean existsByUserIdAndTeamIdAndRole(UUID userId, UUID teamId, String role);

    UUID findTeamMemberIdByUserIdAndTeamId(UUID userId, UUID userId1);
}
