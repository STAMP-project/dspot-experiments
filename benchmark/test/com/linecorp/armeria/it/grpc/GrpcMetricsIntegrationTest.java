/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.it.grpc;


import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction;
import com.linecorp.armeria.common.metric.PrometheusMeterRegistries;
import com.linecorp.armeria.grpc.testing.Messages.SimpleRequest;
import com.linecorp.armeria.grpc.testing.Messages.SimpleResponse;
import com.linecorp.armeria.grpc.testing.TestServiceGrpc.TestServiceImplBase;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.grpc.GrpcServiceBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.testing.server.ServerRule;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class GrpcMetricsIntegrationTest {
    private static final MeterRegistry registry = PrometheusMeterRegistries.newRegistry();

    private static class TestServiceImpl extends TestServiceImplBase {
        @Override
        public void unaryCall(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            if ("world".equals(request.getPayload().getBody().toStringUtf8())) {
                responseObserver.onNext(SimpleResponse.getDefaultInstance());
                responseObserver.onCompleted();
                return;
            }
            responseObserver.onError(new IllegalArgumentException("bad argument"));
        }

        @Override
        public void unaryCall2(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            if ("world".equals(request.getPayload().getBody().toStringUtf8())) {
                responseObserver.onNext(SimpleResponse.getDefaultInstance());
                responseObserver.onCompleted();
                return;
            }
            responseObserver.onError(new IllegalArgumentException("bad argument"));
        }
    }

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.meterRegistry(GrpcMetricsIntegrationTest.registry);
            sb.service(new GrpcServiceBuilder().addService(new GrpcMetricsIntegrationTest.TestServiceImpl()).enableUnframedRequests(true).build(), MetricCollectingService.newDecorator(MeterIdPrefixFunction.ofDefault("server")), LoggingService.newDecorator());
        }
    };

    private static final ClientFactory clientFactory = new ClientFactoryBuilder().meterRegistry(GrpcMetricsIntegrationTest.registry).build();

    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void normal() throws Exception {
        GrpcMetricsIntegrationTest.makeRequest("world");
        GrpcMetricsIntegrationTest.makeRequest("world");
        GrpcMetricsIntegrationTest.makeRequest("space");
        GrpcMetricsIntegrationTest.makeRequest("world");
        GrpcMetricsIntegrationTest.makeRequest("space");
        GrpcMetricsIntegrationTest.makeRequest("space");
        GrpcMetricsIntegrationTest.makeRequest("world");
        // Chance that get() returns NPE before the metric is first added, so ignore exceptions.
        given().ignoreExceptions().untilAsserted(() -> assertThat(findServerMeter("UnaryCall", "requests", COUNT, "result", "success", "httpStatus", "200")).contains(4.0));
        given().ignoreExceptions().untilAsserted(() -> assertThat(findServerMeter("UnaryCall", "requests", COUNT, "result", "failure", "httpStatus", "200")).contains(3.0));
        given().ignoreExceptions().untilAsserted(() -> assertThat(findClientMeter("UnaryCall", "requests", COUNT, "result", "success")).contains(4.0));
        given().ignoreExceptions().untilAsserted(() -> assertThat(findClientMeter("UnaryCall", "requests", COUNT, "result", "failure")).contains(3.0));
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall", "requestLength", COUNT, "httpStatus", "200")).contains(7.0);
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall", "requestLength", TOTAL, "httpStatus", "200")).contains((7.0 * 14));
        assertThat(GrpcMetricsIntegrationTest.findClientMeter("UnaryCall", "requestLength", COUNT)).contains(7.0);
        assertThat(GrpcMetricsIntegrationTest.findClientMeter("UnaryCall", "requestLength", TOTAL)).contains((7.0 * 14));
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall", "responseLength", COUNT, "httpStatus", "200")).contains(7.0);
        /* + 3 * 0 */
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall", "responseLength", TOTAL, "httpStatus", "200")).contains((4.0 * 5));
        assertThat(GrpcMetricsIntegrationTest.findClientMeter("UnaryCall", "responseLength", COUNT)).contains(7.0);
        /* + 3 * 0 */
        assertThat(GrpcMetricsIntegrationTest.findClientMeter("UnaryCall", "responseLength", TOTAL)).contains((4.0 * 5));
    }

    @Test
    public void unframed() throws Exception {
        GrpcMetricsIntegrationTest.makeUnframedRequest("world");
        GrpcMetricsIntegrationTest.makeUnframedRequest("world");
        GrpcMetricsIntegrationTest.makeUnframedRequest("space");
        GrpcMetricsIntegrationTest.makeUnframedRequest("world");
        GrpcMetricsIntegrationTest.makeUnframedRequest("space");
        GrpcMetricsIntegrationTest.makeUnframedRequest("space");
        GrpcMetricsIntegrationTest.makeUnframedRequest("world");
        // Chance that get() returns NPE before the metric is first added, so ignore exceptions.
        given().ignoreExceptions().untilAsserted(() -> assertThat(findServerMeter("UnaryCall2", "requests", COUNT, "result", "success", "httpStatus", "200")).contains(4.0));
        given().ignoreExceptions().untilAsserted(() -> assertThat(findServerMeter("UnaryCall2", "requests", COUNT, "result", "failure", "httpStatus", "500")).contains(3.0));
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall2", "responseLength", COUNT, "httpStatus", "200")).contains(4.0);
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall2", "responseLength", COUNT, "httpStatus", "500")).contains(3.0);
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall2", "responseLength", TOTAL, "httpStatus", "200")).contains(0.0);
        assertThat(GrpcMetricsIntegrationTest.findServerMeter("UnaryCall2", "responseLength", TOTAL, "httpStatus", "500")).contains(225.0);
    }
}

