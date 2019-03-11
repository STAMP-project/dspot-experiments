package com.codahale.metrics.graphite;


import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import org.junit.Test;
import org.mockito.Mockito;


public class GraphiteTest {
    private final String host = "example.com";

    private final int port = 1234;

    private final SocketFactory socketFactory = Mockito.mock(SocketFactory.class);

    private final InetSocketAddress address = new InetSocketAddress(host, port);

    private final Socket socket = Mockito.mock(Socket.class);

    private final ByteArrayOutputStream output = Mockito.spy(new ByteArrayOutputStream());

    private Graphite graphite;

    @Test
    public void connectsToGraphiteWithInetSocketAddress() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        Mockito.verify(socketFactory).createSocket(address.getAddress(), address.getPort());
    }

    @Test
    public void connectsToGraphiteWithHostAndPort() throws Exception {
        graphite = new Graphite(host, port, socketFactory);
        graphite.connect();
        Mockito.verify(socketFactory).createSocket(address.getAddress(), port);
    }

    @Test
    public void measuresFailures() {
        graphite = new Graphite(address, socketFactory);
        assertThat(graphite.getFailures()).isZero();
    }

    @Test
    public void disconnectsFromGraphite() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        graphite.close();
        Mockito.verify(socket, Mockito.times(2)).close();
    }

    @Test
    public void doesNotAllowDoubleConnections() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        try {
            graphite.connect();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Already connected");
        }
    }

    @Test
    public void writesValuesToGraphite() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        graphite.send("name", "value", 100);
        graphite.close();
        assertThat(output.toString()).isEqualTo("name value 100\n");
    }

    @Test
    public void sanitizesNames() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        graphite.send("name woo", "value", 100);
        graphite.close();
        assertThat(output.toString()).isEqualTo("name-woo value 100\n");
    }

    @Test
    public void sanitizesValues() throws Exception {
        graphite = new Graphite(address, socketFactory);
        graphite.connect();
        graphite.send("name", "value woo", 100);
        graphite.close();
        assertThat(output.toString()).isEqualTo("name value-woo 100\n");
    }

    @Test
    public void notifiesIfGraphiteIsUnavailable() {
        final String unavailableHost = "unknown-host-10el6m7yg56ge7dmcom";
        InetSocketAddress unavailableAddress = new InetSocketAddress(unavailableHost, 1234);
        try (Graphite unavailableGraphite = new Graphite(unavailableAddress, socketFactory)) {
            unavailableGraphite.connect();
            failBecauseExceptionWasNotThrown(UnknownHostException.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo(unavailableHost);
        }
    }
}

