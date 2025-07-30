package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hypercell.final_project.football_places_booking_system.model.db.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID>, JpaSpecificationExecutor<Team>{
    boolean existsByNameIgnoreCase(String name);
    List<Team> findByCreatorId(UUID creatorId);
    String findTeamNameById(UUID teamId);
    String findTeamDescriptionById(UUID teamId);
    
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamMembers WHERE t.id = :teamId")
    Optional<Team> findByIdWithMembers(@Param("teamId") UUID teamId);
    
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamMembers WHERE t.id IN :teamIds")
    List<Team> findAllByIdWithMembers(@Param("teamIds") List<UUID> teamIds);
}
