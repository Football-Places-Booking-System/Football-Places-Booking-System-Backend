package hypercell.final_project.football_places_booking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FootballPlacesBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballPlacesBookingSystemApplication.class, args);
	}

}
