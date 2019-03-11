/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest;


import HttpResponseStatus.ACCEPTED;
import HttpResponseStatus.BAD_REQUEST;
import HttpResponseStatus.MOVED_PERMANENTLY;
import HttpResponseStatus.OK;
import MessageParameterRequisiteness.MANDATORY;
import RestAPIVersion.V0;
import RestAPIVersion.V1;
import RestOptions.ADDRESS;
import RestOptions.BIND_PORT;
import SecurityOptions.SSL_REST_ENABLED;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rest.handler.AbstractRestHandler;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.handler.RestHandlerSpecification;
import org.apache.flink.runtime.rest.messages.ConversionException;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.apache.flink.runtime.rest.messages.MessageHeaders;
import org.apache.flink.runtime.rest.messages.MessageParameters;
import org.apache.flink.runtime.rest.messages.MessagePathParameter;
import org.apache.flink.runtime.rest.messages.MessageQueryParameter;
import org.apache.flink.runtime.rest.messages.RequestBody;
import org.apache.flink.runtime.rest.messages.ResponseBody;
import org.apache.flink.runtime.rest.util.RestClientException;
import org.apache.flink.runtime.rest.versioning.RestAPIVersion;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.webmonitor.RestfulGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInboundHandler;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.TooLongFrameException;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static HttpMethodWrapper.GET;
import static HttpMethodWrapper.POST;


/**
 * IT cases for {@link RestClient} and {@link RestServerEndpoint}.
 */
@RunWith(Parameterized.class)
public class RestServerEndpointITCase extends TestLogger {
    private static final JobID PATH_JOB_ID = new JobID();

    private static final JobID QUERY_JOB_ID = new JobID();

    private static final String JOB_ID_KEY = "jobid";

    private static final Time timeout = Time.seconds(10L);

    private static final int TEST_REST_MAX_CONTENT_LENGTH = 4096;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private RestServerEndpoint serverEndpoint;

    private RestClient restClient;

    private RestServerEndpointITCase.TestUploadHandler testUploadHandler;

    private InetSocketAddress serverAddress;

    private final Configuration config;

    private SSLContext defaultSSLContext;

    private SSLSocketFactory defaultSSLSocketFactory;

    private RestServerEndpointITCase.TestHandler testHandler;

    public RestServerEndpointITCase(final Configuration config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Tests that request are handled as individual units which don't interfere with each other.
     * This means that request responses can overtake each other.
     */
    @Test
    public void testRequestInterleaving() throws Exception {
        final RestServerEndpointITCase.HandlerBlocker handlerBlocker = new RestServerEndpointITCase.HandlerBlocker(RestServerEndpointITCase.timeout);
        testHandler.handlerBody = ( id) -> {
            if (id == 1) {
                handlerBlocker.arriveAndBlock();
            }
            return CompletableFuture.completedFuture(new RestServerEndpointITCase.TestResponse(id));
        };
        // send first request and wait until the handler blocks
        final CompletableFuture<RestServerEndpointITCase.TestResponse> response1 = sendRequestToTestHandler(new RestServerEndpointITCase.TestRequest(1));
        handlerBlocker.awaitRequestToArrive();
        // send second request and verify response
        final CompletableFuture<RestServerEndpointITCase.TestResponse> response2 = sendRequestToTestHandler(new RestServerEndpointITCase.TestRequest(2));
        Assert.assertEquals(2, response2.get().id);
        // wake up blocked handler
        handlerBlocker.unblockRequest();
        // verify response to first request
        Assert.assertEquals(1, response1.get().id);
    }

    /**
     * Tests that a bad handler request (HandlerRequest cannot be created) is reported as a BAD_REQUEST
     * and not an internal server error.
     *
     * <p>See FLINK-7663
     */
    @Test
    public void testBadHandlerRequest() throws Exception {
        final RestServerEndpointITCase.FaultyTestParameters parameters = new RestServerEndpointITCase.FaultyTestParameters();
        parameters.faultyJobIDPathParameter.resolve(RestServerEndpointITCase.PATH_JOB_ID);
        ((RestServerEndpointITCase.TestParameters) (parameters)).jobIDQueryParameter.resolve(Collections.singletonList(RestServerEndpointITCase.QUERY_JOB_ID));
        CompletableFuture<RestServerEndpointITCase.TestResponse> response = restClient.sendRequest(serverAddress.getHostName(), serverAddress.getPort(), new RestServerEndpointITCase.TestHeaders(), parameters, new RestServerEndpointITCase.TestRequest(2));
        try {
            response.get();
            Assert.fail("The request should fail with a bad request return code.");
        } catch (ExecutionException ee) {
            Throwable t = ExceptionUtils.stripExecutionException(ee);
            Assert.assertTrue((t instanceof RestClientException));
            RestClientException rce = ((RestClientException) (t));
            Assert.assertEquals(BAD_REQUEST, rce.getHttpResponseStatus());
        }
    }

    /**
     * Tests that requests larger than {@link #TEST_REST_MAX_CONTENT_LENGTH} are rejected.
     */
    @Test
    public void testShouldRespectMaxContentLengthLimitForRequests() throws Exception {
        testHandler.handlerBody = ( id) -> {
            throw new AssertionError("Request should not arrive at server.");
        };
        try {
            sendRequestToTestHandler(new RestServerEndpointITCase.TestRequest(2, RestServerEndpointITCase.createStringOfSize(RestServerEndpointITCase.TEST_REST_MAX_CONTENT_LENGTH))).get();
            Assert.fail("Expected exception not thrown");
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripExecutionException(e);
            Assert.assertThat(throwable, Matchers.instanceOf(RestClientException.class));
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Try to raise"));
        }
    }

    /**
     * Tests that responses larger than {@link #TEST_REST_MAX_CONTENT_LENGTH} are rejected.
     */
    @Test
    public void testShouldRespectMaxContentLengthLimitForResponses() throws Exception {
        testHandler.handlerBody = ( id) -> CompletableFuture.completedFuture(new RestServerEndpointITCase.TestResponse(id, RestServerEndpointITCase.createStringOfSize(RestServerEndpointITCase.TEST_REST_MAX_CONTENT_LENGTH)));
        try {
            sendRequestToTestHandler(new RestServerEndpointITCase.TestRequest(1)).get();
            Assert.fail("Expected exception not thrown");
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripExecutionException(e);
            Assert.assertThat(throwable, Matchers.instanceOf(TooLongFrameException.class));
            Assert.assertThat(throwable.getMessage(), Matchers.containsString("Try to raise"));
        }
    }

    /**
     * Tests that multipart/form-data uploads work correctly.
     *
     * @see FileUploadHandler
     */
    @Test
    public void testFileUpload() throws Exception {
        final String boundary = RestServerEndpointITCase.generateMultiPartBoundary();
        final String crlf = "\r\n";
        final String uploadedContent = "hello";
        final HttpURLConnection connection = openHttpConnectionForUpload(boundary);
        try (OutputStream output = connection.getOutputStream();PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {
            writer.append(("--" + boundary)).append(crlf);
            writer.append("Content-Disposition: form-data; name=\"foo\"; filename=\"bar\"").append(crlf);
            writer.append("Content-Type: plain/text; charset=utf8").append(crlf);
            writer.append(crlf).flush();
            output.write(uploadedContent.getBytes(StandardCharsets.UTF_8));
            output.flush();
            writer.append(crlf).flush();
            writer.append((("--" + boundary) + "--")).append(crlf).flush();
        }
        Assert.assertEquals(200, connection.getResponseCode());
        final byte[] lastUploadedFileContents = testUploadHandler.getLastUploadedFileContents();
        Assert.assertEquals(uploadedContent, new String(lastUploadedFileContents, StandardCharsets.UTF_8));
    }

    /**
     * Sending multipart/form-data without a file should result in a bad request if the handler
     * expects a file upload.
     */
    @Test
    public void testMultiPartFormDataWithoutFileUpload() throws Exception {
        final String boundary = RestServerEndpointITCase.generateMultiPartBoundary();
        final String crlf = "\r\n";
        final HttpURLConnection connection = openHttpConnectionForUpload(boundary);
        try (OutputStream output = connection.getOutputStream();PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {
            writer.append(("--" + boundary)).append(crlf);
            writer.append("Content-Disposition: form-data; name=\"foo\"").append(crlf);
            writer.append(crlf).flush();
            output.write("test".getBytes(StandardCharsets.UTF_8));
            output.flush();
            writer.append(crlf).flush();
            writer.append((("--" + boundary) + "--")).append(crlf).flush();
        }
        Assert.assertEquals(400, connection.getResponseCode());
    }

    /**
     * Tests that files can be served with the {@link StaticFileServerHandler}.
     */
    @Test
    public void testStaticFileServerHandler() throws Exception {
        final File file = temporaryFolder.newFile();
        Files.write(file.toPath(), Collections.singletonList("foobar"));
        final URL url = new URL((((serverEndpoint.getRestBaseUrl()) + "/") + (file.getName())));
        final HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setRequestMethod("GET");
        final String fileContents = IOUtils.toString(connection.getInputStream());
        Assert.assertEquals("foobar", fileContents.trim());
    }

    @Test
    public void testVersioning() throws Exception {
        CompletableFuture<EmptyResponseBody> unspecifiedVersionResponse = restClient.sendRequest(serverAddress.getHostName(), serverAddress.getPort(), RestServerEndpointITCase.TestVersionHeaders.INSTANCE, EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.emptyList());
        unspecifiedVersionResponse.get(5, TimeUnit.SECONDS);
        CompletableFuture<EmptyResponseBody> specifiedVersionResponse = restClient.sendRequest(serverAddress.getHostName(), serverAddress.getPort(), RestServerEndpointITCase.TestVersionHeaders.INSTANCE, EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.emptyList(), V1);
        specifiedVersionResponse.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testVersionSelection() throws Exception {
        CompletableFuture<EmptyResponseBody> version1Response = restClient.sendRequest(serverAddress.getHostName(), serverAddress.getPort(), RestServerEndpointITCase.TestVersionSelectionHeaders1.INSTANCE, EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.emptyList(), V0);
        try {
            version1Response.get(5, TimeUnit.SECONDS);
            Assert.fail();
        } catch (ExecutionException ee) {
            RestClientException rce = ((RestClientException) (ee.getCause()));
            Assert.assertEquals(OK, rce.getHttpResponseStatus());
        }
        CompletableFuture<EmptyResponseBody> version2Response = restClient.sendRequest(serverAddress.getHostName(), serverAddress.getPort(), RestServerEndpointITCase.TestVersionSelectionHeaders2.INSTANCE, EmptyMessageParameters.getInstance(), EmptyRequestBody.getInstance(), Collections.emptyList(), V1);
        try {
            version2Response.get(5, TimeUnit.SECONDS);
            Assert.fail();
        } catch (ExecutionException ee) {
            RestClientException rce = ((RestClientException) (ee.getCause()));
            Assert.assertEquals(ACCEPTED, rce.getHttpResponseStatus());
        }
    }

    @Test
    public void testDefaultVersionRouting() throws Exception {
        Assume.assumeFalse("Ignoring SSL-enabled test to keep OkHttp usage simple.", config.getBoolean(SSL_REST_ENABLED));
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(((serverEndpoint.getRestBaseUrl()) + (RestServerEndpointITCase.TestVersionSelectionHeaders2.INSTANCE.getTargetRestEndpointURL()))).build();
        try (final Response response = client.newCall(request).execute()) {
            Assert.assertEquals(ACCEPTED.code(), response.code());
        }
    }

    @Test
    public void testNonSslRedirectForEnabledSsl() throws Exception {
        Assume.assumeTrue(config.getBoolean(SSL_REST_ENABLED));
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
        String httpsUrl = (serverEndpoint.getRestBaseUrl()) + "/path";
        String httpUrl = httpsUrl.replace("https://", "http://");
        Request request = new Request.Builder().url(httpUrl).build();
        try (final Response response = client.newCall(request).execute()) {
            Assert.assertEquals(MOVED_PERMANENTLY.code(), response.code());
            Assert.assertThat(response.headers().names(), CoreMatchers.hasItems("Location"));
            Assert.assertEquals(httpsUrl, response.header("Location"));
        }
    }

    /**
     * Tests that after calling {@link RestServerEndpoint#closeAsync()}, the handlers are closed
     * first, and we wait for in-flight requests to finish. As long as not all handlers are closed,
     * HTTP requests should be served.
     */
    @Test
    public void testShouldWaitForHandlersWhenClosing() throws Exception {
        testHandler.closeFuture = new CompletableFuture<>();
        final RestServerEndpointITCase.HandlerBlocker handlerBlocker = new RestServerEndpointITCase.HandlerBlocker(RestServerEndpointITCase.timeout);
        testHandler.handlerBody = ( id) -> {
            // Intentionally schedule the work on a different thread. This is to simulate
            // handlers where the CompletableFuture is finished by the RPC framework.
            return CompletableFuture.supplyAsync(() -> {
                handlerBlocker.arriveAndBlock();
                return new RestServerEndpointITCase.TestResponse(id);
            });
        };
        // Initiate closing RestServerEndpoint but the test handler should block.
        final CompletableFuture<Void> closeRestServerEndpointFuture = serverEndpoint.closeAsync();
        Assert.assertThat(closeRestServerEndpointFuture.isDone(), Matchers.is(false));
        final CompletableFuture<RestServerEndpointITCase.TestResponse> request = sendRequestToTestHandler(new RestServerEndpointITCase.TestRequest(1));
        handlerBlocker.awaitRequestToArrive();
        // Allow handler to close but there is still one in-flight request which should prevent
        // the RestServerEndpoint from closing.
        testHandler.closeFuture.complete(null);
        Assert.assertThat(closeRestServerEndpointFuture.isDone(), Matchers.is(false));
        // Finish the in-flight request.
        handlerBlocker.unblockRequest();
        request.get(RestServerEndpointITCase.timeout.getSize(), RestServerEndpointITCase.timeout.getUnit());
        closeRestServerEndpointFuture.get(RestServerEndpointITCase.timeout.getSize(), RestServerEndpointITCase.timeout.getUnit());
    }

    @Test
    public void testRestServerBindPort() throws Exception {
        final int portRangeStart = 52300;
        final int portRangeEnd = 52400;
        final Configuration config = new Configuration();
        config.setString(ADDRESS, "localhost");
        config.setString(BIND_PORT, ((portRangeStart + "-") + portRangeEnd));
        final RestServerEndpointConfiguration serverConfig = RestServerEndpointConfiguration.fromConfiguration(config);
        try (RestServerEndpoint serverEndpoint1 = new RestServerEndpointITCase.TestRestServerEndpoint(serverConfig, Collections.emptyList());RestServerEndpoint serverEndpoint2 = new RestServerEndpointITCase.TestRestServerEndpoint(serverConfig, Collections.emptyList())) {
            serverEndpoint1.start();
            serverEndpoint2.start();
            Assert.assertNotEquals(serverEndpoint1.getServerAddress().getPort(), serverEndpoint2.getServerAddress().getPort());
            Assert.assertThat(serverEndpoint1.getServerAddress().getPort(), Matchers.is(Matchers.greaterThanOrEqualTo(portRangeStart)));
            Assert.assertThat(serverEndpoint1.getServerAddress().getPort(), Matchers.is(Matchers.lessThanOrEqualTo(portRangeEnd)));
            Assert.assertThat(serverEndpoint2.getServerAddress().getPort(), Matchers.is(Matchers.greaterThanOrEqualTo(portRangeStart)));
            Assert.assertThat(serverEndpoint2.getServerAddress().getPort(), Matchers.is(Matchers.lessThanOrEqualTo(portRangeEnd)));
        }
    }

    static class TestRestServerEndpoint extends RestServerEndpoint {
        private final List<Tuple2<RestHandlerSpecification, ChannelInboundHandler>> handlers;

        TestRestServerEndpoint(RestServerEndpointConfiguration configuration, List<Tuple2<RestHandlerSpecification, ChannelInboundHandler>> handlers) throws IOException {
            super(configuration);
            this.handlers = Objects.requireNonNull(handlers);
        }

        @Override
        protected List<Tuple2<RestHandlerSpecification, ChannelInboundHandler>> initializeHandlers(final CompletableFuture<String> localAddressFuture) {
            return handlers;
        }

        @Override
        protected void startInternal() {
        }
    }

    private static class TestHandler extends AbstractRestHandler<RestfulGateway, RestServerEndpointITCase.TestRequest, RestServerEndpointITCase.TestResponse, RestServerEndpointITCase.TestParameters> {
        private CompletableFuture<Void> closeFuture = CompletableFuture.completedFuture(null);

        private Function<Integer, CompletableFuture<RestServerEndpointITCase.TestResponse>> handlerBody;

        TestHandler(GatewayRetriever<RestfulGateway> leaderRetriever, Time timeout) {
            super(leaderRetriever, timeout, Collections.emptyMap(), new RestServerEndpointITCase.TestHeaders());
        }

        @Override
        protected CompletableFuture<RestServerEndpointITCase.TestResponse> handleRequest(@Nonnull
        HandlerRequest<RestServerEndpointITCase.TestRequest, RestServerEndpointITCase.TestParameters> request, RestfulGateway gateway) {
            Assert.assertEquals(request.getPathParameter(RestServerEndpointITCase.JobIDPathParameter.class), RestServerEndpointITCase.PATH_JOB_ID);
            Assert.assertEquals(request.getQueryParameter(RestServerEndpointITCase.JobIDQueryParameter.class).get(0), RestServerEndpointITCase.QUERY_JOB_ID);
            final int id = request.getRequestBody().id;
            return handlerBody.apply(id);
        }

        @Override
        public CompletableFuture<Void> closeHandlerAsync() {
            return closeFuture;
        }
    }

    /**
     * This is a helper class for tests that require to have fine-grained control over HTTP
     * requests so that they are not dispatched immediately.
     */
    private static class HandlerBlocker {
        private final Time timeout;

        private final CountDownLatch requestArrivedLatch = new CountDownLatch(1);

        private final CountDownLatch finishRequestLatch = new CountDownLatch(1);

        private HandlerBlocker(final Time timeout) {
            this.timeout = checkNotNull(timeout);
        }

        /**
         * Waits until {@link #arriveAndBlock()} is called.
         */
        public void awaitRequestToArrive() {
            try {
                Assert.assertTrue(requestArrivedLatch.await(timeout.getSize(), timeout.getUnit()));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Signals that the request arrived. This method blocks until {@link #unblockRequest()} is
         * called.
         */
        public void arriveAndBlock() {
            markRequestArrived();
            try {
                Assert.assertTrue(finishRequestLatch.await(timeout.getSize(), timeout.getUnit()));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         *
         *
         * @see #arriveAndBlock()
         */
        public void unblockRequest() {
            finishRequestLatch.countDown();
        }

        private void markRequestArrived() {
            requestArrivedLatch.countDown();
        }
    }

    static class TestRestClient extends RestClient {
        TestRestClient(RestClientConfiguration configuration) {
            super(configuration, TestingUtils.defaultExecutor());
        }
    }

    private static class TestRequest implements RequestBody {
        public final int id;

        public final String content;

        public TestRequest(int id) {
            this(id, null);
        }

        @JsonCreator
        public TestRequest(@JsonProperty("id")
        int id, @JsonProperty("content")
        final String content) {
            this.id = id;
            this.content = content;
        }
    }

    private static class TestResponse implements ResponseBody {
        public final int id;

        public final String content;

        public TestResponse(int id) {
            this(id, null);
        }

        @JsonCreator
        public TestResponse(@JsonProperty("id")
        int id, @JsonProperty("content")
        String content) {
            this.id = id;
            this.content = content;
        }
    }

    private static class TestHeaders implements MessageHeaders<RestServerEndpointITCase.TestRequest, RestServerEndpointITCase.TestResponse, RestServerEndpointITCase.TestParameters> {
        @Override
        public HttpMethodWrapper getHttpMethod() {
            return POST;
        }

        @Override
        public String getTargetRestEndpointURL() {
            return "/test/:jobid";
        }

        @Override
        public Class<RestServerEndpointITCase.TestRequest> getRequestClass() {
            return RestServerEndpointITCase.TestRequest.class;
        }

        @Override
        public Class<RestServerEndpointITCase.TestResponse> getResponseClass() {
            return RestServerEndpointITCase.TestResponse.class;
        }

        @Override
        public HttpResponseStatus getResponseStatusCode() {
            return HttpResponseStatus.OK;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public RestServerEndpointITCase.TestParameters getUnresolvedMessageParameters() {
            return new RestServerEndpointITCase.TestParameters();
        }
    }

    private static class TestParameters extends MessageParameters {
        private final RestServerEndpointITCase.JobIDPathParameter jobIDPathParameter = new RestServerEndpointITCase.JobIDPathParameter();

        private final RestServerEndpointITCase.JobIDQueryParameter jobIDQueryParameter = new RestServerEndpointITCase.JobIDQueryParameter();

        @Override
        public Collection<MessagePathParameter<?>> getPathParameters() {
            return Collections.singleton(jobIDPathParameter);
        }

        @Override
        public Collection<MessageQueryParameter<?>> getQueryParameters() {
            return Collections.singleton(jobIDQueryParameter);
        }
    }

    private static class FaultyTestParameters extends RestServerEndpointITCase.TestParameters {
        private final RestServerEndpointITCase.FaultyJobIDPathParameter faultyJobIDPathParameter = new RestServerEndpointITCase.FaultyJobIDPathParameter();

        @Override
        public Collection<MessagePathParameter<?>> getPathParameters() {
            return Collections.singleton(faultyJobIDPathParameter);
        }
    }

    static class JobIDPathParameter extends MessagePathParameter<JobID> {
        JobIDPathParameter() {
            super(RestServerEndpointITCase.JOB_ID_KEY);
        }

        @Override
        public JobID convertFromString(String value) {
            return JobID.fromHexString(value);
        }

        @Override
        protected String convertToString(JobID value) {
            return value.toString();
        }

        @Override
        public String getDescription() {
            return "correct JobID parameter";
        }
    }

    static class FaultyJobIDPathParameter extends MessagePathParameter<JobID> {
        FaultyJobIDPathParameter() {
            super(RestServerEndpointITCase.JOB_ID_KEY);
        }

        @Override
        protected JobID convertFromString(String value) throws ConversionException {
            return JobID.fromHexString(value);
        }

        @Override
        protected String convertToString(JobID value) {
            return "foobar";
        }

        @Override
        public String getDescription() {
            return "faulty JobID parameter";
        }
    }

    static class JobIDQueryParameter extends MessageQueryParameter<JobID> {
        JobIDQueryParameter() {
            super(RestServerEndpointITCase.JOB_ID_KEY, MANDATORY);
        }

        @Override
        public JobID convertStringToValue(String value) {
            return JobID.fromHexString(value);
        }

        @Override
        public String convertValueToString(JobID value) {
            return value.toString();
        }

        @Override
        public String getDescription() {
            return "query JobID parameter";
        }
    }

    private static class TestUploadHandler extends AbstractRestHandler<RestfulGateway, EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        private volatile byte[] lastUploadedFileContents;

        private TestUploadHandler(final GatewayRetriever<? extends RestfulGateway> leaderRetriever, final Time timeout) {
            super(leaderRetriever, timeout, Collections.emptyMap(), RestServerEndpointITCase.TestUploadHeaders.INSTANCE);
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        final HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        final RestfulGateway gateway) throws RestHandlerException {
            Collection<Path> uploadedFiles = request.getUploadedFiles().stream().map(File::toPath).collect(Collectors.toList());
            if ((uploadedFiles.size()) != 1) {
                throw new RestHandlerException((("Expected 1 file, received " + (uploadedFiles.size())) + '.'), HttpResponseStatus.BAD_REQUEST);
            }
            try {
                lastUploadedFileContents = Files.readAllBytes(uploadedFiles.iterator().next());
            } catch (IOException e) {
                throw new RestHandlerException("Could not read contents of uploaded file.", HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
            }
            return CompletableFuture.completedFuture(EmptyResponseBody.getInstance());
        }

        public byte[] getLastUploadedFileContents() {
            return lastUploadedFileContents;
        }
    }

    static class TestVersionHandler extends AbstractRestHandler<RestfulGateway, EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        TestVersionHandler(final GatewayRetriever<? extends RestfulGateway> leaderRetriever, final Time timeout) {
            super(leaderRetriever, timeout, Collections.emptyMap(), RestServerEndpointITCase.TestVersionHeaders.INSTANCE);
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        RestfulGateway gateway) throws RestHandlerException {
            return CompletableFuture.completedFuture(EmptyResponseBody.getInstance());
        }
    }

    enum TestVersionHeaders implements MessageHeaders<EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {

        INSTANCE;
        @Override
        public Class<EmptyRequestBody> getRequestClass() {
            return EmptyRequestBody.class;
        }

        @Override
        public HttpMethodWrapper getHttpMethod() {
            return GET;
        }

        @Override
        public String getTargetRestEndpointURL() {
            return "/test/versioning";
        }

        @Override
        public Class<EmptyResponseBody> getResponseClass() {
            return EmptyResponseBody.class;
        }

        @Override
        public HttpResponseStatus getResponseStatusCode() {
            return HttpResponseStatus.OK;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public EmptyMessageParameters getUnresolvedMessageParameters() {
            return EmptyMessageParameters.getInstance();
        }

        @Override
        public Collection<RestAPIVersion> getSupportedAPIVersions() {
            return Collections.singleton(V1);
        }
    }

    private interface TestVersionSelectionHeadersBase extends MessageHeaders<EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        @Override
        default Class<EmptyRequestBody> getRequestClass() {
            return EmptyRequestBody.class;
        }

        @Override
        default HttpMethodWrapper getHttpMethod() {
            return HttpMethodWrapper.GET;
        }

        @Override
        default String getTargetRestEndpointURL() {
            return "/test/select-version";
        }

        @Override
        default Class<EmptyResponseBody> getResponseClass() {
            return EmptyResponseBody.class;
        }

        @Override
        default HttpResponseStatus getResponseStatusCode() {
            return HttpResponseStatus.OK;
        }

        @Override
        default String getDescription() {
            return null;
        }

        @Override
        default EmptyMessageParameters getUnresolvedMessageParameters() {
            return EmptyMessageParameters.getInstance();
        }
    }

    private enum TestVersionSelectionHeaders1 implements RestServerEndpointITCase.TestVersionSelectionHeadersBase {

        INSTANCE;
        @Override
        public Collection<RestAPIVersion> getSupportedAPIVersions() {
            return Collections.singleton(V0);
        }
    }

    private enum TestVersionSelectionHeaders2 implements RestServerEndpointITCase.TestVersionSelectionHeadersBase {

        INSTANCE;
        @Override
        public Collection<RestAPIVersion> getSupportedAPIVersions() {
            return Collections.singleton(V1);
        }
    }

    private static class TestVersionSelectionHandler1 extends AbstractRestHandler<RestfulGateway, EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        private TestVersionSelectionHandler1(final GatewayRetriever<? extends RestfulGateway> leaderRetriever, final Time timeout) {
            super(leaderRetriever, timeout, Collections.emptyMap(), RestServerEndpointITCase.TestVersionSelectionHeaders1.INSTANCE);
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        RestfulGateway gateway) throws RestHandlerException {
            throw new RestHandlerException("test failure 1", HttpResponseStatus.OK);
        }
    }

    private static class TestVersionSelectionHandler2 extends AbstractRestHandler<RestfulGateway, EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        private TestVersionSelectionHandler2(final GatewayRetriever<? extends RestfulGateway> leaderRetriever, final Time timeout) {
            super(leaderRetriever, timeout, Collections.emptyMap(), RestServerEndpointITCase.TestVersionSelectionHeaders2.INSTANCE);
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        RestfulGateway gateway) throws RestHandlerException {
            throw new RestHandlerException("test failure 2", HttpResponseStatus.ACCEPTED);
        }
    }

    private enum TestUploadHeaders implements MessageHeaders<EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {

        INSTANCE;
        @Override
        public Class<EmptyResponseBody> getResponseClass() {
            return EmptyResponseBody.class;
        }

        @Override
        public HttpResponseStatus getResponseStatusCode() {
            return HttpResponseStatus.OK;
        }

        @Override
        public Class<EmptyRequestBody> getRequestClass() {
            return EmptyRequestBody.class;
        }

        @Override
        public EmptyMessageParameters getUnresolvedMessageParameters() {
            return EmptyMessageParameters.getInstance();
        }

        @Override
        public HttpMethodWrapper getHttpMethod() {
            return HttpMethodWrapper.POST;
        }

        @Override
        public String getTargetRestEndpointURL() {
            return "/upload";
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public boolean acceptsFileUploads() {
            return true;
        }
    }
}

