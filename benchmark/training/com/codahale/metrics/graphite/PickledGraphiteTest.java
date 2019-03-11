package com.codahale.metrics.graphite;


import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.SocketFactory;
import javax.script.CompiledScript;
import org.junit.Test;
import org.mockito.Mockito;


public class PickledGraphiteTest {
    private final SocketFactory socketFactory = Mockito.mock(SocketFactory.class);

    private final InetSocketAddress address = new InetSocketAddress("example.com", 1234);

    private final PickledGraphite graphite = new PickledGraphite(address, socketFactory, StandardCharsets.UTF_8, 2);

    private final Socket socket = Mockito.mock(Socket.class);

    private final ByteArrayOutputStream output = Mockito.spy(new ByteArrayOutputStream());

    private CompiledScript unpickleScript;

    @Test
    public void disconnectsFromGraphite() throws Exception {
        graphite.connect();
        graphite.close();
        Mockito.verify(socket).close();
    }

    @Test
    public void writesValuesToGraphite() throws Exception {
        graphite.connect();
        graphite.send("name", "value", 100);
        graphite.close();
        assertThat(unpickleOutput()).isEqualTo("name value 100\n");
    }

    @Test
    public void writesFullBatch() throws Exception {
        graphite.connect();
        graphite.send("name", "value", 100);
        graphite.send("name", "value2", 100);
        graphite.close();
        assertThat(unpickleOutput()).isEqualTo("name value 100\nname value2 100\n");
    }

    @Test
    public void writesPastFullBatch() throws Exception {
        graphite.connect();
        graphite.send("name", "value", 100);
        graphite.send("name", "value2", 100);
        graphite.send("name", "value3", 100);
        graphite.close();
        assertThat(unpickleOutput()).isEqualTo("name value 100\nname value2 100\nname value3 100\n");
    }

    @Test
    public void sanitizesNames() throws Exception {
        graphite.connect();
        graphite.send("name woo", "value", 100);
        graphite.close();
        assertThat(unpickleOutput()).isEqualTo("name-woo value 100\n");
    }

    @Test
    public void sanitizesValues() throws Exception {
        graphite.connect();
        graphite.send("name", "value woo", 100);
        graphite.close();
        assertThat(unpickleOutput()).isEqualTo("name value-woo 100\n");
    }

    @Test
    public void doesNotAllowDoubleConnections() throws Exception {
        graphite.connect();
        try {
            graphite.connect();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Already connected");
        }
    }
}

