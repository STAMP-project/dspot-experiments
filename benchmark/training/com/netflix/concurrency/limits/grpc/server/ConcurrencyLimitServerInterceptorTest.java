package com.netflix.concurrency.limits.grpc.server;


import CallOptions.DEFAULT;
import Limiter.Listener;
import MethodType.UNARY;
import Status.Code.DEADLINE_EXCEEDED;
import Status.Code.INVALID_ARGUMENT;
import Status.Code.UNKNOWN;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.concurrency.limits.Limiter;
import com.netflix.concurrency.limits.grpc.StringMarshaller;
import com.netflix.concurrency.limits.grpc.mockito.OptionalResultCaptor;
import com.netflix.concurrency.limits.limiter.SimpleLimiter;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCalls;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConcurrencyLimitServerInterceptorTest {
    private static final MethodDescriptor<String, String> METHOD_DESCRIPTOR = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("service/method").setRequestMarshaller(StringMarshaller.INSTANCE).setResponseMarshaller(StringMarshaller.INSTANCE).build();

    private Server server;

    private Channel channel;

    final Limiter<GrpcServerRequestContext> limiter = Mockito.spy(SimpleLimiter.newBuilder().build());

    final OptionalResultCaptor<Limiter.Listener> listener = OptionalResultCaptor.forClass(Listener.class);

    @Test
    public void releaseOnSuccess() {
        // Setup server
        startServer(( req, observer) -> {
            observer.onNext("response");
            observer.onCompleted();
        });
        ClientCalls.blockingUnaryCall(channel, ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT, "foo");
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onSuccess();
    }

    @Test
    public void releaseOnError() {
        // Setup server
        startServer(( req, observer) -> {
            observer.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        });
        try {
            ClientCalls.blockingUnaryCall(channel, ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT, "foo");
            Assert.fail("Should have failed with UNKNOWN error");
        } catch (StatusRuntimeException e) {
            Assert.assertEquals(INVALID_ARGUMENT, e.getStatus().getCode());
        }
        // Verify
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onIgnore();
    }

    @Test
    public void releaseOnUncaughtException() throws IOException {
        // Setup server
        startServer(( req, observer) -> {
            throw new RuntimeException("failure");
        });
        try {
            ClientCalls.blockingUnaryCall(channel, ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT, "foo");
            Assert.fail("Should have failed with UNKNOWN error");
        } catch (StatusRuntimeException e) {
            Assert.assertEquals(UNKNOWN, e.getStatus().getCode());
        }
        // Verify
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onIgnore();
    }

    @Test
    public void releaseOnCancellation() {
        // Setup server
        startServer(( req, observer) -> {
        });
        ListenableFuture<String> future = ClientCalls.futureUnaryCall(channel.newCall(ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT), "foo");
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        future.cancel(true);
        // Verify
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onIgnore();
    }

    @Test
    public void releaseOnDeadlineExceeded() {
        // Setup server
        startServer(( req, observer) -> {
        });
        try {
            ClientCalls.blockingUnaryCall(channel.newCall(ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT.withDeadlineAfter(1, TimeUnit.SECONDS)), "foo");
        } catch (StatusRuntimeException e) {
            Assert.assertEquals(DEADLINE_EXCEEDED, e.getStatus().getCode());
        }
        // Verify
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onIgnore();
    }

    @Test
    public void releaseOnMissingHalfClose() {
        // Setup server
        startServer(( req, observer) -> {
        });
        ClientCall<String, String> call = channel.newCall(ConcurrencyLimitServerInterceptorTest.METHOD_DESCRIPTOR, DEFAULT.withDeadlineAfter(500, TimeUnit.MILLISECONDS));
        call.start(new ClientCall.Listener<String>() {}, new Metadata());
        call.request(2);
        call.sendMessage("foo");
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        // Verify
        Mockito.verify(limiter, Mockito.times(1)).acquire(Mockito.isA(GrpcServerRequestContext.class));
        Mockito.verify(listener.getResult().get(), Mockito.timeout(1000).times(1)).onIgnore();
    }
}

