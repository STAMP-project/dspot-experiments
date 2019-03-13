/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.geo;


import GeoJSONUtils.COORDINATES_FIELD;
import GeoJSONUtils.GEOMETRIES_FIELD;
import GeoJSONUtils.GEOMETRY_COLLECTION;
import GeoJSONUtils.LINE_STRING;
import GeoJSONUtils.POINT;
import GeoJSONUtils.POLYGON;
import GeoJSONUtils.TYPE_FIELD;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.io.WKTWriter;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.jts.JtsGeometry;


public class GeoJSONUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(CoordinateArraySequenceFactory.instance());

    public static final List<Shape> SHAPES = ImmutableList.<Shape>of(new JtsGeometry(new Polygon(GeoJSONUtilsTest.GEOMETRY_FACTORY.createLinearRing(new Coordinate[]{ new Coordinate(0.0, 1.0), new Coordinate(100.0, 0.1), new Coordinate(20.0, 23.567), new Coordinate(0.0, 1.0) }), new LinearRing[0], GeoJSONUtilsTest.GEOMETRY_FACTORY), JtsSpatialContext.GEO, true, true), new JtsGeometry(new org.locationtech.jts.geom.MultiPolygon(new Polygon[]{ new Polygon(GeoJSONUtilsTest.GEOMETRY_FACTORY.createLinearRing(new Coordinate[]{ new Coordinate(0.0, 1.0), new Coordinate(0.1, 1.1), new Coordinate(1.1, 60.0), new Coordinate(0.0, 1.0) }), new LinearRing[0], GeoJSONUtilsTest.GEOMETRY_FACTORY), new Polygon(GeoJSONUtilsTest.GEOMETRY_FACTORY.createLinearRing(new Coordinate[]{ new Coordinate(2.0, 1.0), new Coordinate(2.1, 1.1), new Coordinate(2.1, 70.0), new Coordinate(2.0, 1.0) }), new LinearRing[0], GeoJSONUtilsTest.GEOMETRY_FACTORY) }, GeoJSONUtilsTest.GEOMETRY_FACTORY), JtsSpatialContext.GEO, true, true), new JtsGeometry(GeoJSONUtilsTest.GEOMETRY_FACTORY.createMultiPointFromCoords(new Coordinate[]{ new Coordinate(0.0, 0.0), new Coordinate(1.0, 1.0) }), JtsSpatialContext.GEO, true, true), new JtsGeometry(GeoJSONUtilsTest.GEOMETRY_FACTORY.createMultiLineString(new LineString[]{ GeoJSONUtilsTest.GEOMETRY_FACTORY.createLineString(new Coordinate[]{ new Coordinate(0.0, 1.0), new Coordinate(0.1, 1.1), new Coordinate(1.1, 80.0), new Coordinate(0.0, 1.0) }), GeoJSONUtilsTest.GEOMETRY_FACTORY.createLineString(new Coordinate[]{ new Coordinate(2.0, 1.0), new Coordinate(2.1, 1.1), new Coordinate(2.1, 60.0), new Coordinate(2.0, 1.0) }) }), JtsSpatialContext.GEO, true, true));

    @Test
    public void testShape2Map() throws Exception {
        for (Shape shape : GeoJSONUtilsTest.SHAPES) {
            Map<String, Object> map = GeoJSONUtils.shape2Map(shape);
            MatcherAssert.assertThat(map, Matchers.hasKey("type"));
            GeoJSONUtils.validateGeoJson(map);
        }
    }

    @Test
    public void testPoint2Map() throws Exception {
        Point point = GeoJSONUtilsTest.GEOMETRY_FACTORY.createPoint(new Coordinate(0.0, 0.0));
        Shape shape = new org.locationtech.spatial4j.shape.jts.JtsPoint(point, JtsSpatialContext.GEO);
        Map<String, Object> map = GeoJSONUtils.shape2Map(shape);
        MatcherAssert.assertThat(map, Matchers.hasEntry("type", ((Object) ("Point"))));
        MatcherAssert.assertThat(map.get("coordinates").getClass().isArray(), Is.is(true));
        MatcherAssert.assertThat(((double[]) (map.get("coordinates"))).length, Is.is(2));
    }

    @Test
    public void testMapFromWktRoundTrip() throws Exception {
        String wkt = "MULTILINESTRING ((10.05 10.28, 20.95 20.89), (20.95 20.89, 31.92 21.45))";
        Shape shape = GeoJSONUtils.wkt2Shape(wkt);
        Map<String, Object> map = GeoJSONUtils.shape2Map(shape);
        Map<String, Object> wktMap = GeoJSONUtils.wkt2Map(wkt);
        MatcherAssert.assertThat(map.get("type"), Is.is(wktMap.get("type")));
        MatcherAssert.assertThat(map.get("coordinates"), Is.is(wktMap.get("coordinates")));
        Shape mappedShape = GeoJSONUtils.map2Shape(map);
        String wktFromMap = new WKTWriter().toString(mappedShape);
        MatcherAssert.assertThat(wktFromMap, Is.is(wkt));
    }

    @Test
    public void testInvalidWKT() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Matchers.allOf(Matchers.startsWith("Cannot convert WKT \""), Matchers.endsWith("\" to shape")));
        GeoJSONUtils.wkt2Map(("multilinestring (((10.05  10.28  3.4  8.4, 20.95  20.89  4.5  9.5),\n" + (" \n" + "( 20.95  20.89  4.5  9.5, 31.92  21.45  3.6  8.6)))")));
    }

    @Test
    public void testMap2Shape() throws Exception {
        Shape shape = GeoJSONUtils.map2Shape(ImmutableMap.<String, Object>of(TYPE_FIELD, LINE_STRING, COORDINATES_FIELD, new Double[][]{ new Double[]{ 0.0, 0.1 }, new Double[]{ 1.0, 1.1 } }));
        MatcherAssert.assertThat(shape, Matchers.instanceOf(JtsGeometry.class));
        MatcherAssert.assertThat(getGeom(), Matchers.instanceOf(LineString.class));
    }

    @Test
    public void testInvalidMap() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot convert Map \"{}\" to shape");
        GeoJSONUtils.map2Shape(ImmutableMap.<String, Object>of());
    }

    @Test
    public void testValidateMissingType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: type field missing");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of());
    }

    @Test
    public void testValidateWrongType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: invalid type");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, "Foo"));
    }

    @Test
    public void testValidateMissingCoordinates() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: coordinates field missing");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, LINE_STRING));
    }

    @Test
    public void testValidateGeometriesMissing() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: geometries field missing");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, GEOMETRY_COLLECTION));
    }

    @Test
    public void testInvalidGeometryCollection() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: invalid GeometryCollection");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, GEOMETRY_COLLECTION, GEOMETRIES_FIELD, ImmutableList.<Object>of("ABC")));
    }

    @Test
    public void testValidateInvalidCoordinates() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: invalid coordinate");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, POINT, COORDINATES_FIELD, "ABC"));
    }

    @Test
    public void testInvalidNestedCoordinates() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: invalid coordinate");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, POINT, COORDINATES_FIELD, new double[][]{ new double[]{ 0.0, 1.0 }, new double[]{ 1.0, 0.0 } }));
    }

    @Test
    public void testInvalidDepthNestedCoordinates() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid GeoJSON: invalid coordinate");
        GeoJSONUtils.validateGeoJson(ImmutableMap.of(TYPE_FIELD, POLYGON, COORDINATES_FIELD, new double[][]{ new double[]{ 0.0, 1.0 }, new double[]{ 1.0, 0.0 } }));
    }
}

