package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TeamMember tm WHERE tm.team.id = :teamId")
    void deleteAllByTeamId(@Param("teamId") UUID teamId);
    
    boolean existsByUserIdAndTeamIdAndRole(UUID userId, UUID teamId, String role);
    UUID findTeamMemberIdByUserIdAndTeamId(UUID userId, UUID teamId);
    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

}
