package api.controla_preju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ControlaPrejuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlaPrejuApplication.class, args);
	}

}
