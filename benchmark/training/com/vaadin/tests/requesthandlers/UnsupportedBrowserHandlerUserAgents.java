package com.vaadin.tests.requesthandlers;


import org.junit.Assert;
import org.junit.Test;


public class UnsupportedBrowserHandlerUserAgents {
    /* This test doesn't use testbench, but it's still in the uitest source
    folder since it should be run with the testing server deployed.
     */
    @Test
    public void ie7NotSupported() {
        String response = requestWithUserAgent("Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.2; WOW64; .NET CLR 2.0.50727)");
        Assert.assertTrue("IE7 should not be supported", response.contains("your browser is not supported"));
    }

    @Test
    public void ie9NotSupported() {
        String response = requestWithUserAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 7.1; Trident/5.0)");
        Assert.assertTrue("IE9 should not be supported", response.contains("your browser is not supported"));
    }

    @Test
    public void unknownSupported() {
        String response = requestWithUserAgent("Very strange user agent, like wat");
        Assert.assertFalse("Unknown user agent should be supported", response.contains("your browser is not supported"));
    }
}

