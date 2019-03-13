/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import DimensionPresentation.LIST;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.geoserver.catalog.testreader.CustomFormat;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.image.io.ImageIOExt;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CustomDimensionsTest extends WMSTestSupport {
    private static final QName WATTEMP = new QName(MockData.DEFAULT_URI, "watertemp", MockData.DEFAULT_PREFIX);

    private static final String CAPABILITIES_REQUEST = "wms?request=getCapabilities&version=1.3.0";

    private static final String DIMENSION_NAME = CustomFormat.CUSTOM_DIMENSION_NAME;

    private static final String BBOX = "0,40,15,45";

    private static final String LAYERS = "gs:watertemp";

    @Test
    public void testCapabilitiesNoDimension() throws Exception {
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check layer is present with no dimension
        assertXpathEvaluatesTo("1", "count(//wms:Layer[wms:Name='gs:watertemp'])", dom);
        assertXpathEvaluatesTo("0", "count(//wms:Layer/wms:Dimension)", dom);
    }

    @Test
    public void testCapabilities() throws Exception {
        setupRasterDimension(CustomDimensionsTest.DIMENSION_NAME, LIST, null, null);
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo(CustomDimensionsTest.DIMENSION_NAME, "//wms:Layer/wms:Dimension/@name", dom);
        // check we have the dimension values
        assertXpathEvaluatesTo("CustomDimValueA,CustomDimValueB,CustomDimValueC", "//wms:Layer/wms:Dimension", dom);
        assertXpathEvaluatesTo("CustomDimValueA", "//wms:Layer/wms:Dimension/@default", dom);
    }

    @Test
    public void testCapabilitiesUnits() throws Exception {
        setupRasterDimension(CustomDimensionsTest.DIMENSION_NAME, LIST, "nano meters", "nm");
        Document dom = dom(get(CustomDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo(CustomDimensionsTest.DIMENSION_NAME, "//wms:Layer/wms:Dimension/@name", dom);
        // check we have the dimension values
        assertXpathEvaluatesTo("CustomDimValueA,CustomDimValueB,CustomDimValueC", "//wms:Layer/wms:Dimension", dom);
        assertXpathEvaluatesTo("nano meters", "//wms:Layer/wms:Dimension/@units", dom);
        assertXpathEvaluatesTo("nm", "//wms:Layer/wms:Dimension/@unitSymbol", dom);
    }

    @Test
    public void testGetMap() throws Exception {
        ImageIOExt.allowNativeCodec("tif", ImageReaderSpi.class, false);
        setupRasterDimension(CustomDimensionsTest.DIMENSION_NAME, LIST, null, null);
        // check that we get no data when requesting an incorrect value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CustomDimensionsTest.DIMENSION_NAME)) + "=bad_dimension_value"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertTrue(CustomDimensionsTest.isEmpty(image));
        // check that we get data when requesting a correct value for custom dimension
        response = getAsServletResponse(((((((((((((("wms?bbox=" + (CustomDimensionsTest.BBOX)) + "&styles=raster") + "&layers=") + (CustomDimensionsTest.LAYERS)) + "&Format=image/tiff") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&VALIDATESCHEMA=true") + "&DIM_") + (CustomDimensionsTest.DIMENSION_NAME)) + "=CustomDimValueB,CustomDimValueC,CustomDimValueA"));
        image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(CustomDimensionsTest.isEmpty(image));
        Assert.assertTrue("sample model bands", (3 <= (image.getSampleModel().getNumBands())));
    }
}

