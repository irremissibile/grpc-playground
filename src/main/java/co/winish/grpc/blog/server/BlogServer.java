package co.winish.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;


public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {


        Server server = ServerBuilder.forPort(50053)
                .addService(new BlogServiceImpl())
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem"))
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stoped the server");
        } ));

        server.awaitTermination();
    }
}
