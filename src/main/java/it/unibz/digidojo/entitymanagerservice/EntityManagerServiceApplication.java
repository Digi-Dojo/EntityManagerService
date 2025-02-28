package it.unibz.digidojo.entitymanagerservice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
public class EntityManagerServiceApplication {
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(EntityManagerServiceApplication.class, args);
        log.info("Server running on {}", InetAddress.getLocalHost().getHostAddress());
    }

    @Bean
    public WebMvcConfigurer addCorsMappings() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:9000/", "https://startup-digi-dojo-lab.onrender.com/",
                                "https://mfront-startupsandusers.onrender.com", "https://startup-digi-dojo-lab-pr-3.onrender.com/");
            }
        };
    }
}
