package dev.morphia.geo;


import dev.morphia.TestBase;
import dev.morphia.annotations.Indexed;
import dev.morphia.utils.IndexDirection;
import org.junit.Test;


public class GeoJsonIndexTest extends TestBase {
    @Test(expected = Exception.class)
    public void shouldErrorWhenCreatingA2dIndexOnGeoJson() {
        // given
        GeoJsonIndexTest.Place pointB = new GeoJsonIndexTest.Place(GeoJson.point(3.1, 7.5), "Point B");
        getDs().save(pointB);
        // when
        getDs().ensureIndexes();
        // "location object expected, location array not in correct format", code : 13654
    }

    @SuppressWarnings("unused")
    private static final class Place {
        @Indexed(IndexDirection.GEO2D)
        private Point location;

        private String name;

        private Place(final Point location, final String name) {
            this.location = location;
            this.name = name;
        }

        private Place() {
        }
    }
}

