package co.winish.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        System.out.println("Received grpc call");

        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        GreetResponse response = GreetResponse.newBuilder()
                .setResult("Hello, " + firstName + " " + lastName)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        Greeting greeting = request.getGreeting();

        try {
            for (int i = 0; i < 10; i++) {
                GreetManyTimesResponse greetManyTimes = GreetManyTimesResponse.newBuilder()
                        .setResult("Salam alejkym, " + greeting.getFirstName() + " " + greeting.getLastName() + " #" + i)
                        .build();
                responseObserver.onNext(greetManyTimes);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        return new StreamObserver<LongGreetRequest>() {
            private String result = "";
            private int number = 0;

            @Override
            public void onNext(LongGreetRequest value) {
                if (number < 3) {
                    result += "Salam alejkym, " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName() + "! ";
                    number++;
                } else
                    onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                //ignore for now
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                responseObserver.onNext(GreetEveryoneResponse.newBuilder()
                        .setResult("Salam alejkym, " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName() + "!")
                        .build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {
        Context current = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("Sleep for 100ms");
                    Thread.sleep(100L);
                } else
                    return;
            }

            System.out.println("Sending response");
            responseObserver.onNext(
                    GreetWithDeadlineResponse.newBuilder()
                            .setResult("Salam alejkym, " + request.getGreeting().getFirstName() + " " + request.getGreeting().getLastName() + "!")
                            .build()
            );
            responseObserver.onCompleted();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
