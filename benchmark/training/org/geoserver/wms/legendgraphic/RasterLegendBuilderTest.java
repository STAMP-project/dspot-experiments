/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.legendgraphic;


import ColorMap.TYPE_INTERVALS;
import ColorMap.TYPE_RAMP;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.legendgraphic.Cell.ColorMapEntryLegendBuilder;
import org.geotools.styling.ColorMap;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Assert;
import org.junit.Test;

import static LegendUtils.DEFAULT_BORDER_COLOR;


public class RasterLegendBuilderTest {
    GetLegendGraphicRequest request;

    @Test
    public void testRuleTextRampOneElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null }, new double[]{ 10 }, new Color[]{ Color.RED }, TYPE_RAMP);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(1, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("", firstRow.getRuleManager().text);
    }

    @Test
    public void testRuleTextRampTwoElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null }, new double[]{ 10, 100 }, new Color[]{ Color.RED, Color.BLUE }, TYPE_RAMP);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("10.0 >= x", firstRow.getRuleManager().text);
        ColorMapEntryLegendBuilder lastRow = rows.get(1);
        Assert.assertEquals("100.0 <= x", lastRow.getRuleManager().text);
    }

    @Test
    public void testRuleTextRampThreeElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null, null }, new double[]{ 10, 50, 100 }, new Color[]{ Color.RED, Color.WHITE, Color.BLUE }, TYPE_RAMP);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(3, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("10.0 >= x", firstRow.getRuleManager().text);
        ColorMapEntryLegendBuilder midRow = rows.get(1);
        Assert.assertEquals("50.0 = x", midRow.getRuleManager().text);
        ColorMapEntryLegendBuilder lastRow = rows.get(2);
        Assert.assertEquals("100.0 <= x", lastRow.getRuleManager().text);
    }

    @Test
    public void testRuleTextIntervalOneElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null }, new double[]{ 10 }, new Color[]{ Color.RED }, TYPE_INTERVALS);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(1, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("x < 10.0", firstRow.getRuleManager().text);
    }

    @Test
    public void testRuleTextIntervalsTwoElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null }, new double[]{ 10, 100 }, new Color[]{ Color.RED, Color.BLUE }, TYPE_INTERVALS);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("x < 10.0", firstRow.getRuleManager().text);
        ColorMapEntryLegendBuilder lastRow = rows.get(1);
        Assert.assertEquals("10.0 <= x < 100.0", lastRow.getRuleManager().text);
    }

    @Test
    public void testRuleTextIntervalsThreeElements() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null, null }, new double[]{ 10, 50, 100 }, new Color[]{ Color.RED, Color.WHITE, Color.BLUE }, TYPE_INTERVALS);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(3, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("x < 10.0", firstRow.getRuleManager().text);
        ColorMapEntryLegendBuilder midRow = rows.get(1);
        Assert.assertEquals("10.0 <= x < 50.0", midRow.getRuleManager().text);
        ColorMapEntryLegendBuilder lastRow = rows.get(2);
        Assert.assertEquals("50.0 <= x < 100.0", lastRow.getRuleManager().text);
    }

    @Test
    public void testInfiniteOnIntervals() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null, null }, new double[]{ Double.NEGATIVE_INFINITY, 50, Double.POSITIVE_INFINITY }, new Color[]{ Color.RED, Color.WHITE, Color.BLUE }, TYPE_INTERVALS);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals("x < 50.0", firstRow.getRuleManager().text);
        ColorMapEntryLegendBuilder midRow = rows.get(1);
        Assert.assertEquals("50.0 <= x", midRow.getRuleManager().text);
    }

    @Test
    public void testLegendBorderColour() {
        StyleBuilder sb = new StyleBuilder();
        ColorMap cmap = sb.createColorMap(new String[]{ null, null, null }, new double[]{ Double.NEGATIVE_INFINITY, 50, Double.POSITIVE_INFINITY }, new Color[]{ Color.RED, Color.WHITE, Color.BLUE }, TYPE_INTERVALS);
        Style style = sb.createStyle(sb.createRasterSymbolizer(cmap, 1));
        // Check default border colour
        Color colourToTest = DEFAULT_BORDER_COLOR;
        RasterLayerLegendHelper helper = new RasterLayerLegendHelper(request, style, null);
        List<ColorMapEntryLegendBuilder> rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        ColorMapEntryLegendBuilder firstRow = rows.get(0);
        Assert.assertEquals(colourToTest, firstRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, firstRow.getRuleManager().borderColor);
        ColorMapEntryLegendBuilder midRow = rows.get(1);
        Assert.assertEquals(colourToTest, midRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, midRow.getRuleManager().borderColor);
        // Change legend border colour to red
        Map<String, Object> legendOptions = new HashMap<String, Object>();
        colourToTest = Color.red;
        legendOptions.put("BORDERCOLOR", SLD.toHTMLColor(colourToTest));
        request.setLegendOptions(legendOptions);
        helper = new RasterLayerLegendHelper(request, style, null);
        rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        firstRow = rows.get(0);
        Assert.assertEquals(colourToTest, firstRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, firstRow.getRuleManager().borderColor);
        midRow = rows.get(1);
        Assert.assertEquals(colourToTest, midRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, midRow.getRuleManager().borderColor);
        // Change legend border colour to blue
        colourToTest = Color.blue;
        legendOptions.clear();
        legendOptions.put("borderColor", SLD.toHTMLColor(colourToTest));
        request.setLegendOptions(legendOptions);
        helper = new RasterLayerLegendHelper(request, style, null);
        rows = new java.util.ArrayList(helper.getcMapLegendCreator().getBodyRows());
        Assert.assertEquals(2, rows.size());
        firstRow = rows.get(0);
        Assert.assertEquals(colourToTest, firstRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, firstRow.getRuleManager().borderColor);
        midRow = rows.get(1);
        Assert.assertEquals(colourToTest, midRow.getColorManager().borderColor);
        Assert.assertEquals(colourToTest, midRow.getRuleManager().borderColor);
    }
}

