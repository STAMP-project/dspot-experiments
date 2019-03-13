/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.utfgrid;


import BlendingMode.MULTIPLY;
import FeatureTypeStyle.COMPOSITE;
import FeatureTypeStyle.COMPOSITE_BASE;
import java.awt.Color;
import java.util.Map;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;


public class UTFGridStyleVisitorTest {
    UTFGridStyleVisitor visitor;

    StyleBuilder sb = new StyleBuilder();

    FilterFactory2 ff = sb.getFilterFactory();

    private UTFGridColorFunction colorFunction;

    @Test
    public void testOnlyTextSymbolizer() {
        Style style = sb.createStyle(sb.createTextSymbolizer(Color.BLACK, sb.createFont("Serif", 12), "name"));
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertEquals(0, copy.featureTypeStyles().size());
    }

    @Test
    public void testFill() {
        PolygonSymbolizer polygonSymbolizer = sb.createPolygonSymbolizer(Color.BLACK, Color.BLACK, 3);
        polygonSymbolizer.getFill().setOpacity(ff.literal(0.5));
        polygonSymbolizer.getStroke().setOpacity(ff.literal(0.5));
        Style style = sb.createStyle(polygonSymbolizer);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        PolygonSymbolizer ls = ((PolygonSymbolizer) (copy.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        Stroke stroke = ls.getStroke();
        Assert.assertEquals(colorFunction, stroke.getColor());
        Assert.assertEquals(Integer.valueOf(3), stroke.getWidth().evaluate(null, Integer.class));
        Assert.assertNull(stroke.dashArray());
        Assert.assertEquals(Integer.valueOf(1), stroke.getOpacity().evaluate(null, Integer.class));
        Fill fill = ls.getFill();
        Assert.assertEquals(colorFunction, fill.getColor());
        Assert.assertEquals(Integer.valueOf(1), fill.getOpacity().evaluate(null, Integer.class));
    }

    @Test
    public void testGraphicStrokeFill() {
        Graphic graphic = sb.createGraphic(null, sb.createMark("square"), null);
        graphic.setSize(ff.literal(8));
        Stroke stroke = sb.createStroke();
        stroke.setGraphicStroke(graphic);
        stroke.setOpacity(ff.literal(0.5));
        Fill fill = sb.createFill(null, null, 0.5, graphic);
        PolygonSymbolizer polygonSymbolizer = sb.createPolygonSymbolizer(stroke, fill);
        Style style = sb.createStyle(polygonSymbolizer);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        PolygonSymbolizer ls = ((PolygonSymbolizer) (copy.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        Stroke strokeCopy = ls.getStroke();
        Assert.assertEquals(colorFunction, strokeCopy.getColor());
        Assert.assertEquals(Integer.valueOf(8), strokeCopy.getWidth().evaluate(null, Integer.class));
        Assert.assertNull(strokeCopy.dashArray());
        Assert.assertEquals(Integer.valueOf(1), strokeCopy.getOpacity().evaluate(null, Integer.class));
        Fill fillCopy = ls.getFill();
        Assert.assertEquals(colorFunction, fillCopy.getColor());
        Assert.assertEquals(Integer.valueOf(1), fillCopy.getOpacity().evaluate(null, Integer.class));
    }

    @Test
    public void testLine() {
        Style style = sb.createStyle(sb.createLineSymbolizer(sb.createStroke(Color.BLACK, 3)));
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        LineSymbolizer ls = ((LineSymbolizer) (copy.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        Stroke stroke = ls.getStroke();
        Assert.assertEquals(colorFunction, stroke.getColor());
        Assert.assertEquals(Integer.valueOf(3), stroke.getWidth().evaluate(null, Integer.class));
        Assert.assertNull(stroke.dashArray());
    }

    @Test
    public void testStrokedLine() {
        Style style = sb.createStyle(sb.createLineSymbolizer(sb.createStroke(Color.BLACK, 1, new float[]{ 8, 8 })));
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        LineSymbolizer ls = ((LineSymbolizer) (copy.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        Stroke stroke = ls.getStroke();
        Assert.assertEquals(colorFunction, stroke.getColor());
        Assert.assertEquals(Integer.valueOf(1), stroke.getWidth().evaluate(null, Integer.class));
        Assert.assertEquals(0, stroke.dashArray().size());
    }

    @Test
    public void testGraphicStroke() {
        Graphic graphic = sb.createGraphic(null, sb.createMark("square"), null);
        graphic.setSize(ff.literal(8));
        Stroke stroke = sb.createStroke();
        stroke.setGraphicStroke(graphic);
        Style style = sb.createStyle(sb.createLineSymbolizer(stroke));
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        LineSymbolizer ls = ((LineSymbolizer) (copy.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0)));
        stroke = ls.getStroke();
        Assert.assertEquals(colorFunction, stroke.getColor());
        Assert.assertEquals(Integer.valueOf(8), stroke.getWidth().evaluate(null, Integer.class));
        Assert.assertNull(stroke.dashArray());
    }

    /**
     * Blending alters colors (which are ids for UTFGrid), remove it
     */
    @Test
    public void testBlendingRemoval() {
        FeatureTypeStyle fts = sb.createFeatureTypeStyle(sb.createLineSymbolizer());
        fts.getOptions().put(COMPOSITE, MULTIPLY.getName());
        fts.getOptions().put(COMPOSITE_BASE, "true");
        Style style = sb.createStyle();
        style.featureTypeStyles().add(fts);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Map<String, String> options = copy.featureTypeStyles().get(0).getOptions();
        Assert.assertNull(options.get(COMPOSITE));
        Assert.assertNull(options.get(COMPOSITE_BASE));
    }

    /**
     * Alpha composite does not alter colors (which are ids for UTFGrid), we can preserve it
     */
    @Test
    public void testAlphaCompositePreserved() {
        FeatureTypeStyle fts = sb.createFeatureTypeStyle(sb.createLineSymbolizer());
        fts.getOptions().put(COMPOSITE, "source-over");
        fts.getOptions().put(COMPOSITE_BASE, "true");
        Style style = sb.createStyle();
        style.featureTypeStyles().add(fts);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Map<String, String> options = copy.featureTypeStyles().get(0).getOptions();
        Assert.assertEquals("source-over", options.get(COMPOSITE));
        Assert.assertEquals("true", options.get(COMPOSITE_BASE));
    }

    @Test
    public void testRasterToVectorTransform() {
        FeatureTypeStyle fts = sb.createFeatureTypeStyle(sb.createLineSymbolizer());
        Function data = ff.function("parameter", ff.literal("data"));
        Function levels = ff.function("parameter", ff.literal("levels"), ff.literal("1100"), ff.literal("1200"));
        Function tx = ff.function("ras:Contour", data, levels);
        fts.setTransformation(tx);
        fts.getOptions().put(COMPOSITE_BASE, "true");
        Style style = sb.createStyle();
        style.featureTypeStyles().add(fts);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertTrue(visitor.hasTransformations());
        Assert.assertTrue(visitor.hasVectorTransformations());
        Assert.assertThat(copy.featureTypeStyles().get(0).getTransformation(), CoreMatchers.instanceOf(Function.class));
        Function txCopy = ((Function) (copy.featureTypeStyles().get(0).getTransformation()));
        Assert.assertEquals("ras:Contour", txCopy.getName());
    }

    @Test
    public void testVectorToRasterTransform() {
        FeatureTypeStyle fts = sb.createFeatureTypeStyle(sb.createLineSymbolizer());
        Function data = ff.function("parameter", ff.literal("data"));
        Function tx = ff.function("vec:Heatmap", data);
        fts.setTransformation(tx);
        fts.getOptions().put(COMPOSITE_BASE, "true");
        Style style = sb.createStyle();
        style.featureTypeStyles().add(fts);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertTrue(visitor.hasTransformations());
        Assert.assertFalse(visitor.hasVectorTransformations());
        Assert.assertEquals(0, copy.featureTypeStyles().size());
    }
}

