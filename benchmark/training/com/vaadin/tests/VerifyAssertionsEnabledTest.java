package com.vaadin.tests;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class VerifyAssertionsEnabledTest extends SingleBrowserTest {
    @Test
    public void verifyServerAssertions() throws Exception {
        openTestURL();
        Assert.assertEquals("1. Assertions are enabled", getLogRow(0));
    }
}

