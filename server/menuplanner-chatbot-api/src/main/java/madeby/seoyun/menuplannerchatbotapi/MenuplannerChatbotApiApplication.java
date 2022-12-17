package madeby.seoyun.menuplannerchatbotapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MenuplannerChatbotApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuplannerChatbotApiApplication.class, args);
	}

}
