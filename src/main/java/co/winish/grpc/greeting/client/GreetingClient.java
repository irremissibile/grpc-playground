package co.winish.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    private ManagedChannel channel;
    private Greeting greeting;

    private void init(String address, int port) {
        channel = ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();

        greeting = Greeting.newBuilder()
                .setFirstName("Mick")
                .setLastName("Jagger")
                .build();

        System.out.println("Done init");
    }

    private void run() {
        init("localhost", 50051);

        //makeUnaryCall();
        //makeServerStreamingCall();
        //makeClientStreamingCall();
        makeBiDiStreamingCall();

        channel.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("Client started");

        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }


    private void makeUnaryCall() {
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        System.out.println("Making an unary grpc call");
        GreetResponse greetResponse = syncClient.greet(greetRequest);
        System.out.println("Result: " + greetResponse.getResult());
    }

    private void makeServerStreamingCall() {
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        System.out.println("Making a server streaming grpc call");
        syncClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> System.out.println(greetManyTimesResponse.getResult()));
    }

    private void makeClientStreamingCall() {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("Got a response message from the server");
                System.out.println("Message: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("The server has completed sending stuff");
                latch.countDown();
            }
        });

        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Natalie")
                        .setLastName("Portman")
                        .build())
                .build());

        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Anne")
                        .setLastName("Hathaway")
                        .build())
                .build());

        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Eva")
                        .setLastName("Green")
                        .build())
                .build());

        System.out.println("Sending message 4");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Emma")
                        .setLastName("Stone")
                        .build())
                .build());

        //requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeBiDiStreamingCall() {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Got a message from the server");
                System.out.println("Message: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("The server has completed sending stuff");
                latch.countDown();
            }
        });

        Arrays.asList("Natalie Portman", "Keira Knightley", "Anne Hathaway", "Eva Green", "Emma Stone", "Cara Delevingne", "Emma Watson", "Emma Mackey",
                "Natalie Dormer", "Caris van Houten", "Emilia Clarke", "Margot Robbie", "Sharon Stone", "Jennifer Lawrence", "Scarlett Johansson")
                .forEach(name -> {
                    System.out.println("Sending: " + name);
                    String[] person = name.split(" ");
                    requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(person[0])
                                    .setLastName(person[1])
                                    .build())
                            .build());

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
