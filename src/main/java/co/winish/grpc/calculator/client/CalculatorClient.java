package co.winish.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    private ManagedChannel channel;

    private void init(String address, int port) {
        try {
            channel = NettyChannelBuilder.forAddress(address, port)
                    .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                    .build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        System.out.println("Done init");
    }

    private void run() {
        init("localhost", 50052);

        //makeUnaryCall();
        //makeServerStreamingCall();
        //makeClientStreamingCall();
        //makeBiDiStreamingCall();
        makeUnaryCallWithException();

        channel.shutdown();
    }

    public static void main(String[] args) {
        System.out.println("Client started");

        CalculatorClient calculatorClient = new CalculatorClient();
        calculatorClient.run();
    }

    private void makeUnaryCall() {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(channel);

        System.out.println("Sum request: 10.7 + 3.2");
        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNumber(10.7)
                .setSecondNumber(3.2)
                .build();
        SumResponse sumResponse = syncClient.sum(sumRequest);
        System.out.println(sumResponse.getSumResult());

        System.out.println("Subtract request: 10.7 - 3.2");
        SubtractRequest subtractRequest = SubtractRequest.newBuilder()
                .setMinuend(10.7)
                .setSubtrahend(3.2)
                .build();
        SubtractResponse subtractResponse = syncClient.subtract(subtractRequest);
        System.out.println(subtractResponse.getSubtractResult());

        System.out.println("Multiply request: 10.7 * 3.2");
        MultiplyRequest multiplyRequest = MultiplyRequest.newBuilder()
                .setFirstNumber(10.7)
                .setSecondNumber(3.2)
                .build();
        MultiplyResponse multiplyResponse = syncClient.multiply(multiplyRequest);
        System.out.println(multiplyResponse.getMultiplyResult());

        System.out.println("Divide request: 10.7 / 3.2");
        DivideRequest divideRequest = DivideRequest.newBuilder()
                .setDivided(10.7)
                .setDivisor(3.2)
                .build();
        DivideResponse divideResponse = syncClient.divide(divideRequest);
        System.out.println(divideResponse.getDivideResult());
    }

    private void makeServerStreamingCall() {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(channel);
        syncClient.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(287468066)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> System.out.println(primeNumberDecompositionResponse.getPrimeFactor()));
    }

    private void makeClientStreamingCall() {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Got a message from a server");
                System.out.println("Message: " + value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending stuff");
                latch.countDown();
            }
        });

        for (double i = 0; i < 100000; i += 0.5) {
            requestObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }

        requestObserver.onCompleted();

        try {
            latch.await(6L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeBiDiStreamingCall() {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaxRequest> requestObserver = asyncClient.findMax(new StreamObserver<FindMaxResponse>() {
            @Override
            public void onNext(FindMaxResponse value) {
                System.out.println("Got a message from the server");
                System.out.println("Message: " + value.getMax());
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

        Arrays.asList(-542.2, -186.0, 0.0, 1763.53, 10.2, -1876637.0, 17387.9, 287.0, -1763.27283, 0.0,
                285623.38, 81763.0, -387.0, 1763672.0, 12.7, 1763672.0)
                .forEach(number -> {
                    System.out.println("Sending: " + number);
                    requestObserver.onNext(FindMaxRequest.newBuilder()
                            .setNumber(number)
                            .build());
                });

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeUnaryCallWithException() {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(channel);

        int number = -1;
        try {
            SquareRootResponse response = syncClient.squareRoot(SquareRootRequest.newBuilder()
                    .setNumber(number)
                    .build());
        } catch (StatusRuntimeException e) {
            System.out.println("Got an exception!");
            //System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
