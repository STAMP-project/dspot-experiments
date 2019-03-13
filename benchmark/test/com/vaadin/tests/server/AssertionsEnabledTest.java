package com.vaadin.tests.server;


import org.junit.Assert;
import org.junit.Test;


public class AssertionsEnabledTest {
    @Test
    public void testAssertionsEnabled() {
        boolean assertFailed = false;
        try {
            assert false;
        } catch (AssertionError e) {
            assertFailed = true;
        } finally {
            Assert.assertTrue("Unit tests should be run with assertions enabled", assertFailed);
        }
    }
}

