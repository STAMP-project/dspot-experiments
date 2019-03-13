/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import java.util.Locale;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;


public class DecimalTextFieldTest {
    WicketTester tester;

    Double theValue;

    static Locale originalLocale;

    @Test
    public void testEmpty() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "");
        ft.submit();
        Assert.assertNull(theValue);
    }

    @Test
    public void testNaN() throws Exception {
        theValue = Double.NaN;
        setUp();
        FormTester ft = tester.newFormTester("form");
        ft.submit();
        Assert.assertEquals(((Double) (Double.NaN)), theValue);
    }

    @Test
    public void testLocale() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "12,15");
        ft.submit();
        Assert.assertEquals(12.15, theValue, 0.0);
    }

    @Test
    public void testScientific() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "1E5");
        ft.submit();
        Assert.assertEquals(100000, theValue, 0.0);
    }
}

