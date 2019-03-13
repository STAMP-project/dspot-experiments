/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.services;


import HealthGrpc.HealthBlockingStub;
import HealthGrpc.HealthStub;
import HealthStatusManager.SERVICE_NAME_ALL_SERVICES;
import ServingStatus.NOT_SERVING;
import ServingStatus.SERVICE_UNKNOWN;
import ServingStatus.SERVING;
import ServingStatus.UNKNOWN;
import Status.Code.NOT_FOUND;
import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Context.CancellableContext;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link HealthStatusManager}.
 */
@RunWith(JUnit4.class)
public class HealthStatusManagerTest {
    private static final String SERVICE1 = "service1";

    private static final String SERVICE2 = "service2";

    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    private final HealthStatusManager manager = new HealthStatusManager();

    private final HealthServiceImpl service = ((HealthServiceImpl) (manager.getHealthService()));

    private HealthStub stub;

    private HealthBlockingStub blockingStub;

    @Test
    public void enterTerminalState_check() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        HealthStatusManagerTest.RespObserver obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        HealthCheckResponse resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(SERVING);
        manager.enterTerminalState();
        obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(NOT_SERVING);
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(NOT_SERVING);
    }

    @Test
    public void enterTerminalState_watch() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        HealthStatusManagerTest.RespObserver obs = new HealthStatusManagerTest.RespObserver();
        service.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(1);
        HealthCheckResponse resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(SERVING);
        obs.responses.clear();
        manager.enterTerminalState();
        assertThat(obs.responses).hasSize(1);
        resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(NOT_SERVING);
        obs.responses.clear();
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        assertThat(obs.responses).isEmpty();
    }

    @Test
    public void enterTerminalState_ignoreClear() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        HealthStatusManagerTest.RespObserver obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        HealthCheckResponse resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(SERVING);
        manager.enterTerminalState();
        obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(NOT_SERVING);
        manager.clearStatus(HealthStatusManagerTest.SERVICE1);
        obs = new HealthStatusManagerTest.RespObserver();
        service.check(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), obs);
        assertThat(obs.responses).hasSize(2);
        resp = ((HealthCheckResponse) (obs.responses.poll()));
        assertThat(resp.getStatus()).isEqualTo(NOT_SERVING);
    }

    @Test
    public void defaultIsServing() throws Exception {
        HealthCheckRequest request = HealthCheckRequest.newBuilder().setService(SERVICE_NAME_ALL_SERVICES).build();
        HealthCheckResponse response = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
        assertThat(response).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVING).build());
    }

    @Test
    public void getHealthService_getterReturnsTheSameHealthRefAfterUpdate() throws Exception {
        BindableService health = manager.getHealthService();
        manager.setStatus(HealthStatusManagerTest.SERVICE1, UNKNOWN);
        assertThat(health).isSameAs(manager.getHealthService());
    }

    @Test
    public void checkValidStatus() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, NOT_SERVING);
        manager.setStatus(HealthStatusManagerTest.SERVICE2, SERVING);
        HealthCheckRequest request = HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build();
        HealthCheckResponse response = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
        assertThat(response).isEqualTo(HealthCheckResponse.newBuilder().setStatus(NOT_SERVING).build());
        request = HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE2).build();
        response = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
        assertThat(response).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVING).build());
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(0);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(0);
    }

    @Test
    public void checkStatusNotFound() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        // SERVICE2's status is not set
        HealthCheckRequest request = HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE2).build();
        try {
            blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
            Assert.fail("Should've failed");
        } catch (StatusRuntimeException e) {
            assertThat(e.getStatus().getCode()).isEqualTo(NOT_FOUND);
        }
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(0);
    }

    @Test
    public void notFoundForClearedStatus() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        manager.clearStatus(HealthStatusManagerTest.SERVICE1);
        HealthCheckRequest request = HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build();
        try {
            blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).check(request);
            Assert.fail("Should've failed");
        } catch (StatusRuntimeException e) {
            assertThat(e.getStatus().getCode()).isEqualTo(NOT_FOUND);
        }
    }

    @Test
    public void watch() throws Exception {
        manager.setStatus(HealthStatusManagerTest.SERVICE1, UNKNOWN);
        // Start a watch on SERVICE1
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(0);
        HealthStatusManagerTest.RespObserver respObs1 = new HealthStatusManagerTest.RespObserver();
        stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), respObs1);
        // Will get the current status
        assertThat(respObs1.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(UNKNOWN).build());
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(1);
        // Status change is notified of to the RPC
        manager.setStatus(HealthStatusManagerTest.SERVICE1, SERVING);
        assertThat(respObs1.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVING).build());
        // Start another watch on SERVICE1
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(1);
        HealthStatusManagerTest.RespObserver respObs1b = new HealthStatusManagerTest.RespObserver();
        stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), respObs1b);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(2);
        // Will get the current status
        assertThat(respObs1b.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVING).build());
        // Start a watch on SERVICE2, which is not known yet
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(0);
        HealthStatusManagerTest.RespObserver respObs2 = new HealthStatusManagerTest.RespObserver();
        stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE2).build(), respObs2);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(1);
        // Current status is SERVICE_UNKNOWN
        assertThat(respObs2.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        // Set status for SERVICE2, which will be notified of
        manager.setStatus(HealthStatusManagerTest.SERVICE2, NOT_SERVING);
        assertThat(respObs2.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(NOT_SERVING).build());
        // Clear the status for SERVICE1, which will be notified of
        manager.clearStatus(HealthStatusManagerTest.SERVICE1);
        assertThat(respObs1.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        assertThat(respObs1b.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        // All responses have been accounted for
        assertThat(respObs1.responses).isEmpty();
        assertThat(respObs1b.responses).isEmpty();
        assertThat(respObs2.responses).isEmpty();
    }

    @Test
    public void watchRemovedWhenClientCloses() throws Exception {
        CancellableContext withCancellation = Context.current().withCancellation();
        Context prevCtx = withCancellation.attach();
        HealthStatusManagerTest.RespObserver respObs1 = new HealthStatusManagerTest.RespObserver();
        try {
            assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(0);
            stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), respObs1);
        } finally {
            withCancellation.detach(prevCtx);
        }
        HealthStatusManagerTest.RespObserver respObs1b = new HealthStatusManagerTest.RespObserver();
        stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE1).build(), respObs1b);
        HealthStatusManagerTest.RespObserver respObs2 = new HealthStatusManagerTest.RespObserver();
        stub.watch(HealthCheckRequest.newBuilder().setService(HealthStatusManagerTest.SERVICE2).build(), respObs2);
        assertThat(respObs1.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        assertThat(respObs1b.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        assertThat(respObs2.responses.poll()).isEqualTo(HealthCheckResponse.newBuilder().setStatus(SERVICE_UNKNOWN).build());
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(2);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(1);
        assertThat(respObs1.responses).isEmpty();
        assertThat(respObs1b.responses).isEmpty();
        assertThat(respObs2.responses).isEmpty();
        // This will cancel the RPC with respObs1
        withCancellation.close();
        assertThat(respObs1.responses.poll()).isInstanceOf(Throwable.class);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE1)).isEqualTo(1);
        assertThat(service.numWatchersForTest(HealthStatusManagerTest.SERVICE2)).isEqualTo(1);
        assertThat(respObs1.responses).isEmpty();
        assertThat(respObs1b.responses).isEmpty();
        assertThat(respObs2.responses).isEmpty();
    }

    private static class RespObserver implements StreamObserver<HealthCheckResponse> {
        final ArrayDeque<Object> responses = new ArrayDeque<>();

        @Override
        public void onNext(HealthCheckResponse value) {
            responses.add(value);
        }

        @Override
        public void onError(Throwable t) {
            responses.add(t);
        }

        @Override
        public void onCompleted() {
            responses.add("onCompleted");
        }
    }
}

