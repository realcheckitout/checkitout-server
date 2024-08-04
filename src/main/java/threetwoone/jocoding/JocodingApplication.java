package threetwoone.jocoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JocodingApplication {

	public static void main(String[] args) {
		SpringApplication.run(JocodingApplication.class, args);
	}

}
