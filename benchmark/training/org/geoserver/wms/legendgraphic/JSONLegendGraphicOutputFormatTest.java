/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.legendgraphic;


import JSONLegendGraphicBuilder.COLOR;
import JSONLegendGraphicBuilder.COLORMAP;
import JSONLegendGraphicBuilder.COLORMAP_TYPE;
import JSONLegendGraphicBuilder.CONTRAST_ENHANCEMENT;
import JSONLegendGraphicBuilder.ELSE_FILTER;
import JSONLegendGraphicBuilder.ENTRIES;
import JSONLegendGraphicBuilder.EXTERNAL_GRAPHIC_TYPE;
import JSONLegendGraphicBuilder.FILL;
import JSONLegendGraphicBuilder.FONTS;
import JSONLegendGraphicBuilder.FONT_FAMILY;
import JSONLegendGraphicBuilder.FONT_SIZE;
import JSONLegendGraphicBuilder.FONT_STYLE;
import JSONLegendGraphicBuilder.FONT_WEIGHT;
import JSONLegendGraphicBuilder.GAMMA_VALUE;
import JSONLegendGraphicBuilder.GEOMETRY;
import JSONLegendGraphicBuilder.GRAPHIC;
import JSONLegendGraphicBuilder.GRAPHICS;
import JSONLegendGraphicBuilder.GRAPHIC_FILL;
import JSONLegendGraphicBuilder.GRAPHIC_STROKE;
import JSONLegendGraphicBuilder.HALO;
import JSONLegendGraphicBuilder.LABEL;
import JSONLegendGraphicBuilder.LABEL_PLACEMENT;
import JSONLegendGraphicBuilder.LAYER_NAME;
import JSONLegendGraphicBuilder.LEGEND;
import JSONLegendGraphicBuilder.LEGEND_GRAPHIC;
import JSONLegendGraphicBuilder.LINE;
import JSONLegendGraphicBuilder.MARK;
import JSONLegendGraphicBuilder.NORMALIZE;
import JSONLegendGraphicBuilder.OPACITY;
import JSONLegendGraphicBuilder.PERPENDICULAR_OFFSET;
import JSONLegendGraphicBuilder.POINT;
import JSONLegendGraphicBuilder.POLYGON;
import JSONLegendGraphicBuilder.QUANTITY;
import JSONLegendGraphicBuilder.RASTER;
import JSONLegendGraphicBuilder.ROTATION;
import JSONLegendGraphicBuilder.RULES;
import JSONLegendGraphicBuilder.SIZE;
import JSONLegendGraphicBuilder.STROKE;
import JSONLegendGraphicBuilder.STROKE_WIDTH;
import JSONLegendGraphicBuilder.SYMBOLIZERS;
import JSONLegendGraphicBuilder.TEXT;
import JSONLegendGraphicBuilder.TITLE;
import JSONLegendGraphicBuilder.UOM;
import JSONLegendGraphicBuilder.VENDOR_OPTIONS;
import MockData.LAKES;
import MockData.MPOINTS;
import MockData.MPOLYGONS;
import MockData.NAMED_PLACES;
import MockData.ROAD_SEGMENTS;
import MockData.TASMANIA_DEM;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.GetLegendGraphicRequest.LegendRequest;
import org.geoserver.wms.WMS;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.util.FeatureUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Test the functioning of the abstract legend producer for JSON format,
 *
 * @author Ian Turton
 * @version $Id$
 */
public class JSONLegendGraphicOutputFormatTest extends BaseLegendTest<JSONLegendGraphicBuilder> {
    static final String JSONFormat = "application/json";

    /**
     * Tests that a legend is produced for the explicitly specified rule, when the FeatureTypeStyle
     * has more than one rule, and one of them is requested by the RULE parameter.
     */
    @Test
    public void testUserSpecifiedRule() throws Exception {
        // load a style with 3 rules
        Style multipleRulesStyle = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle();
        Assert.assertNotNull(multipleRulesStyle);
        Rule rule = multipleRulesStyle.featureTypeStyles().get(0).rules().get(0);
        BaseLegendTest.LOGGER.info(((("testing single rule " + (rule.getName())) + " from style ") + (multipleRulesStyle.getName())));
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        GetLegendGraphicRequest req = getRequest(ftInfo.getFeatureType(), multipleRulesStyle);
        req.setRule(rule.getName());
        req.setLegendOptions(new HashMap<String, String>());
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        final int HEIGHT_HINT = 30;
        req.setHeight(HEIGHT_HINT);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        // check there is only one rule in the legend
        JSONArray rules = result.getJSONArray(LEGEND);
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals(rule.getDescription().getTitle().toString(), rules.getJSONObject(0).getJSONArray(RULES).getJSONObject(0).get(TITLE));
    }

    /**
     * Tests that a legend is produced for the explicitly specified rule, when the FeatureTypeStyle
     * has more than one rule, and one of them is requested by the RULE parameter.
     */
    @Test
    public void testRainfall() throws Exception {
        // load a style with 3 rules
        Style multipleRulesStyle = getCatalog().getStyleByName("rainfall").getStyle();
        // printStyle(multipleRulesStyle);
        Assert.assertNotNull(multipleRulesStyle);
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        SimpleFeatureCollection feature;
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        GetLegendGraphicRequest req = getRequest(feature.getSchema(), multipleRulesStyle);
        req.setLegendOptions(new HashMap<String, String>());
        // use default values for the rest of parameters
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
    }

    /**
     * Tests that the legend graphic is still produced when the request's strict parameter is set to
     * false and a layer is not specified
     */
    @Test
    public void testNoLayerProvidedAndNonStrictRequest() throws Exception {
        Style style = getCatalog().getStyleByName("rainfall").getStyle();
        Assert.assertNotNull(style);
        GetLegendGraphicRequest req = getRequest(null, style);
        req.setStrict(false);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        // was the legend painted?
        Assert.assertNotNull(resp);
        // was the legend painted?
        // System.out.println(resp.toString(2) );
        Assert.assertEquals(1, resp.getJSONArray(LEGEND).size());
    }

    /**
     * Tests that the legend graphic is produced for multiple layers
     */
    @Test
    public void testMultipleLayers() throws Exception {
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        Style style = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle();
        GetLegendGraphicRequest req = getRequest(ftInfo.getFeatureType(), style);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        // was the legend painted?
        assertNotEmpty(resp);
        LegendRequest legend = req.new LegendRequest(ftInfo.getFeatureType(), req.getWms());
        legend.setStyle(style);
        req.getLegends().add(legend);
        resp = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(resp.toString(2));
        // was the legend painted?
        assertNotEmpty(resp);
    }

    /**
     * Tests that the legend graphic is produced for multiple layers with different style for each
     * layer.
     */
    @Test
    public void testMultipleLayersWithDifferentStyles() throws Exception {
        GetLegendGraphicRequest req = getRequest(null, null);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        List<FeatureType> layers = new ArrayList<FeatureType>();
        layers.add(ftInfo.getFeatureType());
        layers.add(ftInfo.getFeatureType());
        req.setLayers(layers);
        List<Style> styles = new ArrayList<Style>();
        Style style1 = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle();
        styles.add(style1);
        // printStyle(style1);
        Style style2 = getCatalog().getStyleByName(LAKES.getLocalPart()).getStyle();
        styles.add(style2);
        // printStyle(style2);
        req.setStyles(styles);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertNotNull(result);
        JSONArray legend = result.getJSONArray(LEGEND);
        // System.out.println(legend.toString(2) );
        Assert.assertEquals(2, legend.size());
    }

    /**
     * Tests that the legend graphic is produced for multiple layers with vector and coverage
     * layers.
     */
    @Test
    public void testMultipleLayersWithVectorAndCoverage() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setFeatureType(JSONLegendGraphicOutputFormatTest.JSONFormat);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        List<FeatureType> layers = new ArrayList<FeatureType>();
        layers.add(ftInfo.getFeatureType());
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        layers.add(feature.getSchema());
        req.setLayers(layers);
        List<Style> styles = new ArrayList<Style>();
        Style style1 = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle();
        styles.add(style1);
        Style style2 = getCatalog().getStyleByName("rainfall").getStyle();
        styles.add(style2);
        req.setStyles(styles);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(resp);
        // System.out.println(resp.toString(3));
        // vector layer
        Assert.assertEquals("RoadSegments", resp.getJSONArray(LEGEND).getJSONObject(0).get(LAYER_NAME));
        // coverage layer
        Assert.assertEquals("GridCoverage", resp.getJSONArray(LEGEND).getJSONObject(1).get(LAYER_NAME));
    }

    /**
     * Tests that the legend graphic is produced for multiple layers with vector and coverage
     * layers, when coverage is not visible at current scale.
     */
    @Test
    public void testMultipleLayersWithVectorAndInvisibleCoverage() throws Exception {
        GetLegendGraphicRequest req = getRequest(null, null);
        req.setScale(1000);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        List<FeatureType> layers = new ArrayList<FeatureType>();
        layers.add(ftInfo.getFeatureType());
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        layers.add(feature.getSchema());
        req.setLayers(layers);
        List<Style> styles = new ArrayList<Style>();
        Style style1 = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart()).getStyle();
        styles.add(style1);
        styles.add(readSLD("InvisibleRaster.sld"));
        req.setStyles(styles);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(resp);
        // System.out.println(resp.toString(3));
        JSONArray legends = resp.getJSONArray(LEGEND);
        Assert.assertEquals(1, legends.size());
        // vector layer
        Assert.assertEquals("RoadSegments", legends.getJSONObject(0).get(LAYER_NAME));
    }

    /**
     * Tests that the legend graphic is produced for multiple layers, one of which cannot be seen at
     * the current scale
     */
    @Test
    public void testMultipleLayersWithVectorAndInvisibleVector() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setScale(1000);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(ROAD_SEGMENTS.getNamespaceURI(), ROAD_SEGMENTS.getLocalPart());
        List<FeatureType> layers = new ArrayList<FeatureType>();
        layers.add(ftInfo.getFeatureType());
        layers.add(ftInfo.getFeatureType());
        req.setLayers(layers);
        List<Style> styles = new ArrayList<Style>();
        final StyleInfo roadStyle = getCatalog().getStyleByName(ROAD_SEGMENTS.getLocalPart());
        styles.add(roadStyle.getStyle());
        styles.add(readSLD("InvisibleLine.sld"));
        req.setStyles(styles);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(resp);
        JSONArray legends = resp.getJSONArray(LEGEND);
        Assert.assertEquals(1, legends.size());
        // vector layer
        Assert.assertEquals("RoadSegments", legends.getJSONObject(0).get(LAYER_NAME));
    }

    @Test
    public void testMixedGeometry() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("MIXEDGEOMETRY");
        builder.setNamespaceURI("test");
        builder.setDefaultGeometry("GEOMETRY");
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        builder.setCRS(crs);
        GeometryFactory geometryFactory = new GeometryFactory();
        AttributeType at = new org.geotools.feature.type.AttributeTypeImpl(new NameImpl("ID"), String.class, false, false, Collections.EMPTY_LIST, null, null);
        builder.add(new org.geotools.feature.type.AttributeDescriptorImpl(at, new NameImpl("ID"), 0, 1, false, null));
        GeometryType gt = new org.geotools.feature.type.GeometryTypeImpl(new NameImpl("GEOMETRY"), Geometry.class, crs, false, false, Collections.EMPTY_LIST, null, null);
        builder.add(new org.geotools.feature.type.GeometryDescriptorImpl(gt, new NameImpl("GEOMETRY"), 0, 1, false, null));
        FeatureType fType = builder.buildFeatureType();
        List<FeatureType> layers = new ArrayList<FeatureType>();
        layers.add(fType);
        req.setLayers(layers);
        List<Style> styles = new ArrayList<Style>();
        styles.add(readSLD("MixedGeometry.sld"));
        req.setStyles(styles);
        JSONObject resp = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(resp);
        JSONArray legends = resp.getJSONArray(LEGEND);
        Assert.assertEquals(1, legends.size());
        // vector layer
        JSONObject legend = legends.getJSONObject(0);
        Assert.assertEquals("MIXEDGEOMETRY", legend.get(LAYER_NAME));
        JSONArray rules = legend.getJSONArray(RULES);
        Assert.assertTrue(rules.getJSONObject(0).getJSONArray(SYMBOLIZERS).getJSONObject(0).containsKey(LINE));
        Assert.assertTrue(rules.getJSONObject(1).getJSONArray(SYMBOLIZERS).getJSONObject(0).containsKey(POLYGON));
        Assert.assertTrue(rules.getJSONObject(2).getJSONArray(SYMBOLIZERS).getJSONObject(0).containsKey(POINT));
    }

    /**
     * Tests that symbols are not bigger than the requested icon size, also if an expression is used
     * for the symbol Size.
     */
    @Test
    public void testSymbolContainedInIconUsingExpression() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("SymbolExpression.sld");
        req.setStyle(style);
        // printStyle(style);
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        JSONArray rules = result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES);
        Iterator<?> iterator = rules.iterator();
        String[] expectedSizes = new String[]{ "[\"id\"]", "40" };
        int counter = 0;
        while (iterator.hasNext()) {
            JSONObject rule = ((JSONObject) (iterator.next()));
            Assert.assertNotNull(rule);
            JSONObject symbolizer = rule.getJSONArray(SYMBOLIZERS).getJSONObject(0);
            JSONObject pointSymb = symbolizer.getJSONObject(POINT);
            Assert.assertEquals(expectedSizes[(counter++)], pointSymb.get(SIZE).toString());
            Assert.assertEquals("circle", pointSymb.getJSONArray(GRAPHICS).getJSONObject(0).get(MARK));
        } 
    }

    /**
     * Tests that symbols relative sizes are proportional.
     */
    @Test
    public void testProportionalSymbolSize() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        req.setStyle(readSLD("ProportionalSymbols.sld"));
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        // System.out.println(result.toString(2));
        JSONArray rules = result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES);
        Iterator<?> iterator = rules.iterator();
        String[] expectedSizes = new String[]{ "40", "20", "10", "1" };
        int counter = 0;
        while (iterator.hasNext()) {
            JSONObject rule = ((JSONObject) (iterator.next()));
            Assert.assertNotNull(rule);
            JSONObject symbolizer = rule.getJSONArray(SYMBOLIZERS).getJSONObject(0);
            JSONObject pointSymb = symbolizer.getJSONObject(POINT);
            Assert.assertEquals(expectedSizes[(counter++)], pointSymb.get(SIZE).toString());
            Assert.assertEquals("circle", pointSymb.getJSONArray(GRAPHICS).getJSONObject(0).get(MARK));
        } 
    }

    /**
     * Tests that symbols relative sizes are proportional.
     */
    @Test
    public void testProportionalSymbolsLine() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        req.setStyle(readSLD("ProportionalSymbolsLine.sld"));
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        JSONArray rules = result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES);
        Iterator<?> iterator = rules.iterator();
        String[] expectedSizes = new String[]{ "30", "15" };
        int counter = 0;
        while (iterator.hasNext()) {
            JSONObject rule = ((JSONObject) (iterator.next()));
            Assert.assertNotNull(rule);
            JSONObject symbolizer = rule.getJSONArray(SYMBOLIZERS).getJSONObject(0);
            JSONObject pointSymb = symbolizer.getJSONObject(LINE);
            Assert.assertEquals(expectedSizes[(counter++)], pointSymb.getJSONObject(GRAPHIC_STROKE).getString(SIZE));
        } 
    }

    /**
     * Tests that symbols relative sizes are proportional also if using uoms.
     */
    @Test
    public void testProportionalSymbolSizeUOM() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("ProportionalSymbolsUOM.sld");
        // printStyle(style);
        req.setStyle(style);
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        JSONArray rules = result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES);
        Iterator<?> iterator = rules.iterator();
        String[] expectedSizes = new String[]{ "40", "20", "10", "1" };
        int counter = 0;
        while (iterator.hasNext()) {
            JSONObject rule = ((JSONObject) (iterator.next()));
            Assert.assertNotNull(rule);
            JSONObject symbolizer = rule.getJSONArray(SYMBOLIZERS).getJSONObject(0);
            JSONObject pointSymb = symbolizer.getJSONObject(POINT);
            Assert.assertEquals(expectedSizes[(counter++)], pointSymb.get(SIZE).toString());
            Assert.assertEquals("m", pointSymb.get(UOM));
            Assert.assertEquals("circle", pointSymb.getJSONArray(GRAPHICS).getJSONObject(0).get(MARK));
        } 
    }

    /**
     * Tests that symbols relative sizes are proportional also if using uoms in some Symbolizer and
     * not using them in others.
     */
    @Test
    public void testProportionalSymbolSizePartialUOM() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setScale(RendererUtilities.calculatePixelsPerMeterRatio(10, Collections.EMPTY_MAP));
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("ProportionalSymbolsPartialUOM.sld");
        printStyle(style);
        req.setStyle(style);
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        JSONArray rules = result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES);
        String[] expectedSizes = new String[]{ "40.0", "40.0" };
        for (int i = 0; i < (rules.size()); i++) {
            JSONObject rule = rules.getJSONObject(i);
            assertNotEmpty(rule);
            JSONObject symbolizer = rule.getJSONArray(SYMBOLIZERS).getJSONObject(0);
            JSONObject pointSymb = symbolizer.getJSONObject(POINT);
            Assert.assertEquals(expectedSizes[i], pointSymb.get(SIZE));
            Assert.assertEquals("circle", pointSymb.getJSONArray(GRAPHICS).getJSONObject(0).get(MARK));
        }
    }

    @Test
    public void testInternationalizedLabels() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        Map<String, String> options = new HashMap<String, String>();
        options.put("forceLabels", "on");
        req.setLegendOptions(options);
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("Internationalized.sld");
        // printStyle(style);
        req.setStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertEquals("title", result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES).getJSONObject(0).get(TITLE));
        req.setLocale(Locale.ITALIAN);
        result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertEquals("titolomoltolungo", result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES).getJSONObject(0).get(TITLE));
        req.setLocale(Locale.ENGLISH);
        result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertEquals("anothertitle", result.getJSONArray(LEGEND).getJSONObject(0).getJSONArray(RULES).getJSONObject(0).get(TITLE));
        // test that using localized labels we get a different label than when not using it
    }

    /**
     * Test that the legend is not the same if there is a rendering transformation that converts the
     * rendered layer from raster to vector
     */
    @Test
    public void testRenderingTransformationRasterVector() throws Exception {
        Style transformStyle = readSLD("RenderingTransformRasterVector.sld");
        GetLegendGraphicRequest req = getRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName(TASMANIA_DEM.getNamespaceURI(), TASMANIA_DEM.getLocalPart());
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        req.setLayer(feature.getSchema());
        req.setStyle(transformStyle);
        req.setLegendOptions(new HashMap<String, String>());
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // was the legend painted?
        assertNotEmpty(result);
        // System.out.println(result.toString(2));
    }

    /**
     * Test that the legend is not the same if there is a rendering transformation that converts the
     * rendered layer from raster to vector
     */
    @Test
    public void testColorMapWithCql() throws Exception {
        Style style = readSLD("ColorMapWithCql.sld");
        // printStyle(style);
        Assert.assertNotNull(style.featureTypeStyles());
        Assert.assertEquals(1, style.featureTypeStyles().size());
        FeatureTypeStyle fts = style.featureTypeStyles().get(0);
        Assert.assertNotNull(fts.rules());
        Assert.assertEquals(1, fts.rules().size());
        Rule rule = fts.rules().get(0);
        Assert.assertNotNull(rule.symbolizers());
        Assert.assertEquals(1, rule.symbolizers().size());
        Assert.assertTrue(((rule.symbolizers().get(0)) instanceof RasterSymbolizer));
        RasterSymbolizer symbolizer = ((RasterSymbolizer) (rule.symbolizers().get(0)));
        Assert.assertNotNull(symbolizer.getColorMap());
        Assert.assertEquals(3, symbolizer.getColorMap().getColorMapEntries().length);
        ColorMapEntry[] entries = symbolizer.getColorMap().getColorMapEntries();
        Color color = LegendUtils.color(entries[0]);
        int red = color.getRed();
        Assert.assertEquals(255, red);
        int green = color.getGreen();
        Assert.assertEquals(0, green);
        int blue = color.getBlue();
        Assert.assertEquals(0, blue);
        double quantity = LegendUtils.getQuantity(entries[1]);
        Assert.assertEquals(20.0, quantity, 0.0);
        double opacity = LegendUtils.getOpacity(entries[2]);
        Assert.assertEquals(0.5, opacity, 0.0);
        GetLegendGraphicRequest req = getRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        req.setLayer(feature.getSchema());
        req.setStyle(style);
        req.setLegendOptions(new HashMap<String, String>());
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // was the legend painted?
        assertNotEmpty(result);
        JSONArray lx = result.getJSONArray(LEGEND);
        Assert.assertEquals(1, lx.size());
        // rule 1 is a mark
        JSONObject rasterSymb = lx.getJSONObject(0).getJSONArray(RULES).getJSONObject(0).getJSONArray(SYMBOLIZERS).getJSONObject(0).getJSONObject(RASTER);
        JSONArray colorMap = rasterSymb.getJSONObject(COLORMAP).getJSONArray(ENTRIES);
        Assert.assertEquals("['${strConcat(''#FF'',''0000'')}']", colorMap.getJSONObject(0).get(COLOR));
        Assert.assertEquals("[\"${15+5}\"]", colorMap.getJSONObject(1).getString(QUANTITY));
        Assert.assertEquals("[\"${0.25*2}\"]", colorMap.getJSONObject(2).getString(OPACITY));
    }

    /**
     * Test that the legend is not the same if there is a rendering transformation that converts the
     * rendered layer from vector to raster
     */
    @Test
    public void testRenderingTransformationVectorRaster() throws Exception {
        Style transformStyle = readSLD("RenderingTransformVectorRaster.sld");
        GetLegendGraphicRequest req = getRequest();
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(NAMED_PLACES.getNamespaceURI(), NAMED_PLACES.getLocalPart());
        Assert.assertNotNull(ftInfo);
        req.setLayer(ftInfo.getFeatureType());
        req.setStyle(transformStyle);
        // printStyle(transformStyle);
        req.setLegendOptions(new HashMap<String, String>());
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
    }

    /**
     * Tests that a legend containing an ExternalGraphic icon is rendered properly.
     */
    @Test
    public void testExternalGraphic() throws Exception {
        // load a style with 3 rules
        Style externalGraphicStyle = readSLD("ExternalGraphicDemo.sld");
        Assert.assertNotNull(externalGraphicStyle);
        GetLegendGraphicRequest req = getRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        printStyle(externalGraphicStyle);
        req.setStyle(externalGraphicStyle);
        req.setScale(1.0);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        print(result);
        JSONArray lx = result.getJSONArray(LEGEND);
        Assert.assertEquals(1, lx.size());
        // rule 1 is a mark
        JSONObject pointSymb = lx.getJSONObject(0).getJSONArray(RULES).getJSONObject(0).getJSONArray(SYMBOLIZERS).getJSONObject(0).getJSONObject(POINT);
        Assert.assertEquals("14.0", pointSymb.get(SIZE));
        Assert.assertEquals("circle", pointSymb.getJSONArray(GRAPHICS).getJSONObject(0).get(MARK));
        // rule 2 is a LegendGraphic
        Assert.assertEquals("image/png", lx.getJSONObject(0).getJSONArray(RULES).getJSONObject(1).getJSONObject(LEGEND_GRAPHIC).get(EXTERNAL_GRAPHIC_TYPE));
    }

    /**
     * Tests that symbols relative sizes are proportional.
     */
    @Test
    public void testThickPolygonBorder() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("ThickBorder.sld");
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        print(result);
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        Assert.assertFalse(legend.isEmpty());
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject polySymb1 = symbolizers.getJSONObject(0).getJSONObject(POLYGON);
        Assert.assertNotNull(polySymb1);
        Assert.assertEquals("#FF0000", polySymb1.get(FILL));
        Assert.assertEquals("#000000", polySymb1.get(STROKE));
        Assert.assertEquals("4", polySymb1.get(STROKE_WIDTH));
        JSONObject polySymb2 = rules.getJSONObject(1).getJSONArray(SYMBOLIZERS).getJSONObject(0).getJSONObject(POLYGON);
        Assert.assertNotNull(polySymb2);
        Assert.assertEquals("#00FF00", polySymb2.get(FILL));
        Assert.assertEquals("#000000", polySymb2.get(STROKE));
        Assert.assertEquals("1", polySymb2.get(STROKE_WIDTH));
    }

    @Test
    public void testSimplePoint() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("point.sld");
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
    }

    @Test
    public void testHospitalPoint() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("hospital.sld");
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/Hospital?0.0.0=", pointSymb.getString("url"));
        pointSymb = symbolizers.getJSONObject(1).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/Hospital?0.0.1=", pointSymb.getString("url"));
    }

    @Test
    public void testTrickyGraphic() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        Catalog catalog = getCatalog();
        FeatureTypeInfo ftInfo = catalog.getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        // we need this to be in the "styles" directory to test the legend icon code!
        StyleInfo styleinfo = catalog.getStyleByName("tricky_point");
        Style style = styleinfo.getStyle();
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("http://local-test:8080/geoserver/styles/img/landmarks/shop_supermarket.p.16.png", pointSymb.getString("url"));
        symbolizers = rules.getJSONObject(2).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/tricky_point?0.2.0=", pointSymb.getString("url"));
        pointSymb = symbolizers.getJSONObject(1).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/tricky_point?0.2.1=", pointSymb.getString("url"));
    }

    @Test
    public void testGraphicFillLinks() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        Catalog catalog = getCatalog();
        FeatureTypeInfo ftInfo = catalog.getFeatureTypeByName(MPOLYGONS.getNamespaceURI(), MPOLYGONS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        // we need this to be in the "styles" directory to test the legend icon code!
        StyleInfo styleinfo = catalog.getStyleByName("arealandmarks");
        Style style = styleinfo.getStyle();
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // extract the basics
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertEquals(2, rules.size());
        // first rule, static image
        final JSONObject r1 = rules.getJSONObject(0);
        Assert.assertEquals("park", r1.getString("name"));
        Assert.assertEquals("[MTFCC = 'K2180']", r1.getString("filter"));
        final JSONArray symbolizers1 = r1.getJSONArray("symbolizers");
        Assert.assertEquals(1, symbolizers1.size());
        final JSONObject s1 = symbolizers1.getJSONObject(0);
        final JSONObject gf1 = s1.getJSONObject(POLYGON).getJSONObject(GRAPHIC_FILL);
        Assert.assertEquals("http://local-test:8080/geoserver/styles/img/landmarks/area/forest.png", gf1.getString("url"));
        // second rule, mark, needs icon service and enabling non point graphics
        final JSONObject r2 = rules.getJSONObject(1);
        Assert.assertEquals("nationalpark", r2.getString("name"));
        Assert.assertEquals("[MTFCC = 'K2181']", r2.getString("filter"));
        final JSONArray symbolizers2 = r2.getJSONArray("symbolizers");
        Assert.assertEquals(1, symbolizers2.size());
        final JSONObject s2 = symbolizers2.getJSONObject(0);
        final JSONObject gf2 = s2.getJSONObject(POLYGON).getJSONObject(GRAPHIC_FILL);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/arealandmarks?0.1.0=&npg=true", gf2.getString("url"));
    }

    @Test
    public void testTextSymbolizerGraphic() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        Catalog catalog = getCatalog();
        FeatureTypeInfo ftInfo = catalog.getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        // we need this to be in the "styles" directory to test the legend icon code!
        StyleInfo styleinfo = catalog.getStyleByName("fixedArrows");
        Style style = styleinfo.getStyle();
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // extract the basics
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        // check the link
        final JSONObject r1 = rules.getJSONObject(0);
        final JSONArray symbolizers1 = r1.getJSONArray("symbolizers");
        Assert.assertEquals(1, symbolizers1.size());
        final JSONObject s1 = symbolizers1.getJSONObject(0);
        final JSONObject graphic = s1.getJSONObject(TEXT).getJSONObject(GRAPHIC);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/fixedArrows?0.0.0=&npg=true", graphic.getString("url"));
    }

    @Test
    public void testTextSymbolizerDynamicGraphic() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        Catalog catalog = getCatalog();
        FeatureTypeInfo ftInfo = catalog.getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        // we need this to be in the "styles" directory to test the legend icon code!
        StyleInfo styleinfo = catalog.getStyleByName("dynamicArrows");
        Style style = styleinfo.getStyle();
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // extract the basics
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        // check the link
        final JSONObject r1 = rules.getJSONObject(0);
        final JSONArray symbolizers1 = r1.getJSONArray("symbolizers");
        Assert.assertEquals(1, symbolizers1.size());
        final JSONObject s1 = symbolizers1.getJSONObject(0);
        final JSONObject graphic = s1.getJSONObject(TEXT).getJSONObject(GRAPHIC);
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/dynamicArrows?0.0.0=&0.0.0.rotation=0.0&0.0.0.size=16.0&npg=true", graphic.getString("url"));
    }

    @Test
    public void testElseFilter() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("PopulationElse.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        JSONObject rule = rules.getJSONObject(2);
        Assert.assertEquals("true", rule.get(ELSE_FILTER));
    }

    @Test
    public void testFullPoint() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("full_point.sld");
        req.setStyle(style);
        printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject pointSymb = symbolizers.getJSONObject(0).getJSONObject(POINT);
        Assert.assertNotNull(pointSymb);
        Assert.assertEquals("[centroid(the_geom)]", pointSymb.get(GEOMETRY));
        Assert.assertEquals("6", pointSymb.get(SIZE));
        Assert.assertEquals("[rotation * '-1']", pointSymb.get(ROTATION));
        Assert.assertEquals("0.4", pointSymb.get(OPACITY));
    }

    @Test
    public void testSimpleLine() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("line.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject lineSymb = symbolizers.getJSONObject(0).getJSONObject(LINE);
        Assert.assertNotNull(lineSymb);
        Assert.assertEquals("#0000FF", lineSymb.get(STROKE));
        Assert.assertEquals("2", lineSymb.get(STROKE_WIDTH));
    }

    @Test
    public void testFullLine() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("full_line.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        print(result);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject lineSymb = symbolizers.getJSONObject(0).getJSONObject(LINE);
        Assert.assertFalse(lineSymb.isNullObject());
        JSONObject lineSymb1 = symbolizers.getJSONObject(1).getJSONObject(LINE);
        Assert.assertFalse(lineSymb1.isNullObject());
        Assert.assertEquals("10", lineSymb.get(PERPENDICULAR_OFFSET));
        final JSONObject graphicStroke1 = lineSymb1.getJSONObject(GRAPHIC_STROKE);
        Assert.assertFalse(graphicStroke1.isNullObject());
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/Default%20Styler?0.0.0=&0.0.0.rotation=0.0&npg=true", graphicStroke1.getString("url"));
        JSONObject lineSymb2 = symbolizers.getJSONObject(2).getJSONObject(LINE);
        Assert.assertFalse(lineSymb2.isNullObject());
        final JSONObject graphicFill2 = lineSymb2.getJSONObject(GRAPHIC_FILL);
        Assert.assertFalse(graphicFill2.isNullObject());
        Assert.assertEquals("http://local-test:8080/geoserver/kml/icon/Default%20Styler?0.0.1=&0.0.1.rotation=0.0&npg=true", graphicFill2.getString("url"));
    }

    @Test
    public void testSimplePolygon() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("polygon.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject polySymb = symbolizers.getJSONObject(0).getJSONObject(POLYGON);
        Assert.assertNotNull(polySymb);
        Assert.assertEquals("#0099CC", polySymb.get(FILL));
        Assert.assertEquals("#000000", polySymb.get(STROKE));
        Assert.assertEquals("0.5", polySymb.get(STROKE_WIDTH));
    }

    @Test
    public void testFullPolygon() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("full_polygon.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject polySymb = symbolizers.getJSONObject(0).getJSONObject(POLYGON);
        Assert.assertNotNull(polySymb);
        Assert.assertEquals("#0099CC", polySymb.get(FILL));
        Assert.assertEquals("#000000", polySymb.get(STROKE));
        Assert.assertEquals("0.5", polySymb.get(STROKE_WIDTH));
        JSONObject polySymb2 = symbolizers.getJSONObject(1).getJSONObject(POLYGON);
        Assert.assertFalse(((polySymb2.isNullObject()) && (polySymb2.isEmpty())));
        JSONObject stroke = polySymb2.getJSONObject(GRAPHIC_STROKE);
        Assert.assertFalse(((stroke.isNullObject()) && (stroke.isEmpty())));
        JSONObject fill = polySymb2.getJSONObject(GRAPHIC_FILL);
        Assert.assertFalse(((fill.isNullObject()) && (fill.isEmpty())));
    }

    @Test
    public void testSimpleText() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("text.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        Assert.assertNotNull(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject polySymb = symbolizers.getJSONObject(0).getJSONObject(LINE);
        Assert.assertNotNull(polySymb);
        Assert.assertEquals("#000000", polySymb.get(STROKE));
        Assert.assertEquals("0.2", polySymb.get(STROKE_WIDTH));
        JSONObject textSymb = symbolizers.getJSONObject(1).getJSONObject(TEXT);
        Assert.assertFalse(textSymb.isNullObject());
        Assert.assertEquals("[STATE_ABBR]", textSymb.getString(LABEL));
        JSONArray fonts = textSymb.getJSONArray(FONTS);
        Assert.assertEquals(2, fonts.size());
        Assert.assertEquals("[STATE_FONT]", fonts.getJSONObject(0).getJSONArray(FONT_FAMILY).get(0));
        Assert.assertEquals("Lobster", fonts.getJSONObject(0).getJSONArray(FONT_FAMILY).get(1));
        Assert.assertEquals("Times New Roman", fonts.getJSONObject(0).getJSONArray(FONT_FAMILY).get(2));
        Assert.assertEquals("Normal", fonts.getJSONObject(0).get(FONT_STYLE));
        Assert.assertEquals("normal", fonts.getJSONObject(0).get(FONT_WEIGHT));
        Assert.assertEquals("14", fonts.getJSONObject(0).get(FONT_SIZE));
        Assert.assertEquals("Times New Roman", fonts.getJSONObject(1).getJSONArray(FONT_FAMILY).get(0));
        Assert.assertEquals("Italic", fonts.getJSONObject(1).get(FONT_STYLE));
        Assert.assertEquals("normal", fonts.getJSONObject(1).get(FONT_WEIGHT));
        Assert.assertEquals("9", fonts.getJSONObject(1).get(FONT_SIZE));
        Assert.assertFalse(textSymb.getJSONObject(LABEL_PLACEMENT).isNullObject());
        Assert.assertFalse(textSymb.getJSONObject(HALO).isNullObject());
        JSONObject vops = textSymb.getJSONObject(VENDOR_OPTIONS);
        assertNotEmpty(vops);
        Assert.assertEquals("true", vops.get("followLine"));
    }

    @Test
    public void testComplexText() throws Exception {
        GetLegendGraphicRequest req = getRequest();
        req.setWidth(20);
        req.setHeight(20);
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(MPOINTS.getNamespaceURI(), MPOINTS.getLocalPart());
        req.setLayer(ftInfo.getFeatureType());
        Style style = readSLD("text_scaleSize.sld");
        req.setStyle(style);
        // printStyle(style);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        // System.out.println(result.toString(2));
        assertNotEmpty(result);
        // blue 2px wide line
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject polySymb = symbolizers.getJSONObject(0).getJSONObject(LINE);
        Assert.assertNotNull(polySymb);
        Assert.assertEquals("#000000", polySymb.get(STROKE));
        Assert.assertEquals("0.2", polySymb.get(STROKE_WIDTH));
        JSONObject textSymb = symbolizers.getJSONObject(1).getJSONObject(TEXT);
        Assert.assertFalse(textSymb.isNullObject());
        Assert.assertEquals("[STATE_ABBR]", textSymb.getString(LABEL));
        JSONArray fonts = textSymb.getJSONArray(FONTS);
        Assert.assertEquals(2, fonts.size());
        Assert.assertEquals("9", fonts.getJSONObject(1).get(FONT_SIZE));
    }

    @Test
    public void testContrastRaster() throws Exception {
        Style multipleRulesStyle = readSLD("raster_brightness.sld");
        // printStyle(multipleRulesStyle);
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = getRequest();
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        req.setLayer(feature.getSchema());
        req.setStyle(multipleRulesStyle);
        req.setLegendOptions(new HashMap<String, String>());
        // use default values for the rest of parameters
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        // System.out.println(result.toString(2));
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject rasterSymb = symbolizers.getJSONObject(0).getJSONObject(RASTER);
        assertNotEmpty(rasterSymb);
        JSONObject ce = rasterSymb.getJSONObject(CONTRAST_ENHANCEMENT);
        assertNotEmpty(ce);
        Assert.assertEquals("0.5", ce.getString(GAMMA_VALUE));
        Assert.assertEquals("true", ce.get(NORMALIZE));
    }

    @Test
    public void testDescreteRaster() throws Exception {
        Style multipleRulesStyle = readSLD("raster_discretecolors.sld");
        // printStyle(multipleRulesStyle);
        Assert.assertNotNull(multipleRulesStyle);
        GetLegendGraphicRequest req = new GetLegendGraphicRequest(WMS.get());
        CoverageInfo cInfo = getCatalog().getCoverageByName("world");
        Assert.assertNotNull(cInfo);
        GridCoverage coverage = cInfo.getGridCoverage(null, null);
        SimpleFeatureCollection feature;
        feature = FeatureUtilities.wrapGridCoverage(((GridCoverage2D) (coverage)));
        req.setLayer(feature.getSchema());
        req.setStyle(multipleRulesStyle);
        req.setLegendOptions(new HashMap<String, String>());
        req.setFormat(JSONLegendGraphicOutputFormatTest.JSONFormat);
        JSONObject result = this.legendProducer.buildLegendGraphic(req);
        assertNotEmpty(result);
        JSONArray legend = result.getJSONArray(LEGEND);
        Assert.assertNotNull(legend);
        JSONArray rules = legend.getJSONObject(0).getJSONArray(RULES);
        Assert.assertNotNull(rules);
        Assert.assertFalse(rules.isEmpty());
        JSONArray symbolizers = rules.getJSONObject(0).getJSONArray(SYMBOLIZERS);
        Assert.assertNotNull(symbolizers);
        Assert.assertFalse(symbolizers.isEmpty());
        JSONObject rasterSymb = symbolizers.getJSONObject(0).getJSONObject(RASTER);
        assertNotEmpty(rasterSymb);
        JSONObject ce = rasterSymb.getJSONObject(CONTRAST_ENHANCEMENT);
        Assert.assertTrue(ce.isEmpty());
        JSONObject colormap = rasterSymb.getJSONObject(COLORMAP);
        Assert.assertEquals("intervals", colormap.get(COLORMAP_TYPE));
    }
}

