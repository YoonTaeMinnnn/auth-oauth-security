package auth.integrationauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IntegrationauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationauthApplication.class, args);
	}

}
