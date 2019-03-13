package com.danikula.videocache;


import com.google.common.collect.Lists;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static java.net.Proxy.Type.HTTP;


/**
 * Tests {@link IgnoreHostProxySelector}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ProxySelectorTest extends BaseTest {
    // https://github.com/danikula/AndroidVideoCache/issues/28
    @Test
    public void testIgnoring() throws Exception {
        InetSocketAddress proxyAddress = new InetSocketAddress("proxy.com", 80);
        Proxy systemProxy = new Proxy(HTTP, proxyAddress);
        ProxySelector mockedProxySelector = Mockito.mock(ProxySelector.class);
        Mockito.when(mockedProxySelector.select(Mockito.<URI>any())).thenReturn(Lists.newArrayList(systemProxy));
        ProxySelector.setDefault(mockedProxySelector);
        IgnoreHostProxySelector.install("localhost", 42);
        ProxySelector proxySelector = ProxySelector.getDefault();
        List<Proxy> githubProxies = proxySelector.select(new URI("http://github.com"));
        assertThat(githubProxies).hasSize(1);
        assertThat(githubProxies.get(0).address()).isEqualTo(proxyAddress);
        List<Proxy> localhostProxies = proxySelector.select(new URI("http://localhost:42"));
        assertThat(localhostProxies).hasSize(1);
        assertThat(localhostProxies.get(0)).isEqualTo(Proxy.NO_PROXY);
        List<Proxy> localhostPort69Proxies = proxySelector.select(new URI("http://localhost:69"));
        assertThat(localhostPort69Proxies).hasSize(1);
        assertThat(localhostPort69Proxies.get(0).address()).isEqualTo(proxyAddress);
    }
}

