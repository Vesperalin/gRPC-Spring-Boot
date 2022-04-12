package pl.edu.pwr.rsi.grpc.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

// Spring Boot client app
@SpringBootApplication
public class Client {
    public static void main(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Client.class);
        builder.headless(false);
        builder.run(args);
    }
}
