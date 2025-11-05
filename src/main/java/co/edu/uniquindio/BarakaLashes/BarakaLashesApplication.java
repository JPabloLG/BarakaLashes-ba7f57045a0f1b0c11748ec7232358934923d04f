package co.edu.uniquindio.BarakaLashes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BarakaLashesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarakaLashesApplication.class, args);
	}
}
