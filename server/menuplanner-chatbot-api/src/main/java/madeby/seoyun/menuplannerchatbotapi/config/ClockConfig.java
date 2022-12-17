package madeby.seoyun.menuplannerchatbotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Clock bean 등록을 위한 config
 *
 * @filename : ClockConfig.java
 * @Author : lsy
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
