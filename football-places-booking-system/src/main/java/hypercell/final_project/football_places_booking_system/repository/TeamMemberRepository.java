package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import hypercell.final_project.football_places_booking_system.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    Optional<TeamMember> findByTeam(Team team);
    Optional<TeamMember> findByUser(User user);
    Optional<TeamMember> deleteAllByTeam(Team team);
    boolean existsByTeamAndUser(Team team, User user);
    List<TeamMember> getTeamMemberByTeam(Team team);
    List<TeamMember> findByUserId(Long userId);
     void deleteAllByTeamId(Long id);
}
