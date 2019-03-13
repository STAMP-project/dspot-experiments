/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.logstash.logback.appender;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Duration;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.net.SocketFactory;
import net.logstash.logback.Logback11Support;
import net.logstash.logback.appender.destination.RandomDestinationConnectionStrategy;
import net.logstash.logback.appender.destination.RoundRobinDestinationConnectionStrategy;
import net.logstash.logback.appender.listener.TcpAppenderListener;
import net.logstash.logback.encoder.SeparatorParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LogstashTcpSocketAppenderTest {
    private static final int VERIFICATION_TIMEOUT = 1000 * 10;

    @InjectMocks
    private LogstashTcpSocketAppender appender = new LogstashTcpSocketAppenderTest.TestableLogstashTcpSocketAppender();

    @Mock
    private Context context;

    @Mock
    private StatusManager statusManager;

    @Mock
    private ILoggingEvent event1;

    @Mock
    private ILoggingEvent event2;

    @Mock
    private SocketFactory socketFactory;

    @Mock
    private Socket socket;

    @Mock
    private OutputStream outputStream;

    @Mock
    private Encoder<ILoggingEvent> encoder;

    @Mock
    private Future<?> readableCallableFuture;

    @Mock
    private TcpAppenderListener<ILoggingEvent> listener;

    @Mock
    private Random random;

    @Mock
    private Logback11Support logback11Support;

    private class TestableLogstashTcpSocketAppender extends LogstashTcpSocketAppender {
        @Override
        protected Future<?> scheduleReaderCallable(Callable<Void> readerCallable) {
            return readableCallableFuture;
        }

        @Override
        protected Logback11Support getLogback11Support() {
            return logback11Support;
        }
    }

    @Test
    public void testEncoderCalled_logback11() throws Exception {
        Mockito.when(logback11Support.isLogback11OrBefore()).thenReturn(true);
        appender.addDestination("localhost:10000");
        appender.setIncludeCallerData(true);
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        Mockito.verify(event1).getCallerData();
        Mockito.verify(logback11Support, Mockito.timeout(2000L)).init(ArgumentMatchers.eq(encoder), ArgumentMatchers.any(OutputStream.class));
        Mockito.verify(logback11Support, Mockito.timeout(2000L)).doEncode(encoder, event1);
    }

    @Test
    public void testEncoderCalled_logback12OrLater() throws Exception {
        appender.addDestination("localhost:10000");
        appender.setIncludeCallerData(true);
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        Mockito.verify(event1).getCallerData();
        Mockito.verify(encoder, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT)).encode(event1);
    }

    @Test
    public void testReconnectOnOpen() throws Exception {
        appender.addDestination("localhost:10000");
        appender.setReconnectionDelay(new Duration(100));
        Mockito.reset(socketFactory);
        SocketTimeoutException exception = new SocketTimeoutException();
        Mockito.when(socketFactory.createSocket()).thenThrow(exception).thenReturn(socket);
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        Mockito.verify(encoder, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT)).encode(event1);
        Mockito.verify(listener).appenderStarted(appender);
        Mockito.verify(listener).eventAppended(ArgumentMatchers.eq(appender), ArgumentMatchers.eq(event1), ArgumentMatchers.anyLong());
        Mockito.verify(listener).connectionFailed(ArgumentMatchers.eq(appender), ArgumentMatchers.any(InetSocketAddress.class), ArgumentMatchers.eq(exception));
        Mockito.verify(listener).connectionOpened(appender, socket);
        Mockito.verify(listener).eventSent(ArgumentMatchers.eq(appender), ArgumentMatchers.eq(socket), ArgumentMatchers.eq(event1), ArgumentMatchers.anyLong());
        appender.stop();
        Mockito.verify(listener).connectionClosed(appender, socket);
    }

    @Test
    public void testReconnectOnWrite() throws Exception {
        appender.addDestination("localhost:10000");
        appender.setReconnectionDelay(new Duration(100));
        appender.start();
        Mockito.verify(encoder).start();
        Mockito.doThrow(new RuntimeException()).doReturn("event1".getBytes("UTF-8")).when(encoder).encode(event1);
        appender.append(event1);
        Mockito.verify(encoder, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(2)).encode(event1);
    }

    @Test
    public void testReconnectOnReadFailure() throws Exception {
        appender.addDestination("localhost:10000");
        appender.setReconnectionDelay(new Duration(100));
        /* Then return false so that the event can be written */
        /* First return true, so that the reconnect logic is executed */
        Mockito.when(readableCallableFuture.isDone()).thenReturn(true).thenReturn(false);
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        Mockito.verify(encoder, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT)).encode(event1);
    }

    /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup
     *   Appender should connect to PRIMARY and not any secondaries
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectOnPrimary() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        appender.start();
        Mockito.verify(encoder).start();
        // Only one socket should have been created
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(1)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        // The only socket should be connected to primary
        Mockito.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
    }

    /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is not available at startup
     *   Appender should first try primary then immediately connect to secondary
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReconnectToSecondaryOnOpen() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        // Make it failed to connect to primary
        Mockito.doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // Start the appender and verify it is actually started.
        // It should try to connect to primary, fail then retry on secondary.
        appender.start();
        Mockito.verify(encoder).start();
        // TWO connection attempts must have been made (without delay)
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(2)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket);
        // 1) First attempt on PRIMARY: failure
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) Second attempt on SECONDARY
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
    }

    @Test
    public void testRandomDestinationAndReconnectToSecondaryOnOpen() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        RandomDestinationConnectionStrategy strategy = Mockito.spy(new RandomDestinationConnectionStrategy());
        Mockito.doReturn(random).when(strategy).getRandom();
        appender.setConnectionStrategy(strategy);
        // Make it failed to connect to second destination
        Mockito.doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
        // The first index is second destination.
        Mockito.when(random.nextInt(appender.getDestinations().size())).thenReturn(1).thenReturn(0);
        // Start the appender and verify it is actually started.
        // It should try to connect to second destination by random destination, fail then retry on first destination.
        appender.start();
        Mockito.verify(encoder).start();
        // TWO connection attempts must have been made (without delay)
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(2)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket);
        // 1) First attempt on second destination: failure
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
        // 2) Second attempt on first destination
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
    }

    /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup then fails after the first event.
     *   Appender should then connect on secondary for the next event.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReconnectToSecondaryOnWrite() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        // Primary accepts first connection attempt then refuses
        Mockito.doNothing().doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // First attempt of sending the event throws an exception while subsequent
        // attempts will succeed. This should force the appender to close the connection
        // and attempt to reconnect starting from the first host of the list.
        Mockito.doThrow(new RuntimeException()).doReturn("event1".getBytes("UTF-8")).when(encoder).encode(event1);
        // Start the appender and verify it is actually started
        // At this point, it should be connected to primary.
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        // THREE connection attempts must have been made in total
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(3)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket);
        // 1) connected to primary at startup
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) retry on primary after failed attempt to send event
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 3) connect to secondary
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
    }

    /**
     * Make sure the appender tries to reconnect to primary after a while.
     */
    @Test
    public void testReconnectToPrimaryWhileOnSecondary() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        appender.setSecondaryConnectionTTL(Duration.buildByMilliseconds(100));
        // Primary refuses first connection to force the appender to go on the secondary.
        Mockito.doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // Start the appender and verify it is actually started
        // At this point, it should be connected to primary.
        appender.start();
        Mockito.verify(encoder).start();
        // The appender is supposed to be on the secondary.
        // Wait until after the appender is supposed to reattempt to connect to primary, then
        // send an event (requires some activity to trigger the reconnection process).
        Thread.sleep(((appender.getSecondaryConnectionTTL().getMilliseconds()) + 50));
        appender.append(event1);
        // THREE connection attempts must have been made in total
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(3)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket, encoder);
        // 1) fail to connect on primary at startup
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) connect to secondary
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
        // 3) send the event
        inOrder.verify(encoder).encode(event1);
        // 4) connect to primary
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
    }

    /**
     * When a connection failure occurs, the appender retries immediately with the next
     * available host. When all hosts are exhausted, the appender should wait {reconnectionDelay}
     * before retrying with the first server.
     */
    @Test
    public void testReconnectWaitWhenExhausted() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        appender.setReconnectionDelay(Duration.buildByMilliseconds(100));
        // Both hosts refuse the first connection attempt
        Mockito.doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        Mockito.doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
        // Start the appender and verify it is actually started
        // At this point, it should be connected to primary.
        appender.start();
        Mockito.verify(encoder).start();
        // THREE connection attempts must have been made in total
        Mockito.verify(socket, Mockito.timeout(((appender.getReconnectionDelay().getMilliseconds()) + 50)).times(3)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket, encoder);
        // 1) fail to connect on primary at startup
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) connect to secondary
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
        // 3) connect to primary
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
    }

    /**
     * Schedule keep alive and make sure we got the expected amount of messages
     * in the given time.
     */
    @Test
    public void testKeepAlive() throws Exception {
        appender.addDestination("localhost");
        // Schedule keepalive message every 100ms
        appender.setKeepAliveMessage("UNIX");
        appender.setKeepAliveCharset(Charset.forName("UTF-8"));
        appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
        String expectedKeepAlives = (SeparatorParser.parseSeparator("UNIX")) + (SeparatorParser.parseSeparator("UNIX"));
        byte[] expectedKeepAlivesBytes = expectedKeepAlives.getBytes("UTF-8");
        // Use a ByteArrayOutputStream to capture the actual keep alive message bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(bos);
        // Start the appender and verify it is actually started
        // At this point, it should be connected to primary.
        appender.start();
        Mockito.verify(encoder).start();
        // Wait for a bit more than 2 keep alive messages then make sure we got the expected content
        Thread.sleep(250);
        Assert.assertArrayEquals(expectedKeepAlivesBytes, bos.toByteArray());
    }

    /**
     * Make sure keep alive messages trigger reconnect to another host upon failure.
     */
    @Test
    public void testReconnectToSecondaryOnKeepAlive() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        // Schedule keep alive message every 100ms
        appender.setKeepAliveMessage("UNIX");
        appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
        // Primary accepts first connection then refuse subsequent attemps
        Mockito.doNothing().doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // Throw an exception the first time the the appender attempts to write in the output stream.
        // This should cause the appender to initiate the reconnect sequence.
        Mockito.doThrow(SocketException.class).doNothing().when(outputStream).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Start the appender and verify it is actually started
        // At this point, it should be connected to primary.
        appender.start();
        Mockito.verify(encoder).start();
        // Wait for a bit more than a single keep alive message.
        // THREE connection attempts must have been made in total:
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(3)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket);
        // 1) connected to primary at startup
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) retry on primary after failed attempt to send event
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 3) connect to secondary
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
    }

    /**
     * At least one valid destination must be configured.
     * The appender refuses to start in case of error.
     */
    @Test
    public void testDestination_None() throws Exception {
        appender.start();
        Assert.assertFalse(appender.isStarted());
    }

    /**
     * Specify destinations using both <remoteHost>/<port> and <destination>.
     * Only one scheme can be used - make sure the appender refuses to start.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testDestination_MixedType() throws Exception {
        appender.setRemoteHost("localhost");
        appender.setPort(10000);
        appender.addDestination("localhost:10001");
        appender.start();
        Assert.assertFalse(appender.isStarted());
    }

    @Test
    public void testRoundRobin() throws Exception {
        appender.addDestination("localhost:10000");
        appender.addDestination("localhost:10001");
        RoundRobinDestinationConnectionStrategy strategy = new RoundRobinDestinationConnectionStrategy();
        strategy.setConnectionTTL(Duration.buildByMilliseconds(100));
        appender.setConnectionStrategy(strategy);
        appender.start();
        Mockito.verify(encoder).start();
        appender.append(event1);
        // Wait for round robin to occur, then send an event.
        Thread.sleep(((strategy.getConnectionTTL().getMilliseconds()) + 50));
        appender.append(event1);
        Mockito.verify(socket, Mockito.timeout(LogstashTcpSocketAppenderTest.VERIFICATION_TIMEOUT).times(2)).connect(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.anyInt());
        InOrder inOrder = Mockito.inOrder(socket);
        // 1) connected to primary at startup
        inOrder.verify(socket).connect(host("localhost", 10000), ArgumentMatchers.anyInt());
        // 2) connected to next destination by round-robin
        inOrder.verify(socket).connect(host("localhost", 10001), ArgumentMatchers.anyInt());
    }
}

