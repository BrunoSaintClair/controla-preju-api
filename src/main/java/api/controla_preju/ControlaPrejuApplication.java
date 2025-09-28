package api.controla_preju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ControlaPrejuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlaPrejuApplication.class, args);
	}

    @GetMapping("/hello")
    public static String helloWorld(){
        return "Hello world!";
    }

}
