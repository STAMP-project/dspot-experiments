/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.icons;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;


public class IconPropertyInjectorTest extends IconTestSupport {
    @Test
    public void testSimplePointStyle() throws Exception {
        Style result;
        {
            Symbolizer symb = grayCircle();
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assertSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assertSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), Mark.class);
        }
    }

    @Test
    public void testSimplePointStyleOff() throws Exception {
        Style result;
        {
            Symbolizer symb = grayCircle();
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            // properties.put("0.0.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assumeSingleElement(fts.rules());
            Assert.assertThat(rule.symbolizers().size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void testSimpleGraphicStyle() throws Exception {
        Style result;
        {
            Symbolizer symb = this.externalGraphic("http://example.com/foo.png", "image/png");
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assumeSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            ExternalGraphic eg = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), ExternalGraphic.class);
            Assert.assertThat(eg.getOnlineResource().getLinkage().toString(), CoreMatchers.is("http://example.com/foo.png"));
        }
    }

    @Test
    public void testSubstitutedGraphicStyle() throws Exception {
        Style result;
        {
            Symbolizer symb = this.externalGraphic("http://example.com/${PROV_ABBR}.png", "image/png");
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            properties.put("0.0.0.url", "http://example.com/BC.png");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assumeSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            ExternalGraphic eg = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), ExternalGraphic.class);
            Assert.assertThat(eg.getOnlineResource().getLinkage().toString(), CoreMatchers.is("http://example.com/BC.png"));
        }
    }

    @Test
    public void testUnneccessaryURLInjection() throws Exception {
        Style result;
        {
            Symbolizer symb = this.externalGraphic("http://example.com/NF.png", "image/png");
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            properties.put("0.0.0.url", "http://example.com/BC.png");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assumeSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            ExternalGraphic eg = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), ExternalGraphic.class);
            Assert.assertThat(eg.getOnlineResource().getLinkage().toString(), CoreMatchers.is("http://example.com/NF.png"));
        }
    }

    @Test
    public void testRotation() throws Exception {
        Style result;
        {
            PointSymbolizer symb = this.externalGraphic("http://example.com/foo.png", "image/png");
            symb.getGraphic().setRotation(IconTestSupport.filterFactory.property("heading"));
            Style input = styleFromRules(catchAllRule(symb));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            properties.put("0.0.0.rotation", "45.0");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assumeSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            Graphic eg = symb.getGraphic();
            Assert.assertThat(eg.getRotation().evaluate(null).toString(), CoreMatchers.is("45.0"));
        }
    }

    @Test
    public void testFilteredRulesPickFirstExternal() throws Exception {
        Style result;
        {
            Filter f1 = IconTestSupport.filterFactory.less(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            Filter f2 = IconTestSupport.filterFactory.greaterOrEqual(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            PointSymbolizer symb1 = externalGraphic("http://example.com/foo.png", "image/png");
            PointSymbolizer symb2 = externalGraphic("http://example.com/bar.png", "image/png");
            Style input = styleFromRules(rule(f1, symb1), rule(f2, symb2));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assertSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            ExternalGraphic eg = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), ExternalGraphic.class);
            Assert.assertThat(eg.getOnlineResource().getLinkage().toString(), CoreMatchers.is("http://example.com/foo.png"));
        }
    }

    @Test
    public void testFilteredRulesPickSecondExternal() throws Exception {
        Style result;
        {
            Filter f1 = IconTestSupport.filterFactory.less(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            Filter f2 = IconTestSupport.filterFactory.greaterOrEqual(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            PointSymbolizer symb1 = externalGraphic("http://example.com/foo.png", "image/png");
            PointSymbolizer symb2 = externalGraphic("http://example.com/bar.png", "image/png");
            Style input = styleFromRules(rule(f1, symb1), rule(f2, symb2));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.1.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assertSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            ExternalGraphic eg = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), ExternalGraphic.class);
            Assert.assertThat(eg.getOnlineResource().getLinkage().toString(), CoreMatchers.is("http://example.com/bar.png"));
        }
    }

    @Test
    public void testFilteredRulesPickFirstMark() throws Exception {
        Style result;
        {
            Filter f1 = IconTestSupport.filterFactory.less(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            Filter f2 = IconTestSupport.filterFactory.greaterOrEqual(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            PointSymbolizer symb1 = mark("arrow", Color.BLACK, Color.RED, 1.0F, 16);
            PointSymbolizer symb2 = mark("arrow", Color.BLACK, Color.BLUE, 1.0F, 16);
            Style input = styleFromRules(rule(f1, symb1), rule(f2, symb2));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.0.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assertSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            Mark mark = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), Mark.class);
            Assert.assertThat(mark.getFill().getColor().evaluate(null, Color.class), CoreMatchers.is(Color.RED));
        }
    }

    @Test
    public void testFilteredRulesPickSecondMark() throws Exception {
        Style result;
        {
            Filter f1 = IconTestSupport.filterFactory.less(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            Filter f2 = IconTestSupport.filterFactory.greaterOrEqual(IconTestSupport.filterFactory.property("foo"), IconTestSupport.filterFactory.literal(4));
            PointSymbolizer symb1 = mark("arrow", Color.BLACK, Color.RED, 1.0F, 16);
            PointSymbolizer symb2 = mark("arrow", Color.BLACK, Color.BLUE, 1.0F, 16);
            Style input = styleFromRules(rule(f1, symb1), rule(f2, symb2));
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("0.1.0", "");
            result = IconPropertyInjector.injectProperties(input, properties);
        }
        {
            FeatureTypeStyle fts = IconPropertyInjectorTest.assumeSingleElement(result.featureTypeStyles());
            Rule rule = IconPropertyInjectorTest.assertSingleElement(fts.rules());
            PointSymbolizer symb = IconPropertyInjectorTest.assertSingleElement(rule.symbolizers(), PointSymbolizer.class);
            Mark mark = IconPropertyInjectorTest.assertSingleElement(symb.getGraphic().graphicalSymbols(), Mark.class);
            Assert.assertThat(mark.getFill().getColor().evaluate(null, Color.class), CoreMatchers.is(Color.BLUE));
        }
    }

    @Test
    public void testGraphicFallbacks() {
        FilterFactory ff = CommonFactoryFinder.getFilterFactory();
        Style style = SLD.createPointStyle("circle", Color.RED, Color.yellow, 0.5F, 10.0F);
        Graphic g = SLD.graphic(SLD.pointSymbolizer(style));
        g.setRotation(ff.literal(45));
        g.setOpacity(ff.literal(0.5));
        Map<String, String> props = new HashMap<String, String>();
        props.put("0.0.0", "");
        style = IconPropertyInjector.injectProperties(style, props);
        g = SLD.graphic(SLD.pointSymbolizer(style));
        Assert.assertEquals(10.0, g.getSize().evaluate(null, Double.class), 0.1);
        Assert.assertEquals(45.0, g.getRotation().evaluate(null, Double.class), 0.1);
        Assert.assertEquals(0.5, g.getOpacity().evaluate(null, Double.class), 0.1);
    }
}

