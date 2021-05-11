package io.odpf.dagger.core.processors.external.grpc.client;

import io.odpf.dagger.core.exception.ChannelNotAvailableException;
import io.odpf.dagger.core.processors.external.grpc.GrpcSourceConfig;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

import java.util.Map;

public class GrpcClient {
    private final GrpcSourceConfig grpcConfig;

    private Channel decoratedChannel;

    public GrpcClient(GrpcSourceConfig grpcConfig) {
        this.grpcConfig = grpcConfig;
    }

    public void addChannel() {
        Channel channel = ManagedChannelBuilder.forAddress(grpcConfig.getEndpoint(), grpcConfig.getServicePort()).usePlaintext().build();

        Metadata metadata = new Metadata();

        if (grpcConfig.getHeaders() != null && !grpcConfig.getHeaders().isEmpty()) {
            for (Map.Entry<String, String> header : grpcConfig.getHeaders().entrySet()) {
                metadata.put(Metadata.Key.of(header.getKey(), Metadata.ASCII_STRING_MARSHALLER), header.getValue());
            }
        }
        decoratedChannel = ClientInterceptors.intercept(channel,
                MetadataUtils.newAttachHeadersInterceptor(metadata));


    }

    public void asyncUnaryCall(
            DynamicMessage request,
            StreamObserver<DynamicMessage> responseObserver, Descriptor inputDescriptor, Descriptor outputDescriptor) throws Exception {

        if (decoratedChannel == null) {
            throw new ChannelNotAvailableException("channel not available");
        }

        ClientCalls.asyncUnaryCall(
                createCall(CallOptions.DEFAULT, inputDescriptor, outputDescriptor),
                request,
                responseObserver);
    }

    private ClientCall<DynamicMessage, DynamicMessage> createCall(CallOptions callOptions, Descriptor inputDescriptor, Descriptor outputDescriptor) {

        return decoratedChannel.newCall(MethodDescriptor.newBuilder(new DynamicMessageMarshaller(inputDescriptor), new DynamicMessageMarshaller(outputDescriptor))
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(grpcConfig.getGrpcMethodUrl())
                .build(), callOptions);
    }

    public void close() {
        this.decoratedChannel = null;
    }

    public Channel getDecoratedChannel() {
        return decoratedChannel;
    }
}

