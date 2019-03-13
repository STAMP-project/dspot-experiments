package dev.morphia.geo;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.testutil.JSONMatcher;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeometryCollectionTest extends TestBase {
    @Test
    public void shouldCorrectlySerialiseLineStringsInGeometryCollection() {
        // given
        LineString lineString = GeoJson.lineString(GeoJson.point(1, 2), GeoJson.point(3, 5), GeoJson.point(19, 13));
        GeometryCollection geometryCollection = GeoJson.geometryCollection(lineString);
        getMorphia().getMapper().addMappedClass(Point.class);
        // when
        DBObject dbObject = getMorphia().toDBObject(geometryCollection);
        Assert.assertThat(dbObject, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  {" + (((((((((("  type: 'GeometryCollection', " + "  geometries: ") + "  [") + "    {") + "     type: 'LineString', ") + "     coordinates: [ [ 2.0,  1.0],") + "                    [ 5.0,  3.0],") + "                    [13.0, 19.0] ]") + "    },") + "  ]") + "}"))));
    }

    @Test
    public void shouldCorrectlySerialiseMultiPointsInGeometryCollection() {
        // given
        MultiPoint multiPoint = GeoJson.multiPoint(GeoJson.point(1, 2), GeoJson.point(3, 5), GeoJson.point(19, 13));
        GeometryCollection geometryCollection = GeoJson.geometryCollection(multiPoint);
        // when
        DBObject dbObject = getMorphia().toDBObject(geometryCollection);
        Assert.assertThat(dbObject, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  {" + ((((((((((("  type: 'GeometryCollection', " + "  geometries: ") + "  [") + "    {") + "     type: 'MultiPoint', ") + "     coordinates: [ [ 2.0,  1.0],") + "                    [ 5.0,  3.0],") + "                    [13.0, 19.0] ]") + "    },") + "  ]") + " }") + "}"))));
    }

    @Test
    public void shouldCorrectlySerialiseMultiPolygonsInGeometryCollection() {
        // given
        MultiPolygon multiPolygon = GeoJson.multiPolygon(GeoJson.polygon(GeoJson.lineString(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0), GeoJson.point(1.1, 2.0))), GeoJson.polygon(GeoJson.lineString(GeoJson.point(1.2, 3.0), GeoJson.point(2.5, 4.5), GeoJson.point(6.7, 1.9), GeoJson.point(1.2, 3.0)), GeoJson.lineString(GeoJson.point(3.5, 2.4), GeoJson.point(1.7, 2.8), GeoJson.point(3.5, 2.4))));
        GeometryCollection geometryCollection = GeoJson.geometryCollection(multiPolygon);
        // when
        DBObject dbObject = getMorphia().toDBObject(geometryCollection);
        Assert.assertThat(dbObject, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  {" + ((((((((((((((((((((((((("  type: 'GeometryCollection', " + "  geometries: ") + "  [") + "    {") + "     type: 'MultiPolygon', ") + "     coordinates: [ [ [ [ 2.0, 1.1],") + "                        [ 3.5, 2.3],") + "                        [ 1.0, 3.7],") + "                        [ 2.0, 1.1],") + "                      ]") + "                    ],") + "                    [ [ [ 3.0, 1.2],") + "                        [ 4.5, 2.5],") + "                        [ 1.9, 6.7],") + "                        [ 3.0, 1.2] ") + "                      ],") + "                      [ [ 2.4, 3.5],") + "                        [ 2.8, 1.7],") + "                        [ 2.4, 3.5] ") + "                      ],") + "                    ]") + "                  ]") + "    }") + "  ]") + " }") + "}"))));
    }

    @Test
    public void shouldCorrectlySerialisePointsInGeometryCollection() {
        // given
        Point point = GeoJson.point(3.0, 7.0);
        GeometryCollection geometryCollection = GeoJson.geometryCollection(point);
        // when
        DBObject dbObject = getMorphia().toDBObject(geometryCollection);
        // then use the underlying driver to ensure it was persisted correctly to the database
        Assert.assertThat(dbObject, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  {" + (((((((("  type: 'GeometryCollection', " + "  geometries: ") + "  [") + "    {") + "     type: 'Point', ") + "     coordinates: [7.0, 3.0]") + "    }, ") + "  ]") + "}"))));
    }

    @Test
    public void shouldCorrectlySerialisePolygonsInGeometryCollection() {
        // given
        Polygon polygonWithHoles = GeoJson.polygon(GeoJson.lineString(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0), GeoJson.point(1.1, 2.0)), GeoJson.lineString(GeoJson.point(1.5, 2.0), GeoJson.point(1.9, 2.0), GeoJson.point(1.9, 1.8), GeoJson.point(1.5, 2.0)), GeoJson.lineString(GeoJson.point(2.2, 2.1), GeoJson.point(2.4, 1.9), GeoJson.point(2.4, 1.7), GeoJson.point(2.1, 1.8), GeoJson.point(2.2, 2.1)));
        GeometryCollection geometryCollection = GeoJson.geometryCollection(polygonWithHoles);
        // when
        DBObject dbObject = getMorphia().toDBObject(geometryCollection);
        Assert.assertThat(dbObject, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  {" + (((((((((((((((((((((((((("  type: 'GeometryCollection', " + "  geometries: ") + "  [") + "    {") + "     type: 'Polygon', ") + "     coordinates: ") + "       [ [ [ 2.0, 1.1],") + "           [ 3.5, 2.3],") + "           [ 1.0, 3.7],") + "           [ 2.0, 1.1] ") + "         ],") + "         [ [ 2.0, 1.5],") + "           [ 2.0, 1.9],") + "           [ 1.8, 1.9],") + "           [ 2.0, 1.5] ") + "         ],") + "         [ [ 2.1, 2.2],") + "           [ 1.9, 2.4],") + "           [ 1.7, 2.4],") + "           [ 1.8, 2.1],") + "           [ 2.1, 2.2] ") + "         ]") + "       ]") + "    },") + "  ]") + " }") + "}"))));
    }
}

