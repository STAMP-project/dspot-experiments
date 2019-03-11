package com.baeldung.rest.wiremock.scenario;


import Scenario.STARTED;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class WireMockScenarioExampleIntegrationTest {
    private static final String THIRD_STATE = "third";

    private static final String SECOND_STATE = "second";

    private static final String TIP_01 = "finally block is not called when System.exit() is called in the try block";

    private static final String TIP_02 = "keep your code clean";

    private static final String TIP_03 = "use composition rather than inheritance";

    private static final String TEXT_PLAIN = "text/plain";

    static int port = 9999;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockScenarioExampleIntegrationTest.port);

    @Test
    public void changeStateOnEachCallTest() throws IOException {
        createWireMockStub(STARTED, WireMockScenarioExampleIntegrationTest.SECOND_STATE, WireMockScenarioExampleIntegrationTest.TIP_01);
        createWireMockStub(WireMockScenarioExampleIntegrationTest.SECOND_STATE, WireMockScenarioExampleIntegrationTest.THIRD_STATE, WireMockScenarioExampleIntegrationTest.TIP_02);
        createWireMockStub(WireMockScenarioExampleIntegrationTest.THIRD_STATE, STARTED, WireMockScenarioExampleIntegrationTest.TIP_03);
        Assert.assertEquals(WireMockScenarioExampleIntegrationTest.TIP_01, nextTip());
        Assert.assertEquals(WireMockScenarioExampleIntegrationTest.TIP_02, nextTip());
        Assert.assertEquals(WireMockScenarioExampleIntegrationTest.TIP_03, nextTip());
        Assert.assertEquals(WireMockScenarioExampleIntegrationTest.TIP_01, nextTip());
    }
}

