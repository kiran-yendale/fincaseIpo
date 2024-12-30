package in.fincase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan("in.*")
@EnableAsync
public class FincaseIpoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FincaseIpoApplication.class, args);
	}

}
