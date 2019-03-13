package dev.morphia.query;


import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.geo.AllTheThings;
import dev.morphia.geo.Area;
import dev.morphia.geo.City;
import dev.morphia.geo.GeoJson;
import dev.morphia.geo.Point;
import dev.morphia.geo.PointBuilder;
import dev.morphia.geo.Regions;
import dev.morphia.geo.Route;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoNearQueriesTest extends TestBase {
    @Test
    public void shouldFindAreasCloseToAGivenPointWithinARadiusOfMeters() {
        // given
        Area sevilla = new Area("Spain", GeoJson.polygon(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build(), PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build()));
        getDs().save(sevilla);
        Area newYork = new Area("New York", GeoJson.polygon(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build(), PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build()));
        getDs().save(newYork);
        Area london = new Area("London", GeoJson.polygon(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build(), PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build()));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Area> routesOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Area.class).field("area").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build(), 20000).find());
        // then
        Assert.assertThat(routesOrderedByDistanceFromLondon.size(), CoreMatchers.is(1));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
    }

    @Test
    public void shouldFindAreasOrderedByDistanceFromAGivenPoint() {
        // given
        Area sevilla = new Area("Spain", GeoJson.polygon(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build(), PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build()));
        getDs().save(sevilla);
        Area newYork = new Area("New York", GeoJson.polygon(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build(), PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build()));
        getDs().save(newYork);
        Area london = new Area("London", GeoJson.polygon(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build(), PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build()));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Area> routesOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Area.class).field("area").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build()).find());
        // then
        Assert.assertThat(routesOrderedByDistanceFromLondon.size(), CoreMatchers.is(3));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(1), CoreMatchers.is(sevilla));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(2), CoreMatchers.is(newYork));
    }

    @Test
    public void shouldFindNearAPoint() {
        // given
        double latitude = 51.5286416;
        double longitude = -0.1015987;
        Datastore datastore = getDs();
        City london = new City("London", GeoJson.point(51.5286416, (-0.1015987)));
        datastore.save(london);
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        datastore.save(manchester);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        datastore.save(sevilla);
        getDs().ensureIndexes();
        final Point searchPoint = PointBuilder.pointBuilder().latitude(50).longitude(0.1278).build();
        List<City> cities = TestBase.toList(datastore.find(City.class).field("location").near(searchPoint, 200000).find());
        Assert.assertThat(cities.size(), CoreMatchers.is(1));
        Assert.assertThat(cities.get(0), CoreMatchers.is(london));
        cities = TestBase.toList(datastore.find(City.class).field("location").nearSphere(searchPoint, 200000.0, null).find());
        Assert.assertThat(cities.size(), CoreMatchers.is(1));
        Assert.assertThat(cities.get(0), CoreMatchers.is(london));
        Assert.assertThat(TestBase.toList(datastore.find(City.class).field("location").near(searchPoint, 200000.0, 195000.0).find()).size(), CoreMatchers.is(0));
        Assert.assertThat(TestBase.toList(datastore.find(City.class).field("location").nearSphere(searchPoint, 200000.0, 195000.0).find()).size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldFindCitiesOrderedByDistance() {
        double latitudeLondon = 51.5286416;
        double longitudeLondon = -0.1015987;
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        getDs().save(manchester);
        City london = new City("London", GeoJson.point(latitudeLondon, longitudeLondon));
        getDs().save(london);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        getDs().save(sevilla);
        getDs().ensureIndexes();
        List<City> cities = TestBase.toList(getDs().find(City.class).field("location").near(PointBuilder.pointBuilder().latitude(latitudeLondon).longitude(longitudeLondon).build()).find());
        Assert.assertThat(cities.size(), CoreMatchers.is(3));
        Assert.assertThat(cities.get(0), CoreMatchers.is(london));
        Assert.assertThat(cities.get(1), CoreMatchers.is(manchester));
        Assert.assertThat(cities.get(2), CoreMatchers.is(sevilla));
        cities = TestBase.toList(getDs().find(City.class).field("location").nearSphere(PointBuilder.pointBuilder().latitude(latitudeLondon).longitude(longitudeLondon).build()).find());
        Assert.assertThat(cities.size(), CoreMatchers.is(3));
        Assert.assertThat(cities.get(0), CoreMatchers.is(london));
        Assert.assertThat(cities.get(1), CoreMatchers.is(manchester));
        Assert.assertThat(cities.get(2), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindGeometryCollectionsCloseToAGivenPointWithinARadiusOfMeters() {
        checkMinServerVersion(2.6);
        // given
        AllTheThings sevilla = new AllTheThings("Spain", GeoJson.geometryCollection(GeoJson.multiPoint(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202))), GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        // insert something that's not a geocollection
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        AllTheThings london = new AllTheThings("London", GeoJson.geometryCollection(GeoJson.point(53.4722454, (-2.2235922)), GeoJson.lineString(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<AllTheThings> list = TestBase.toList(getDs().find(AllTheThings.class).field("everything").near(PointBuilder.pointBuilder().latitude(37.3753707).longitude((-5.9550583)).build(), 20000).find());
        // then
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(list.get(0), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindGeometryCollectionsOrderedByDistanceFromAGivenPoint() {
        checkMinServerVersion(2.6);
        // given
        AllTheThings sevilla = new AllTheThings("Spain", GeoJson.geometryCollection(GeoJson.multiPoint(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202))), GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        // insert something that's not a geocollection
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        AllTheThings london = new AllTheThings("London", GeoJson.geometryCollection(GeoJson.point(53.4722454, (-2.2235922)), GeoJson.lineString(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<AllTheThings> resultsOrderedByDistanceFromLondon = TestBase.toList(getDs().find(AllTheThings.class).field("everything").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build()).find());
        // then
        Assert.assertThat(resultsOrderedByDistanceFromLondon.size(), CoreMatchers.is(2));
        Assert.assertThat(resultsOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
        Assert.assertThat(resultsOrderedByDistanceFromLondon.get(1), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindRegionsCloseToAGivenPointWithinARadiusOfMeters() {
        checkMinServerVersion(2.6);
        // given
        Regions sevilla = new Regions("Spain", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        Regions london = new Regions("London", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425)), GeoJson.point(51.507780365645885, (-0.21786745637655258))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Regions> regionsOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Regions.class).field("regions").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build(), 20000).find());
        // then
        Assert.assertThat(regionsOrderedByDistanceFromLondon.size(), CoreMatchers.is(1));
        Assert.assertThat(regionsOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
    }

    @Test
    public void shouldFindRegionsOrderedByDistanceFromAGivenPoint() {
        checkMinServerVersion(2.6);
        // given
        Regions sevilla = new Regions("Spain", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        Regions london = new Regions("London", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425)), GeoJson.point(51.507780365645885, (-0.21786745637655258))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Regions> regionsOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Regions.class).field("regions").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build()).find());
        // then
        Assert.assertThat(regionsOrderedByDistanceFromLondon.size(), CoreMatchers.is(3));
        Assert.assertThat(regionsOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
        Assert.assertThat(regionsOrderedByDistanceFromLondon.get(1), CoreMatchers.is(sevilla));
        Assert.assertThat(regionsOrderedByDistanceFromLondon.get(2), CoreMatchers.is(usa));
    }

    @Test
    public void shouldFindRoutesCloseToAGivenPointWithinARadiusOfMeters() {
        // given
        Route sevilla = new Route("Spain", GeoJson.lineString(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build()));
        getDs().save(sevilla);
        Route newYork = new Route("New York", GeoJson.lineString(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build()));
        getDs().save(newYork);
        Route london = new Route("London", GeoJson.lineString(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build()));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Route> routesOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Route.class).field("route").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build(), 20000).find());
        // then
        Assert.assertThat(routesOrderedByDistanceFromLondon.size(), CoreMatchers.is(1));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
    }

    @Test
    public void shouldFindRoutesOrderedByDistanceFromAGivenPoint() {
        // given
        Route sevilla = new Route("Spain", GeoJson.lineString(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build()));
        getDs().save(sevilla);
        Route newYork = new Route("New York", GeoJson.lineString(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build()));
        getDs().save(newYork);
        Route london = new Route("London", GeoJson.lineString(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build()));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Route> routesOrderedByDistanceFromLondon = TestBase.toList(getDs().find(Route.class).field("route").near(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build()).find());
        // then
        Assert.assertThat(routesOrderedByDistanceFromLondon.size(), CoreMatchers.is(3));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(0), CoreMatchers.is(london));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(1), CoreMatchers.is(sevilla));
        Assert.assertThat(routesOrderedByDistanceFromLondon.get(2), CoreMatchers.is(newYork));
    }
}

