/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geotools.process.raster;


import it.geosolutions.imageio.utilities.ImageIOUtilities;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wms.map.GetMapKvpRequestReader;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Function;

import static FilterFunction_svgColorMap.LOG_SAMPLING_DEFAULT;


public class DynamicColorMapTest extends GeoServerSystemTestSupport {
    private static final String COVERAGE_NAME = "watertemp_dynamic";

    private static final double TOLERANCE = 0.01;

    GetMapKvpRequestReader requestReader;

    protected static XpathEngine xp;

    @Test
    public void testGridCoverageStats() throws Exception {
        // check the coverage is actually there
        Catalog catalog = getCatalog();
        CoverageStoreInfo storeInfo = catalog.getCoverageStoreByName(DynamicColorMapTest.COVERAGE_NAME);
        Assert.assertNotNull(storeInfo);
        CoverageInfo ci = catalog.getCoverageByName(DynamicColorMapTest.COVERAGE_NAME);
        Assert.assertNotNull(ci);
        Assert.assertEquals(storeInfo, ci.getStore());
        // Test on the GridCoverageStats
        FilterFunction_gridCoverageStats funcStat = new FilterFunction_gridCoverageStats();
        GridCoverageReader reader = ci.getGridCoverageReader(null, null);
        GridCoverage2D gridCoverage = ((GridCoverage2D) (reader.read(null)));
        double min = ((Double) (funcStat.evaluate(gridCoverage, "minimum")));
        double max = ((Double) (funcStat.evaluate(gridCoverage, "maximum")));
        Assert.assertEquals(min, 13.1369, DynamicColorMapTest.TOLERANCE);
        Assert.assertEquals(max, 20.665, DynamicColorMapTest.TOLERANCE);
        ImageIOUtilities.disposeImage(gridCoverage.getRenderedImage());
    }

    @Test
    public void testGridBandStats() throws Exception {
        // check the coverage is actually there
        Catalog catalog = getCatalog();
        CoverageStoreInfo storeInfo = catalog.getCoverageStoreByName(DynamicColorMapTest.COVERAGE_NAME);
        Assert.assertNotNull(storeInfo);
        CoverageInfo ci = catalog.getCoverageByName(DynamicColorMapTest.COVERAGE_NAME);
        Assert.assertNotNull(ci);
        Assert.assertEquals(storeInfo, ci.getStore());
        // Test on the GridCoverageStats
        final FilterFactory ff = CommonFactoryFinder.getFilterFactory();
        Function minStat = ff.function("bandStats", ff.literal(0), ff.literal("minimum"));
        Function maxStat = ff.function("bandStats", ff.literal(0), ff.literal("maximum"));
        GridCoverageReader reader = ci.getGridCoverageReader(null, null);
        GridCoverage2D gridCoverage = ((GridCoverage2D) (reader.read(null)));
        double min = ((Double) (minStat.evaluate(gridCoverage)));
        double max = ((Double) (maxStat.evaluate(gridCoverage)));
        Assert.assertEquals(min, 0, DynamicColorMapTest.TOLERANCE);
        Assert.assertEquals(max, 0.5, DynamicColorMapTest.TOLERANCE);
        ImageIOUtilities.disposeImage(gridCoverage.getRenderedImage());
    }

    @Test
    public void testSvgColorMapFilterFunctionRGB() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("rgb(0,0,255);rgb(0,255,0);rgb(255,0,0)", 10, 100, null, null, false, FilterFunction_svgColorMap.MAX_PALETTE_COLORS)));
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        check(entries);
    }

    @Test
    public void testSvgColorMapFilterFunctionHEX() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, null, null, false, FilterFunction_svgColorMap.MAX_PALETTE_COLORS)));
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        check(entries);
    }

    @Test
    public void testSvgColorMapFilterFunctionRGBWithExpression() throws Exception {
        FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        checkFunction(ff.function("colormap", ff.literal("rgb(0,0,255);rgb(0,255,0);rgb(255,0,0)"), ff.literal(10), ff.literal(100)));
        checkFunction(ff.function("colormap", ff.literal("rgb(0,0,255);rgb(0,255,0);rgb(255,0,0)"), ff.literal(10), ff.literal(100), ff.literal(null), ff.literal(null), ff.literal("false"), ff.literal(FilterFunction_svgColorMap.MAX_PALETTE_COLORS)));
    }

    @Test
    public void testBeforeAfterColor() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, "#FFFFFF", "#000000", false, FilterFunction_svgColorMap.MAX_PALETTE_COLORS)));
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        assertColorMapEntry(entries[0], "#FFFFFF", 1.0, 9.99);
        assertColorMapEntry(entries[1], "#0000FF", 1.0, 10.0);
        assertColorMapEntry(entries[2], "#00FF00", 1.0, 55.0);
        assertColorMapEntry(entries[3], "#FF0000", 1.0, 100.0);
        assertColorMapEntry(entries[4], "#000000", 1.0, 100.0);
    }

    @Test
    public void testLogarithmic() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, null, null, true, FilterFunction_svgColorMap.MAX_PALETTE_COLORS)));
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        Assert.assertEquals(((LOG_SAMPLING_DEFAULT) + 2), entries.length);
        // first and last are transparent
        Assert.assertEquals(0, entries[0].getOpacity().evaluate(null, Double.class), DynamicColorMapTest.TOLERANCE);
        Assert.assertEquals(0, entries[((LOG_SAMPLING_DEFAULT) + 1)].getOpacity().evaluate(null, Double.class), DynamicColorMapTest.TOLERANCE);
        // check the logaritmic progression
        double logMin = Math.log(10);
        double logMax = Math.log(100);
        double step = (logMax - logMin) / (LOG_SAMPLING_DEFAULT);
        for (int i = 0; i < ((LOG_SAMPLING_DEFAULT) - 1); i++) {
            final double v = logMin + (step * i);
            double expected = Math.exp(v);
            Assert.assertEquals(("Failed at " + i), expected, entries[(i + 1)].getQuantity().evaluate(null, Double.class), DynamicColorMapTest.TOLERANCE);
        }
        Assert.assertEquals(100, entries[LOG_SAMPLING_DEFAULT].getQuantity().evaluate(null, Double.class), DynamicColorMapTest.TOLERANCE);
    }

    @Test
    public void testOneColor() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, null, null, false, 1)));
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        Assert.assertEquals(3, entries.length);
        assertColorMapEntry(entries[0], "#000000", 0.0, 10.0);
        assertColorMapEntry(entries[1], "#FF0000", 1.0, 100.0);
        assertColorMapEntry(entries[2], "#000000", 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testTwoColors() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, null, null, false, 2)));
        // logColorMap(colorMap);
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        Assert.assertEquals(4, entries.length);
        assertColorMapEntry(entries[0], "#000000", 0.0, 10.0);
        assertColorMapEntry(entries[1], "#0000FF", 1.0, 55.0);
        assertColorMapEntry(entries[2], "#FF0000", 1.0, 100.0);
        assertColorMapEntry(entries[3], "#000000", 0.0, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testThreeColors() throws Exception {
        final FilterFunction_svgColorMap func = new FilterFunction_svgColorMap();
        final ColorMap colorMap = ((ColorMap) (func.evaluate("#0000FF;#00FF00;#FF0000", 10, 100, null, null, false, 3)));
        // logColorMap(colorMap);
        final ColorMapEntry[] entries = colorMap.getColorMapEntries();
        Assert.assertEquals(5, entries.length);
        assertColorMapEntry(entries[0], "#000000", 0.0, 10.0);
        assertColorMapEntry(entries[1], "#0000FF", 1.0, 40.0);
        assertColorMapEntry(entries[2], "#00A956", 1.0, 70.0);
        assertColorMapEntry(entries[3], "#FF0000", 1.0, 100.0);
        assertColorMapEntry(entries[4], "#000000", 0.0, Double.POSITIVE_INFINITY);
    }
}

