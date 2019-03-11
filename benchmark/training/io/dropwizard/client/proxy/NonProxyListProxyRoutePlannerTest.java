package io.dropwizard.client.proxy;


import java.util.Arrays;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class NonProxyListProxyRoutePlannerTest {
    private HttpHost proxy = new HttpHost("192.168.52.15");

    private NonProxyListProxyRoutePlanner routePlanner = new NonProxyListProxyRoutePlanner(proxy, Arrays.asList("localhost", "*.example.com", "192.168.52.*"));

    private HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

    private HttpContext httpContext = Mockito.mock(HttpContext.class);

    @Test
    public void testProxyListIsNotSet() {
        assertThat(new NonProxyListProxyRoutePlanner(proxy, null).getNonProxyHostPatterns()).isEmpty();
    }

    @Test
    public void testHostNotInBlackList() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("dropwizard.io"), httpRequest, httpContext)).isEqualTo(proxy);
    }

    @Test
    public void testPlainHostIsMatched() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("localhost"), httpRequest, httpContext)).isNull();
    }

    @Test
    public void testHostWithStartWildcardIsMatched() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("test.example.com"), httpRequest, httpContext)).isNull();
    }

    @Test
    public void testHostWithEndWildcardIsMatched() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("192.168.52.94"), httpRequest, httpContext)).isNull();
    }
}

