/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.geojson;


import com.google.common.collect.ImmutableMap;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Map;
import org.geoserver.wms.WMSMapContent;
import org.geoserver.wms.map.RawMap;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.mockito.Mockito;


public class GeoJsonTileBuilderTest {
    @Test
    public void testGeoJsonWMSBuilder() throws IOException, ParseException {
        GeoJsonBuilderFactory builderFact = new GeoJsonBuilderFactory();
        Rectangle screenSize = new Rectangle(256, 256);
        ReferencedEnvelope mapArea = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        Geometry point = geom("POINT(1 10)");
        Map<String, Object> pointProps = ImmutableMap.<String, Object>of("name", "point1");
        Geometry line = geom("LINESTRING(0 0, 1 1, 2 2)");
        Map<String, Object> lineProps = ImmutableMap.<String, Object>of("name", "line1");
        GeoJsonWMSBuilder tileBuilder = builderFact.newBuilder(screenSize, mapArea);
        tileBuilder.addFeature("Points", "unused", "unused", point, pointProps);
        tileBuilder.addFeature("Lines", "unused", "unused", line, lineProps);
        WMSMapContent mapContent = Mockito.mock(WMSMapContent.class);
        RawMap map = tileBuilder.build(mapContent);
        String json = decode(map);
        Assert.assertEquals(("{\"type\":\"FeatureCollection\",\"totalFeatures\":\"unknown\",\"features\":[{\"type\":\"Feature\",\"id\":\"unused\",\"geometry\":{\"type\":\"Point\"," + ("\"coordinates\":[1,10]},\"geometry_name\":\"unused\",\"properties\":{\"name\":\"point1\"}},{\"type\":\"Feature\",\"id\":\"unused\",\"geometry\":{\"type\":\"LineString\"," + "\"coordinates\":[[0,0],[1,1],[2,2]]},\"geometry_name\":\"unused\",\"properties\":{\"name\":\"line1\"}}]}")), json);
    }
}

