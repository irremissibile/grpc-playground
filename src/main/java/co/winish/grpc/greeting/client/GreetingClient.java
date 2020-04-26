package co.winish.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Client started");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        System.out.println("Channel created");

        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);
        System.out.println("SyncClient created");

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Mick")
                .setLastName("Jagger")
                .build();

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        System.out.println("Making grpc call");
        GreetResponse greetResponse = syncClient.greet(greetRequest);
        System.out.println("Result: " + greetResponse.getResult());

        channel.shutdown();
    }
}
