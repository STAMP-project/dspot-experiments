/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.legendgraphic;


import java.net.URL;
import java.util.Locale;
import java.util.Map;
import org.geoserver.platform.ServiceException;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.feature.NameImpl;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class GetLegendGraphicKvpReaderTest extends WMSTestSupport {
    /**
     * request reader to test against, initialized by default with all parameters from <code>
     * requiredParameters</code> and <code>optionalParameters</code>
     */
    GetLegendGraphicKvpReader requestReader;

    /**
     * test values for required parameters
     */
    Map<String, String> requiredParameters;

    /**
     * test values for optional parameters
     */
    Map<String, String> optionalParameters;

    /**
     * both required and optional parameters joint up
     */
    Map<String, String> allParameters;

    /**
     * mock request
     */
    MockHttpServletRequest httpRequest;

    /**
     * mock config object
     */
    WMS wms;

    /**
     * This test ensures that when a SLD parameter has been passed that refers to a SLD document
     * with multiple styles, the required one is choosed based on the LAYER parameter.
     *
     * <p>This is the case where a remote SLD document is used in "library" mode.
     */
    @Test
    public void testRemoteSLDMultipleStyles() throws Exception {
        final URL remoteSldUrl = getClass().getResource("MultipleStyles.sld");
        this.allParameters.put("SLD", remoteSldUrl.toExternalForm());
        this.allParameters.put("LAYER", "cite:Ponds");
        this.allParameters.put("STYLE", "Ponds");
        GetLegendGraphicRequest request = requestReader.read(new GetLegendGraphicRequest(), allParameters, allParameters);
        // the style names Ponds is declared in third position on the sld doc
        Style selectedStyle = request.getStyles().get(0);
        Assert.assertNotNull(selectedStyle);
        Assert.assertEquals("Ponds", selectedStyle.getName());
        this.allParameters.put("LAYER", "cite:Lakes");
        this.allParameters.put("STYLE", "Lakes");
        request = requestReader.read(new GetLegendGraphicRequest(), allParameters, allParameters);
        // the style names Ponds is declared in third position on the sld doc
        selectedStyle = request.getStyles().get(0);
        Assert.assertNotNull(selectedStyle);
        Assert.assertEquals("Lakes", selectedStyle.getName());
    }

    @Test
    public void testMissingLayerParameter() throws Exception {
        requiredParameters.remove("LAYER");
        try {
            requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
            Assert.fail("Expected ServiceException");
        } catch (ServiceException e) {
            Assert.assertEquals("LayerNotDefined", e.getCode());
        }
    }

    @Test
    public void testMissingFormatParameter() throws Exception {
        requiredParameters.remove("FORMAT");
        try {
            requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
            Assert.fail("Expected ServiceException");
        } catch (ServiceException e) {
            Assert.assertEquals("MissingFormat", e.getCode());
        }
    }

    @Test
    public void testStrictParameter() throws Exception {
        GetLegendGraphicRequest request;
        // default value
        request = requestReader.read(new GetLegendGraphicRequest(), allParameters, allParameters);
        Assert.assertTrue(request.isStrict());
        allParameters.put("STRICT", "false");
        allParameters.remove("LAYER");
        request = requestReader.read(new GetLegendGraphicRequest(), allParameters, allParameters);
        Assert.assertFalse(request.isStrict());
    }

    @Test
    public void testLayerGroup() throws Exception {
        GetLegendGraphicRequest request;
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertTrue(((request.getLayers().size()) == 1));
        requiredParameters.put("LAYER", WMSTestSupport.NATURE_GROUP);
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertTrue(((request.getLayers().size()) > 1));
    }

    @Test
    public void testLanguage() throws Exception {
        GetLegendGraphicRequest request;
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertNull(request.getLocale());
        request = requestReader.read(new GetLegendGraphicRequest(), allParameters, allParameters);
        Assert.assertEquals(Locale.ENGLISH, request.getLocale());
    }

    @Test
    public void testStylesForLayerGroup() throws Exception {
        GetLegendGraphicRequest request;
        requiredParameters.put("LAYER", WMSTestSupport.NATURE_GROUP);
        requiredParameters.put("STYLE", "style1,style2");
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertTrue(((request.getStyles().size()) == 2));
    }

    @Test
    public void testRulesForLayerGroup() throws Exception {
        GetLegendGraphicRequest request;
        requiredParameters.put("LAYER", WMSTestSupport.NATURE_GROUP);
        requiredParameters.put("RULE", "rule1,rule2");
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertTrue(((request.getRules().size()) == 2));
    }

    @Test
    public void testLabelsForLayerGroup() throws Exception {
        GetLegendGraphicRequest request;
        requiredParameters.put("LAYER", WMSTestSupport.NATURE_GROUP);
        request = requestReader.read(new GetLegendGraphicRequest(), requiredParameters, requiredParameters);
        Assert.assertNotNull(request.getTitle(new NameImpl("http://www.opengis.net/cite", "Lakes")));
    }
}

