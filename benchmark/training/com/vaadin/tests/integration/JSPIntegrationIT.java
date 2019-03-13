package com.vaadin.tests.integration;


import com.vaadin.testbench.TestBenchTestCase;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JSPIntegrationIT extends TestBenchTestCase {
    private static final String URL_PREFIX = "http://localhost:8080/";

    private static final String primaryUIUrl = (JSPIntegrationIT.URL_PREFIX) + "primaryui";

    private static final String jspUrl = (JSPIntegrationIT.URL_PREFIX) + "staticfiles/vaadinsessions.jsp";

    private static final String secondaryUIUrl = (JSPIntegrationIT.URL_PREFIX) + "secondaryui";

    @Test
    public void listVaadinSessions() {
        assertUICount(0);
        // Open a new UI
        getDriver().navigate().to(JSPIntegrationIT.primaryUIUrl);
        assertUICount(1);
        JSPIntegrationIT.UIData firstUI = getUIs().get(0);
        // Open a new UI
        getDriver().navigate().to(JSPIntegrationIT.primaryUIUrl);
        JSPIntegrationIT.UIData secondUI = getUIs().get(0);
        // Should now have UI for the same service with different uiId
        assertUICount(1);
        Assert.assertNotEquals(firstUI.uiId, secondUI.uiId);
        Assert.assertEquals(firstUI.serviceName, secondUI.serviceName);
        getDriver().navigate().to(JSPIntegrationIT.secondaryUIUrl);
        // Should now have another services
        List<JSPIntegrationIT.UIData> twoUIs = getUIs();
        Assert.assertEquals(2, twoUIs.size());
        Assert.assertNotEquals(twoUIs.get(0).serviceName, twoUIs.get(1).serviceName);
    }

    private static class UIData {
        private String serviceName;

        private int uiId;
    }
}

