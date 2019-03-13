package dev.morphia.geo;


import GeometryShapeConverter.LineStringConverter;
import GeometryShapeConverter.MultiLineStringConverter;
import GeometryShapeConverter.MultiPolygonConverter;
import GeometryShapeConverter.PointConverter;
import GeometryShapeConverter.PolygonConverter;
import dev.morphia.TestBase;
import dev.morphia.testutil.JSONMatcher;
import org.junit.Assert;
import org.junit.Test;


public class GeometryShapeConverterTest extends TestBase {
    @Test
    public void shouldConvertAnEntityWithAPolygonGeoJsonType() {
        // given
        GeometryShapeConverter.PolygonConverter converter = new GeometryShapeConverter.PolygonConverter();
        converter.setMapper(getMorphia().getMapper());
        Polygon polygon = GeoJson.polygon(GeoJson.lineString(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0), GeoJson.point(1.1, 2.0)), GeoJson.lineString(GeoJson.point(1.5, 2.0), GeoJson.point(1.9, 2.0), GeoJson.point(1.9, 1.8), GeoJson.point(1.5, 2.0)), GeoJson.lineString(GeoJson.point(2.2, 2.1), GeoJson.point(2.4, 1.9), GeoJson.point(2.4, 1.7), GeoJson.point(2.1, 1.8), GeoJson.point(2.2, 2.1)));
        // when
        Object encodedPolygon = converter.encode(polygon);
        // then
        Assert.assertThat(encodedPolygon.toString(), JSONMatcher.jsonEqual(("  {" + ((((((((((((((((((("  type: 'Polygon', " + "  coordinates: ") + "    [ [ [ 2.0, 1.1],") + "        [ 3.5, 2.3],") + "        [ 1.0, 3.7],") + "        [ 2.0, 1.1] ") + "      ],") + "      [ [ 2.0, 1.5],") + "        [ 2.0, 1.9],") + "        [ 1.8, 1.9],") + "        [ 2.0, 1.5] ") + "      ],") + "      [ [ 2.1, 2.2],") + "        [ 1.9, 2.4],") + "        [ 1.7, 2.4],") + "        [ 1.8, 2.1],") + "        [ 2.1, 2.2] ") + "      ]") + "    ]") + "}"))));
    }

    @Test
    public void shouldCorrectlyEncodePointsIntoEntityDocument() {
        // given
        GeometryShapeConverter.PointConverter pointConverter = new GeometryShapeConverter.PointConverter();
        pointConverter.setMapper(getMorphia().getMapper());
        Point point = GeoJson.point(3.0, 7.0);
        // when
        Object dbObject = pointConverter.encode(point, null);
        // then
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual(("  { " + (("  type : 'Point' , " + "  coordinates : [7, 3]") + "}"))));
    }

    @Test
    public void shouldEncodeAnEntityWithAMultiLineStringGeoJsonType() {
        // given
        GeometryShapeConverter.MultiLineStringConverter converter = new GeometryShapeConverter.MultiLineStringConverter();
        converter.setMapper(getMorphia().getMapper());
        MultiLineString multiLineString = GeoJson.multiLineString(GeoJson.lineString(GeoJson.point(1, 2), GeoJson.point(3, 5), GeoJson.point(19, 13)), GeoJson.lineString(GeoJson.point(1.5, 2.0), GeoJson.point(1.9, 2.0), GeoJson.point(1.9, 1.8), GeoJson.point(1.5, 2.0)));
        // when
        Object encoded = converter.encode(multiLineString);
        // then
        Assert.assertThat(encoded.toString(), JSONMatcher.jsonEqual(("  {" + (((((((((((("  type: 'MultiLineString', " + "  coordinates: ") + "     [ [ [ 2.0,  1.0],") + "         [ 5.0,  3.0],") + "         [13.0, 19.0] ") + "       ], ") + "       [ [ 2.0, 1.5],") + "         [ 2.0, 1.9],") + "         [ 1.8, 1.9],") + "         [ 2.0, 1.5] ") + "       ]") + "     ]") + "}"))));
    }

    @Test
    public void shouldEncodeAnEntityWithAMultiPolygonGeoJsonType() {
        // given
        GeometryShapeConverter.MultiPolygonConverter converter = new GeometryShapeConverter.MultiPolygonConverter();
        converter.setMapper(getMorphia().getMapper());
        Polygon polygonWithHoles = GeoJson.polygon(GeoJson.lineString(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0), GeoJson.point(1.1, 2.0)), GeoJson.lineString(GeoJson.point(1.5, 2.0), GeoJson.point(1.9, 2.0), GeoJson.point(1.9, 1.8), GeoJson.point(1.5, 2.0)), GeoJson.lineString(GeoJson.point(2.2, 2.1), GeoJson.point(2.4, 1.9), GeoJson.point(2.4, 1.7), GeoJson.point(2.1, 1.8), GeoJson.point(2.2, 2.1)));
        MultiPolygon multiPolygon = GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0), GeoJson.point(1.1, 2.0)), polygonWithHoles);
        // when
        Object encoded = converter.encode(multiPolygon);
        // then
        Assert.assertThat(encoded.toString(), JSONMatcher.jsonEqual(("  {" + ((((((((((((((((((((((((("  type: 'MultiPolygon', " + "  coordinates: [ [ [ [ 2.0, 1.1],") + "                     [ 3.5, 2.3],") + "                     [ 1.0, 3.7],") + "                     [ 2.0, 1.1],") + "                   ]") + "                 ],") + "                 [ [ [ 2.0, 1.1],") + "                     [ 3.5, 2.3],") + "                     [ 1.0, 3.7],") + "                     [ 2.0, 1.1] ") + "                   ],") + "                   [ [ 2.0, 1.5],") + "                     [ 2.0, 1.9],") + "                     [ 1.8, 1.9],") + "                     [ 2.0, 1.5] ") + "                   ],") + "                   [ [ 2.1, 2.2],") + "                     [ 1.9, 2.4],") + "                     [ 1.7, 2.4],") + "                     [ 1.8, 2.1],") + "                     [ 2.1, 2.2] ") + "                   ]") + "                 ]") + "               ]") + "}"))));
    }

    @Test
    public void shouldSaveAnEntityWithALineStringGeoJsonType() {
        // given
        GeometryShapeConverter.LineStringConverter converter = new GeometryShapeConverter.LineStringConverter();
        converter.setMapper(getMorphia().getMapper());
        LineString lineString = GeoJson.lineString(GeoJson.point(1, 2), GeoJson.point(3, 5), GeoJson.point(19, 13));
        // when
        Object encodedLineString = converter.encode(lineString);
        // then
        Assert.assertThat(encodedLineString.toString(), JSONMatcher.jsonEqual(("  {" + (((("  type: 'LineString', " + "  coordinates: [ [ 2.0,  1.0],") + "                 [ 5.0,  3.0],") + "                 [13.0, 19.0] ]") + "}"))));
    }
}

