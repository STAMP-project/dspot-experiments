package org.mockserver.junit;


import MockServerRule.ClientAndServerFactory;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerClassRuleTestWithMocks {
    @Mock
    private Statement mockStatement;

    @Mock
    private ClientAndServer mockClientAndServer;

    private static MockServerClient mockServerClient;

    @Mock
    private ClientAndServerFactory clientAndServerFactory;

    private int httpPort;

    @InjectMocks
    private MockServerRule mockServerRuleDynamicPorts;

    @Test
    public void shouldStartAndStopMockServerWithDynamicPort() throws Throwable {
        // when
        mockServerRuleDynamicPorts.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        Assert.assertThat(MockServerClassRuleTestWithMocks.mockServerClient, IsInstanceOf.instanceOf(ClientAndServer.class));
        Assert.assertThat(mockServerRuleDynamicPorts.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(mockClientAndServer, Mockito.times(0)).stop();
    }
}

