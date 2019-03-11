package org.testcontainers.containers;


import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockserver.client.MockServerClient;


public class MockServerContainerTest {
    @Test
    public void shouldCallActualMockserverVersion() throws Exception {
        String actualVersion = MockServerClient.class.getPackage().getImplementationVersion();
        try (MockServerContainer mockServer = new MockServerContainer(actualVersion)) {
            mockServer.start();
            String expectedBody = "Hello World!";
            assertThat("MockServer returns correct result", MockServerContainerTest.responseFromMockserver(mockServer, expectedBody, "/hello"), CoreMatchers.containsString(expectedBody));
        }
    }

    @Test
    public void shouldCallDefaultMockserverVersion() throws Exception {
        try (MockServerContainer mockServerDefault = new MockServerContainer()) {
            mockServerDefault.start();
            String expectedBody = "Hello Default World!";
            assertThat("MockServer returns correct result for default constructor", MockServerContainerTest.responseFromMockserver(mockServerDefault, expectedBody, "/hellodefault"), CoreMatchers.containsString(expectedBody));
        }
    }
}

