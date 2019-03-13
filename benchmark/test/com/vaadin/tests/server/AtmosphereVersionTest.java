package com.vaadin.tests.server;


import Constants.REQUIRED_ATMOSPHERE_RUNTIME_VERSION;
import org.atmosphere.util.Version;
import org.junit.Assert;
import org.junit.Test;


public class AtmosphereVersionTest {
    /**
     * Test that the atmosphere version constant matches the version on our
     * classpath
     */
    @Test
    public void testAtmosphereVersion() {
        Assert.assertEquals(REQUIRED_ATMOSPHERE_RUNTIME_VERSION, Version.getRawVersion());
    }
}

