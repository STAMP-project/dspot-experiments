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
package com.linecorp.armeria.server.grpc;


import GrpcSerializationFormats.JSON;
import GrpcSerializationFormats.JSON_WEB;
import GrpcSerializationFormats.PROTO;
import GrpcSerializationFormats.PROTO_WEB;
import MediaType.JSON_UTF_8;
import MediaType.PROTOBUF;
import TestServiceGrpc.SERVICE_NAME;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.grpc.testing.Messages.Payload;
import com.linecorp.armeria.grpc.testing.Messages.SimpleRequest;
import com.linecorp.armeria.grpc.testing.Messages.SimpleResponse;
import com.linecorp.armeria.grpc.testing.ReconnectServiceGrpc.ReconnectServiceImplBase;
import com.linecorp.armeria.grpc.testing.Test;
import com.linecorp.armeria.grpc.testing.TestServiceGrpc.TestServiceImplBase;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.docs.EndpointInfoBuilder;
import com.linecorp.armeria.server.grpc.GrpcDocServicePlugin.ServiceEntry;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.server.ServerRule;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.ClassRule;
import org.mockito.Mockito;


public class GrpcDocServiceTest {
    private static final ServiceDescriptor TEST_SERVICE_DESCRIPTOR = Test.getDescriptor().findServiceByName("TestService");

    private static final ServiceDescriptor RECONNECT_SERVICE_DESCRIPTOR = Test.getDescriptor().findServiceByName("ReconnectService");

    private static final String INJECTED_HEADER_PROVIDER1 = "armeria.registerHeaderProvider(function() { return Promise.resolve({ 'foo': 'bar' }); });";

    private static final String INJECTED_HEADER_PROVIDER2 = "armeria.registerHeaderProvider(function() { return Promise.resolve({ 'cat': 'dog' }); });";

    private static final String INJECTED_HEADER_PROVIDER3 = "armeria.registerHeaderProvider(function() { return Promise.resolve({ 'moo': 'cow' }); });";

    private static class TestService extends TestServiceImplBase {
        @Override
        public void unaryCall(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            final ByteString body = ByteString.copyFromUtf8(("hello " + (request.getPayload().getBody().toStringUtf8())));
            responseObserver.onNext(SimpleResponse.newBuilder().setPayload(Payload.newBuilder().setBody(body)).build());
            responseObserver.onCompleted();
        }
    }

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.serviceUnder("/test", new GrpcServiceBuilder().addService(new GrpcDocServiceTest.TestService()).supportedSerializationFormats(GrpcSerializationFormats.values()).enableUnframedRequests(true).build());
            sb.serviceUnder("/docs/", new DocServiceBuilder().exampleRequestForMethod(SERVICE_NAME, "UnaryCall", SimpleRequest.newBuilder().setPayload(Payload.newBuilder().setBody(ByteString.copyFromUtf8("world"))).build()).injectedScript(GrpcDocServiceTest.INJECTED_HEADER_PROVIDER1, GrpcDocServiceTest.INJECTED_HEADER_PROVIDER2).injectedScriptSupplier(( ctx, req) -> GrpcDocServiceTest.INJECTED_HEADER_PROVIDER3).build().decorate(LoggingService.newDecorator()));
            sb.serviceUnder("/", new GrpcServiceBuilder().addService(Mockito.mock(ReconnectServiceImplBase.class)).build());
        }
    };

    @org.junit.Test
    public void testOk() throws Exception {
        final List<ServiceEntry> entries = ImmutableList.of(new ServiceEntry(GrpcDocServiceTest.TEST_SERVICE_DESCRIPTOR, ImmutableList.of(new EndpointInfoBuilder("*", "/test/armeria.grpc.testing.TestService/").availableMimeTypes(PROTO.mediaType(), JSON.mediaType(), PROTO_WEB.mediaType(), JSON_WEB.mediaType(), PROTOBUF.withParameter("protocol", "gRPC"), JSON_UTF_8.withParameter("protocol", "gRPC")).build())), new ServiceEntry(GrpcDocServiceTest.RECONNECT_SERVICE_DESCRIPTOR, ImmutableList.of(new EndpointInfoBuilder("*", "/armeria.grpc.testing.ReconnectService/").availableFormats(PROTO, PROTO_WEB).build())));
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode expectedJson = mapper.valueToTree(new GrpcDocServicePlugin().generate(entries));
        // The specification generated by GrpcDocServicePlugin does not include the examples specified
        // when building a DocService, so we add them manually here.
        GrpcDocServiceTest.addExamples(expectedJson);
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet req = new HttpGet(GrpcDocServiceTest.specificationUri());
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                final JsonNode actualJson = mapper.readTree(EntityUtils.toString(res.getEntity()));
                // The specification generated by ThriftDocServicePlugin does not include the docstrings
                // because it's injected by the DocService, so we remove them here for easier comparison.
                GrpcDocServiceTest.removeDocStrings(actualJson);
                // Convert to the prettified strings for human-readable comparison.
                final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                final String actualJsonString = writer.writeValueAsString(actualJson);
                final String expectedJsonString = writer.writeValueAsString(expectedJson);
                assertThat(actualJsonString).isEqualTo(expectedJsonString);
            }
            final HttpGet injectedJsReq = new HttpGet(GrpcDocServiceTest.server.uri("/docs/injected.js"));
            try (CloseableHttpResponse res = hc.execute(injectedJsReq)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo((((((GrpcDocServiceTest.INJECTED_HEADER_PROVIDER1) + '\n') + (GrpcDocServiceTest.INJECTED_HEADER_PROVIDER2)) + '\n') + (GrpcDocServiceTest.INJECTED_HEADER_PROVIDER3)));
            }
        }
    }

    @org.junit.Test
    public void testMethodNotAllowed() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpPost req = new HttpPost(GrpcDocServiceTest.specificationUri());
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 405 Method Not Allowed");
            }
        }
    }
}

