/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import DimensionPresentation.LIST;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 * Dynaminc dimensions differ for custom ones in that the reader does not know about the until it's
 * generated, while simple custom dimensions are hard coded and known to the Format class as well
 *
 * @author Andrea Aime - GeoSolutions
 */
public class DynamicDimensionsTest extends WMSTestSupport {
    private static final QName WATTEMP = new QName(MockData.DEFAULT_URI, "watertemp", MockData.DEFAULT_PREFIX);

    private static final String DIMENSION_NAME = "WAVELENGTH";

    private static final String CAPABILITIES_REQUEST = "wms?request=getCapabilities&version=1.1.1";

    private static final String BBOX = "0,40,15,45";

    private static final String LAYERS = "gs:watertemp";

    @Test
    public void testCapabilitiesNoDimension() throws Exception {
        Document dom = dom(get(DynamicDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check layer is present with no dimension
        assertXpathEvaluatesTo("1", "count(//Layer[Name='gs:watertemp'])", dom);
        assertXpathEvaluatesTo("0", "count(//Layer/Dimension)", dom);
    }

    @Test
    public void testCapabilities() throws Exception {
        setupRasterDimension(DynamicDimensionsTest.DIMENSION_NAME, LIST, null, null);
        Document dom = dom(get(DynamicDimensionsTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//Layer/Dimension)", dom);
        // check we have the extent
        assertXpathEvaluatesTo(DynamicDimensionsTest.DIMENSION_NAME, "//Layer/Extent/@name", dom);
        assertXpathEvaluatesTo("020", "//Layer/Extent/@default", dom);
        assertXpathEvaluatesTo("020,100", "//Layer/Extent", dom);
    }

    @Test
    public void testGetMapInvalidValue() throws Exception {
        setupRasterDimension(DynamicDimensionsTest.DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get no data when requesting an incorrect value for custom dimension
        MockHttpServletResponse response = getAsServletResponse((((((((((((("wms?bbox=" + (DynamicDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (DynamicDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&DIM_") + (DynamicDimensionsTest.DIMENSION_NAME)) + "=bad_dimension_value"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertTrue(DynamicDimensionsTest.isEmpty(image));
    }

    @Test
    public void testGetMapDefaultValue() throws Exception {
        setupRasterDimension(DynamicDimensionsTest.DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get data when requesting a correct value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((("wms?bbox=" + (DynamicDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (DynamicDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(DynamicDimensionsTest.isEmpty(image));
        // this pixel is red-ish with the default value, 020, but black with 100
        assertPixel(image, 337, 177, new Color(255, 197, 197));
    }

    @Test
    public void testGetMapValidValue() throws Exception {
        setupRasterDimension(DynamicDimensionsTest.DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get data when requesting a correct value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((((((("wms?bbox=" + (DynamicDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (DynamicDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&bgColor=0x0000FF") + "&DIM_") + (DynamicDimensionsTest.DIMENSION_NAME)) + "=100"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(DynamicDimensionsTest.isEmpty(image));
        // this pixel is red-ish with the default value, 020, but blue with 100
        assertPixel(image, 337, 177, Color.BLUE);
    }

    @Test
    public void testGetMapCaseInsensitiveKey() throws Exception {
        setupRasterDimension(DynamicDimensionsTest.DIMENSION_NAME, LIST, "nano meters", "nm");
        // check that we get data when requesting a correct value for custom dimension
        MockHttpServletResponse response = getAsServletResponse(((((((((((("wms?bbox=" + (DynamicDimensionsTest.BBOX)) + "&styles=") + "&layers=") + (DynamicDimensionsTest.LAYERS)) + "&Format=image/png") + "&request=GetMap") + "&width=550") + "&height=250") + "&srs=EPSG:4326") + "&bgColor=0x0000FF") + "&DIM_wAvElEnGtH=100"));
        BufferedImage image = ImageIO.read(getBinaryInputStream(response));
        Assert.assertFalse(DynamicDimensionsTest.isEmpty(image));
        // this pixel is red-ish with the default value, 020, but blue with 100
        assertPixel(image, 337, 177, Color.BLUE);
    }
}

