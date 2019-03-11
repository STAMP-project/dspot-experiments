package com.codahale.metrics.jetty9;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Test;


public class InstrumentedConnectionFactoryTest {
    private final MetricRegistry registry = new MetricRegistry();

    private final Server server = new Server();

    private final ServerConnector connector = new ServerConnector(server, new InstrumentedConnectionFactory(new HttpConnectionFactory(), registry.timer("http.connections"), registry.counter("http.active-connections")));

    private final HttpClient client = new HttpClient();

    @Test
    public void instrumentsConnectionTimes() throws Exception {
        final ContentResponse response = client.GET((("http://localhost:" + (connector.getLocalPort())) + "/hello"));
        assertThat(response.getStatus()).isEqualTo(200);
        client.stop();// close the connection

        Thread.sleep(100);// make sure the connection is closed

        final Timer timer = registry.timer(MetricRegistry.name("http.connections"));
        assertThat(timer.getCount()).isEqualTo(1);
    }

    @Test
    public void instrumentsActiveConnections() throws Exception {
        final Counter counter = registry.counter("http.active-connections");
        final ContentResponse response = client.GET((("http://localhost:" + (connector.getLocalPort())) + "/hello"));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(counter.getCount()).isEqualTo(1);
        client.stop();// close the connection

        Thread.sleep(100);// make sure the connection is closed

        assertThat(counter.getCount()).isEqualTo(0);
    }
}

