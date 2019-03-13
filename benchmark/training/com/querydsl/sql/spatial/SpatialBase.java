package com.querydsl.sql.spatial;


import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.AbstractBaseTest;
import java.util.List;
import org.geolatte.geom.codec.Wkt;
import org.junit.Assert;
import org.junit.Test;

import static QSpatialRefSys.spatialRefSys;
import static Target.POSTGRESQL;


public class SpatialBase extends AbstractBaseTest {
    private static final QShapes shapes = QShapes.shapes;

    @Test
    @IncludeIn(POSTGRESQL)
    public void spatialRefSys() {
        QSpatialRefSys spatialRefSys = spatialRefSys;
        query().from(spatialRefSys).select(spatialRefSys).fetch();
    }

    // FIXME, maybe use enum as the type ?!?
    @Test
    @ExcludeIn(H2)
    public void geometryType() {
        List<Tuple> results = query().from(SpatialBase.shapes).select(SpatialBase.shapes.geometry, SpatialBase.shapes.geometry.geometryType()).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Tuple row : results) {
            Assert.assertEquals(normalize(row.get(SpatialBase.shapes.geometry).getGeometryType().name()), normalize(row.get(SpatialBase.shapes.geometry.geometryType())));
        }
    }

    @Test
    public void asText() {
        List<Tuple> results = query().from(SpatialBase.shapes).select(SpatialBase.shapes.geometry, SpatialBase.shapes.geometry.asText()).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Tuple row : results) {
            if (!((row.get(SpatialBase.shapes.geometry)) instanceof MultiPoint)) {
                Assert.assertEquals(normalize(row.get(SpatialBase.shapes.geometry).asText()), normalize(row.get(SpatialBase.shapes.geometry.asText())));
            }
        }
    }

    @Test
    @ExcludeIn(H2)
    public void point_x_y() {
        PointPath<Point> point = SpatialBase.shapes.geometry.asPoint();
        List<Tuple> results = withPoints().select(point, point.x(), point.y()).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Tuple row : results) {
            Assert.assertEquals(Double.valueOf(row.get(point).getX()), row.get(point.x()));
            Assert.assertEquals(Double.valueOf(row.get(point).getY()), row.get(point.y()));
        }
    }

    @Test
    @ExcludeIn(MYSQL)
    public void point_distance() {
        QShapes shapes1 = QShapes.shapes;
        QShapes shapes2 = new QShapes("shapes2");
        for (Tuple tuple : query().from(shapes1, shapes2).where(shapes1.id.loe(5), shapes2.id.loe(5)).select(shapes1.geometry.asPoint(), shapes2.geometry.asPoint(), shapes1.geometry.distance(shapes2.geometry)).fetch()) {
            Point point1 = tuple.get(shapes1.geometry.asPoint());
            Point point2 = tuple.get(shapes2.geometry.asPoint());
            Double distance = tuple.get(shapes1.geometry.distance(shapes2.geometry));
            Assert.assertEquals(point1.distance(point2), distance, 1.0E-4);
        }
    }

    @Test
    public void point_instances() {
        List<Shapes> results = withPoints().select(SpatialBase.shapes).fetch();
        Assert.assertEquals(5, results.size());
        for (Shapes row : results) {
            Assert.assertNotNull(row.getId());
            Assert.assertNotNull(row.getGeometry());
            Assert.assertTrue(((row.getGeometry()) instanceof Point));
        }
    }

    @Test
    public void lineString_instances() {
        List<Geometry> results = withLineStrings().select(SpatialBase.shapes.geometry).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Geometry row : results) {
            Assert.assertNotNull(row);
            Assert.assertTrue((row instanceof LineString));
        }
    }

    @Test
    public void polygon_instances() {
        List<Geometry> results = withPolygons().select(SpatialBase.shapes.geometry).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Geometry row : results) {
            Assert.assertNotNull(row);
            Assert.assertTrue((row instanceof Polygon));
        }
    }

    @Test
    public void multiPoint_instances() {
        List<Geometry> results = withMultipoints().select(SpatialBase.shapes.geometry).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Geometry row : results) {
            Assert.assertNotNull(row);
            Assert.assertTrue((row instanceof MultiPoint));
        }
    }

    @Test
    public void multiLineString_instances() {
        List<Geometry> results = withMultiLineStrings().select(SpatialBase.shapes.geometry).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Geometry row : results) {
            Assert.assertNotNull(row);
            Assert.assertTrue((row instanceof MultiLineString));
        }
    }

    @Test
    public void multiPolygon_instances() {
        List<Geometry> results = withMultiPolygons().select(SpatialBase.shapes.geometry).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Geometry row : results) {
            Assert.assertNotNull(row);
            Assert.assertTrue((row instanceof MultiPolygon));
        }
    }

    @Test
    public void point_methods() {
        PointPath<Point> point = SpatialBase.shapes.geometry.asPoint();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, point.asBinary(), H2);
        add(expressions, point.asText());
        add(expressions, point.boundary(), MYSQL);
        add(expressions, point.convexHull(), MYSQL);
        add(expressions, point.dimension());
        add(expressions, point.envelope(), H2);
        add(expressions, point.geometryType(), H2);
        add(expressions, point.isEmpty());
        add(expressions, point.isSimple());
        add(expressions, point.m(), MYSQL, TERADATA, H2);
        add(expressions, point.srid());
        // TODO add emulations
        add(expressions, point.transform(26986), MYSQL, POSTGRESQL, SQLSERVER, TERADATA, H2);
        // point specific
        add(expressions, point.x(), H2);
        add(expressions, point.y(), H2);
        add(expressions, point.z(), MYSQL, TERADATA, H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withPoints().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void point_methods2() {
        QShapes shapes1 = QShapes.shapes;
        QShapes shapes2 = new QShapes("shapes2");
        List<Expression<?>> expressions = Lists.newArrayList();
        expressions.addAll(createExpressions(shapes1.geometry.asPoint(), shapes2.geometry.asPoint()));
        expressions.addAll(createExpressions(shapes1.geometry.asPoint(), ConstantImpl.create(((Point) (Wkt.fromWkt("Point(2 2)"))))));
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : query().from(shapes1, shapes2).where(shapes1.id.loe(5), shapes2.id.loe(5)).select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void lineString_methods() {
        LineStringPath<LineString> lineString = SpatialBase.shapes.geometry.asLineString();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, lineString.asBinary(), H2);
        add(expressions, lineString.asText());
        add(expressions, lineString.boundary(), MYSQL);
        add(expressions, lineString.convexHull(), MYSQL);
        add(expressions, lineString.dimension());
        add(expressions, lineString.envelope(), H2);
        add(expressions, lineString.geometryType(), H2);
        add(expressions, lineString.isEmpty());
        add(expressions, lineString.isSimple());
        // curve specific
        add(expressions, lineString.length(), H2);
        add(expressions, lineString.startPoint(), H2);
        add(expressions, lineString.endPoint(), H2);
        add(expressions, lineString.isClosed(), H2);
        add(expressions, lineString.isRing(), H2, MYSQL);
        // linestring specific
        add(expressions, lineString.numPoints(), H2);
        add(expressions, lineString.pointN(1), H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withLineStrings().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void polygon_methods() {
        PolygonPath<Polygon> polygon = SpatialBase.shapes.geometry.asPolygon();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, polygon.asBinary(), H2);
        add(expressions, polygon.asText());
        add(expressions, polygon.boundary(), MYSQL);
        add(expressions, polygon.convexHull(), MYSQL);
        add(expressions, polygon.dimension());
        add(expressions, polygon.envelope(), H2);
        add(expressions, polygon.geometryType(), H2);
        add(expressions, polygon.isEmpty());
        add(expressions, polygon.isSimple());
        // surface specific
        add(expressions, polygon.area());
        add(expressions, polygon.centroid());
        add(expressions, polygon.pointOnSurface(), H2, MYSQL);
        // polygon specific
        add(expressions, polygon.exteriorRing(), H2);
        add(expressions, polygon.numInteriorRing(), H2);
        add(expressions, polygon.interiorRingN(1), H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withPolygons().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void multiPoint_methods() {
        MultiPointPath<MultiPoint> multipoint = SpatialBase.shapes.geometry.asMultiPoint();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, multipoint.asBinary(), H2);
        add(expressions, multipoint.asText());
        add(expressions, multipoint.boundary(), MYSQL);
        add(expressions, multipoint.convexHull(), MYSQL);
        add(expressions, multipoint.dimension());
        add(expressions, multipoint.envelope(), H2);
        add(expressions, multipoint.geometryType(), H2);
        add(expressions, multipoint.isEmpty());
        add(expressions, multipoint.isSimple());
        // multipoint specific
        add(expressions, multipoint.numGeometries(), H2);
        add(expressions, multipoint.geometryN(1), H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withMultipoints().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void multiLineString_methods() {
        MultiLineStringPath<MultiLineString> multilinestring = SpatialBase.shapes.geometry.asMultiLineString();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, multilinestring.asBinary(), H2);
        add(expressions, multilinestring.asText());
        add(expressions, multilinestring.boundary(), MYSQL);
        add(expressions, multilinestring.convexHull(), MYSQL);
        add(expressions, multilinestring.dimension());
        add(expressions, multilinestring.envelope(), H2);
        add(expressions, multilinestring.geometryType(), H2);
        add(expressions, multilinestring.isEmpty());
        add(expressions, multilinestring.isSimple());
        // multicurve specific
        add(expressions, multilinestring.isClosed(), H2);
        add(expressions, multilinestring.length(), H2);
        // multilinestring specific
        add(expressions, multilinestring.numGeometries(), H2);
        add(expressions, multilinestring.geometryN(1), H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withMultiLineStrings().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    public void multiPolygon_methods() {
        MultiPolygonPath<MultiPolygon> multipolygon = SpatialBase.shapes.geometry.asMultiPolygon();
        List<Expression<?>> expressions = Lists.newArrayList();
        add(expressions, multipolygon.asBinary(), H2);
        add(expressions, multipolygon.asText());
        add(expressions, multipolygon.boundary(), MYSQL);
        add(expressions, multipolygon.convexHull(), MYSQL);
        add(expressions, multipolygon.dimension());
        add(expressions, multipolygon.envelope(), H2);
        add(expressions, multipolygon.geometryType(), H2);
        add(expressions, multipolygon.isEmpty());
        add(expressions, multipolygon.isSimple());
        // multipolygon specific
        add(expressions, multipolygon.numGeometries(), H2);
        add(expressions, multipolygon.geometryN(1), H2);
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withMultiPolygons().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }

    @Test
    @IncludeIn(POSTGRESQL)
    public void extensions() {
        List<Expression<?>> expressions = Lists.newArrayList();
        GeometryExpression<?> expr1 = SpatialBase.shapes.geometry;
        expressions.add(GeometryExpressions.asEWKT(expr1));
        expressions.add(GeometryExpressions.fromText(expr1.asText()));
        expressions.add(GeometryExpressions.setSRID(expr1, 4326));
        expressions.add(GeometryExpressions.xmin(expr1));
        expressions.add(GeometryExpressions.xmax(expr1));
        expressions.add(GeometryExpressions.ymin(expr1));
        expressions.add(GeometryExpressions.ymax(expr1));
        expressions.add(GeometryExpressions.dwithin(expr1, expr1, 1));
        expressions.add(GeometryExpressions.collect(expr1, expr1));
        expressions.add(GeometryExpressions.translate(expr1, 1, 1));
        for (Expression<?> expr : expressions) {
            boolean logged = false;
            for (Object row : withPoints().select(expr).fetch()) {
                if ((row == null) && (!logged)) {
                    System.err.println(expr.toString());
                    logged = true;
                }
            }
        }
    }
}

