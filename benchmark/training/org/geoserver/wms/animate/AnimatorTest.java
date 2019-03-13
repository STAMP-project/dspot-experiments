/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.animate;


import LayerGroupInfo.Mode.EO;
import LayerGroupInfo.Mode.NAMED;
import LayerGroupInfo.Mode.SINGLE;
import MockData.BASIC_POLYGONS;
import WMS.DISPOSAL_METHOD_DEFAULT;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.namespace.QName;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.WebMapService;
import org.geoserver.wms.map.RenderedImageMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Some functional tests for animator
 *
 * @author Alessio Fabiani, GeoSolutions S.A.S., alessio.fabiani@geo-solutions.it
 * @author Andrea Aime, GeoSolutions S.A.S., andrea.aime@geo-solutions.it
 */
public class AnimatorTest extends WMSTestSupport {
    /**
     * default 'format' value
     */
    public static final String GIF_ANIMATED_FORMAT = "image/gif;subtype=animated";

    /**
     * Testing FrameCatalog constructor from a generic WMS request.
     */
    @Test
    public void testFrameCatalog() throws Exception {
        final WebMapService wms = ((WebMapService) (applicationContext.getBean("wmsService2")));
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        GetMapRequest getMapRequest = createGetMapRequest(new QName(layerName));
        FrameCatalog catalog = null;
        try {
            catalog = new FrameCatalog(getMapRequest, wms, getWMS());
        } catch (RuntimeException e) {
            Assert.assertEquals("Missing \"animator\" mandatory params \"aparam\" and \"avalues\".", e.getLocalizedMessage());
        }
        getMapRequest.getRawKvp().put("aparam", "fake_param");
        getMapRequest.getRawKvp().put("avalues", "val0,val\\,1,val2\\,\\,,val3");
        catalog = new FrameCatalog(getMapRequest, wms, getWMS());
        Assert.assertNotNull(catalog);
        Assert.assertEquals("fake_param", catalog.getParameter());
        Assert.assertEquals(4, catalog.getValues().length);
        Assert.assertEquals("val0", catalog.getValues()[0]);
        Assert.assertEquals("val\\,1", catalog.getValues()[1]);
        Assert.assertEquals("val2\\,\\,", catalog.getValues()[2]);
        Assert.assertEquals("val3", catalog.getValues()[3]);
    }

    /**
     * Testing FrameVisitor animation frames setup and production.
     */
    @Test
    public void testFrameVisitor() throws Exception {
        final WebMapService wms = ((WebMapService) (applicationContext.getBean("wmsService2")));
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        GetMapRequest getMapRequest = createGetMapRequest(new QName(layerName));
        FrameCatalog catalog = null;
        getMapRequest.getRawKvp().put("aparam", "fake_param");
        getMapRequest.getRawKvp().put("avalues", "val0,val\\,1,val2\\,\\,,val3");
        getMapRequest.getRawKvp().put("format", AnimatorTest.GIF_ANIMATED_FORMAT);
        getMapRequest.getRawKvp().put("LAYERS", layerName);
        catalog = new FrameCatalog(getMapRequest, wms, getWMS());
        Assert.assertNotNull(catalog);
        FrameCatalogVisitor visitor = new FrameCatalogVisitor();
        catalog.getFrames(visitor);
        Assert.assertEquals(4, visitor.framesNumber);
        List<RenderedImageMap> frames = visitor.produce(getWMS());
        Assert.assertNotNull(frames);
        Assert.assertEquals(4, frames.size());
    }

    /**
     * Produce animated gif through the WMS request.
     */
    @Test
    public void testAnimator() throws Exception {
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String requestURL = ("wms/animate?layers=" + layerName) + "&aparam=fake_param&avalues=val0,val\\,1,val2\\,\\,,val3";
        checkAnimatedGif(requestURL, false, DISPOSAL_METHOD_DEFAULT);
        checkAnimatedGif((requestURL + "&format_options=gif_loop_continuously:true"), true, DISPOSAL_METHOD_DEFAULT);
        checkAnimatedGif((requestURL + "&format_options=gif_loop_continuosly:true"), true, DISPOSAL_METHOD_DEFAULT);
        // check all valid disposal methods
        for (String disposal : WMS.DISPOSAL_METHODS) {
            checkAnimatedGif(((requestURL + "&format_options=gif_disposal:") + disposal), false, disposal);
        }
    }

    /**
     * Animate layers
     */
    @Test
    public void testAnimatorLayers() throws Exception {
        final String layerName = ((BASIC_POLYGONS.getPrefix()) + ":") + (BASIC_POLYGONS.getLocalPart());
        String requestURL = "cite/wms/animate?&aparam=layers&avalues=MapNeatline,Buildings,Lakes";
        // check we got a gif
        MockHttpServletResponse resp = getAsServletResponse(requestURL);
        Assert.assertEquals("image/gif", resp.getContentType());
        // check it has three frames
        ByteArrayInputStream bis = getBinaryInputStream(resp);
        ImageInputStream iis = ImageIO.createImageInputStream(bis);
        ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
        reader.setInput(iis);
        Assert.assertEquals(3, reader.getNumImages(true));
    }

    /**
     * Animate layer groups
     */
    @Test
    public void testAnimatorLayerGroups() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo singleGroup = createLakesPlacesLayerGroup(catalog, "singleGroup", SINGLE, null);
        try {
            LayerGroupInfo namedGroup = createLakesPlacesLayerGroup(catalog, "namedGroup", NAMED, null);
            try {
                LayerGroupInfo eoGroup = createLakesPlacesLayerGroup(catalog, "eoGroup", EO, catalog.getLayerByName(getLayerId(MockData.LAKES)));
                try {
                    String requestURL = (((("wms/animate?BBOX=0.0000,-0.0020,0.0035,0.0010&width=512&aparam=layers&avalues=" + (singleGroup.getName())) + ",") + (namedGroup.getName())) + ",") + (eoGroup.getName());
                    // check we got a gif
                    MockHttpServletResponse resp = getAsServletResponse(requestURL);
                    Assert.assertEquals("image/gif", resp.getContentType());
                    // check it has three frames
                    ByteArrayInputStream bis = getBinaryInputStream(resp);
                    ImageInputStream iis = ImageIO.createImageInputStream(bis);
                    ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
                    reader.setInput(iis);
                    // BufferedImage gif = getAsImage(requestURL, "image/gif");
                    // ImageIO.write(gif, "gif", new File("anim.gif"));
                    Assert.assertEquals(3, reader.getNumImages(true));
                    // single group:
                    BufferedImage image = reader.read(0);
                    assertPixel(image, 300, 270, Color.WHITE);
                    // places
                    assertPixel(image, 380, 30, WMSTestSupport.COLOR_PLACES_GRAY);
                    // lakes
                    assertPixel(image, 180, 350, WMSTestSupport.COLOR_LAKES_BLUE);
                    // named group:
                    image = reader.read(1);
                    assertPixel(image, 300, 270, Color.WHITE);
                    // places
                    assertPixel(image, 380, 30, WMSTestSupport.COLOR_PLACES_GRAY);
                    // lakes
                    assertPixel(image, 180, 350, WMSTestSupport.COLOR_LAKES_BLUE);
                    // EO group:
                    image = reader.read(2);
                    assertPixel(image, 300, 270, Color.WHITE);
                    // no places
                    assertPixel(image, 380, 30, Color.WHITE);
                    // lakes
                    assertPixel(image, 180, 350, WMSTestSupport.COLOR_LAKES_BLUE);
                } finally {
                    catalog.remove(eoGroup);
                }
            } finally {
                catalog.remove(namedGroup);
            }
        } finally {
            catalog.remove(singleGroup);
        }
    }
}

