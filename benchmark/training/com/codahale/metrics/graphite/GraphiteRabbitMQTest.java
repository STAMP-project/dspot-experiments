package com.codahale.metrics.graphite;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.mockito.Mockito;


public class GraphiteRabbitMQTest {
    private final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);

    private final Connection connection = Mockito.mock(Connection.class);

    private final Channel channel = Mockito.mock(Channel.class);

    private final ConnectionFactory bogusConnectionFactory = Mockito.mock(ConnectionFactory.class);

    private final Connection bogusConnection = Mockito.mock(Connection.class);

    private final Channel bogusChannel = Mockito.mock(Channel.class);

    private final GraphiteRabbitMQ graphite = new GraphiteRabbitMQ(connectionFactory, "graphite");

    @Test
    public void shouldConnectToGraphiteServer() throws Exception {
        graphite.connect();
        Mockito.verify(connectionFactory, Mockito.atMost(1)).newConnection();
        Mockito.verify(connection, Mockito.atMost(1)).createChannel();
    }

    @Test
    public void measuresFailures() throws Exception {
        try (final GraphiteRabbitMQ graphite = new GraphiteRabbitMQ(bogusConnectionFactory, "graphite")) {
            graphite.connect();
            try {
                graphite.send("name", "value", 0);
                failBecauseExceptionWasNotThrown(IOException.class);
            } catch (IOException e) {
                assertThat(graphite.getFailures()).isEqualTo(1);
            }
        }
    }

    @Test
    public void shouldDisconnectsFromGraphiteServer() throws Exception {
        graphite.connect();
        graphite.close();
        Mockito.verify(connection).close();
    }

    @Test
    public void shouldNotConnectToGraphiteServerMoreThenOnce() throws Exception {
        graphite.connect();
        try {
            graphite.connect();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Already connected");
        }
    }

    @Test
    public void shouldSendMetricsToGraphiteServer() throws Exception {
        graphite.connect();
        graphite.send("name", "value", 100);
        String expectedMessage = "name value 100\n";
        Mockito.verify(channel, Mockito.times(1)).basicPublish("graphite", "name", null, expectedMessage.getBytes(StandardCharsets.UTF_8));
        assertThat(graphite.getFailures()).isZero();
    }

    @Test
    public void shouldSanitizeAndSendMetricsToGraphiteServer() throws Exception {
        graphite.connect();
        graphite.send("name to sanitize", "value to sanitize", 100);
        String expectedMessage = "name-to-sanitize value-to-sanitize 100\n";
        Mockito.verify(channel, Mockito.times(1)).basicPublish("graphite", "name-to-sanitize", null, expectedMessage.getBytes(StandardCharsets.UTF_8));
        assertThat(graphite.getFailures()).isZero();
    }

    @Test
    public void shouldFailWhenGraphiteHostUnavailable() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("some-unknown-host");
        try (GraphiteRabbitMQ unavailableGraphite = new GraphiteRabbitMQ(connectionFactory, "graphite")) {
            unavailableGraphite.connect();
            failBecauseExceptionWasNotThrown(UnknownHostException.class);
        } catch (Exception e) {
            assertThat(e.getMessage()).startsWith("some-unknown-host");
        }
    }
}

