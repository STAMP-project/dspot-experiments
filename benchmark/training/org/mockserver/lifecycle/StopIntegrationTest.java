package org.mockserver.lifecycle;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.MockServerClient;
import org.mockserver.mockserver.MockServer;
import org.mockserver.socket.PortFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class StopIntegrationTest {
    private static final int MOCK_SERVER_PORT = PortFactory.findFreePort();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void returnsExceptionWhenAlreadyStopped() {
        // given
        exception.expect(IllegalStateException.class);
        exception.expectMessage(Matchers.containsString("Request sent after client has been stopped - the event loop has been shutdown so it is not possible to send a request"));
        // when - server started
        new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
        // and - start client
        MockServerClient mockServerClient = new MockServerClient("localhost", StopIntegrationTest.MOCK_SERVER_PORT);
        mockServerClient.stop();
        // then
        Assert.assertFalse(mockServerClient.isRunning());
    }

    @Test
    public void canStartAndStopMultipleTimesViaClient() {
        // start server
        new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
        // start client
        MockServerClient mockServerClient = new MockServerClient("localhost", StopIntegrationTest.MOCK_SERVER_PORT);
        for (int i = 0; i < 2; i++) {
            // when
            mockServerClient.stop();
            mockServerClient = new MockServerClient("localhost", StopIntegrationTest.MOCK_SERVER_PORT);
            // then
            Assert.assertFalse(mockServerClient.isRunning());
            new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
            Assert.assertTrue(mockServerClient.isRunning());
        }
        Assert.assertTrue(mockServerClient.isRunning());
        mockServerClient.stop();
        mockServerClient = new MockServerClient("localhost", StopIntegrationTest.MOCK_SERVER_PORT);
        Assert.assertFalse(mockServerClient.isRunning());
    }

    @Test
    public void canStartAndStopMultipleTimes() {
        // start server
        MockServer mockServer = new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
        for (int i = 0; i < 2; i++) {
            // when
            mockServer.stop();
            // then
            Assert.assertFalse(mockServer.isRunning());
            mockServer = new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
            Assert.assertTrue(mockServer.isRunning());
        }
        Assert.assertTrue(mockServer.isRunning());
        mockServer.stop();
        Assert.assertFalse(mockServer.isRunning());
    }

    @Test
    public void closesSocketBeforeStopMethodReturns() {
        // start server
        MockServer mockServer = new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
        // when
        mockServer.stop();
        // then
        try {
            new Socket("localhost", StopIntegrationTest.MOCK_SERVER_PORT);
            Assert.fail("socket should be closed");
        } catch (IOException ioe) {
            MatcherAssert.assertThat(ioe.getMessage(), CoreMatchers.containsString("Connection refused"));
        }
    }

    @Test
    public void freesPortBeforeStopMethodReturns() throws IOException {
        // start server
        MockServer mockServer = new MockServer(StopIntegrationTest.MOCK_SERVER_PORT);
        // when
        mockServer.stop();
        // then
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(StopIntegrationTest.MOCK_SERVER_PORT);
            MatcherAssert.assertThat(serverSocket.isBound(), Is.is(true));
        } catch (IOException ioe) {
            Assert.fail("port should be freed");
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}

