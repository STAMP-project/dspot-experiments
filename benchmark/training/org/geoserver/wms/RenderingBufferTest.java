/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import LayerInfo.BUFFER;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.data.test.MockData;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Tests that the admin specified per layer buffer parameter is taken into account
 *
 * @author Andrea Aime - OpenGeo
 */
public class RenderingBufferTest extends WMSTestSupport {
    static final QName LINE_WIDTH_LAYER = new QName(MockData.CITE_URI, "LineWidth", MockData.CITE_PREFIX);

    static final String LINE_WIDTH_STYLE = "linewidth";

    @Test
    public void testGetMapNoBuffer() throws Exception {
        String request = ((((("cite/wms?request=getmap&service=wms" + "&layers=") + (getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER))) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        WMSTestSupport.showImage("testGetMap", image);
        Assert.assertEquals(0, countNonBlankPixels("testGetMap", image, WMSTestSupport.BG_COLOR));
    }

    @Test
    public void testGetFeatureInfoNoBuffer() throws Exception {
        final String layerName = getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER);
        String request = ((((((("cite/wms?request=getfeatureinfo&service=wms" + "&layers=") + layerName) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5&x=49&y=49&query_layers=") + layerName) + "&info_format=application/vnd.ogc.gml";
        Document dom = getAsDOM(request);
        assertXpathEvaluatesTo("0", "count(//gml:featureMember)", dom);
    }

    @Test
    public void testGetMapExplicitBuffer() throws Exception {
        String request = ((((("cite/wms?request=getmap&service=wms" + "&layers=") + (getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER))) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5&buffer=30";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        WMSTestSupport.showImage("testGetMap", image);
        int nonBlankPixels = countNonBlankPixels("testGetMap", image, WMSTestSupport.BG_COLOR);
        Assert.assertTrue((nonBlankPixels > 0));
    }

    @Test
    public void testGetFeatureInfoExplicitBuffer() throws Exception {
        final String layerName = getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER);
        String request = ((((((("cite/wms?version=1.1.1&request=getfeatureinfo&service=wms" + "&layers=") + layerName) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5&x=49&y=49&query_layers=") + layerName) + "&info_format=application/vnd.ogc.gml&buffer=30";
        Document dom = getAsDOM(request);
        // print(dom);
        assertXpathEvaluatesTo("1", "count(//gml:featureMember)", dom);
    }

    @Test
    public void testGetMapConfiguredBuffer() throws Exception {
        Catalog catalog = getCatalog();
        LayerInfo layer = catalog.getLayerByName(getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER));
        layer.getMetadata().put(BUFFER, 30);
        catalog.save(layer);
        String request = ((((("cite/wms?request=getmap&service=wms" + "&layers=") + (getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER))) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5";
        MockHttpServletResponse response = getAsServletResponse(request);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        WMSTestSupport.showImage("testGetMap", image);
        Assert.assertTrue(((countNonBlankPixels("testGetMap", image, WMSTestSupport.BG_COLOR)) > 0));
    }

    @Test
    public void testGetFeatureInfoConfiguredBuffer() throws Exception {
        Catalog catalog = getCatalog();
        LayerInfo layer = catalog.getLayerByName(getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER));
        layer.getMetadata().put(BUFFER, 30);
        catalog.save(layer);
        final String layerName = getLayerId(RenderingBufferTest.LINE_WIDTH_LAYER);
        String request = ((((((("cite/wms?version=1.1.1&request=getfeatureinfo&service=wms" + "&layers=") + layerName) + "&styles=") + (RenderingBufferTest.LINE_WIDTH_STYLE)) + "&width=50&height=50&format=image/png") + "&srs=epsg:4326&bbox=-6,0,-1,5&x=49&y=49&query_layers=") + layerName) + "&info_format=application/vnd.ogc.gml";
        Document dom = getAsDOM(request);
        assertXpathEvaluatesTo("1", "count(//gml:featureMember)", dom);
    }
}

