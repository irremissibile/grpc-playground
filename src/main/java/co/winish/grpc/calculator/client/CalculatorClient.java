package co.winish.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

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

        channel.shutdown();
    }
}
