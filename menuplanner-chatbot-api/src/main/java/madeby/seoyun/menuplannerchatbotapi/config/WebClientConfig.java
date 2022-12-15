package madeby.seoyun.menuplannerchatbotapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient bean 등록을 위한 config
 *
 * @filename : WebClientConfig.java
 * @Author : lsy
 */
@Configuration
public class WebClientConfig {

    @Value("${parsing-endpoint}")
    private String endPoint;

    @Bean
    public WebClient webClient() {
        return WebClient.create(endPoint);
    }
}
