package hypercell.final_project.football_places_booking_system.model.db;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;


    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private TeamMember creator;
}

