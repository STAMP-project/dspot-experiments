package io.undertow.server.handlers;


import ForwardedHandler.Token;
import ForwardedHandler.Token.BY;
import ForwardedHandler.Token.FOR;
import ForwardedHandler.Token.PROTO;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.ProxyIgnore;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@ProxyIgnore
public class ForwardedHandlerTestCase {
    @Test
    public void testHeaderParsing() {
        Map<ForwardedHandler.Token, String> results = new HashMap<>();
        ForwardedHandler.parseHeader("For=\"[2001:db8:cafe::17]:4711\"", results);
        Assert.assertEquals("[2001:db8:cafe::17]:4711", results.get(FOR));
        results.clear();
        ForwardedHandler.parseHeader("for=192.0.2.60;proto=http;by=203.0.113.43", results);
        Assert.assertEquals("192.0.2.60", results.get(FOR));
        Assert.assertEquals("http", results.get(PROTO));
        Assert.assertEquals("203.0.113.43", results.get(BY));
        results.clear();
        ForwardedHandler.parseHeader("for=192.0.2.43, for=198.51.100.17", results);
        Assert.assertEquals("192.0.2.43", results.get(FOR));
        results.clear();
        ForwardedHandler.parseHeader("for=192.0.2.43, for=198.51.100.17;by=\"foo\"", results);
        Assert.assertEquals("192.0.2.43", results.get(FOR));
        Assert.assertEquals("foo", results.get(BY));
        results.clear();
    }

    @Test
    public void testAddressParsing() throws UnknownHostException {
        Assert.assertEquals(null, ForwardedHandler.parseAddress("unknown"));
        Assert.assertEquals(null, ForwardedHandler.parseAddress("_foo"));
        Assert.assertEquals(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ ((byte) (192)), ((byte) (168)), 1, 1 }), 0), ForwardedHandler.parseAddress("192.168.1.1"));
        Assert.assertEquals(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ ((byte) (192)), ((byte) (168)), 1, 1 }), 8080), ForwardedHandler.parseAddress("192.168.1.1:8080"));
        Assert.assertEquals(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), 0), ForwardedHandler.parseAddress("[::1]"));
        Assert.assertEquals(new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }), 8080), ForwardedHandler.parseAddress("[::1]:8080"));
    }

    @Test
    public void testForwardedHandler() throws IOException {
        String[] res = ForwardedHandlerTestCase.run();
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        res = ForwardedHandlerTestCase.run("host=google.com");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals("google.com", res[1]);
        Assert.assertEquals("google.com:80", res[2]);
        res = ForwardedHandlerTestCase.run("host=google.com, proto=https");
        Assert.assertEquals("https", res[0]);
        Assert.assertEquals("google.com", res[1]);
        Assert.assertEquals("google.com:80", res[2]);
        res = ForwardedHandlerTestCase.run("for=8.8.8.8:3545");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        Assert.assertEquals("/8.8.8.8:3545", res[3]);
        res = ForwardedHandlerTestCase.run("for=8.8.8.8:3545, for=9.9.9.9:2343");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        Assert.assertEquals("/8.8.8.8:3545", res[3]);
        res = ForwardedHandlerTestCase.run("for=[::1]:3545, for=9.9.9.9:2343");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        Assert.assertEquals("/0:0:0:0:0:0:0:1:3545", res[3]);
        res = ForwardedHandlerTestCase.run("for=[::1]:_foo, for=9.9.9.9:2343");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        Assert.assertEquals("/0:0:0:0:0:0:0:1:0", res[3]);
        res = ForwardedHandlerTestCase.run("for=[::1], for=9.9.9.9:2343");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals(((("/" + (InetAddress.getByName(DefaultServer.getHostAddress()).getHostAddress())) + ":") + (DefaultServer.getHostPort())), res[2]);
        Assert.assertEquals("/0:0:0:0:0:0:0:1:0", res[3]);
        res = ForwardedHandlerTestCase.run("by=[::1]; for=9.9.9.9:2343");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals((((DefaultServer.getHostAddress()) + ":") + (DefaultServer.getHostPort())), res[1]);
        Assert.assertEquals("/0:0:0:0:0:0:0:1:0", res[2]);
        Assert.assertEquals("/9.9.9.9:2343", res[3]);
        res = ForwardedHandlerTestCase.run("by=[::1]; for=9.9.9.9:2343; host=foo.com");
        Assert.assertEquals("http", res[0]);
        Assert.assertEquals("foo.com", res[1]);
        Assert.assertEquals("foo.com:80", res[2]);
        Assert.assertEquals("/9.9.9.9:2343", res[3]);
    }
}

