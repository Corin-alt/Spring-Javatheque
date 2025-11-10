package fr.corentin.javatheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JavathequeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavathequeApplication.class, args);
    }

}
