/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.exception;


import java.util.Locale;
import junit.framework.Assert;
import org.junit.Test;


public class ExceptionLocalizeTest {
    @Test
    public void test() throws Exception {
        GeoServerException e = id("hi");
        Assert.assertEquals("hello", GeoServerExceptions.localize(e));
    }

    @Test
    public void testFallback() throws Exception {
        GeoServerException e = id("hi");
        // assertEquals("hello", GeoServerExceptions.localize(e));
        // assertEquals("hello", GeoServerExceptions.localize(e, Locale.ENGLISH));
        Assert.assertEquals("hello", GeoServerExceptions.localize(e, Locale.FRENCH));
    }

    @Test
    public void testLocale() throws Exception {
        GeoServerException e = id("hi");
        Assert.assertEquals("bonjour", GeoServerExceptions.localize(e, Locale.FRENCH));
    }

    @Test
    public void testUnknownLocale() throws Exception {
        GeoServerException e = id("hi");
        Assert.assertEquals("hello", GeoServerExceptions.localize(e, Locale.GERMAN));
    }

    @Test
    public void testWithArgs() throws Exception {
        GeoServerException e = new TestException().id("hey").args("neo");
        Assert.assertEquals("hello neo", GeoServerExceptions.localize(e));
        Assert.assertEquals("bonjour neo", GeoServerExceptions.localize(e, Locale.FRENCH));
    }

    @Test
    public void testWithNewDefault() throws Exception {
        Locale old = Locale.getDefault();
        Locale.setDefault(Locale.FRENCH);
        try {
            Assert.assertEquals("bonjour", GeoServerExceptions.localize(new TestException().id("hi")));
        } finally {
            Locale.setDefault(old);
        }
    }
}

