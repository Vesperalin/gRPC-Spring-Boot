package pl.edu.pwr.rsi.grpc.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/*
Cheat sheet for grpcurl for server
    - list of services: grpcurl --plaintext localhost:9000 list
    - list of server methods: grpcurl --plaintext localhost:9000 list pl.edu.pwr.rsi.grpc.interface.MyService
*/
// Spring Boot server app
@SpringBootApplication
public class Server {
    public static void main(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Server.class);
        builder.headless(false);
        builder.run(args);
    }
}
