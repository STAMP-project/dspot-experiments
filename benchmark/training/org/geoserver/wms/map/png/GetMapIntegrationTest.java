/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map.png;


import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GetMapIntegrationTest extends WMSTestSupport {
    String bbox = "-2,0,2,6";

    String layers = getLayerId(MockData.BASIC_POLYGONS);

    @Test
    public void testPngOpaque() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/png", response.getContentType());
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        ColorModel cm = bi.getColorModel();
        Assert.assertFalse(cm.hasAlpha());
        Assert.assertEquals(3, cm.getNumColorComponents());
    }

    @Test
    public void testPngTransparent() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&transparent=true"));
        Assert.assertEquals("image/png", response.getContentType());
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        ColorModel cm = bi.getColorModel();
        Assert.assertTrue(cm.hasAlpha());
        Assert.assertEquals(3, cm.getNumColorComponents());
    }

    @Test
    public void testPng8Opaque() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png8") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/png; mode=8bit", response.getContentType());
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        IndexColorModel cm = ((IndexColorModel) (bi.getColorModel()));
        Assert.assertEquals(Transparency.OPAQUE, cm.getTransparency());
        Assert.assertEquals((-1), cm.getTransparentPixel());
    }

    @Test
    public void testPng8ForceBitmask() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png8") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&transparent=true&format_options=quantizer:octree"));
        Assert.assertEquals("image/png; mode=8bit", response.getContentType());
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        IndexColorModel cm = ((IndexColorModel) (bi.getColorModel()));
        Assert.assertEquals(Transparency.BITMASK, cm.getTransparency());
        Assert.assertTrue(((cm.getTransparentPixel()) >= 0));
    }

    @Test
    public void testPng8Translucent() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png8") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&transparent=true"));
        Assert.assertEquals("image/png; mode=8bit", response.getContentType());
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        IndexColorModel cm = ((IndexColorModel) (bi.getColorModel()));
        Assert.assertEquals(Transparency.TRANSLUCENT, cm.getTransparency());
    }
}

