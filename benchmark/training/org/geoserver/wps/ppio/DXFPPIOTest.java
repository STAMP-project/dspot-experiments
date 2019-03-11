/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.feature.DefaultFeatureCollection;
import org.junit.Assert;
import org.junit.Test;


/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
(c) 2001 - 2013 OpenPlans
This code is licensed under the GPL 2.0 license, available at the root
application directory.
 */
public class DXFPPIOTest extends WPSTestSupport {
    DXFPPIO ppio;

    DefaultFeatureCollection features;

    @Test
    public void testEncode() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ppio.encode(features, os);
        Assert.assertNotNull(os.toByteArray());
        String dxf = new String(os.toByteArray(), "UTF-8");
        checkSequence(dxf, new String[]{ "BLOCKS", "LWPOLYLINE" }, 0);
    }

    @Test
    public void testDecodeString() {
        boolean error = false;
        try {
            ppio.decode("");
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    @Test
    public void testDecodeObject() {
        boolean error = false;
        try {
            ppio.decode(new Object());
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    @Test
    public void testDecodeInputStream() {
        boolean error = false;
        try {
            ppio.decode(new ByteArrayInputStream(new byte[]{  }));
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    @Test
    public void testFileExtension() {
        Assert.assertEquals("dxf", ppio.getFileExtension());
    }
}

