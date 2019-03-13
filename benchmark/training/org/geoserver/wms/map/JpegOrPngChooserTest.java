/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import java.awt.image.BufferedImage;
import org.junit.Test;


public class JpegOrPngChooserTest {
    private BufferedImage indexed;

    private BufferedImage gray;

    private BufferedImage rgb;

    private BufferedImage rgba;

    private BufferedImage rgba_opaque;

    private BufferedImage rgba_partial;

    @Test
    public void testJpegPngImageWriter() {
        assertPng(indexed);
        assertJpeg(gray);
        assertJpeg(rgb);
        assertPng(rgba);
        assertJpeg(rgba_opaque);
        assertPng(rgba_partial);
    }
}

