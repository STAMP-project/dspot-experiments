/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;


public class DecimalListTextFieldTest {
    WicketTester tester;

    List<Double> theList = new ArrayList<>();

    static Locale originalLocale;

    @Test
    public void testEmpty() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "      ");
        ft.submit();
        Assert.assertEquals(0, theList.size());
    }

    @Test
    public void testLocale() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "1,3 12,15");
        ft.submit();
        Assert.assertEquals(2, theList.size());
        Assert.assertEquals(1.3, theList.get(0), 0.0);
        Assert.assertEquals(12.15, theList.get(1), 0.0);
    }

    @Test
    public void testScientific() throws Exception {
        FormTester ft = tester.newFormTester("form");
        ft.setValue("input", "1E-3 1E5");
        ft.submit();
        Assert.assertEquals(2, theList.size());
        Assert.assertEquals(0.001, theList.get(0), 0.0);
        Assert.assertEquals(100000, theList.get(1), 0.0);
    }
}

