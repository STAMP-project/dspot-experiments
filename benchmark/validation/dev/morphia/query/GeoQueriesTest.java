package dev.morphia.query;


import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.geo.City;
import dev.morphia.geo.GeoJson;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static dev.morphia.geo.PointBuilder.pointBuilder;


public class GeoQueriesTest extends TestBase {
    @Test
    public void shouldFindCitiesCloseToAGivenPointWithinARadiusOfMeters() {
        // given
        double latitude = 51.5286416;
        double longitude = -0.1015987;
        Datastore datastore = getDs();
        City london = new City("London", GeoJson.point(latitude, longitude));
        datastore.save(london);
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        datastore.save(manchester);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        datastore.save(sevilla);
        getDs().ensureIndexes();
        // when
        List<City> citiesOrderedByDistanceFromLondon = TestBase.toList(datastore.find(City.class).field("location").near(pointBuilder().latitude(latitude).longitude(longitude).build(), 200000).find());
        // then
        Assert.assertThat(citiesOrderedByDistanceFromLondon.size(), CoreMatchers.is(1));
        Assert.assertThat(citiesOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
    }
}

