package com.netflix.conductor.grpc.server.service;


import HealthCheckResponse.ServingStatus.NOT_SERVING;
import HealthCheckResponse.ServingStatus.SERVING;
import HealthGrpc.HealthBlockingStub;
import Status.INTERNAL;
import com.netflix.runtime.health.api.HealthCheckAggregator;
import com.netflix.runtime.health.api.HealthCheckStatus;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class HealthServiceImplTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void healthServing() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();
        HealthCheckAggregator hca = Mockito.mock(HealthCheckAggregator.class);
        CompletableFuture<HealthCheckStatus> hcsf = Mockito.mock(CompletableFuture.class);
        HealthCheckStatus hcs = Mockito.mock(HealthCheckStatus.class);
        Mockito.when(hcs.isHealthy()).thenReturn(true);
        Mockito.when(hcsf.get()).thenReturn(hcs);
        Mockito.when(hca.check()).thenReturn(hcsf);
        HealthServiceImpl healthyService = new HealthServiceImpl(hca);
        addService(serverName, healthyService);
        HealthGrpc.HealthBlockingStub blockingStub = // Create a client channel and register for automatic graceful shutdown.
        HealthGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
        HealthCheckResponse reply = blockingStub.check(HealthCheckRequest.newBuilder().build());
        Assert.assertEquals(SERVING, reply.getStatus());
    }

    @Test
    public void healthNotServing() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();
        HealthCheckAggregator hca = Mockito.mock(HealthCheckAggregator.class);
        CompletableFuture<HealthCheckStatus> hcsf = Mockito.mock(CompletableFuture.class);
        HealthCheckStatus hcs = Mockito.mock(HealthCheckStatus.class);
        Mockito.when(hcs.isHealthy()).thenReturn(false);
        Mockito.when(hcsf.get()).thenReturn(hcs);
        Mockito.when(hca.check()).thenReturn(hcsf);
        HealthServiceImpl healthyService = new HealthServiceImpl(hca);
        addService(serverName, healthyService);
        HealthGrpc.HealthBlockingStub blockingStub = // Create a client channel and register for automatic graceful shutdown.
        HealthGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
        HealthCheckResponse reply = blockingStub.check(HealthCheckRequest.newBuilder().build());
        Assert.assertEquals(NOT_SERVING, reply.getStatus());
    }

    @Test
    public void healthException() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();
        HealthCheckAggregator hca = Mockito.mock(HealthCheckAggregator.class);
        CompletableFuture<HealthCheckStatus> hcsf = Mockito.mock(CompletableFuture.class);
        Mockito.when(hcsf.get()).thenThrow(InterruptedException.class);
        Mockito.when(hca.check()).thenReturn(hcsf);
        HealthServiceImpl healthyService = new HealthServiceImpl(hca);
        addService(serverName, healthyService);
        HealthGrpc.HealthBlockingStub blockingStub = // Create a client channel and register for automatic graceful shutdown.
        HealthGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));
        thrown.expect(StatusRuntimeException.class);
        thrown.expect(Matchers.hasProperty("status", Matchers.is(INTERNAL)));
        blockingStub.check(HealthCheckRequest.newBuilder().build());
    }
}

