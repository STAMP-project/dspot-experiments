/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.vector;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.WebMap;
import org.geotools.data.Query;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class VectorTileMapOutputFormatTest {
    private static CoordinateReferenceSystem WEB_MERCATOR;

    private static CoordinateReferenceSystem WGS84;

    private static Style defaultPointStyle;

    private static Style defaultLineStyle;

    private static Style defaultPolygonStyle;

    private static Style scaleDependentPolygonStyle;

    private WMS wmsMock;

    private VectorTileMapOutputFormat outputFormat;

    private VectorTileBuilder tileBuilderMock;

    private FeatureLayer pointLayer;

    private FeatureLayer lineLayer;

    private FeatureLayer polygonLayer;

    private FeatureLayer scaleDependentPolygonLayer;

    private List<MapContent> mapContents = new ArrayList<>();

    // Test case for when a style has no active rules (i.e. when the current map scale is not
    // compatible with the Min/MaxScaleDenominator in the SLD Rule).
    @Test
    public void testNoRulesByScale() throws Exception {
        // ----------- normal case, there is a rule that draws
        // this has map scale denominator of about 1:7,700, rule will draw
        ReferencedEnvelope mapBounds = new ReferencedEnvelope(0, 0.005, 0, 0.005, VectorTileMapOutputFormatTest.WGS84);
        Rectangle renderingArea = new Rectangle(256, 256);
        WMSMapContent mapContent = createMapContent(mapBounds, renderingArea, 0, scaleDependentPolygonLayer);
        Query q = getStyleQuery(scaleDependentPolygonLayer, mapContent);
        Assert.assertTrue(((q.getFilter()) != (Filter.EXCLUDE)));
        // ------------------- abnormal case, there are no rules in the sld that will draw
        // this has map scale denominator of about 1:77k, rule will NOT draw
        mapBounds = new ReferencedEnvelope(0, 0.05, 0, 0.05, VectorTileMapOutputFormatTest.WGS84);
        renderingArea = new Rectangle(256, 256);
        mapContent = createMapContent(mapBounds, renderingArea, 0, scaleDependentPolygonLayer);
        q = getStyleQuery(scaleDependentPolygonLayer, mapContent);
        Assert.assertTrue(((q.getFilter()) == (Filter.EXCLUDE)));
    }

    @Test
    public void testBuffer() throws Exception {
        ReferencedEnvelope mapBounds = new ReferencedEnvelope((-90), 90, 0, 180, VectorTileMapOutputFormatTest.WGS84);
        Rectangle renderingArea = new Rectangle(256, 256);
        WMSMapContent mapContent = createMapContent(mapBounds, renderingArea, 32, pointLayer);
        WebMap mockMap = Mockito.mock(WebMap.class);
        Mockito.when(tileBuilderMock.build(ArgumentMatchers.same(mapContent))).thenReturn(mockMap);
        Assert.assertSame(mockMap, outputFormat.produceMap(mapContent));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point1"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point2"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point3"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointFar"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointNear"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
    }

    @Test
    public void testBufferProject() throws Exception {
        ReferencedEnvelope mapBounds = new ReferencedEnvelope(0, 2.003750834E7, 0, 2.003750834E7, VectorTileMapOutputFormatTest.WEB_MERCATOR);
        Rectangle renderingArea = new Rectangle(256, 256);
        ReferencedEnvelope qbounds = new ReferencedEnvelope(mapBounds);
        qbounds.expandBy(((2.003750834E7 / 256) * 32));
        WMSMapContent mapContent = createMapContent(mapBounds, renderingArea, 32, pointLayer);
        WebMap mockMap = Mockito.mock(WebMap.class);
        Mockito.when(tileBuilderMock.build(ArgumentMatchers.same(mapContent))).thenReturn(mockMap);
        Assert.assertSame(mockMap, outputFormat.produceMap(mapContent));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point1"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point2"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point3"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointFar"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointNear"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
    }

    @Test
    public void testSimple() throws Exception {
        ReferencedEnvelope mapBounds = new ReferencedEnvelope((-90), 90, 0, 180, VectorTileMapOutputFormatTest.WGS84);
        Rectangle renderingArea = new Rectangle(256, 256);
        WMSMapContent mapContent = createMapContent(mapBounds, renderingArea, null, pointLayer);
        WebMap mockMap = Mockito.mock(WebMap.class);
        Mockito.when(tileBuilderMock.build(ArgumentMatchers.same(mapContent))).thenReturn(mockMap);
        Assert.assertSame(mockMap, outputFormat.produceMap(mapContent));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point1"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point2"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point3"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointFar"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointNear"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
    }

    @Test
    public void testCQLfilter() throws Exception {
        ReferencedEnvelope mapBounds = new ReferencedEnvelope((-90), 90, 0, 180, VectorTileMapOutputFormatTest.WGS84);
        Rectangle renderingArea = new Rectangle(256, 256);
        WMSMapContent mapContent = createMapContent(mapBounds, renderingArea, null, pointLayer);
        FeatureLayer layer = ((FeatureLayer) (mapContent.layers().get(0)));
        layer.setQuery(new Query(null, ECQL.toFilter("sp = 'StringProp1_2'")));
        WebMap mockMap = Mockito.mock(WebMap.class);
        Mockito.when(tileBuilderMock.build(ArgumentMatchers.same(mapContent))).thenReturn(mockMap);
        Assert.assertSame(mockMap, outputFormat.produceMap(mapContent));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point1"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.times(1)).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point2"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("point3"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointFar"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(tileBuilderMock, Mockito.never()).addFeature(ArgumentMatchers.eq("points"), ArgumentMatchers.eq("pointNear"), ArgumentMatchers.eq("geom"), ArgumentMatchers.any(Geometry.class), ArgumentMatchers.any(Map.class));
    }
}

