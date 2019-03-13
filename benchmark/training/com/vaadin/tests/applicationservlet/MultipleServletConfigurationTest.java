package com.vaadin.tests.applicationservlet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class MultipleServletConfigurationTest extends MultiBrowserTest {
    @Test
    public void testMultipleServletConfiguration() throws Exception {
        getDriver().get(((getBaseURL()) + "/embed1"));
        assertLabelText("Verify that Button HTML rendering works");
        getDriver().get(((getBaseURL()) + "/embed2"));
        assertLabelText("Margins inside labels should not be allowed to collapse out of the label as it causes problems with layotus measuring the label.");
        getDriver().get(((getBaseURL()) + "/embed1"));
        assertLabelText("Verify that Button HTML rendering works");
    }
}

