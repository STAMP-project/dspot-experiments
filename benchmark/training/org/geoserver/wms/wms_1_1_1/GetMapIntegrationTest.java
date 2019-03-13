/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import HttpHeaders.CONTENT_DISPOSITION;
import ImageMosaicFormat.INPUT_TRANSPARENT_COLOR;
import ImageMosaicFormat.OUTPUT_TRANSPARENT_COLOR;
import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.EO;
import LayerGroupInfo.Mode.NAMED;
import LayerGroupInfo.Mode.SINGLE;
import MockData.BASIC_POLYGONS;
import WMS.ADVANCED_PROJECTION_KEY;
import WMS.MAP_WRAPPING_KEY;
import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletResponse;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.TestData;
import org.geoserver.test.RemoteOWSTestSupport;
import org.geoserver.wms.GetMap;
import org.geoserver.wms.GetMapOutputFormat;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.map.OpenLayersMapOutputFormat;
import org.geotools.image.ImageWorker;
import org.geotools.image.test.ImageAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GetMapIntegrationTest extends WMSTestSupport {
    private static final QName ONE_BIT = new QName(MockData.SF_URI, "onebit", MockData.SF_PREFIX);

    private static final QName MOSAIC_HOLES = new QName(MockData.SF_URI, "mosaic_holes", MockData.SF_PREFIX);

    private static final QName MOSAIC = new QName(MockData.SF_URI, "mosaic", MockData.SF_PREFIX);

    private static final QName MASKED = new QName(MockData.SF_URI, "masked", MockData.SF_PREFIX);

    public static QName GIANT_POLYGON = new QName(MockData.CITE_URI, "giantPolygon", MockData.CITE_PREFIX);

    public static QName LARGE_POLYGON = new QName(MockData.CITE_URI, "slightlyLessGiantPolygon", MockData.CITE_PREFIX);

    String bbox = "-130,24,-66,50";

    String styles = "states";

    String layers = "sf:states";

    public static final String STATES_SLD = "<StyledLayerDescriptor version=\"1.0.0\">" + ((((("<UserLayer><Name>sf:states</Name><UserStyle><Name>UserSelection</Name>" + "<FeatureTypeStyle><Rule><Filter xmlns:gml=\"http://www.opengis.net/gml\">") + "<PropertyIsEqualTo><PropertyName>STATE_ABBR</PropertyName><Literal>IL</Literal></PropertyIsEqualTo>") + "</Filter><PolygonSymbolizer><Fill><CssParameter name=\"fill\">#FF0000</CssParameter></Fill>") + "</PolygonSymbolizer></Rule><Rule><LineSymbolizer><Stroke/></LineSymbolizer></Rule>") + "</FeatureTypeStyle></UserStyle></UserLayer></StyledLayerDescriptor>");

    public static final String STATES_SLD11 = "<StyledLayerDescriptor version=\"1.1.0\"> " + ((((((((((((((((((((((((((" <UserLayer> " + "  <Name>sf:states</Name> ") + "  <UserStyle> ") + "   <Name>UserSelection</Name> ") + "   <se:FeatureTypeStyle xmlns:se=\"http://www.opengis.net/se\"> ") + "    <se:Rule> ") + "     <ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"> ") + "      <ogc:PropertyIsEqualTo> ") + "       <ogc:PropertyName>STATE_ABBR</ogc:PropertyName> ") + "       <ogc:Literal>IL</ogc:Literal> ") + "      </ogc:PropertyIsEqualTo> ") + "     </ogc:Filter> ") + "     <se:PolygonSymbolizer> ") + "      <se:Fill> ") + "       <se:SvgParameter name=\"fill\">#FF0000</se:SvgParameter> ") + "      </se:Fill> ") + "     </se:PolygonSymbolizer> ") + "    </se:Rule> ") + "    <se:Rule> ") + "     <se:LineSymbolizer> ") + "      <se:Stroke/> ") + "     </se:LineSymbolizer> ") + "    </se:Rule> ") + "   </se:FeatureTypeStyle> ") + "  </UserStyle> ") + " </UserLayer> ") + "</StyledLayerDescriptor>");

    // 
    public static final String STATES_GETMAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n " + ((((((((((((((((((((((((((((((("<ogc:GetMap service=\"WMS\"  version=\"1.1.1\" \n " + "        xmlns:gml=\"http://www.opengis.net/gml\"\n ") + "        xmlns:ogc=\"http://www.opengis.net/ows\"\n ") + "        xmlns:sld=\"http://www.opengis.net/sld\"\n ") + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n ") + "        xsi:schemaLocation=\"http://www.opengis.net/ows GetMap.xsd http://www.opengis.net/gml geometry.xsd http://www.opengis.net/sld StyledLayerDescriptor.xsd \">\n ") + "        <sld:StyledLayerDescriptor>\n ") + "                <sld:NamedLayer>\n ") + "                        <sld:Name>sf:states</sld:Name>\n ") + "                        <sld:NamedStyle>\n ") + "                                <sld:Name>Default</sld:Name>\n ") + "                        </sld:NamedStyle>\n ") + "                </sld:NamedLayer>\n ") + "        </sld:StyledLayerDescriptor>\n ") + "        <ogc:BoundingBox srsName=\"4326\">\n ") + "                <gml:coord>\n ") + "                        <gml:X>-130</gml:X>\n ") + "                        <gml:Y>24</gml:Y>\n ") + "                </gml:coord>\n ") + "                <gml:coord>\n ") + "                        <gml:X>-66</gml:X>\n ") + "                        <gml:Y>50</gml:Y>\n ") + "                </gml:coord>\n ") + "        </ogc:BoundingBox>\n ") + "        <ogc:Output>\n ") + "                <ogc:Format>image/png</ogc:Format>\n ") + "                <ogc:Size>\n ") + "                        <ogc:Width>550</ogc:Width>\n ") + "                        <ogc:Height>250</ogc:Height>\n ") + "                </ogc:Size>\n ") + "        </ogc:Output>\n ") + "</ogc:GetMap>\n ");

    @Test
    public void testImage() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        checkImage(response);
    }

    @Test
    public void testAllowedMimeTypes() throws Exception {
        WMSInfo wms = getWMS().getServiceInfo();
        GetMapOutputFormat format = new org.geoserver.wms.map.RenderedImageMapOutputFormat(getWMS());
        wms.getGetMapMimeTypes().add(format.getMimeType());
        wms.setGetMapMimeTypeCheckingEnabled(true);
        getGeoServer().save(wms);
        // check mime type allowed
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        checkImage(response);
        // check mime type not allowed
        String result = getAsString(((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=") + (OpenLayersMapOutputFormat.MIME_TYPE)) + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertTrue(((result.indexOf("ForbiddenFormat")) > 0));
        wms.setGetMapMimeTypeCheckingEnabled(false);
        wms.getGetMapMimeTypes().clear();
        getGeoServer().save(wms);
        result = getAsString(((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=") + (OpenLayersMapOutputFormat.MIME_TYPE)) + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertTrue(((result.indexOf("OpenLayers")) > 0));
    }

    @Test
    public void testLayoutLegendNPE() throws Exception {
        // set the title to null
        FeatureTypeInfo states = getCatalog().getFeatureTypeByName("states");
        states.setTitle(null);
        getCatalog().save(states);
        // add the layout to the data dir
        File layouts = getDataDirectory().findOrCreateDir("layouts");
        URL layout = GetMapIntegrationTest.class.getResource("test-layout.xml");
        FileUtils.copyURLToFile(layout, new File(layouts, "test-layout.xml"));
        // get a map with the layout, it used to NPE
        BufferedImage image = getAsImage((((((((("wms?bbox=" + (bbox)) + "&styles=Population&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&format_options=layout:test-layout"), "image/png");
        // RenderedImageBrowser.showChain(image);
        // check the pixels that should be in the legend
        assertPixel(image, 12, 16, Color.RED);
        assertPixel(image, 12, 32, Color.GREEN);
        assertPixel(image, 12, 52, Color.BLUE);
    }

    @Test
    public void testLayoutLegendStyleTitle() throws Exception {
        // set the title to null
        FeatureTypeInfo states = getCatalog().getFeatureTypeByName("states");
        states.setTitle(null);
        getCatalog().save(states);
        // add the layout to the data dir
        File layouts = getDataDirectory().findOrCreateDir("layouts");
        URL layout = GetMapIntegrationTest.class.getResource("test-layout-sldtitle.xml");
        FileUtils.copyURLToFile(layout, new File(layouts, "test-layout-sldtitle.xml"));
        // get a map with the layout, it used to NPE
        BufferedImage image = getAsImage((((((((("wms?bbox=" + (bbox)) + "&styles=Population&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&format_options=layout:test-layout-sldtitle"), "image/png");
        // RenderedImageBrowser.showChain(image);
        // check the pixels that should be in the legend
        assertPixel(image, 12, 36, Color.RED);
        assertPixel(image, 12, 52, Color.GREEN);
        assertPixel(image, 12, 72, Color.BLUE);
    }

    @Test
    public void testLayoutTranslucent() throws Exception {
        // add the layout to the data dir
        File layouts = getDataDirectory().findOrCreateDir("layouts");
        URL layout = GetMapIntegrationTest.class.getResource("test-layout.xml");
        FileUtils.copyURLToFile(layout, new File(layouts, "test-layout.xml"));
        // get a map with the layout after using a translucent style
        BufferedImage image = getAsImage(((((((((("wms?bbox=" + (bbox)) + "&styles=translucent&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&format_options=layout:test-layout&transparent=true"), "image/png");
        // RenderedImageBrowser.showChain(image);
        // check the pixels that should be in the scale bar
        assertPixel(image, 56, 211, Color.WHITE);
        // see GEOS-6482
        Assert.assertTrue(((getPixelColor(image, 52, 221).equals(Color.BLACK)) || (getPixelColor(image, 52, 222).equals(Color.BLACK))));
    }

    @Test
    public void testGeotiffMime() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/geotiff") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/geotiff", response.getContentType());
        Assert.assertEquals("inline; filename=sf-states.tif", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testLargerThanWorld() throws Exception {
        // setup a logging "bomb" rigged to explode when the warning message we
        // want to eliminate
        Logger l4jLogger = getLog4JLogger(GetMap.class, "LOGGER");
        l4jLogger.addAppender(new AppenderSkeleton() {
            @Override
            public boolean requiresLayout() {
                return false;
            }

            @Override
            public void close() {
            }

            @Override
            protected void append(LoggingEvent event) {
                if (((event.getMessage()) != null) && (event.getMessage().toString().startsWith("Failed to compute the scale denominator"))) {
                    // ka-blam!
                    Assert.fail("The error message is still there!");
                }
            }
        });
        MockHttpServletResponse response = getAsServletResponse(((((((("wms?bbox=-9.6450076761637E7,-3.9566251818225E7,9.6450076761637E7,3.9566251818225E7" + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:900913"));
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("inline; filename=sf-states.png", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testPng8Opaque() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png8") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/png; mode=8bit", response.getContentType());
        Assert.assertEquals("inline; filename=sf-states.png", response.getHeader("Content-Disposition"));
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
        Assert.assertEquals("inline; filename=sf-states.png", response.getHeader("Content-Disposition"));
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
        Assert.assertEquals("inline; filename=sf-states.png", response.getHeader("Content-Disposition"));
        InputStream is = getBinaryInputStream(response);
        BufferedImage bi = ImageIO.read(is);
        IndexColorModel cm = ((IndexColorModel) (bi.getColorModel()));
        Assert.assertEquals(Transparency.TRANSLUCENT, cm.getTransparency());
    }

    @Test
    public void testDefaultContentDisposition() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("inline; filename=sf-states.png", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testForcedContentDisposition() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&content-disposition=attachment"));
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("attachment; filename=sf-states.png", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testForcedFilename() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&filename=dude.png"));
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("inline; filename=dude.png", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testForcedContentDispositionFilename() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&content-disposition=attachment&filename=dude.png"));
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("attachment; filename=dude.png", response.getHeader("Content-Disposition"));
    }

    @Test
    public void testSldBody() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&SLD_BODY=") + (GetMapIntegrationTest.STATES_SLD.replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testStyleBody() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&STYLE_BODY=") + (GetMapIntegrationTest.STATES_SLD.replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testSldBody11() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&SLD_BODY=") + (GetMapIntegrationTest.STATES_SLD11.replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testStyleBody11() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&STYLE_BODY=") + (GetMapIntegrationTest.STATES_SLD11.replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testSldBodyNoVersion() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&SLD_BODY=") + (GetMapIntegrationTest.STATES_SLD.replace(" version=\"1.1.0\"", "").replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testStyleBodyNoVersion() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&STYLE_BODY=") + (GetMapIntegrationTest.STATES_SLD.replace(" version=\"1.1.0\"", "").replaceAll("=", "%3D"))));
        checkImage(response);
    }

    @Test
    public void testSldBodyPost() throws Exception {
        MockHttpServletResponse response = postAsServletResponse(((("wms?bbox=" + (bbox)) + "&format=image/png&request=GetMap&width=550&height=250") + "&srs=EPSG:4326"), GetMapIntegrationTest.STATES_SLD);
        checkImage(response);
    }

    @Test
    public void testSldBodyPost11() throws Exception {
        MockHttpServletResponse response = postAsServletResponse(((("wms?bbox=" + (bbox)) + "&format=image/png&request=GetMap&width=550&height=250") + "&srs=EPSG:4326"), GetMapIntegrationTest.STATES_SLD11);
        checkImage(response);
    }

    @Test
    public void testXmlPost() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("wms?", GetMapIntegrationTest.STATES_GETMAP);
        checkImage(response);
    }

    @Test
    public void testRemoteOWSGet() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWFSStatesAvailable(LOGGER)))
            return;

        ServletResponse response = getAsServletResponse((((((((((((("wms?request=getmap&service=wms&version=1.1.1" + ("&format=image/png" + "&layers=")) + (RemoteOWSTestSupport.TOPP_STATES)) + ",") + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())) + "&styles=Population,") + (BASIC_POLYGONS.getLocalPart())) + "&remote_ows_type=WFS") + "&remote_ows_url=") + (RemoteOWSTestSupport.WFS_SERVER_URL)) + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326"));
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testRemoteOWSUserStyleGet() throws Exception {
        if (!(RemoteOWSTestSupport.isRemoteWFSStatesAvailable(LOGGER))) {
            return;
        }
        URL url = GetMapIntegrationTest.class.getResource("remoteOws.sld");
        ServletResponse response = getAsServletResponse(((("wms?request=getmap&service=wms&version=1.1.1" + ("&format=image/png" + "&sld=")) + (url.toString())) + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326"));
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        Document doc = getAsDOM(("cite/wms?request=getmap&service=wms" + ("&layers=PrimitiveGeoFeature&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-180,-90,180,90")), true);
        Assert.assertEquals("ServiceExceptionReport", doc.getDocumentElement().getNodeName());
        ServletResponse response = getAsServletResponse(("cite/wms?request=getmap&service=wms" + ("&layers=Lakes&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-180,-90,180,90")));
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testLayerQualified() throws Exception {
        Document doc = getAsDOM(("cite/Ponds/wms?request=getmap&service=wms" + ("&layers=Forests&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-180,-90,180,90")), true);
        Assert.assertEquals("ServiceExceptionReport", doc.getDocumentElement().getNodeName());
        ServletResponse response = getAsServletResponse(("cite/Ponds/wms?request=getmap&service=wms" + ("&layers=Ponds&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-180,-90,180,90")));
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testGroupWorkspaceQualified() throws Exception {
        // check the group works without workspace qualification
        String url = "wms?request=getmap&service=wms" + ("&layers=nature&width=100&height=100&format=image/png" + "&srs=epsg:4326&bbox=-0.002,-0.003,0.005,0.002");
        ServletResponse response = getAsServletResponse(url);
        Assert.assertEquals("image/png", response.getContentType());
        // see that it works also with workspace qualification
        response = getAsServletResponse(("cite/" + url));
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testEnvDefault() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=parametric&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        Assert.assertEquals("image/png", response.getContentType());
        RenderedImage image = ImageIO.read(getBinaryInputStream(response));
        int[] rgba = new int[3];
        // fully black pixel in the middle of the map
        image.getData().getPixel(250, 125, rgba);
        // assertEquals(0, rgba[0]);
        // assertEquals(0, rgba[1]);
        // assertEquals(0, rgba[2]);
    }

    @Test
    public void testEnvRed() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((((((((("wms?bbox=" + (bbox)) + "&styles=parametric&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326&env=color:0xFF0000"));
        Assert.assertEquals("image/png", response.getContentType());
        RenderedImage image = ImageIO.read(getBinaryInputStream(response));
        int[] rgba = new int[3];
        // fully red pixel in the middle of the map
        image.getData().getPixel(250, 125, rgba);
        // assertEquals(255, rgba[0]);
        // assertEquals(0, rgba[1]);
        // assertEquals(0, rgba[2]);
    }

    @Test
    public void testMosaicHoles() throws Exception {
        String url = "wms?LAYERS=sf%3Amosaic_holes&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1" + (("&REQUEST=GetMap&STYLES=&SRS=EPSG%3A4326" + "&BBOX=6.40284375,36.385494140625,12.189662109375,42.444494140625") + "&WIDTH=489&HEIGHT=512&transparent=true");
        BufferedImage bi = getAsImage(url, "image/png");
        int[] pixel = new int[4];
        bi.getRaster().getPixel(0, 250, pixel);
        Assert.assertTrue(Arrays.equals(new int[]{ 0, 0, 0, 255 }, pixel));
        // now reconfigure the mosaic for transparency
        CoverageInfo ci = getCatalog().getCoverageByName("sf:mosaic_holes");
        Map<String, Serializable> params = ci.getParameters();
        params.put(INPUT_TRANSPARENT_COLOR.getName().getCode(), "#000000");
        params.put(OUTPUT_TRANSPARENT_COLOR.getName().getCode(), "#000000");
        getCatalog().save(ci);
        // this time that pixel should be transparent
        bi = getAsImage(url, "image/png");
        bi.getRaster().getPixel(0, 250, pixel);
        Assert.assertTrue(Arrays.equals(new int[]{ 255, 255, 255, 0 }, pixel));
    }

    @Test
    public void testTransparentPaletteOpaqueOutput() throws Exception {
        String url = (((("wms?LAYERS=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=demTranslucent&") + "FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=145,-43,146,-41&WIDTH=100&HEIGHT=200&bgcolor=0xFF0000";
        BufferedImage bi = getAsImage(url, "image/png");
        ColorModel cm = bi.getColorModel();
        Assert.assertTrue((cm instanceof IndexColorModel));
        Assert.assertEquals(Transparency.OPAQUE, cm.getTransparency());
        // grab a pixel in the low left corner, should be red (BG color)
        int[] pixel = new int[1];
        bi.getRaster().getPixel(4, 196, pixel);
        int[] color = new int[3];
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(255, color[0]);
        Assert.assertEquals(0, color[1]);
        Assert.assertEquals(0, color[2]);
        // a pixel high enough to be solid, should be fully green
        bi.getRaster().getPixel(56, 49, pixel);
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(0, color[0]);
        Assert.assertEquals(255, color[1]);
        Assert.assertEquals(0, color[2]);
    }

    @Test
    public void testCoverageViewMap() throws Exception {
        String url = "wms?LAYERS=mosaic&" + (("&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=7,37,11,41&WIDTH=100&HEIGHT=200&bgcolor=0xFF0000");
        BufferedImage bi = getAsImage(url, "image/png");
        int[] pixel = new int[3];
        bi.getRaster().getPixel(50, 100, pixel);
        final int R_PIXEL = 45;
        final int G_PIXEL = 46;
        final int B_PIXEL = 69;
        Assert.assertEquals(R_PIXEL, pixel[0]);
        Assert.assertEquals(G_PIXEL, pixel[1]);
        Assert.assertEquals(B_PIXEL, pixel[2]);
        // The shuffled view revert RGB bands to BGR
        url = "wms?LAYERS=mosaic_shuffle&" + (("&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=7,37,11,41&WIDTH=100&HEIGHT=200&bgcolor=0xFF0000");
        bi = getAsImage(url, "image/png");
        bi.getRaster().getPixel(50, 100, pixel);
        Assert.assertEquals(B_PIXEL, pixel[0]);
        Assert.assertEquals(G_PIXEL, pixel[1]);
        Assert.assertEquals(R_PIXEL, pixel[2]);
    }

    @Test
    public void testTransparentPaletteTransparentOutput() throws Exception {
        String url = (((("wms?LAYERS=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=demTranslucent&") + "FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=145,-43,146,-41&WIDTH=100&HEIGHT=200&transparent=true";
        BufferedImage bi = getAsImage(url, "image/png");
        ColorModel cm = bi.getColorModel();
        Assert.assertTrue((cm instanceof IndexColorModel));
        Assert.assertEquals(Transparency.TRANSLUCENT, cm.getTransparency());
        // grab a pixel in the low left corner, should be transparent
        int[] pixel = new int[1];
        bi.getRaster().getPixel(4, 196, pixel);
        int[] color = new int[4];
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(0, color[3]);
        // a pixel high enough to be solid, should be solid green
        bi.getRaster().getPixel(56, 49, pixel);
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(0, color[0]);
        Assert.assertEquals(255, color[1]);
        Assert.assertEquals(0, color[2]);
        Assert.assertEquals(255, color[3]);
    }

    @Test
    public void testTransparentPaletteTransparentOutputPng8() throws Exception {
        String url = (((("wms?LAYERS=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=demTranslucent&") + "FORMAT=image%2Fpng8&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=145,-43,146,-41&WIDTH=100&HEIGHT=200&transparent=true";
        BufferedImage bi = getAsImage(url, "image/png; mode=8bit");
        ColorModel cm = bi.getColorModel();
        Assert.assertTrue((cm instanceof IndexColorModel));
        Assert.assertEquals(Transparency.TRANSLUCENT, cm.getTransparency());
        // grab a pixel in the low left corner, should be transparent
        int[] pixel = new int[1];
        bi.getRaster().getPixel(4, 196, pixel);
        int[] color = new int[4];
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(0, color[3]);
        // a pixel high enough to be solid, should be solid green
        bi.getRaster().getPixel(56, 49, pixel);
        cm.getComponents(pixel[0], color, 0);
        Assert.assertEquals(0, color[0]);
        Assert.assertEquals(255, color[1]);
        Assert.assertEquals(0, color[2]);
        Assert.assertEquals(255, color[3]);
    }

    @Test
    public void testLayoutLegendStyleTitleDPI() throws Exception {
        // set the title to null
        FeatureTypeInfo states = getCatalog().getFeatureTypeByName("states");
        states.setTitle(null);
        getCatalog().save(states);
        // add the layout to the data dir
        File layouts = getDataDirectory().findOrCreateDir("layouts");
        URL layout = GetMapIntegrationTest.class.getResource("test-layout-sldtitle.xml");
        FileUtils.copyURLToFile(layout, new File(layouts, "test-layout-sldtitle.xml"));
        int dpi = 90 * 2;
        int width = 550 * 2;
        int height = 250 * 2;
        // get a map with the layout, it used to NPE
        BufferedImage image = getAsImage(((((((((((("wms?bbox=" + (bbox)) + "&styles=Population&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=") + width) + "&height=") + height) + "&srs=EPSG:4326&format_options=layout:test-layout-sldtitle;dpi:") + dpi), "image/png");
        // RenderedImageBrowser.showChain(image);
        // check the pixels that should be in the legend
        assertPixel(image, 15, 67, Color.RED);
        assertPixel(image, 15, 107, Color.GREEN);
        assertPixel(image, 15, 147, Color.BLUE);
    }

    @Test
    public void testLayerGroupSingle() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo group = createLakesPlacesLayerGroup(catalog, SINGLE, null);
        try {
            String url = (("wms?LAYERS=" + (group.getName())) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010";
            BufferedImage image = getAsImage(url, "image/png");
            assertPixel(image, 150, 160, Color.WHITE);
            // places
            assertPixel(image, 180, 16, WMSTestSupport.COLOR_PLACES_GRAY);
            // lakes
            assertPixel(image, 90, 200, WMSTestSupport.COLOR_LAKES_BLUE);
        } finally {
            catalog.remove(group);
        }
    }

    @Test
    public void testLayerGroupNamed() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo group = createLakesPlacesLayerGroup(catalog, NAMED, null);
        try {
            String url = (("wms?LAYERS=" + (group.getName())) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010";
            BufferedImage image = getAsImage(url, "image/png");
            assertPixel(image, 150, 160, Color.WHITE);
            // places
            assertPixel(image, 180, 16, WMSTestSupport.COLOR_PLACES_GRAY);
            // lakes
            assertPixel(image, 90, 200, WMSTestSupport.COLOR_LAKES_BLUE);
        } finally {
            catalog.remove(group);
        }
    }

    @Test
    public void testLayerGroupContainer() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo group = createLakesPlacesLayerGroup(catalog, CONTAINER, null);
        try {
            String url = (("wms?LAYERS=" + (group.getName())) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010";
            // this group is not meant to be called directly so we should get an exception
            MockHttpServletResponse resp = getAsServletResponse(url);
            Assert.assertEquals("application/vnd.ogc.se_xml", resp.getContentType());
            Document dom = getAsDOM(url);
            Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
            Element serviceException = ((Element) (dom.getDocumentElement().getElementsByTagName("ServiceException").item(0)));
            Assert.assertEquals("LayerNotDefined", serviceException.getAttribute("code"));
            Assert.assertEquals("layers", serviceException.getAttribute("locator"));
            Assert.assertEquals(("Could not find layer " + (group.getName())), serviceException.getTextContent().trim());
        } finally {
            catalog.remove(group);
        }
    }

    @Test
    public void testLayerGroupModeEo() throws Exception {
        Catalog catalog = getCatalog();
        LayerGroupInfo group = createLakesPlacesLayerGroup(catalog, EO, catalog.getLayerByName(getLayerId(MockData.LAKES)));
        try {
            String url = (("wms?LAYERS=" + (group.getName())) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010";
            BufferedImage image = getAsImage(url, "image/png");
            assertPixel(image, 150, 160, Color.WHITE);
            // no places
            assertPixel(image, 180, 16, Color.WHITE);
            // lakes
            assertPixel(image, 90, 200, WMSTestSupport.COLOR_LAKES_BLUE);
        } finally {
            catalog.remove(group);
        }
    }

    @Test
    public void testOneBit() throws Exception {
        String url = (("wms?LAYERS=" + (getLayerId(GetMapIntegrationTest.ONE_BIT))) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=10&HEIGHT=10&BBOX=0,0,10,10";
        // used to crash, should give us back a empty image instead
        getAsImage(url, "image/png");
    }

    @Test
    public void testSldExternalEntities() throws Exception {
        URL sldUrl = TestData.class.getResource("externalEntities.sld");
        String url = (((((((((("wms?bbox=" + (bbox)) + "&styles=") + "&layers=") + (layers)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&sld=") + (sldUrl.toString());
        WMS wms = new WMS(getGeoServer());
        GeoServerInfo geoserverInfo = wms.getGeoServer().getGlobal();
        try {
            // enable entities in external SLD files
            geoserverInfo.setXmlExternalEntitiesEnabled(true);
            getGeoServer().save(geoserverInfo);
            // if entities evaluation is enabled
            // the parser will try to read a file on the local file system
            // if the file is found, its content will be used to replace the entity
            // if the file is not found the parser will throw a FileNotFoundException
            String response = getAsString(url);
            Assert.assertTrue(((response.indexOf("Error while getting SLD.")) > (-1)));
            // disable entities
            geoserverInfo.setXmlExternalEntitiesEnabled(false);
            getGeoServer().save(geoserverInfo);
            // if entities evaluation is disabled
            // the parser will throw a MalformedURLException when it finds an entity
            response = getAsString(url);
            Assert.assertTrue(((response.indexOf("Entity resolution disallowed")) > (-1)));
            // try default value: disabled entities
            geoserverInfo.setXmlExternalEntitiesEnabled(null);
            getGeoServer().save(geoserverInfo);
            // if entities evaluation is disabled
            // the parser will throw a MalformedURLException when it finds an entity
            response = getAsString(url);
            Assert.assertTrue(((response.indexOf("Entity resolution disallowed")) > (-1)));
        } finally {
            // default
            geoserverInfo.setXmlExternalEntitiesEnabled(null);
            getGeoServer().save(geoserverInfo);
        }
    }

    @Test
    public void testRssMime() throws Exception {
        MockHttpServletResponse response = getAsServletResponse((("wms?request=reflect&layers=" + (getLayerId(BASIC_POLYGONS))) + "&format=rss"));
        Assert.assertEquals("application/rss+xml", response.getContentType());
    }

    /**
     * Basic sanity tests on a polar stereographic projection (EPSG:5041) WMS response.
     */
    @Test
    public void testPolarStereographic() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(("wms?" + (((((((("service=WMS" + "&version=1.1.1") + "&request=GetMap") + "&layers=sf:states") + "&bbox=-10700000,-10700000,14700000,14700000,EPSG:5041") + "&width=200") + "&height=200") + "&srs=EPSG:5041") + "&format=image%2Fpng")));
        checkImage(response, "image/png", 200, 200);
        String testName = "testPolarStereographic";
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        assertNotBlank(testName, image);
        // top-left quadrant should not be blank
        assertNotBlank(testName, image.getSubimage(0, 0, 100, 100));
        // top 25% should be blank
        Assert.assertEquals(0, countNonBlankPixels(testName, image.getSubimage(0, 0, 200, 50), WMSTestSupport.BG_COLOR));
        // right-hand side should be blank
        Assert.assertEquals(0, countNonBlankPixels(testName, image.getSubimage(100, 0, 100, 200), WMSTestSupport.BG_COLOR));
        // bottom 35% should be blank
        Assert.assertEquals(0, countNonBlankPixels(testName, image.getSubimage(0, 130, 200, 70), WMSTestSupport.BG_COLOR));
    }

    @Test
    public void testMapWrapping() throws Exception {
        GeoServer gs = getGeoServer();
        WMSInfo wms = gs.getService(WMSInfo.class);
        Boolean original = wms.getMetadata().get(MAP_WRAPPING_KEY, Boolean.class);
        try {
            wms.getMetadata().put(MAP_WRAPPING_KEY, Boolean.TRUE);
            gs.save(wms);
            String layer = getLayerId(GetMapIntegrationTest.GIANT_POLYGON);
            String request = ((("wms?version=1.1.1&bbox=170,-10,190,10&format=image/png" + "&request=GetMap&layers=") + layer) + "&styles=polygon") + "&width=100&height=100&srs=EPSG:4326";
            String wrapDisabledOptionRequest = request + "&format_options=mapWrapping:false";
            String wrapEnabledOptionRequest = request + "&format_options=mapWrapping:true";
            BufferedImage image = getAsImage(request, "image/png");
            // with wrapping enabled we should get a gray pixel
            assertPixel(image, 75, 0, new Color(170, 170, 170));
            image = getAsImage(wrapDisabledOptionRequest, "image/png");
            // This should disable wrapping, so we get white pixel (nothing)
            assertPixel(image, 75, 0, Color.WHITE);
            image = getAsImage(wrapEnabledOptionRequest, "image/png");
            // with wrapping explictly enabled we should get a gray pixel
            assertPixel(image, 75, 0, new Color(170, 170, 170));
            wms.getMetadata().put(MAP_WRAPPING_KEY, Boolean.FALSE);
            gs.save(wms);
            image = getAsImage(request, "image/png");
            // with wrapping disabled we should get a white one (nothing)
            assertPixel(image, 75, 0, Color.WHITE);
            image = getAsImage(wrapDisabledOptionRequest, "image/png");
            // With explicit config disable, our option should be disabled
            assertPixel(image, 75, 0, Color.WHITE);
            image = getAsImage(wrapEnabledOptionRequest, "image/png");
            assertPixel(image, 75, 0, Color.WHITE);
        } finally {
            wms.getMetadata().put(MAP_WRAPPING_KEY, original);
            gs.save(wms);
        }
    }

    @Test
    public void testAdvancedProjectionHandling() throws Exception {
        GeoServer gs = getGeoServer();
        WMSInfo wms = gs.getService(WMSInfo.class);
        Boolean original = wms.getMetadata().get(ADVANCED_PROJECTION_KEY, Boolean.class);
        try {
            wms.getMetadata().put(ADVANCED_PROJECTION_KEY, Boolean.TRUE);
            gs.save(wms);
            String layer = getLayerId(GetMapIntegrationTest.LARGE_POLYGON);
            String request = ((("wms?version=1.1.1&bbox=-18643898.1832,0,18084728.7111,20029262&format=image/png" + "&request=GetMap&layers=") + layer) + "&styles=polygon") + "&width=400&height=400&srs=EPSG:3832";
            String disabledRequest = request + "&format_options=advancedProjectionHandling:false";
            String enabledRequest = request + "&format_options=advancedProjectionHandling:true";
            BufferedImage image = getAsImage(request, "image/png");
            // with APH, we should get a gap
            assertPixel(image, 200, 200, Color.WHITE);
            // APH enabled in the GUI, disabled in the request
            image = getAsImage(disabledRequest, "image/png");
            // expect it to cross the image
            assertPixel(image, 200, 200, new Color(170, 170, 170));
            // APH enabled in the GUI, explictly enabled in the request
            image = getAsImage(enabledRequest, "image/png");
            assertPixel(image, 200, 200, Color.WHITE);
            wms.getMetadata().put(ADVANCED_PROJECTION_KEY, Boolean.FALSE);
            gs.save(wms);
            image = getAsImage(request, "image/png");
            assertPixel(image, 200, 200, new Color(170, 170, 170));
            // APH disabled in the GUI, disabled in the request
            image = getAsImage(disabledRequest, "image/png");
            // expect it to cross the image
            assertPixel(image, 200, 200, new Color(170, 170, 170));
            // APH disabled in the GUI, explictly enabled in the request
            image = getAsImage(enabledRequest, "image/png");
            // does not override admin disabled.
            assertPixel(image, 200, 200, new Color(170, 170, 170));
        } finally {
            wms.getMetadata().put(ADVANCED_PROJECTION_KEY, original);
            gs.save(wms);
        }
    }

    @Test
    public void testJpegPngTransparent() throws Exception {
        String request = "wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fvnd.jpeg-png&TRANSPARENT=true&STYLES" + "&LAYERS=cite%3ABasicPolygons&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-2.4%2C1.4%2C0.4%2C4.2";
        // checks it's a PNG
        BufferedImage image = getAsImage(request, "image/png");
        assertNotBlank("testJpegPngTransparent", image);
    }

    @Test
    public void testJpegPng8Transparent() throws Exception {
        String request = "wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fvnd.jpeg-png8&TRANSPARENT=true&STYLES" + "&LAYERS=cite%3ABasicPolygons&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-2.4%2C1.4%2C0.4%2C4.2";
        // checks it's a PNG
        MockHttpServletResponse resp = getAsServletResponse(request);
        Assert.assertEquals("image/png", resp.getContentType());
        Assert.assertEquals("inline; filename=cite-BasicPolygons.png", resp.getHeader(CONTENT_DISPOSITION));
        BufferedImage image = ImageIO.read(getBinaryInputStream(resp));
        assertNotBlank("testJpegPngTransparent", image);
        // check it's paletted
        Assert.assertThat(image.getColorModel(), CoreMatchers.instanceOf(IndexColorModel.class));
    }

    @Test
    public void testJpegPngOpaque() throws Exception {
        String request = "wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fvnd.jpeg-png&TRANSPARENT=true&STYLES" + "&LAYERS=cite%3ABasicPolygons&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.4%2C3.6%2C1%2C5";
        // checks it's a JPEG, since it's opaque
        MockHttpServletResponse resp = getAsServletResponse(request);
        Assert.assertEquals("image/jpeg", resp.getContentType());
        Assert.assertEquals("inline; filename=cite-BasicPolygons.jpg", resp.getHeader(CONTENT_DISPOSITION));
        InputStream is = getBinaryInputStream(resp);
        BufferedImage image = ImageIO.read(is);
        assertNotBlank("testJpegPngOpaque", image);
    }

    @Test
    public void testJpegPng8Opaque() throws Exception {
        String request = "wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fvnd.jpeg-png8&TRANSPARENT=true&STYLES" + "&LAYERS=cite%3ABasicPolygons&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.4%2C3.6%2C1%2C5";
        // checks it's a JPEG, since it's opaque
        BufferedImage image = getAsImage(request, "image/jpeg");
        assertNotBlank("testJpegPngOpaque", image);
    }

    @Test
    public void testJpegPngEmpty() throws Exception {
        String request = "wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fvnd.jpeg-png&TRANSPARENT=true&STYLES" + "&LAYERS=cite%3ABasicPolygons&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-1.9%2C1.8%2C-1.3%2C2.5";
        // checks it's a PNG
        BufferedImage image = getAsImage(request, "image/png");
        assertBlank("testJpegPngEmpty", image, new Color(255, 255, 255, 0));
    }

    @Test
    public void testFeatureIdMultipleLayers() throws Exception {
        String lakes = getLayerId(MockData.LAKES);
        String places = getLayerId(MockData.NAMED_PLACES);
        String urlSingle = (("wms?LAYERS=" + lakes) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010";
        BufferedImage imageLakes = getAsImage(urlSingle, "image/png");
        // ask with featureid filter against two layers... used to fail
        String url = ((((("wms?LAYERS=" + lakes) + ",") + places) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=0.0000,-0.0020,0.0035,0.0010") + "&featureId=Lakes.1107531835962";
        BufferedImage imageLakesPlaces = getAsImage(url, "image/png");
        // should be the same image, the second request filters out anything in "places"
        ImageAssert.assertEquals(imageLakes, imageLakesPlaces, 0);
    }

    @Test
    public void testGetMapOpaqueGroup() throws Exception {
        String url = (("wms?LAYERS=" + (WMSTestSupport.OPAQUE_GROUP)) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.0043,-0.0025,0.0043,0.0025";
        BufferedImage imageGroup = getAsImage(url, "image/png");
        ImageAssert.assertEquals(new File("./src/test/resources/org/geoserver/wms/wms_1_1_1/opaqueGroup.png"), imageGroup, 300);
    }

    @Test
    public void testGetMapLayersInOpaqueGroup() throws Exception {
        LayerGroupInfo group = getCatalog().getLayerGroupByName(WMSTestSupport.OPAQUE_GROUP);
        for (PublishedInfo pi : group.layers()) {
            String url = (("wms?LAYERS=" + (pi.prefixedName())) + "&STYLES=&FORMAT=image%2Fpng") + "&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A4326&WIDTH=256&HEIGHT=256&BBOX=-0.0043,-0.0025,0.0043,0.0025";
            Document dom = getAsDOM(url);
            // print(dom);
            // should not be found
            XMLAssert.assertXpathEvaluatesTo("1", "count(/ServiceExceptionReport)", dom);
            XMLAssert.assertXpathEvaluatesTo("layers", "//ServiceException/@locator", dom);
            XMLAssert.assertXpathEvaluatesTo("LayerNotDefined", "//ServiceException/@code", dom);
        }
    }

    @Test
    public void testReprojectRGBTransparent() throws Exception {
        // UTM53N, close enough to tasmania but sure to add rotation
        BufferedImage image = getAsImage((("wms/reflect?layers=" + (getLayerId(MockData.TASMANIA_BM))) + "&SRS=EPSG:32753&format=image/png&transparent=true"), "image/png");
        // it's transparent
        Assert.assertTrue(image.getColorModel().hasAlpha());
        Assert.assertEquals(4, image.getSampleModel().getNumBands());
        // assert pixels in the 4 corners, the rotation should have made them all transparent
        assertPixelIsTransparent(image, 0, 0);
        assertPixelIsTransparent(image, ((image.getWidth()) - 1), 0);
        assertPixelIsTransparent(image, ((image.getWidth()) - 1), ((image.getHeight()) - 1));
        assertPixelIsTransparent(image, 0, ((image.getHeight()) - 1));
    }

    @Test
    public void testReprojectRGBWithBgColor() throws Exception {
        // UTM53N, close enough to tasmania but sure to add rotation
        BufferedImage image = getAsImage((("wms/reflect?layers=" + (getLayerId(MockData.TASMANIA_BM))) + "&SRS=EPSG:32753&format=image/png&bgcolor=#FF0000"), "image/png");
        // it's not transparent
        Assert.assertFalse(image.getColorModel().hasAlpha());
        Assert.assertEquals(3, image.getSampleModel().getNumBands());
        // assert pixels in the 4 corners, the rotation should have made them all red
        assertPixel(image, 0, 0, Color.RED);
        assertPixel(image, ((image.getWidth()) - 1), 0, Color.RED);
        assertPixel(image, ((image.getWidth()) - 1), ((image.getHeight()) - 1), Color.RED);
        assertPixel(image, 0, ((image.getHeight()) - 1), Color.RED);
    }

    @Test
    public void testReprojectedDemWithTransparency() throws Exception {
        // UTM53N, close enough to tasmania but sure to add rotation
        BufferedImage image = getAsImage((("wms/reflect?layers=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=demTranslucent&SRS=EPSG:32753&format=image/png&transparent=true"), "image/png");
        // RenderedImageBrowser.showChain(image);
        // it's transparent
        Assert.assertTrue(image.getColorModel().hasAlpha());
        Assert.assertEquals(1, image.getSampleModel().getNumBands());
        // assert pixels in the 4 corners, the rotation should have made them all dark gray
        assertPixelIsTransparent(image, 0, 0);
        assertPixelIsTransparent(image, ((image.getWidth()) - 1), 0);
        assertPixelIsTransparent(image, ((image.getWidth()) - 1), ((image.getHeight()) - 1));
        assertPixelIsTransparent(image, 0, ((image.getHeight()) - 1));
    }

    @Test
    public void testDemWithBgColor() throws Exception {
        // UTM53N, close enough to tasmania but sure to add rotation
        BufferedImage image = getAsImage((("wms/reflect?layers=" + (getLayerId(MockData.TASMANIA_DEM))) + "&styles=demTranslucent&SRS=EPSG:32753&format=image/png&bgcolor=#404040"), "image/png");
        // RenderedImageBrowser.showChain(image);
        // it's transparent
        Assert.assertFalse(image.getColorModel().hasAlpha());
        Assert.assertEquals(1, image.getSampleModel().getNumBands());
        // assert pixels in the 4 corners, the rotation should have made them all dark gray
        assertPixel(image, 0, 0, Color.DARK_GRAY);
        assertPixel(image, ((image.getWidth()) - 1), 0, Color.DARK_GRAY);
        assertPixel(image, ((image.getWidth()) - 1), ((image.getHeight()) - 1), Color.DARK_GRAY);
        assertPixel(image, 0, ((image.getHeight()) - 1), Color.DARK_GRAY);
    }

    @Test
    public void testMaskedNoAPH() throws Exception {
        // used to fail when hitting a tiff with ROI with reprojection (thus buffer) in an area
        // close to, but not hitting, the ROI
        GeoServer gs = getGeoServer();
        WMSInfo wms = gs.getService(WMSInfo.class);
        Serializable oldValue = wms.getMetadata().get(ADVANCED_PROJECTION_KEY);
        try {
            wms.getMetadata().put(ADVANCED_PROJECTION_KEY, false);
            gs.save(wms);
            BufferedImage image = getAsImage((("wms/reflect?layers=" + (getLayerId(GetMapIntegrationTest.MASKED))) + "&SRS=AUTO%3A97002%2C9001%2C-1%2C40&BBOX=694182%2C-4631295%2C695092%2C-4630379&format=image/png&transparent=true"), "image/png");
            // transparent model
            Assert.assertTrue(image.getColorModel().hasAlpha());
            Assert.assertThat(image.getColorModel(), CoreMatchers.instanceOf(ComponentColorModel.class));
            double[] maximums = new ImageWorker(image).getMaximums();
            // last band, alpha, is fully at zero, so transparent
            Assert.assertEquals(0, maximums[((maximums.length) - 1)], 0.0);
        } finally {
            wms.getMetadata().put(ADVANCED_PROJECTION_KEY, oldValue);
            gs.save(wms);
        }
    }

    @Test
    public void testRTAndBandSelection() throws Exception {
        String url = "wms?LAYERS=mosaic_shuffle&styles=jiffleBandSelect" + (("&FORMAT=image%2Fpng&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG%3A4326") + "&BBOX=7,37,11,41&WIDTH=100&HEIGHT=200&bgcolor=0xFF0000");
        // used to go NPE
        BufferedImage jiffleBandSelected = getAsImage(url, "image/png");
        ImageAssert.assertEquals(new File("./src/test/resources/org/geoserver/wms/wms_1_1_1/jiffleBandSelected.png"), jiffleBandSelected, 300);
    }
}

