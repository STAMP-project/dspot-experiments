/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geoserver.test.RemoteOWSTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Performs integration tests using a mock {@link ResourceAccessManager}
 *
 * @author Andrea Aime - GeoSolutions
 */
public class ResourceAccessManagerWMSTest extends WMSTestSupport {
    static final Logger LOGGER = Logging.getLogger(ResourceAccessManagerWMSTest.class);

    static final String BASE = "wms?" + // 
    ((((("SERVICE=WMS&VERSION=1.1.1" + "&HEIGHT=330&WIDTH=780") + // 
    "&LAYERS=rstates&STYLES=") + // 
    "&FORMAT=image%2Fpng") + // 
    "&SRS=EPSG%3A4326") + // 
    "&BBOX=-139.84813671875,18.549615234375,-51.85286328125,55.778384765625");

    static final String GET_MAP = (ResourceAccessManagerWMSTest.BASE) + "&REQUEST=GetMap";

    static final String BASE_GET_FEATURE_INFO = (((ResourceAccessManagerWMSTest.BASE) + // 
    "&REQUEST=GetFeatureInfo") + // 
    "&QUERY_LAYERS=rstates") + // 
    "&INFO_FORMAT=text/plain";

    static final String GET_FEATURE_INFO_CALIFORNIA = ((ResourceAccessManagerWMSTest.BASE_GET_FEATURE_INFO) + // 
    "&X=191") + // 
    "&Y=178";

    static final String GET_FEATURE_INFO_TEXAS = ((ResourceAccessManagerWMSTest.BASE_GET_FEATURE_INFO) + // 
    "&X=368") + // 
    "&Y=227";

    @Test
    public void testGetMapNoRestrictions() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testNoRestrictions");
            return;
        }
        setRequestAuth("cite", "cite");
        MockHttpServletResponse response = getAsServletResponse(ResourceAccessManagerWMSTest.GET_MAP);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNotNull(image);
        assertNotBlank("testNoRestrictions", image);
        // check the colors of some pixels to ensure there has been no filtering
        // a Texas one
        int[] pixel = new int[4];
        image.getData().getPixel(368, 227, pixel);
        Assert.assertEquals(130, pixel[0]);
        Assert.assertEquals(130, pixel[1]);
        Assert.assertEquals(255, pixel[2]);
        // a California one
        image.getData().getPixel(191, 178, pixel);
        Assert.assertEquals(130, pixel[0]);
        Assert.assertEquals(130, pixel[1]);
        Assert.assertEquals(255, pixel[2]);
    }

    @Test
    public void testGetMapDisallowed() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testGetMapDisallowed");
            return;
        }
        setRequestAuth("cite_nostates", "cite");
        MockHttpServletResponse response = getAsServletResponse(ResourceAccessManagerWMSTest.GET_MAP);
        Assert.assertEquals("application/vnd.ogc.se_xml", response.getContentType());
        Document dom = dom(getBinaryInputStream(response));
        assertXpathEvaluatesTo("LayerNotDefined", "//ServiceException/@code", dom);
    }

    @Test
    public void testGetMapFiltered() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testGetMapFiltered");
            return;
        }
        setRequestAuth("cite_texas", "cite");
        MockHttpServletResponse response = getAsServletResponse(ResourceAccessManagerWMSTest.GET_MAP);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertNotNull(image);
        assertNotBlank("testGetMapFiltered", image);
        // check the colors of some pixels to ensure there has been no filtering
        // a Texas one
        int[] pixel = new int[4];
        image.getData().getPixel(368, 227, pixel);
        Assert.assertEquals(130, pixel[0]);
        Assert.assertEquals(130, pixel[1]);
        Assert.assertEquals(255, pixel[2]);
        // a California one, this one should be white
        image.getData().getPixel(191, 178, pixel);
        Assert.assertEquals(255, pixel[0]);
        Assert.assertEquals(255, pixel[1]);
        Assert.assertEquals(255, pixel[2]);
    }

    @Test
    public void testGetFeatureInfoNoRestrictions() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testNoRestrictions");
            return;
        }
        setRequestAuth("cite", "cite");
        String texas = getAsString(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_TEXAS);
        Assert.assertTrue(texas.contains("STATE_NAME = Texas"));
        String california = getAsString(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_CALIFORNIA);
        Assert.assertTrue(california.contains("STATE_NAME = California"));
    }

    @Test
    public void testGetFeatureInfoDisallowedLayer() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testNoRestrictions");
            return;
        }
        setRequestAuth("cite_nostates", "cite");
        MockHttpServletResponse response = getAsServletResponse(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_TEXAS);
        Assert.assertEquals("application/vnd.ogc.se_xml", response.getContentType());
        Document dom = dom(getBinaryInputStream(response));
        assertXpathEvaluatesTo("LayerNotDefined", "//ServiceException/@code", dom);
    }

    @Test
    public void testGetFeatureInfoDisallowedInfo() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testNoRestrictions");
            return;
        }
        setRequestAuth("cite_noinfo", "cite");
        MockHttpServletResponse response = getAsServletResponse(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_TEXAS);
        Assert.assertEquals("application/vnd.ogc.se_xml", response.getContentType());
        Document dom = dom(getBinaryInputStream(response));
        assertXpathEvaluatesTo("OperationNotSupported", "//ServiceException/@code", dom);
    }

    @Test
    public void testGetFeatureInfoFiltered() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWMSStatesAvailable(ResourceAccessManagerWMSTest.LOGGER))) {
            ResourceAccessManagerWMSTest.LOGGER.log(Level.WARNING, "Skipping testNoRestrictions");
            return;
        }
        setRequestAuth("cite_texas", "cite");
        String texas = getAsString(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_TEXAS);
        Assert.assertTrue(texas.contains("STATE_NAME = Texas"));
        String california = getAsString(ResourceAccessManagerWMSTest.GET_FEATURE_INFO_CALIFORNIA);
        Assert.assertTrue(california.contains("no features were found"));
    }

    @Test
    public void testDoubleMosaic() throws Exception {
        setRequestAuth("cite_mosaic1", "cite");
        String path = "wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=150&HEIGHT=150&transparent=false";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // this one would fail due to the wrapper finalizer dispose of the coverage reader
        response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testRasterFilterGreen() throws Exception {
        // no cql filter, the security one should do
        setRequestAuth("cite_mosaic1", "cite");
        MockHttpServletResponse response = getAsServletResponse(("wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=150&HEIGHT=150&transparent=false"));
        Assert.assertEquals("image/png", response.getContentType());
        RenderedImage image = ImageIO.read(getBinaryInputStream(response));
        int[] pixel = new int[3];
        image.getData().getPixel(0, 0, pixel);
        Assert.assertEquals(0, pixel[0]);
        Assert.assertEquals(255, pixel[1]);
        Assert.assertEquals(0, pixel[2]);
    }

    @Test
    public void testRasterCrop() throws Exception {
        // this time we should get a cropped image
        setRequestAuth("cite_mosaic2", "cite");
        MockHttpServletResponse response = getAsServletResponse(("wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,0,1,1&WIDTH=150&HEIGHT=150&transparent=false"));
        Assert.assertEquals("image/png", response.getContentType());
        RenderedImage image = ImageIO.read(getBinaryInputStream(response));
        // bottom right pixel, should be green (inside the crop area)
        int[] pixel = new int[3];
        image.getData().getPixel(0, 149, pixel);
        Assert.assertEquals(0, pixel[0]);
        Assert.assertEquals(255, pixel[1]);
        Assert.assertEquals(0, pixel[2]);
        // bottom left, out of the crop area should be black (bgcolor)
        image.getData().getPixel(149, 149, pixel);
        Assert.assertEquals(0, pixel[0]);
        Assert.assertEquals(0, pixel[1]);
        Assert.assertEquals(0, pixel[2]);
    }
}

