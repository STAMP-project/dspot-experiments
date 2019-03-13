/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;


import java.io.IOException;
import java.util.List;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.expression.Function;


public class RasterSymbolizerVisitorTest {
    @Test
    public void testRasterRenderingTransformation() throws IOException {
        Style style = parseStyle("CropTransform.sld");
        RasterSymbolizerVisitor visitor = new RasterSymbolizerVisitor(1000, null);
        style.accept(visitor);
        List<RasterSymbolizer> symbolizers = visitor.getRasterSymbolizers();
        Assert.assertEquals(1, symbolizers.size());
        Function tx = ((Function) (visitor.getRasterRenderingTransformation()));
        Assert.assertNotNull(tx);
        Assert.assertEquals("ras:CropCoverage", tx.getName());
    }

    @Test
    public void testRasterToVectorTransformation() throws IOException {
        Style style = parseStyle("ContourTransform.sld");
        RasterSymbolizerVisitor visitor = new RasterSymbolizerVisitor(1000, null);
        style.accept(visitor);
        List<RasterSymbolizer> symbolizers = visitor.getRasterSymbolizers();
        Assert.assertEquals(0, symbolizers.size());
        Function tx = ((Function) (visitor.getRasterRenderingTransformation()));
        Assert.assertNull(tx);
    }

    @Test
    public void testVectorToRasterRenderingTransformation() throws IOException {
        Style style = parseStyle("HeatmapTransform.sld");
        RasterSymbolizerVisitor visitor = new RasterSymbolizerVisitor(1000, null);
        style.accept(visitor);
        List<RasterSymbolizer> symbolizers = visitor.getRasterSymbolizers();
        Assert.assertEquals(1, symbolizers.size());
        Function tx = ((Function) (visitor.getRasterRenderingTransformation()));
        Assert.assertNotNull(tx);
        Assert.assertEquals("vec:Heatmap", tx.getName());
    }
}

