package dev.morphia.query;


import dev.morphia.geo.GeoJson;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test - more complete testing that uses the GeoJson factory is contained in functional Geo tests.
 */
public class GeoJsonTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldErrorIfStartAndEndOfPolygonAreNotTheSame() {
        // expect
        GeoJson.polygon(GeoJson.point(1.1, 2.0), GeoJson.point(2.3, 3.5), GeoJson.point(3.7, 1.0));
    }

    @Test
    public void shouldNotErrorIfPolygonIsEmpty() {
        // expect
        Assert.assertThat(GeoJson.polygon(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

