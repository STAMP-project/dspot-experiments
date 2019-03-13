package com.vaadin.tests.components.embedded;


import org.junit.Assert;
import org.junit.Test;


public class FlashIsVisibleTest extends com.vaadin.tests.components.flash.FlashIsVisibleTest {
    @Override
    @Test
    public void testFlashIsCorrectlyDisplayed() throws Exception {
        Assert.assertTrue("Test is using wrong url", getTestUrl().contains(".embedded."));
        super.testFlashIsCorrectlyDisplayed();
    }
}

