/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.response;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class GdalWrapperTest {
    private GdalWrapper gdal;

    @Test
    public void testAvaialable() {
        // kind of a smoke test, since GdalTestUtils uses the same command!
        gdal.isAvailable();
    }

    @Test
    public void testFormats() {
        Set<String> formats = gdal.getSupportedFormats();
        // well, we can't know which formats GDAL was complied with, but at least there will be one,
        // right?
        Assert.assertTrue(((formats.size()) > 0));
        // these work on my machine, with gdal 1.11.2, libgeotiff 1.4.0, libpng 1.6
        // and libjpeg-turbo 1.3.1
        Assert.assertTrue(formats.contains("GTiff"));
        Assert.assertTrue(formats.contains("PNG"));
        Assert.assertTrue(formats.contains("JPEG"));
        Assert.assertTrue(formats.contains("PDF"));
        Assert.assertTrue(formats.contains("AAIGrid"));
    }
}

