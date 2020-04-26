package co.winish.grpc.greeting.client;

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

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Mick")
                .setLastName("Jagger")
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        //Unary grpc call
        /*System.out.println("SyncClient created");

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        System.out.println("Making grpc call");
        GreetResponse greetResponse = syncClient.greet(greetRequest);
        System.out.println("Result: " + greetResponse.getResult());
        */

        //Server streaming grpc
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        System.out.println("Making server streaming grpc call");
        syncClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> System.out.println(greetManyTimesResponse.getResult()));


        channel.shutdown();
    }
}
