package commerce.emmerce_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EmmerceJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmmerceJpaApplication.class, args);
	}

}
