package co.winish.grpc.calculator.server;

import com.proto.calculator.*;
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
}
