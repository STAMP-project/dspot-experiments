/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import DimensionInfo.DEFAULT_MAX_REQUESTED_DIMENSION_VALUES;
import DimensionPresentation.LIST;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.image.io.ImageIOExt;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CustomDimensionsTest extends WMSTestSupport {
    private static final QName WATTEMP = new QName(MockData.DEFAULT_URI, "watertemp", MockData.DEFAULT_PREFIX);

    private static final String CAPABILITIES_REQUEST = "wms?request=getCapabilities&version=1.1.1";

    private static final String BBOX = "0,40,15,45";

    private static final String LAYERS = "gs:watertemp";

    @Test
    public void testCapabilitiesNoDimension() throws Exception {
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check layer is present with no dimension
        assertXpathEvaluatesTo("1", "count(//Layer[Name='gs:watertemp'])", dom);
        assertXpathEvaluatesTo("0", "count(//Layer/Dimension)", dom);
    }

    @Test
    public void testCapabilities() throws Exception {
        setupRasterDimension(CUSTOM_DIMENSION_NAME, LIST, null, null);
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//Layer/Dimension)", dom);
        // check we have the extent
        assertXpathEvaluatesTo(CUSTOM_DIMENSION_NAME, "//Layer/Extent/@name", dom);
        assertXpathEvaluatesTo("CustomDimValueA", "//Layer/Extent/@default", dom);
        assertXpathEvaluatesTo("CustomDimValueA,CustomDimValueB,CustomDimValueC", "//Layer/Extent", dom);
    }

    @Test
    public void testInvalidCustomCapabilities() throws Exception {
        setupRasterDimension(CUSTOM_DIMENSION_NAME, LIST, null, null);
        // poison with one dimension that does not exist, used to throw a
        // ConcurrentModificationException
        setupRasterDimension("foobar", LIST, null, null);
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//Layer/Dimension)", dom);
        // check we have the extent
        assertXpathEvaluatesTo(CUSTOM_DIMENSION_NAME, "//Layer/Extent/@name", dom);
        assertXpathEvaluatesTo("CustomDimValueA", "//Layer/Extent/@default", dom);
        assertXpathEvaluatesTo("CustomDimValueA,CustomDimValueB,CustomDimValueC", "//Layer/Extent", dom);
    }

    @Test
    public void testCapabilitiesUnits() throws Exception {
        setupRasterDimension(CUSTOM_DIMENSION_NAME, LIST, "nano meters", "nm");
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//Layer/Dimension)", dom);
        assertXpathEvaluatesTo("nano meters", "//Layer/Dimension/@units", dom);
        assertXpathEvaluatesTo("nm", "//Layer/Dimension/@unitSymbol", dom);
        // check we have the extent
        assertXpathEvaluatesTo(CUSTOM_DIMENSION_NAME, "//Layer/Extent/@name", dom);
        assertXpathEvaluatesTo("CustomDimValueA", "//Layer/Extent/@default", dom);
        assertXpathEvaluatesTo("CustomDimValueA,CustomDimValueB,CustomDimValueC", "//Layer/Extent", dom);
    }

    @Test
    public void testGetMap() throws Exception {
        ImageIOExt.allowNativeCodec("tif", ImageReaderSpi.class, false);
        setupRasterDimension(CUSTOM_DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get no data when requesting an incorrect value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CUSTOM_DIMENSION_NAME)) + "=bad_dimension_value"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertTrue(CustomDimensionsTest.isEmpty(image));
        // check that we get data when requesting a correct value for custom dimension
        response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=raster") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/tiff") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CUSTOM_DIMENSION_NAME)) + "=CustomDimValueB,CustomDimValueC,CustomDimValueA"));
        image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(CustomDimensionsTest.isEmpty(image));
        Assert.assertTrue("sample model bands", (3 <= (image.getSampleModel().getNumBands())));
    }

    @Test
    public void testCustomDimensionTooMany() throws Exception {
        GeoServer gs = getGeoServer();
        WMSInfo wms = gs.getService(WMSInfo.class);
        wms.setMaxRequestedDimensionValues(2);
        gs.save(wms);
        try {
            ImageIOExt.allowNativeCodec("tif", ImageReaderSpi.class, false);
            // check that we get data when requesting a correct value for custom dimension
            MockHttpServletResponse response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=raster") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/tiff") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CUSTOM_DIMENSION_NAME)) + "=CustomDimValueB,CustomDimValueC,CustomDimValueA"));
            Assert.assertEquals("text/xml", response.getContentType());
            Document dom = dom(response, true);
            // print(dom);
            String text = checkLegacyException(dom, org.geoserver.platform.ServiceException.INVALID_PARAMETER_VALUE, ("DIM_" + (CUSTOM_DIMENSION_NAME)));
            MatcherAssert.assertThat(text, CoreMatchers.containsString("More than 2 dimension values"));
        } finally {
            wms.setMaxRequestedDimensionValues(DEFAULT_MAX_REQUESTED_DIMENSION_VALUES);
            gs.save(wms);
        }
    }

    @Test
    public void testGetMapCaseInsesitiveKey() throws Exception {
        setupRasterDimension(CUSTOM_DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get data when requesting a correct value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CUSTOM_DIMENSION_NAME.toLowerCase())) + "=CustomDimValueB"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(CustomDimensionsTest.isEmpty(image));
    }
}

