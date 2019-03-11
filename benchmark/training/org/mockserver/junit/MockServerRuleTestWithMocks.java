package org.mockserver.junit;


import MockServerRule.ClientAndServerFactory;
import org.hamcrest.Matchers;
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
public class MockServerRuleTestWithMocks {
    @Mock
    private Statement mockStatement;

    @Mock
    private ClientAndServer mockClientAndServer;

    private MockServerClient mockServerClient;

    @Mock
    private ClientAndServerFactory clientAndServerFactory;

    private int httpPort;

    @InjectMocks
    private MockServerRule mockServerRuleDynamicPorts;

    @InjectMocks
    private MockServerRule mockServerRuleSinglePort;

    @InjectMocks
    private MockServerRule mockServerRuleMultiplePorts;

    @InjectMocks
    private MockServerRule mockServerRulePerSuite;

    @InjectMocks
    private MockServerRule mockServerRulePerSuiteDuplicate;

    @Test
    public void shouldStartAndStopMockServerWithDynamicPort() throws Throwable {
        // when
        mockServerRuleDynamicPorts.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        Assert.assertThat(mockServerClient, IsInstanceOf.instanceOf(ClientAndServer.class));
        Assert.assertThat(mockServerRuleDynamicPorts.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(clientAndServerFactory, Mockito.times(1)).newClientAndServer();
        Mockito.verify(mockClientAndServer, Mockito.times(0)).stop();
    }

    @Test
    public void shouldStartAndStopMockServerWithSinglePort() throws Throwable {
        // when
        mockServerRuleSinglePort.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        Assert.assertThat(((ClientAndServer) (mockServerClient)), Matchers.sameInstance(mockClientAndServer));
        Assert.assertThat(mockServerRuleSinglePort.getPort(), Is.is(httpPort));
        Assert.assertThat(mockServerRuleSinglePort.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(mockClientAndServer).stop();
    }

    @Test
    public void shouldStartAndStopMockServerWithMultiplePorts() throws Throwable {
        // when
        mockServerRuleMultiplePorts.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        Assert.assertThat(((ClientAndServer) (mockServerClient)), Matchers.sameInstance(mockClientAndServer));
        Assert.assertThat(mockServerRuleMultiplePorts.getPort(), Is.is(httpPort));
        Assert.assertThat(mockServerRuleMultiplePorts.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(mockClientAndServer).stop();
    }

    @Test
    public void shouldStartAndStopMockServerOncePerSuite() throws Throwable {
        // when
        mockServerRulePerSuite.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        MockServerClient firstMockServerClient = mockServerClient;
        Assert.assertThat(mockServerClient, IsInstanceOf.instanceOf(ClientAndServer.class));
        Assert.assertThat(mockServerRulePerSuite.getPort(), Is.is(httpPort));
        Assert.assertThat(mockServerRulePerSuite.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(clientAndServerFactory, Mockito.times(1)).newClientAndServer();
        Mockito.verify(mockClientAndServer, Mockito.times(0)).stop();
        Mockito.reset(mockStatement, clientAndServerFactory);
        // when
        mockServerRulePerSuiteDuplicate.apply(mockStatement, Description.EMPTY).evaluate();
        // then
        Assert.assertThat(mockServerClient, Matchers.sameInstance(firstMockServerClient));
        Assert.assertThat(mockServerRulePerSuiteDuplicate.getPort(), Is.is(httpPort));
        Assert.assertThat(mockServerRulePerSuiteDuplicate.getPorts(), Is.is(new Integer[]{ (httpPort) + 1, (httpPort) + 2 }));
        Mockito.verify(mockStatement).evaluate();
        Mockito.verify(clientAndServerFactory, Mockito.times(0)).newClientAndServer();
        Mockito.verify(mockClientAndServer, Mockito.times(0)).stop();
    }
}

