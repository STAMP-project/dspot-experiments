package org.mockserver.integration.mocking;


import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.mocking.initializer.ExpectationInitializerIntegrationExample;
import org.mockserver.mockserver.MockServer;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationInitializerIntegrationTest {
    static NettyHttpClient httpClient;

    private static EventLoopGroup clientEventLoopGroup;

    @Test
    public void shouldLoadExpectationsFromJson() throws Exception {
        // given
        String initializationJsonPath = ConfigurationProperties.initializationJsonPath();
        MockServer mockServer = null;
        try {
            // when
            ConfigurationProperties.initializationJsonPath("org/mockserver/integration/mocking/initializer/initializerJson.json");
            mockServer = new MockServer();
            // then
            Assert.assertThat(ExpectationInitializerIntegrationTest.httpClient.sendRequest(request().withMethod("GET").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (mockServer.getLocalPort()))).withPath("/simpleFirst")).get(10, TimeUnit.SECONDS).getBodyAsString(), Matchers.is("some first response"));
            Assert.assertThat(ExpectationInitializerIntegrationTest.httpClient.sendRequest(request().withMethod("GET").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (mockServer.getLocalPort()))).withPath("/simpleSecond")).get(10, TimeUnit.SECONDS).getBodyAsString(), Matchers.is("some second response"));
        } finally {
            ConfigurationProperties.initializationJsonPath(initializationJsonPath);
            stopQuietly(mockServer);
        }
    }

    @Test
    public void shouldLoadExpectationsFromInitializerClass() throws Exception {
        // given
        String initializationClass = ConfigurationProperties.initializationClass();
        MockServer mockServer = null;
        try {
            // when
            ConfigurationProperties.initializationClass(ExpectationInitializerIntegrationExample.class.getName());
            mockServer = new MockServer();
            // then
            Assert.assertThat(ExpectationInitializerIntegrationTest.httpClient.sendRequest(request().withMethod("GET").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (mockServer.getLocalPort()))).withPath("/simpleFirst")).get(10, TimeUnit.SECONDS).getBodyAsString(), Matchers.is("some first response"));
            Assert.assertThat(ExpectationInitializerIntegrationTest.httpClient.sendRequest(request().withMethod("GET").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (mockServer.getLocalPort()))).withPath("/simpleSecond")).get(10, TimeUnit.SECONDS).getBodyAsString(), Matchers.is("some second response"));
        } finally {
            ConfigurationProperties.initializationClass(initializationClass);
            stopQuietly(mockServer);
        }
    }
}

