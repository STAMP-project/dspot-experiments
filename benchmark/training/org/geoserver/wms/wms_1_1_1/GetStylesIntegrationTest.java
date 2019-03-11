/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import java.io.InputStream;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDParser;
import org.junit.Assert;
import org.junit.Test;


public class GetStylesIntegrationTest extends WMSTestSupport {
    @Test
    public void testSimple() throws Exception {
        InputStream stream = get((("wms?service=WMS&version=1.1.1&&request=GetStyles&layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + "&sldver=1.0.0"));
        SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory(null));
        parser.setInput(stream);
        StyledLayerDescriptor sld = parser.parseSLD();
        Assert.assertEquals(1, sld.getStyledLayers().length);
        NamedLayer layer = ((NamedLayer) (sld.getStyledLayers()[0]));
        Assert.assertEquals(getLayerId(MockData.BASIC_POLYGONS), layer.getName());
        Assert.assertEquals(1, layer.styles().size());
        Style style = layer.styles().get(0);
        Assert.assertTrue(style.isDefault());
        Assert.assertEquals("BasicPolygons", style.getName());
    }

    @Test
    public void testGroup() throws Exception {
        InputStream stream = get("wms?service=WMS&version=1.1.1&request=GetStyles&layers=lakesGroup&sldver=1.0.0");
        SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory(null));
        parser.setInput(stream);
        StyledLayerDescriptor sld = parser.parseSLD();
        Assert.assertEquals(1, sld.getStyledLayers().length);
        NamedLayer layer = ((NamedLayer) (sld.getStyledLayers()[0]));
        Assert.assertEquals("lakesGroup", layer.getName());
        // groups have no style
        Assert.assertEquals(0, layer.styles().size());
    }

    @Test
    public void testMultiStyle() throws Exception {
        InputStream stream = get((("wms?service=WMS&version=1.1.1&request=GetStyles&layers=" + (getLayerId(MockData.LAKES))) + "&sldver=1.0.0"));
        SLDParser parser = new SLDParser(CommonFactoryFinder.getStyleFactory(null));
        parser.setInput(stream);
        StyledLayerDescriptor sld = parser.parseSLD();
        Assert.assertEquals(1, sld.getStyledLayers().length);
        NamedLayer layer = ((NamedLayer) (sld.getStyledLayers()[0]));
        Assert.assertEquals(getLayerId(MockData.LAKES), layer.getName());
        Assert.assertEquals(2, layer.styles().size());
        Style style = layer.styles().get(0);
        Assert.assertTrue(style.isDefault());
        Assert.assertEquals("Lakes", style.getName());
        style = layer.styles().get(1);
        Assert.assertFalse(style.isDefault());
        Assert.assertEquals("Forests", style.getName());
    }
}

