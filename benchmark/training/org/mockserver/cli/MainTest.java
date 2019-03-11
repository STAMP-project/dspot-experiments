package org.mockserver.cli;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.slf4j.event.Level;

import static Main.USAGE;
import static Main.systemOut;


/**
 *
 *
 * @author jamesdbloom
 */
public class MainTest {
    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    @Test
    public void shouldStartMockServer() {
        // given
        final int freePort = PortFactory.findFreePort();
        MockServerClient mockServerClient = new MockServerClient("127.0.0.1", freePort);
        Level originalLogLevel = ConfigurationProperties.logLevel();
        try {
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-logLevel", "DEBUG");
            // then
            MatcherAssert.assertThat(mockServerClient.isRunning(), Is.is(true));
            MatcherAssert.assertThat(ConfigurationProperties.logLevel().toString(), Is.is("DEBUG"));
        } finally {
            ConfigurationProperties.logLevel(originalLogLevel.toString());
            stopQuietly(mockServerClient);
        }
    }

    @Test
    public void shouldStartMockServerWithRemotePortAndHost() {
        // given
        final int freePort = PortFactory.findFreePort();
        MockServerClient mockServerClient = new MockServerClient("127.0.0.1", freePort);
        try {
            EchoServer echoServer = new EchoServer(false);
            echoServer.withNextResponse(response("port_forwarded_response"));
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-proxyRemotePort", String.valueOf(echoServer.getPort()), "-proxyRemoteHost", "127.0.0.1");
            final HttpResponse response = new org.mockserver.client.NettyHttpClient(MainTest.clientEventLoopGroup, null).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("127.0.0.1:" + freePort)), 10, TimeUnit.SECONDS);
            // then
            MatcherAssert.assertThat(mockServerClient.isRunning(), Is.is(true));
            MatcherAssert.assertThat(response.getBodyAsString(), Is.is("port_forwarded_response"));
        } finally {
            stopQuietly(mockServerClient);
        }
    }

    @Test
    public void shouldStartMockServerWithRemotePort() {
        // given
        final int freePort = PortFactory.findFreePort();
        MockServerClient mockServerClient = new MockServerClient("127.0.0.1", freePort);
        try {
            EchoServer echoServer = new EchoServer(false);
            echoServer.withNextResponse(response("port_forwarded_response"));
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-proxyRemotePort", String.valueOf(echoServer.getPort()));
            final HttpResponse response = new org.mockserver.client.NettyHttpClient(MainTest.clientEventLoopGroup, null).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("127.0.0.1:" + freePort)), 10, TimeUnit.SECONDS);
            // then
            MatcherAssert.assertThat(mockServerClient.isRunning(), Is.is(true));
            MatcherAssert.assertThat(response.getBodyAsString(), Is.is("port_forwarded_response"));
        } finally {
            stopQuietly(mockServerClient);
        }
    }

    @Test
    public void shouldPrintOutUsageForInvalidServerPort() throws UnsupportedEncodingException {
        // given
        PrintStream originalPrintStream = systemOut;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            systemOut = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8.name());
            // when
            Main.main("-serverPort", "A");
            // then
            String actual = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            String expected = ((((((((NEW_LINE) + "   =====================================================================================================") + (NEW_LINE)) + "   serverPort value \"A\" is invalid, please specify a comma separated list of ports i.e. \"1080,1081,1082\"") + (NEW_LINE)) + "   =====================================================================================================") + (NEW_LINE)) + (NEW_LINE)) + (USAGE);
            MatcherAssert.assertThat(actual, Is.is(expected));
        } finally {
            systemOut = originalPrintStream;
        }
    }

    @Test
    public void shouldPrintOutUsageForInvalidRemotePort() throws UnsupportedEncodingException {
        // given
        final int freePort = PortFactory.findFreePort();
        PrintStream originalPrintStream = systemOut;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            systemOut = new PrintStream(baos, true, StandardCharsets.UTF_8.name());
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-proxyRemotePort", "A");
            // then
            String actual = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            String expected = ((((((((NEW_LINE) + "   =======================================================================") + (NEW_LINE)) + "   proxyRemotePort value \"A\" is invalid, please specify a port i.e. \"1080\"") + (NEW_LINE)) + "   =======================================================================") + (NEW_LINE)) + (NEW_LINE)) + (USAGE);
            MatcherAssert.assertThat(actual, Is.is(expected));
        } finally {
            systemOut = originalPrintStream;
        }
    }

    @Test
    public void shouldPrintOutUsageForInvalidRemoteHost() throws UnsupportedEncodingException {
        // given
        final int freePort = PortFactory.findFreePort();
        PrintStream originalPrintStream = systemOut;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            systemOut = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8.name());
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-proxyRemoteHost", "%^&*(");
            // then
            String actual = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            String expected = ((((((((NEW_LINE) + "   ====================================================================================================") + (NEW_LINE)) + "   proxyRemoteHost value \"%^&*(\" is invalid, please specify a host name i.e. \"localhost\" or \"127.0.0.1\"") + (NEW_LINE)) + "   ====================================================================================================") + (NEW_LINE)) + (NEW_LINE)) + (USAGE);
            MatcherAssert.assertThat(actual, Is.is(expected));
        } finally {
            systemOut = originalPrintStream;
        }
    }

    @Test
    public void shouldPrintOutUsageForInvalidLogLevel() throws UnsupportedEncodingException {
        // given
        final int freePort = PortFactory.findFreePort();
        PrintStream originalPrintStream = systemOut;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            systemOut = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8.name());
            // when
            Main.main("-serverPort", String.valueOf(freePort), "-logLevel", "FOO");
            // then
            String actual = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            String expected = ((((((((NEW_LINE) + "   =======================================================================================================") + (NEW_LINE)) + "   logLevel value \"FOO\" is invalid, please specify one of \"TRACE\", \"DEBUG\", \"INFO\", \"WARN\", \"ERROR\", \"OFF\"") + (NEW_LINE)) + "   =======================================================================================================") + (NEW_LINE)) + (NEW_LINE)) + (USAGE);
            MatcherAssert.assertThat(actual, Is.is(expected));
        } finally {
            systemOut = originalPrintStream;
        }
    }
}

