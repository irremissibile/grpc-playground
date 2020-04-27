package co.winish.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();
        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subtract(SubtractRequest request, StreamObserver<SubtractResponse> responseObserver) {
        SubtractResponse subtractResponse = SubtractResponse.newBuilder()
                .setSubtractResult(request.getMinuend() - request.getSubtrahend())
                .build();
        responseObserver.onNext(subtractResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void multiply(MultiplyRequest request, StreamObserver<MultiplyResponse> responseObserver) {
        MultiplyResponse multiplyResponse = MultiplyResponse.newBuilder()
                .setMultiplyResult(request.getFirstNumber() * request.getSecondNumber())
                .build();
        responseObserver.onNext(multiplyResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void divide(DivideRequest request, StreamObserver<DivideResponse> responseObserver) {
        DivideResponse divideResponse = DivideResponse.newBuilder()
                .setDivideResult(request.getDivided() / request.getDivisor())
                .build();
        responseObserver.onNext(divideResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        int number = request.getNumber();
        int divisor = 2;

        while (number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            } else
                divisor++;
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        return new StreamObserver<ComputeAverageRequest>() {
            private double sum;
            private int count;

            @Override
            public void onNext(ComputeAverageRequest value) {
                sum += value.getNumber();
                count++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(ComputeAverageResponse.newBuilder()
                        .setAverage(sum / count)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<FindMaxRequest> findMax(StreamObserver<FindMaxResponse> responseObserver) {
        return new StreamObserver<FindMaxRequest>() {
            double max = Double.NEGATIVE_INFINITY;

            @Override
            public void onNext(FindMaxRequest value) {
                if (max < value.getNumber()) {
                    max = value.getNumber();
                    responseObserver.onNext(FindMaxResponse.newBuilder()
                            .setMax(max)
                            .build());
                }
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
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        int number = request.getNumber();
        if (number >= 0) {
            responseObserver.onNext(
                    SquareRootResponse.newBuilder()
                        .setNumberRoot(Math.sqrt(number))
                        .build());
            responseObserver.onCompleted();
        } else
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                        .withDescription("The sent number is not positive")
                        .augmentDescription("Number sent: " + number)
                        .asRuntimeException());
    }
}
