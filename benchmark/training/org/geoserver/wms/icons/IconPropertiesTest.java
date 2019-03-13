/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.icons;


import Expression.NIL;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Stroke;


public class IconPropertiesTest extends IconTestSupport {
    @Test
    public void testSimpleStyleEncodesNoProperties() {
        final Style simple = styleFromRules(catchAllRule(grayCircle()));
        Assert.assertEquals("0.0.0=", encode(simple, IconTestSupport.fieldIs1));
    }

    @Test
    public void testWorkspacedStyleEncodesNoProperties() {
        final Style simple = styleFromRules(catchAllRule(grayCircle()));
        Assert.assertEquals("0.0.0=", encode("workspace", simple, IconTestSupport.fieldIs1));
    }

    @Test
    public void testFilters() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Rule a = rule(toFilter("field = 1"), symbolizer);
        final Rule b = rule(toFilter("field = 2"), symbolizer);
        Style style = styleFromRules(a, b);
        Assert.assertEquals("0.0.0=", encode(style, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.1.0=", encode(style, IconTestSupport.fieldIs2));
    }

    @Test
    public void testMultipleSymbolizers() {
        final PointSymbolizer symbolizer = grayCircle();
        final Rule a = catchAllRule(symbolizer, symbolizer);
        final Style style = styleFromRules(a);
        Assert.assertEquals("0.0.0=&0.0.1=", encode(style, IconTestSupport.fieldIs1));
    }

    @Test
    public void testMultipleFeatureTypeStyle() {
        final PointSymbolizer symbolizer = grayCircle();
        final Style s = style(featureTypeStyle(catchAllRule(symbolizer)), featureTypeStyle(catchAllRule(symbolizer)));
        Assert.assertEquals("0.0.0=&1.0.0=", encode(s, IconTestSupport.fieldIs1));
    }

    @Test
    public void testElseFilter() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Style style = styleFromRules(rule(toFilter("field = 1"), symbolizer), elseRule(symbolizer));
        Assert.assertEquals("0.0.0=", encode(style, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.1.0=", encode(style, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicMark() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Mark mark = ((Mark) (symbolizer.getGraphic().graphicalSymbols().get(0)));
        mark.setWellKnownName(toExpression("if_then_else(equalTo(field, 1), 'circle', 'square')"));
        final Style s = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("0.0.0=&0.0.0.name=circle", encode(s, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.0.0=&0.0.0.name=square", encode(s, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicColor() throws CQLException {
        Expression color = toExpression("if_then_else(equalTo(field, 1), '#8080C0', '#CC8030')");
        Stroke stroke = IconTestSupport.styleFactory.stroke(color, null, null, null, null, null, null);
        Fill fill = IconTestSupport.styleFactory.fill(null, color, null);
        Mark mark = IconTestSupport.styleFactory.mark(toExpression("circle"), fill, stroke);
        Graphic graphic = IconTestSupport.styleFactory.graphic(Collections.singletonList(mark), null, null, null, null, null);
        PointSymbolizer symbolizer = IconTestSupport.styleFactory.pointSymbolizer("symbolizer", toExpression("geom"), null, null, graphic);
        final Style s = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("0.0.0=&0.0.0.fill.color=%238080C0&0.0.0.name=square&0.0.0.stroke.color=%238080C0", encode(s, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.0.0=&0.0.0.fill.color=%23CC8030&0.0.0.name=square&0.0.0.stroke.color=%23CC8030", encode(s, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicOpacity() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Graphic graphic = symbolizer.getGraphic();
        graphic.setOpacity(toExpression("1 / field"));
        final Style s = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("0.0.0=&0.0.0.opacity=1.0", encode(s, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.0.0=&0.0.0.opacity=0.5", encode(s, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicRotation() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Graphic graphic = symbolizer.getGraphic();
        graphic.setRotation(toExpression("45 * field"));
        final Style s = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("0.0.0=&0.0.0.rotation=45.0", encode(s, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.0.0=&0.0.0.rotation=90.0", encode(s, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicSize() throws CQLException {
        final PointSymbolizer symbolizer = grayCircle();
        final Graphic graphic = symbolizer.getGraphic();
        graphic.setSize(toExpression("field * 16"));
        final Style s = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("0.0.0=&0.0.0.size=16.0", encode(s, IconTestSupport.fieldIs1));
        Assert.assertEquals("0.0.0=&0.0.0.size=32.0", encode(s, IconTestSupport.fieldIs2));
    }

    @Test
    public void testDynamicURL() throws UnsupportedEncodingException, CQLException {
        // TODO: This test should overlay two different images
        final PointSymbolizer symbolizer = externalGraphic("http://127.0.0.1/foo${field}.png", "image/png");
        final Style style = styleFromRules(catchAllRule(symbolizer, symbolizer));
        final String url = URLEncoder.encode("http://127.0.0.1/", "UTF-8");
        Assert.assertEquals((((("0.0.0=&0.0.0.url=" + url) + "foo1.png&0.0.1=&0.0.1.url=") + url) + "foo1.png"), encode(style, IconTestSupport.fieldIs1));
        Assert.assertEquals((((("0.0.0=&0.0.0.url=" + url) + "foo2.png&0.0.1=&0.0.1.url=") + url) + "foo2.png"), encode(style, IconTestSupport.fieldIs2));
    }

    @Test
    public void testPublicURL() throws CQLException {
        final PointSymbolizer symbolizer = externalGraphic("http://127.0.0.1/foo.png", "image/png");
        final Style style = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("http://127.0.0.1/foo.png", encode(style, IconTestSupport.fieldIs1));
    }

    @Test
    public void testLocalFile() throws Exception {
        final PointSymbolizer symbolizer = externalGraphic("file:foo.png", "image/png");
        final Style style = styleFromRules(catchAllRule(symbolizer));
        Assert.assertEquals("http://127.0.0.1/styles/foo.png", encode(style, IconTestSupport.fieldIs1));
    }

    @Test
    public void testLocalFileRotate() throws Exception {
        final PointSymbolizer symbolizer = externalGraphic("file:foo.png", "image/png");
        final Graphic graphic = symbolizer.getGraphic();
        graphic.setRotation(toExpression("45 * field"));
        final Style style = styleFromRules(catchAllRule(symbolizer));
        IconProperties prop1 = IconPropertyExtractor.extractProperties(style, IconTestSupport.fieldIs1);
        Assert.assertEquals("http://127.0.0.1/styles/foo.png", prop1.href("http://127.0.0.1/", null, "test"));
        Assert.assertEquals(45.0, prop1.getHeading(), 1.0E-4);
        IconProperties prop2 = IconPropertyExtractor.extractProperties(style, IconTestSupport.fieldIs2);
        Assert.assertEquals("http://127.0.0.1/styles/foo.png", prop2.href("http://127.0.0.1/", null, "test"));
        Assert.assertEquals(90.0, prop2.getHeading(), 1.0E-4);
    }

    @Test
    public void testTwoLocalFilesRotate() throws Exception {
        final PointSymbolizer symbolizer1 = externalGraphic("file:foo.png", "image/png");
        final PointSymbolizer symbolizer2 = externalGraphic("file:bar.png", "image/png");
        final Graphic graphic1 = symbolizer1.getGraphic();
        graphic1.setRotation(toExpression("45 * field"));
        final Graphic graphic2 = symbolizer2.getGraphic();
        graphic2.setRotation(toExpression("22.5 * field"));
        final Style style = styleFromRules(catchAllRule(symbolizer1, symbolizer2));
        IconProperties prop = IconPropertyExtractor.extractProperties(style, IconTestSupport.fieldIs1);
        Assert.assertEquals("http://127.0.0.1/kml/icon/test?0.0.0=&0.0.0.rotation=45.0&0.0.1=&0.0.1.rotation=22.5", prop.href("http://127.0.0.1/", null, "test"));
        Assert.assertEquals(0.0, prop.getHeading(), 1.0E-4);
    }

    @Test
    public void testTwoLocalFilesOneRotate() throws Exception {
        final PointSymbolizer symbolizer1 = externalGraphic("file:foo.png", "image/png");
        final PointSymbolizer symbolizer2 = externalGraphic("file:bar.png", "image/png");
        final Graphic graphic1 = symbolizer1.getGraphic();
        graphic1.setRotation(toExpression("45 * field"));
        final Graphic graphic2 = symbolizer2.getGraphic();
        graphic2.setRotation(NIL);
        final Style style = styleFromRules(catchAllRule(symbolizer1, symbolizer2));
        IconProperties prop = IconPropertyExtractor.extractProperties(style, IconTestSupport.fieldIs1);
        Assert.assertEquals("http://127.0.0.1/kml/icon/test?0.0.0=&0.0.0.rotation=45.0&0.0.1=", prop.href("http://127.0.0.1/", null, "test"));
        Assert.assertEquals(0, prop.getHeading(), 0.0);
    }

    @Test
    public void testMarkRotate() throws Exception {
        final PointSymbolizer symbolizer = grayCircle();
        final Graphic graphic = symbolizer.getGraphic();
        graphic.setRotation(toExpression("45 * field"));
        final Style s = styleFromRules(catchAllRule(symbolizer));
        IconProperties prop = IconPropertyExtractor.extractProperties(s, IconTestSupport.fieldIs1);
        Assert.assertEquals("http://127.0.0.1/kml/icon/test?0.0.0=&0.0.0.rotation=45.0", prop.href("http://127.0.0.1/", null, "test"));
        Assert.assertEquals(0.0, prop.getHeading(), 1.0E-4);
    }
}

