package dev.morphia.geo;


import dev.morphia.TestBase;
import dev.morphia.testutil.JSONMatcher;
import org.junit.Assert;
import org.junit.Test;


public class GeometryQueryConverterTest extends TestBase {
    @Test
    public void shouldCorrectlyEncodePointsIntoQueryDocument() {
        // given
        GeometryQueryConverter geometryConverter = new GeometryQueryConverter(getMorphia().getMapper());
        geometryConverter.setMapper(getMorphia().getMapper());
        Point point = GeoJson.point(3.0, 7.0);
        // when
        Object dbObject = geometryConverter.encode(point);
        // then
        Assert.assertThat(dbObject.toString(), JSONMatcher.jsonEqual((((("  { $geometry : " + ("  { type : 'Point' , " + "    coordinates : ")) + (point.getCoordinates())) + "  }") + "}")));
    }
}

