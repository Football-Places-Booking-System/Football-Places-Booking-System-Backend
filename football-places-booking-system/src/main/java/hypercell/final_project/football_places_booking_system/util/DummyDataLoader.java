package hypercell.final_project.football_places_booking_system.util;

import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.*;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyDataLoader {

    private final BookingMatchService bookingMatchService;

    @PostConstruct
    public void initDummyData() {
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .username("Ali")
                        .email("ali@example.com")
                        .password("dummy123")
                        .role(UserRole.USER)
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        List<Place> places = List.of(
                Place.builder()
                        .id(1L)
                        .name("Cairo Pitch")
                        .location("Cairo")
                        .placeType(PlaceType.FIVE)
                        .imageUrl("img.jpg")
                        .build()
        );

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .name("Falcons")
                        .description("Top team")
                        .createdBy(1L)
                        .build()
        );

        bookingMatchService.seedData(users, places, teams);
    }
}
