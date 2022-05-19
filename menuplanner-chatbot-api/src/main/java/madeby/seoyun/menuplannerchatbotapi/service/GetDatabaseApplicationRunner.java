package madeby.seoyun.menuplannerchatbotapi.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class GetDatabaseApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("빈 객체 주입 완료");
    }
}
