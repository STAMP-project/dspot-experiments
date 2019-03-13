/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs.download;


import KMZMapOutputFormat.MIME_TYPE;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.kml.KMZMapOutputFormat;
import org.geoserver.test.http.MockHttpClient;
import org.geoserver.test.http.MockHttpResponse;
import org.geotools.data.ows.HTTPClient;
import org.geotools.image.test.ImageAssert;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DownloadMapProcessTest extends BaseDownloadImageProcessTest {
    @Test
    public void testDescribeProcess() throws Exception {
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:DownloadMap"));
        // print(d);
        assertXpathExists("//ComplexOutput/Supported/Format[MimeType='image/png']", d);
        assertXpathExists("//ComplexOutput/Supported/Format[MimeType='image/jpeg']", d);
        assertXpathExists((("//ComplexOutput/Supported/Format[MimeType='" + (KMZMapOutputFormat.MIME_TYPE)) + "']"), d);
    }

    @Test
    public void testExecuteSingleLayer() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapSimple.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")), image, 100);
    }

    @Test
    public void testExecuteSingleLayerFilter() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapSimpleFilter.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimpleFilter.png")), image, 100);
    }

    @Test
    public void testExecuteSingleDecorated() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapSimpleDecorated.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "watermarked.png")), image, 100);
    }

    @Test
    public void testExecuteMultiName() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapMultiName.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        Assert.assertEquals("inline; filename=result.png", response.getHeader("Content-disposition"));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapMultiName.png")), image, 100);
    }

    @Test
    public void testExecuteMultiLayer() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapMultiLayer.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        // not a typo, the output should indeed be the same as testExecuteMultiName
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapMultiName.png")), image, 100);
    }

    @Test
    public void testExecuteMultiLayerKmz() throws Exception {
        testExecutMultiLayerKmz(MIME_TYPE);
    }

    @Test
    public void testExecuteMultiLayerKmzShort() throws Exception {
        testExecutMultiLayerKmz("kmz");
    }

    @Test
    public void testExecuteGeotiff() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("mapMultiLayer.xml"));
        request = request.replaceAll("image/png", "image/geotiff");
        MockHttpServletResponse response = postAsServletResponse("wps", request);
        Assert.assertEquals("image/geotiff", response.getContentType());
        Assert.assertEquals("attachment; filename=result.tif", response.getHeader("Content-disposition"));
    }

    @Test
    public void testTimeFilter() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapTimeFilter.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        // same test as DimensionRasterGetMapTest#testTime
        assertPixel(image, 36, 31, new Color(246, 246, 255));
        assertPixel(image, 68, 72, new Color(255, 181, 181));
        // making extra sure
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapTimeFilter.png")), image, 100);
    }

    @Test
    public void testTimeFilterTimestamped() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapTimeFilterTimestamped.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapTimeFilterTimestamped.png")), image, 200);
    }

    @Test
    public void testTimeFilterFormattedTimestamp() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("mapTimeFilterFormattedTimestamp.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("image/png", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapTimeFilterFormattedTimestamp.png")), image, 200);
    }

    @Test
    public void downloadMapGif() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("mapSimple.xml"));
        request = request.replaceAll("image/png", "image/gif");
        MockHttpServletResponse response = postAsServletResponse("wps", request);
        Assert.assertEquals("image/gif", response.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")), image, 200);
    }

    @Test
    public void downloadRemoteSimple11() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("mapRemoteSimple11.xml"));
        String caps111 = IOUtils.toString(getClass().getResourceAsStream("caps111.xml"));
        byte[] getMapBytes = FileUtils.readFileToByteArray(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")));
        DownloadMapProcess process = applicationContext.getBean(DownloadMapProcess.class);
        MockHttpClient client = new MockHttpClient();
        client.expectGet(new URL(("http://geoserver" + ".org/geoserver/wms?service=WMS&request=GetCapabilities&version=1.1.0")), new MockHttpResponse(caps111, "text/xml"));
        // check it follows the links in the caps document
        client.expectGet(new URL(("http://mock.test.geoserver" + (".org/wms11?SERVICE=WMS&LAYERS=cite:BasicPolygons&FORMAT=image%2Fpng&HEIGHT=256&TRANSPARENT=false" + "&REQUEST=GetMap&WIDTH=256&BBOX=-2.4,1.4,0.4,4.2&SRS=EPSG:4326&VERSION=1.1.1"))), new MockHttpResponse(getMapBytes, "image/png"));
        // switch from the standard supplier to one using the mock client prepared above
        Supplier<HTTPClient> oldSupplier = process.getHttpClientSupplier();
        try {
            process.setHttpClientSupplier(() -> client);
            MockHttpServletResponse response = postAsServletResponse("wps", request);
            Assert.assertEquals("image/png", response.getContentType());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
            ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")), image, 100);
        } finally {
            process.setHttpClientSupplier(oldSupplier);
        }
    }

    @Test
    public void downloadRemoteSimple13() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("mapRemoteSimple13.xml"));
        String caps130 = IOUtils.toString(getClass().getResourceAsStream("caps130.xml"));
        byte[] getMapBytes = FileUtils.readFileToByteArray(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")));
        DownloadMapProcess process = applicationContext.getBean(DownloadMapProcess.class);
        MockHttpClient client = new MockHttpClient();
        client.expectGet(new URL("http://geoserver.org/geoserver/wms?service=WMS&request=GetCapabilities&version=1.3.0"), new MockHttpResponse(caps130, "text/xml"));
        // check it follows the links in the caps document and does axis flipping as required
        client.expectGet(new URL(("http://mock.test.geoserver" + (".org/wms13?SERVICE=WMS&LAYERS=cite:BasicPolygons&FORMAT=image%2Fpng&HEIGHT=256&TRANSPARENT=false" + "&REQUEST=GetMap&WIDTH=256&BBOX=1.4,-2.4,4.2,0.4&CRS=EPSG:4326&VERSION=1.3.0"))), new MockHttpResponse(getMapBytes, "image/png"));
        // switch from the standard supplier to one using the mock client prepared above
        Supplier<HTTPClient> oldSupplier = process.getHttpClientSupplier();
        try {
            process.setHttpClientSupplier(() -> client);
            MockHttpServletResponse response = postAsServletResponse("wps", request);
            Assert.assertEquals("image/png", response.getContentType());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
            ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "mapSimple.png")), image, 100);
        } finally {
            process.setHttpClientSupplier(oldSupplier);
        }
    }

    @Test
    public void downloadLocalRemote() throws Exception {
        String request = IOUtils.toString(getClass().getResourceAsStream("mapLocalRemote.xml"));
        String caps111 = IOUtils.toString(getClass().getResourceAsStream("caps111.xml"));
        byte[] getMapBytes = FileUtils.readFileToByteArray(new File(((BaseDownloadImageProcessTest.SAMPLES) + "lakes.png")));
        DownloadMapProcess process = applicationContext.getBean(DownloadMapProcess.class);
        MockHttpClient client = new MockHttpClient();
        client.expectGet(new URL(("http://geoserver" + ".org/geoserver/wms?service=WMS&request=GetCapabilities&version=1.1.0")), new MockHttpResponse(caps111, "text/xml"));
        // check it follows the links in the caps document
        client.expectGet(new URL(("http://mock.test.geoserver" + (".org/wms11?SERVICE=WMS&LAYERS=cite:Lakes&FORMAT=image%2Fpng&HEIGHT=256&TRANSPARENT=true" + "&REQUEST=GetMap&WIDTH=256&BBOX=0.0,-0.003,0.004,0.001&SRS=EPSG:4326&VERSION=1.1.1"))), new MockHttpResponse(getMapBytes, "image/png"));
        // switch from the standard supplier to one using the mock client prepared above
        Supplier<HTTPClient> oldSupplier = process.getHttpClientSupplier();
        try {
            process.setHttpClientSupplier(() -> client);
            MockHttpServletResponse response = postAsServletResponse("wps", request);
            Assert.assertEquals("image/png", response.getContentType());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));
            ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "localRemote.png")), image, 100);
        } finally {
            process.setHttpClientSupplier(oldSupplier);
        }
    }
}

