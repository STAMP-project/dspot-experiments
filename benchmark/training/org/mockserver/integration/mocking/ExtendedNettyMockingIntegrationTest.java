package org.mockserver.integration.mocking;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.integration.ClientAndServer;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExtendedNettyMockingIntegrationTest extends AbstractExtendedNettyMockingIntegrationTest {
    private static int mockServerPort;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfFailToBindToSocket() {
        // given
        System.out.println((((((NEW_LINE) + (NEW_LINE)) + "+++ IGNORE THE FOLLOWING java.net.BindException EXCEPTION +++") + (NEW_LINE)) + (NEW_LINE)));
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(Matchers.containsString("Exception while binding MockServer to port "));
        // when
        ClientAndServer.startClientAndServer(ExtendedNettyMockingIntegrationTest.mockServerPort);
    }
}

