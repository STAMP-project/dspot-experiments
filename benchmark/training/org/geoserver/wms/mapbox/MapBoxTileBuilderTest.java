/**
 * (c) 2015-2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.mapbox;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import java.awt.Rectangle;
import java.util.Map;
import no.ecc.vectortile.VectorTileDecoder.Feature;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.map.RawMap;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.mockito.Mockito;


public class MapBoxTileBuilderTest {
    @Test
    public void testMapBoxTileBuilder() throws Exception {
        MapBoxTileBuilderFactory builderFact = new MapBoxTileBuilderFactory();
        Rectangle screenSize = new Rectangle(256, 256);
        ReferencedEnvelope mapArea = new ReferencedEnvelope();
        MapBoxTileBuilder tileBuilder = builderFact.newBuilder(screenSize, mapArea);
        Geometry point = geom("POINT(1 10)");
        Map<String, Object> pointProps = ImmutableMap.<String, Object>of("name", "point1");
        Geometry line = geom("LINESTRING(0 0, 1 1, 2 2)");
        Map<String, Object> lineProps = ImmutableMap.<String, Object>of("name", "line1");
        tileBuilder.addFeature("Points", "unused", "unused", point, pointProps);
        tileBuilder.addFeature("Lines", "unused", "unused", line, lineProps);
        WMSMapContent mapContent = Mockito.mock(WMSMapContent.class);
        RawMap map = tileBuilder.build(mapContent);
        ListMultimap<String, Feature> features = decode(map);
        Assert.assertEquals(2, features.size());
        Assert.assertEquals(ImmutableSet.of("Points", "Lines"), features.keySet());
        Feature pointFeature = features.get("Points").get(0);
        Feature lineFeature = features.get("Lines").get(0);
        Assert.assertTrue(((pointFeature.getGeometry()) instanceof Point));
        Assert.assertEquals(point, pointFeature.getGeometry());
        Assert.assertEquals(pointProps, pointFeature.getAttributes());
        Assert.assertTrue(((lineFeature.getGeometry()) instanceof LineString));
        Assert.assertEquals(line, lineFeature.getGeometry());
        Assert.assertEquals(lineProps, lineFeature.getAttributes());
    }

    /* we ensure that the encoder is NOT clipping geometries by giving it
    a "too big" line and ensuring it isn't changed by the encoder.
     */
    @Test
    public void testEncoderClipping() throws Exception {
        MapBoxTileBuilderFactory builderFact = new MapBoxTileBuilderFactory();
        Rectangle screenSize = new Rectangle(256, 256);
        ReferencedEnvelope mapArea = new ReferencedEnvelope();
        MapBoxTileBuilder tileBuilder = builderFact.newBuilder(screenSize, mapArea);
        Geometry line = geom("LINESTRING(-100 -100,300 300)");// box is 0 to 256, so this is outside the

        // box
        Map<String, Object> lineProps = ImmutableMap.<String, Object>of("name", "line1");
        tileBuilder.addFeature("Lines", "unused", "unused", line, lineProps);
        WMSMapContent mapContent = Mockito.mock(WMSMapContent.class);
        RawMap map = tileBuilder.build(mapContent);
        ListMultimap<String, Feature> features = decode(map);
        Assert.assertEquals(1, features.size());
        Assert.assertEquals(ImmutableSet.of("Lines"), features.keySet());
        Feature lineFeature = features.get("Lines").get(0);
        Assert.assertTrue(((lineFeature.getGeometry()) instanceof LineString));
        Assert.assertEquals(line, lineFeature.getGeometry());// line should not be clipped

    }

    @Test
    public void testFeatureIdsWithDigitsAtTheEnd() throws Exception {
        MapBoxTileBuilder tileBuilder = tileBuilder(256, 256);
        Map<String, Object> lineProps1 = ImmutableMap.<String, Object>of("name", "line1");
        Map<String, Object> lineProps2 = ImmutableMap.<String, Object>of("name", "line2");
        tileBuilder.addFeature("Lines", "Lines.1", "unused", geom("LINESTRING(10 10, 20 20)"), lineProps1);
        tileBuilder.addFeature("Lines", "Lines.27", "unused", geom("LINESTRING(50 50, 50 30)"), lineProps2);
        RawMap map = tileBuilder.build(Mockito.mock(WMSMapContent.class));
        ListMultimap<String, Feature> features = decode(map);
        Assert.assertEquals(2, features.size());
        Assert.assertEquals(1, features.get("Lines").get(0).getId());
        Assert.assertEquals(27, features.get("Lines").get(1).getId());
    }

    @Test
    public void testFeatureIdsWithoutDigits() throws Exception {
        MapBoxTileBuilder tileBuilder = tileBuilder(256, 256);
        Map<String, Object> lineProps1 = ImmutableMap.<String, Object>of("name", "line1");
        Map<String, Object> lineProps2 = ImmutableMap.<String, Object>of("name", "line2");
        tileBuilder.addFeature("Lines", "an_id", "unused", geom("LINESTRING(10 10, 20 20)"), lineProps1);
        tileBuilder.addFeature("Lines", "another_id", "unused", geom("LINESTRING(50 50, 50 30)"), lineProps2);
        RawMap map = tileBuilder.build(Mockito.mock(WMSMapContent.class));
        ListMultimap<String, Feature> features = decode(map);
        Assert.assertEquals(2, features.size());
        // According to the Mapbox VectorTile Spec 0 is the default value
        // https://github.com/mapbox/vector-tile-spec/blob/master/2.1/vector_tile.proto
        // which we use when the fid cannot be parsed
        Assert.assertEquals(0, features.get("Lines").get(0).getId());
        Assert.assertEquals(0, features.get("Lines").get(1).getId());
    }
}

