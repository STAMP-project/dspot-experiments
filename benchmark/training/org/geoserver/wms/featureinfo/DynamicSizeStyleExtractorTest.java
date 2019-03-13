/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.featureinfo;


import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Assert;
import org.junit.Test;


public class DynamicSizeStyleExtractorTest {
    StyleBuilder sb = new StyleBuilder();

    private Rule staticPolygonRule;

    private Rule staticLineRule;

    private DynamicSizeStyleExtractor visitor;

    @Test
    public void testOneFtsFullyStatic() {
        Style style = sb.createStyle();
        FeatureTypeStyle fts = sb.createFeatureTypeStyle("Feature", staticPolygonRule);
        fts.rules().add(staticLineRule);
        style.featureTypeStyles().add(fts);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertNull(copy);
    }

    @Test
    public void testTwoFtsFullyStatic() {
        Style style = sb.createStyle();
        FeatureTypeStyle fts1 = sb.createFeatureTypeStyle("Feature", staticPolygonRule);
        FeatureTypeStyle fts2 = sb.createFeatureTypeStyle("Feature", staticLineRule);
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertNull(copy);
    }

    @Test
    public void testMixDynamicStroke() {
        Style style = sb.createStyle();
        FeatureTypeStyle fts1 = sb.createFeatureTypeStyle("Feature", staticPolygonRule);
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setWidth(sb.getFilterFactory().property("myAttribute"));
        FeatureTypeStyle fts2 = sb.createFeatureTypeStyle(ls);
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);
        checkSingleSymbolizer(style, ls);
    }

    @Test
    public void testMultipleSymbolizers() {
        Style style = sb.createStyle();
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setWidth(sb.getFilterFactory().property("myAttribute"));
        FeatureTypeStyle fts = sb.createFeatureTypeStyle(sb.createPolygonSymbolizer());
        style.featureTypeStyles().add(fts);
        fts.rules().get(0).symbolizers().add(ls);
        fts.rules().get(0).symbolizers().add(sb.createLineSymbolizer());
        checkSingleSymbolizer(style, ls);
    }

    @Test
    public void testMixDynamicGraphicStroke() {
        Style style = sb.createStyle();
        FeatureTypeStyle fts1 = sb.createFeatureTypeStyle("Feature", staticPolygonRule);
        Graphic graphic = sb.createGraphic(null, sb.createMark("square"), null);
        graphic.setSize(sb.getFilterFactory().property("myAttribute"));
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setGraphicStroke(graphic);
        FeatureTypeStyle fts2 = sb.createFeatureTypeStyle(ls);
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);
        checkSingleSymbolizer(style, ls);
    }

    @Test
    public void testDynamicSymbolizerStrokeLineSymbolizer() {
        ExternalGraphic dynamicSymbolizer = sb.createExternalGraphic("file://./${myAttribute}.jpeg", "image/jpeg");
        Graphic graphic = sb.createGraphic(dynamicSymbolizer, null, null);
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setGraphicStroke(graphic);
        Style style = sb.createStyle(ls);
        checkSingleSymbolizer(style, ls);
    }

    @Test
    public void testStaticGraphicLineSymbolizer() {
        ExternalGraphic dynamicSymbolizer = sb.createExternalGraphic("file://./hello.jpeg", "image/jpeg");
        Graphic graphic = sb.createGraphic(dynamicSymbolizer, null, null);
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setGraphicStroke(graphic);
        Style style = sb.createStyle(ls);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertNull(copy);
    }

    @Test
    public void testDynamicStrokeInGraphicMark() {
        Stroke markStroke = sb.createStroke();
        markStroke.setWidth(sb.getFilterFactory().property("myAttribute"));
        Mark mark = sb.createMark("square");
        mark.setStroke(markStroke);
        Graphic graphic = sb.createGraphic(null, mark, null);
        LineSymbolizer ls = sb.createLineSymbolizer();
        ls.getStroke().setGraphicStroke(graphic);
        Style style = sb.createStyle(ls);
        checkSingleSymbolizer(style, ls);
    }

    // this one should fail now??
    @Test
    public void testDynamicStrokeInGraphicFill() {
        Stroke markStroke = sb.createStroke();
        markStroke.setWidth(sb.getFilterFactory().property("myAttribute"));
        Mark mark = sb.createMark("square");
        mark.setStroke(markStroke);
        Graphic graphic = sb.createGraphic(null, mark, null);
        PolygonSymbolizer ps = sb.createPolygonSymbolizer();
        ps.getFill().setGraphicFill(graphic);
        Style style = sb.createStyle(ps);
        style.accept(visitor);
        Style copy = ((Style) (visitor.getCopy()));
        Assert.assertNull(copy);
    }
}

