/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.sldservice.utils.classifier;


import java.awt.Color;
import java.util.List;
import org.geoserver.sldservice.utils.classifier.impl.BlueColorRamp;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.util.factory.GeoTools;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;


public class RulesBuilderTest {
    private static final int MAX_COLOR_INT = 255;

    private static final int MIN_COLOR_INT = 52;

    private RulesBuilder builder;

    protected SimpleFeatureCollection pointCollection;

    protected SimpleFeatureCollection lineCollection;

    protected SimpleFeatureCollection polygonCollection;

    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());

    protected SimpleFeatureType dataType;

    protected SimpleFeature[] testFeatures;

    @Test
    public void testQuantileClassification() throws Exception {
        if ((pointCollection) != null) {
            List<Rule> rules = builder.quantileClassification(pointCollection, "foo", Integer.class, 4, false, false);
            Assert.assertEquals(4, rules.size());
        }
    }

    @Test
    public void testEqualIntervalClassification() throws Exception {
        if ((pointCollection) != null) {
            List<Rule> rules = builder.equalIntervalClassification(pointCollection, "foo", Integer.class, 4, false, false);
            Assert.assertEquals(4, rules.size());
        }
    }

    @Test
    public void testUniqueIntervalClassification() throws Exception {
        if ((pointCollection) != null) {
            List<Rule> rules = builder.uniqueIntervalClassification(pointCollection, "group", Integer.class, (-1), false);
            Assert.assertEquals(4, rules.size());
            rules = builder.uniqueIntervalClassification(pointCollection, "id", Integer.class, (-1), false);
            Assert.assertEquals(8, rules.size());
        }
    }

    @Test
    public void testJenksClassification() throws Exception {
        if ((pointCollection) != null) {
            List<Rule> rules = builder.jenksClassification(lineCollection, "jenks71", Integer.class, 10, false, false);
            Assert.assertEquals(10, rules.size());
        }
    }

    @Test
    public void testPolygonStyle() throws Exception {
        if ((pointCollection) != null) {
            int numClasses = 10;
            List<Rule> rules = builder.equalIntervalClassification(pointCollection, "foo", Integer.class, numClasses, false, false);
            builder.polygonStyle(rules, new BlueColorRamp(), false);
            Rule ruleOne = rules.get(0);
            Assert.assertTrue(((ruleOne.getSymbolizers()[0]) instanceof PolygonSymbolizer));
            PolygonSymbolizer symbolizer = ((PolygonSymbolizer) (ruleOne.getSymbolizers()[0]));
            Assert.assertEquals(new Color(0, 0, RulesBuilderTest.MIN_COLOR_INT), symbolizer.getFill().getColor().evaluate(null, Color.class));
            Assert.assertNotNull(ruleOne.getFilter());
            Assert.assertEquals(numClasses, rules.size());
        }
    }

    @Test
    public void testPolygonStyleReverse() throws Exception {
        if ((pointCollection) != null) {
            int numClasses = 10;
            List<Rule> rules = builder.equalIntervalClassification(pointCollection, "foo", Integer.class, numClasses, false, false);
            builder.polygonStyle(rules, new BlueColorRamp(), true);
            PolygonSymbolizer symbolizer = ((PolygonSymbolizer) (rules.get(0).getSymbolizers()[0]));
            Assert.assertEquals(new Color(0, 0, RulesBuilderTest.MAX_COLOR_INT), symbolizer.getFill().getColor().evaluate(null, Color.class));
            Assert.assertEquals(numClasses, rules.size());
        }
    }

    @Test
    public void testLineStyle() throws Exception {
        if ((lineCollection) != null) {
            int numClasses = 10;
            List<Rule> rules = builder.jenksClassification(lineCollection, "jenks71", Integer.class, numClasses, false, false);
            builder.lineStyle(rules, new BlueColorRamp(), false);
            Rule ruleOne = rules.get(0);
            Assert.assertTrue(((ruleOne.getSymbolizers()[0]) instanceof LineSymbolizer));
            LineSymbolizer symbolizer = ((LineSymbolizer) (ruleOne.getSymbolizers()[0]));
            Assert.assertEquals(new Color(0, 0, RulesBuilderTest.MIN_COLOR_INT), symbolizer.getStroke().getColor().evaluate(null, Color.class));
            Assert.assertNotNull(ruleOne.getFilter());
            Assert.assertEquals(10, rules.size());
        }
    }

    @Test
    public void testLineStyleReverse() throws Exception {
        if ((lineCollection) != null) {
            int numClasses = 10;
            List<Rule> rules = builder.jenksClassification(lineCollection, "jenks71", Integer.class, numClasses, false, false);
            builder.lineStyle(rules, new BlueColorRamp(), true);
            LineSymbolizer symbolizer = ((LineSymbolizer) (rules.get(0).getSymbolizers()[0]));
            Assert.assertEquals(new Color(0, 0, RulesBuilderTest.MAX_COLOR_INT), symbolizer.getStroke().getColor().evaluate(null, Color.class));
        }
    }

    @Test
    public void testEqualAreaClassification() throws Exception {
        List<Rule> rules = builder.equalAreaClassification(polygonCollection, "foo", Integer.class, 5, false, false);
        Assert.assertEquals(4, rules.size());
        Assert.assertEquals(CQL.toFilter("foo >= 4.0 AND foo < 43.0"), rules.get(0).getFilter());
        Assert.assertEquals(CQL.toFilter("foo >= 43.0 AND foo < 61.0"), rules.get(1).getFilter());
        Assert.assertEquals(CQL.toFilter("foo >= 61.0 AND foo < 90.0"), rules.get(2).getFilter());
        Assert.assertEquals(CQL.toFilter("foo = 90.0"), rules.get(3).getFilter());
    }
}

