package io.undertow.server.protocol.proxy;


import Undertow.ListenerType.HTTP;
import Undertow.ListenerType.HTTPS;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.util.HttpString;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


/**
 * Tests the proxy protocol
 *
 * @author Stuart Douglas
 * @author Jan Stourac
 * @author Ulrich Herberg
 */
public class ProxyProtocolTestCase {
    private static final byte[] SIG = new byte[]{ 13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10 };

    private static final byte[] NAME = "PROXY ".getBytes(StandardCharsets.US_ASCII);

    private static final byte PROXY = 33;

    private static final byte LOCAL = 32;

    private static final byte TCPv4 = 17;

    private static final byte TCPv6 = 33;

    // Undertow with HTTP listener and proxy-protocol enabled
    private Undertow undertow = Undertow.builder().addListener(new Undertow.ListenerBuilder().setType(HTTP).setHost(DefaultServer.getHostAddress()).setUseProxyProtocol(true).setPort(0)).setHandler(new HttpHandler() {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            exchange.setPersistent(false);
            exchange.getResponseHeaders().put(new HttpString("result"), (((exchange.getSourceAddress().toString()) + " ") + (exchange.getDestinationAddress().toString())));
        }
    }).build();

    // Undertow with HTTPS listener and proxy-protocol enabled
    private Undertow undertowSsl = Undertow.builder().addListener(new Undertow.ListenerBuilder().setType(HTTPS).setSslContext(DefaultServer.getServerSslContext()).setHost(DefaultServer.getHostAddress()).setUseProxyProtocol(true).setPort(0)).setHandler(new HttpHandler() {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            exchange.setPersistent(false);
            exchange.getResponseHeaders().put(new HttpString("result"), (((exchange.getSourceAddress().toString()) + " ") + (exchange.getDestinationAddress().toString())));
        }
    }).build();

    @Test
    public void testProxyProtocolTcp4() throws Exception {
        // simple valid request
        String request = "PROXY TCP4 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        String expectedResponse = "result: /1.2.3.4:444 /5.6.7.8:555";
        proxyProtocolRequestResponseCheck(request, expectedResponse);
        // check port range
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 0 65535\r\nGET / HTTP/1.0\r\n\r\n";
        expectedResponse = "result: /1.2.3.4:0 /5.6.7.8:65535";
        proxyProtocolRequestResponseCheck(request, expectedResponse);
    }

    @Test
    public void testProxyProtocolTcp4Negative() throws Exception {
        // wrong number of spaces in requests
        String request = "PROXY  TCP4 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4  1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4  5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 5.6.7.8  444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 444  555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing destination port
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 444\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing destination address
        request = "PROXY TCP4 1.2.3.4 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing \n on the first line of the request
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 444 555\rGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing \r on the first line of the request
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 444 555\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src address contains 0 characters at the beginning
        request = "PROXY TCP4 001.002.003.004 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // dst address contains '0' characters at the beginning
        request = "PROXY TCP4 1.2.3.4 005.006.007.008 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src/dst ports out of range
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 111444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 005.006.007.008 444 111555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 005.006.007.008 -444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 005.006.007.008 444 -555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src/dst ports contains '0' characters at the beginning
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 0444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP4 1.2.3.4 5.6.7.8 444 0555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src address contains invalid characters
        request = "PROXY TCP4 277.2.3.4 5.6.7.8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // dst address contains invalid characters
        request = "PROXY TCP4 1.2.3.4 5d.6.7.8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // unallowed character after PROXY string
        request = "PROXY, TCP4 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // IPv6 address when TCP4 is used
        request = "PROXY TCP4 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
    }

    @Test
    public void testProxyProtocolTcp6() throws Exception {
        // simple valid request
        String request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        String expectedResponse = "result: /fe80:0:0:0:56ee:75ff:fe44:85bc:444 /fe80:0:0:0:5ec5:d4ff:fede:66d8:555";
        proxyProtocolRequestResponseCheck(request, expectedResponse);
        // check port range
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 0 65535\r\nGET / HTTP/1.0\r\n\r\n";
        expectedResponse = "result: /fe80:0:0:0:56ee:75ff:fe44:85bc:0 /fe80:0:0:0:5ec5:d4ff:fede:66d8:65535";
        proxyProtocolRequestResponseCheck(request, expectedResponse);
    }

    @Test
    public void testProxyProtocolTcp6Negative() throws Exception {
        // wrong number of spaces in requests
        String request = "PROXY  TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6  fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc  fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8  444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444  555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing destination port
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing destination address
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing \n on the first line of the request
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\rGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // missing \r on the first line of the request
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src address contains invalid characters
        request = "PROXY TCP6 fz80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // dst address contains invalid characters
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5zc5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src/dst ports out of range
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 111444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 111555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 -444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 -555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // src/dst ports contains '0' characters at the beginning
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 0444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 0555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // unallowed character after PROXY string
        request = "PROXY, TCP6 fe80::56ee:75ff:fe44:85bc fe80::5ec5:d4ff:fede:66d8 444 555\r\nGET / " + "HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        // IPv6 address when TCP4 is used
        request = "PROXY TCP6 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
    }

    /**
     * General negative tests for proxy-protocol. We expect that server closes connection sending no data.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProxyProtocolNegative() throws Exception {
        String request = "NONSENSE\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "NONSENSE TCP4 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "NONSENSE\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY NONSENSE\r\n";
        proxyProtocolRequestResponseCheck(request, "");
        request = "PROXY NONSENSE 1.2.3.4 5.6.7.8 444 555\r\nGET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, "");
    }

    /**
     * Main cases are covered in plain-text HTTP connection tests. So here is just simple check that connection can
     * be established also via HTTPS.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProxyProtocolSSl() throws Exception {
        String request = "PROXY TCP4 1.2.3.4 5.6.7.8 444 555\r\n";
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        String expectedResponse = "result: /1.2.3.4:444 /5.6.7.8:555";
        proxyProtocolRequestResponseCheck(request, requestHttp, expectedResponse);
        // negative test
        request = "PROXY TCP4  1.2.3.4 5.6.7.8 444 555\r\n";
        requestHttp = "GET / HTTP/1.0\r\n\r\n";
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
    }

    @Test
    public void testProxyProtocolV2Tcp4() throws Exception {
        // simple valid request
        byte[] header = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 12, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, 555);
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        String expectedResponse = "result: /1.2.3.4:444 /5.6.7.8:555";
        proxyProtocolRequestResponseCheck(header, requestHttp, expectedResponse);
        // check port range
        header = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 12, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 0, 65535);
        expectedResponse = "result: /1.2.3.4:0 /5.6.7.8:65535";
        proxyProtocolRequestResponseCheck(header, requestHttp, expectedResponse);
        // check extra len
        header = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 100, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, 555);
        expectedResponse = "result: /1.2.3.4:444 /5.6.7.8:555";
        proxyProtocolRequestResponseCheck(header, requestHttp, expectedResponse);
    }

    /**
     * Main cases are covered in plain-text HTTP connection tests. So here is just simple check that connection can
     * be established also via HTTPS.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProxyProtocolV2SSl() throws Exception {
        // simple valid request
        byte[] header = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 12, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, 555);
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        String expectedResponse = "result: /1.2.3.4:444 /5.6.7.8:555";
        proxyProtocolRequestResponseCheck(header, requestHttp, expectedResponse);
    }

    @Test
    public void testProxyProtocolV2Tcp4Negative() throws Exception {
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        byte[] request;
        // missing destination port
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 10, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, null);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // missing destination address
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 8, InetAddress.getByName("1.2.3.4"), null, 444, 555);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // invalid family
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ((byte) (66)), 12, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, 555);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // len too low
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv4, 4, InetAddress.getByName("1.2.3.4"), null, null, null);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
    }

    @Test
    public void testProxyProtocolV2Tcp6() throws Exception {
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        byte[] request;
        // simple valid request
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv6, 36, InetAddress.getByName("fe80::56ee:75ff:fe44:85bc"), InetAddress.getByName("fe80::5ec5:d4ff:fede:66d8"), 444, 555);
        String expectedResponse = "result: /fe80:0:0:0:56ee:75ff:fe44:85bc:444 /fe80:0:0:0:5ec5:d4ff:fede:66d8:555";
        proxyProtocolRequestResponseCheck(request, requestHttp, expectedResponse);
        // check port range
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv6, 36, InetAddress.getByName("fe80::56ee:75ff:fe44:85bc"), InetAddress.getByName("fe80::5ec5:d4ff:fede:66d8"), 0, 65535);
        expectedResponse = "result: /fe80:0:0:0:56ee:75ff:fe44:85bc:0 /fe80:0:0:0:5ec5:d4ff:fede:66d8:65535";
        proxyProtocolRequestResponseCheck(request, requestHttp, expectedResponse);
    }

    @Test
    public void testProxyProtocolV2Tcp6Negative() throws Exception {
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        byte[] request;
        // missing destination port
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv6, 34, InetAddress.getByName("fe80::56ee:75ff:fe44:85bc"), InetAddress.getByName("fe80::5ec5:d4ff:fede:66d8"), 444, null);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // missing destination address
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv6, 20, InetAddress.getByName("1.2.3.4"), null, 444, 555);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // invalid family
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ((byte) (66)), 36, InetAddress.getByName("fe80::56ee:75ff:fe44:85bc"), InetAddress.getByName("fe80::5ec5:d4ff:fede:66d8"), 444, 555);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // len too low
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.PROXY, ProxyProtocolTestCase.TCPv6, 16, InetAddress.getByName("fe80::56ee:75ff:fe44:85bc"), null, null, null);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
    }

    @Test
    public void testProxyProtocolV2Local() throws Exception {
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        byte[] request;
        // simple valid request
        request = ProxyProtocolTestCase.createProxyHeaderV2(ProxyProtocolTestCase.LOCAL, ((byte) (0)), 0, null, null, null, null);
        String expectedResponse = "result: /127.0.0.1";
        proxyProtocolRequestResponseCheck(request, requestHttp, expectedResponse);
    }

    /**
     * General negative tests for proxy-protocol. We expect that server closes connection sending no data.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProxyProtocolV2Negative() throws Exception {
        String requestHttp = "GET / HTTP/1.0\r\n\r\n";
        byte[] request;
        // wrong version
        request = ProxyProtocolTestCase.createProxyHeaderV2(((byte) (0)), ProxyProtocolTestCase.TCPv4, 12, InetAddress.getByName("1.2.3.4"), InetAddress.getByName("5.6.7.8"), 444, 555);
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // wrong signature (starting with NAME)
        request = new byte[]{ ProxyProtocolTestCase.NAME[0], 0, 0, 0 };
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // wrong signature (starting with SIG)
        request = new byte[]{ ProxyProtocolTestCase.SIG[0], 0, 0, 0 };
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
        // wrong signature (starting with 0x0)
        request = new byte[]{ 0, 0, 0, 0 };
        proxyProtocolRequestResponseCheck(request, requestHttp, "");
    }

    @Test
    public void testProxyProtocolUnknownEmpty() throws Exception {
        doTestProxyProtocolUnknown("");
    }

    @Test
    public void testProxyProtocolUnknownSpace() throws Exception {
        doTestProxyProtocolUnknown(" ");
    }

    @Test
    public void testProxyProtocolUnknownJunk() throws Exception {
        doTestProxyProtocolUnknown(" mekmitasdigoat");
    }
}

