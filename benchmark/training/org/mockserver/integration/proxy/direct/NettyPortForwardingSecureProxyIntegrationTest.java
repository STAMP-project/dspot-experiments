package org.mockserver.integration.proxy.direct;


import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.mockserver.MockServer;
import org.mockserver.streams.IOStreamUtils;


/**
 *
 *
 * @author jamesdbloom
 */
public class NettyPortForwardingSecureProxyIntegrationTest {
    private static EchoServer echoServer;

    private static MockServer mockServer;

    private static MockServerClient mockServerClient;

    @Test
    public void shouldForwardRequestsUsingSocketDirectlyHeadersOnly() throws Exception {
        Socket socket = null;
        try {
            socket = sslSocketFactory().wrapSocket(new Socket("localhost", NettyPortForwardingSecureProxyIntegrationTest.mockServer.getLocalPort()));
            // given
            OutputStream output = socket.getOutputStream();
            // when
            // - send GET request for headers only
            output.write(((((("" + ("GET /test_headers_only HTTP/1.1\r\n" + "Host: localhost:")) + (NettyPortForwardingSecureProxyIntegrationTest.echoServer.getPort())) + "\r\n") + "X-Test: test_headers_only\r\n") + "\r\n").getBytes(StandardCharsets.UTF_8));
            output.flush();
            // then
            assertContains(IOStreamUtils.readInputStreamToString(socket), "X-Test: test_headers_only");
            // and
            NettyPortForwardingSecureProxyIntegrationTest.mockServerClient.verify(request().withMethod("GET").withPath("/test_headers_only"), exactly(1));
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Test
    public void shouldForwardRequestsUsingSocketDirectlyHeadersAndBody() throws Exception {
        Socket socket = null;
        try {
            socket = sslSocketFactory().wrapSocket(new Socket("localhost", NettyPortForwardingSecureProxyIntegrationTest.mockServer.getLocalPort()));
            // given
            OutputStream output = socket.getOutputStream();
            // - send GET request for headers and body
            output.write((((((((((("" + ("GET /test_headers_and_body HTTP/1.1\r\n" + "Host: localhost:")) + (NettyPortForwardingSecureProxyIntegrationTest.echoServer.getPort())) + "\r\n") + "X-Test: test_headers_and_body\r\n") + "Content-Length:") + ("an_example_body".getBytes(StandardCharsets.UTF_8).length)) + "\r\n") + "\r\n") + "an_example_body") + "\r\n").getBytes(StandardCharsets.UTF_8));
            output.flush();
            // then
            String response = IOStreamUtils.readInputStreamToString(socket);
            assertContains(response, "X-Test: test_headers_and_body");
            assertContains(response, "an_example_body");
            // and
            NettyPortForwardingSecureProxyIntegrationTest.mockServerClient.verify(request().withMethod("GET").withPath("/test_headers_and_body").withBody("an_example_body"), exactly(1));
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Test
    public void shouldForwardRequestsUsingSocketDirectlyNotFound() throws Exception {
        Socket socket = null;
        try {
            socket = sslSocketFactory().wrapSocket(new Socket("localhost", NettyPortForwardingSecureProxyIntegrationTest.mockServer.getLocalPort()));
            // given
            OutputStream output = socket.getOutputStream();
            // - send GET request for headers and body
            output.write((((("" + ("GET /not_found HTTP/1.1\r\n" + "Host: localhost:")) + (NettyPortForwardingSecureProxyIntegrationTest.echoServer.getPort())) + "\r\n") + "\r\n").getBytes(StandardCharsets.UTF_8));
            output.flush();
            // then
            assertContains(IOStreamUtils.readInputStreamToString(socket), "HTTP/1.1 404 Not Found");
            // and
            NettyPortForwardingSecureProxyIntegrationTest.mockServerClient.verify(request().withMethod("GET").withPath("/not_found"), exactly(1));
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

