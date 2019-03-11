/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import java.util.Locale;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.util.convert.IConverter;
import org.junit.Assert;
import org.junit.Test;


public class ConverterTest extends GeoServerWicketTestSupport {
    @Test
    public void testConvertEmtpyString() {
        // Wicket forms rely on converters returning null from an empty string conversion
        IConverterLocator locator = GeoServerWicketTestSupport.tester.getApplication().getConverterLocator();
        IConverter<Integer> convert = locator.getConverter(Integer.class);
        Assert.assertNotNull(convert);
        Assert.assertNull(convert.convertToObject("", Locale.getDefault()));
    }
}

