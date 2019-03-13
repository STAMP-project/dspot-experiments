package com.vaadin.tests.components.ui;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class InitialFragmentEventTest extends MultiBrowserTest {
    @Test
    public void testChangeFragmentOnServerToMatchClient() {
        openTestURL("#foo");
        Assert.assertEquals("Log should not contain any text initially", " ", getLogRow(0));
        $(ButtonElement.class).caption("Set fragment to 'foo'").first().click();
        Assert.assertEquals("Log should not contain any text after clicking button", " ", getLogRow(0));
    }
}

