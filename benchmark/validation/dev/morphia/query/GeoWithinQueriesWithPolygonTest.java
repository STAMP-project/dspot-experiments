package dev.morphia.query;


import NamedCoordinateReferenceSystem.EPSG_4326_STRICT_WINDING;
import com.mongodb.client.MongoCursor;
import dev.morphia.TestBase;
import dev.morphia.geo.AllTheThings;
import dev.morphia.geo.Area;
import dev.morphia.geo.City;
import dev.morphia.geo.GeoJson;
import dev.morphia.geo.PointBuilder;
import dev.morphia.geo.Polygon;
import dev.morphia.geo.Regions;
import dev.morphia.geo.Route;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoWithinQueriesWithPolygonTest extends TestBase {
    @Test
    public void shouldFindAreasWithinTheUK() {
        // given
        Polygon uk = GeoJson.polygon(PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build());
        Area sevilla = new Area("Spain", GeoJson.polygon(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build(), PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build()));
        getDs().save(sevilla);
        Area newYork = new Area("New York", GeoJson.polygon(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build(), PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build()));
        getDs().save(newYork);
        Area london = new Area("London", GeoJson.polygon(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build(), PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build()));
        getDs().save(london);
        Area europe = new Area("Europe", GeoJson.polygon(PointBuilder.pointBuilder().latitude(58.0).longitude((-10.0)).build(), PointBuilder.pointBuilder().latitude(58.0).longitude(3).build(), PointBuilder.pointBuilder().latitude(48.858859).longitude(3).build(), PointBuilder.pointBuilder().latitude(48.858859).longitude((-10)).build(), PointBuilder.pointBuilder().latitude(58.0).longitude((-10.0)).build()));
        getDs().save(europe);
        getDs().ensureIndexes();
        // when
        MongoCursor<Area> areasInTheUK = getDs().find(Area.class).field("area").within(uk).find();
        // then
        Assert.assertThat(areasInTheUK.next(), CoreMatchers.is(london));
        Assert.assertFalse(areasInTheUK.hasNext());
        if (serverIsAtLeastVersion(3.0)) {
            // should not error
            getDs().find(Area.class).field("area").within(uk, EPSG_4326_STRICT_WINDING).find();
        }
    }

    @Test
    public void shouldFindCitiesInTheUK() {
        // given
        Polygon uk = GeoJson.polygon(PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build());
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        getDs().save(manchester);
        City london = new City("London", GeoJson.point(51.5286416, (-0.1015987)));
        getDs().save(london);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        getDs().save(sevilla);
        getDs().ensureIndexes();
        // when
        List<City> citiesInTheUK = TestBase.toList(getDs().find(City.class).field("location").within(uk).find());
        // then
        Assert.assertThat(citiesInTheUK.size(), CoreMatchers.is(2));
        Assert.assertThat(citiesInTheUK, Matchers.contains(london, manchester));
    }

    @Test
    public void shouldFindGeometryCollectionsWithinTheUK() {
        checkMinServerVersion(2.6);
        // given
        Polygon uk = GeoJson.polygon(PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build());
        AllTheThings sevilla = new AllTheThings("Spain", GeoJson.geometryCollection(GeoJson.multiPoint(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202))), GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        // insert something that's not a geocollection
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        AllTheThings london = new AllTheThings("London", GeoJson.geometryCollection(GeoJson.point(53.4722454, (-2.2235922)), GeoJson.lineString(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<AllTheThings> everythingInTheUK = TestBase.toList(getDs().find(AllTheThings.class).field("everything").within(uk).find());
        // then
        Assert.assertThat(everythingInTheUK.size(), CoreMatchers.is(1));
        Assert.assertThat(everythingInTheUK.get(0), CoreMatchers.is(london));
    }

    @Test
    public void shouldFindRegionsWithinTheUK() {
        checkMinServerVersion(2.6);
        // given
        Polygon uk = GeoJson.polygon(PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build());
        Regions sevilla = new Regions("Spain", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))), GeoJson.polygon(GeoJson.point(37.38744598813355, (-6.001141928136349)), GeoJson.point(37.385990973562, (-6.002588979899883)), GeoJson.point(37.386126928031445, (-6.002463921904564)), GeoJson.point(37.38744598813355, (-6.001141928136349)))));
        getDs().save(sevilla);
        Regions usa = new Regions("US", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))), GeoJson.polygon(GeoJson.point(28.326568258926272, (-81.60542246885598)), GeoJson.point(28.327541397884488, (-81.6022228449583)), GeoJson.point(28.32950334995985, (-81.60564735531807)), GeoJson.point(28.326568258926272, (-81.60542246885598)))));
        getDs().save(usa);
        Regions london = new Regions("London", GeoJson.multiPolygon(GeoJson.polygon(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425)), GeoJson.point(51.507780365645885, (-0.21786745637655258))), GeoJson.polygon(GeoJson.point(51.498216362670064, 0.0074849557131528854), GeoJson.point(51.49176875129342, 0.01821178011596203), GeoJson.point(51.492886897176504, 0.05523204803466797), GeoJson.point(51.49393044412136, 0.06663135252892971), GeoJson.point(51.498216362670064, 0.0074849557131528854))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Regions> regionsInTheUK = TestBase.toList(getDs().find(Regions.class).field("regions").within(uk).find());
        // then
        Assert.assertThat(regionsInTheUK.size(), CoreMatchers.is(1));
        Assert.assertThat(regionsInTheUK.get(0), CoreMatchers.is(london));
    }

    @Test
    public void shouldFindRoutesCompletelyWithinTheUK() {
        // given
        Polygon uk = GeoJson.polygon(PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude(1.78).build(), PointBuilder.pointBuilder().latitude(59).longitude((-10.5)).build(), PointBuilder.pointBuilder().latitude(49.78).longitude((-10.5)).build());
        Route sevilla = new Route("Spain", GeoJson.lineString(PointBuilder.pointBuilder().latitude(37.40759155713022).longitude((-5.964911067858338)).build(), PointBuilder.pointBuilder().latitude(37.40341208875179).longitude((-5.9643941558897495)).build(), PointBuilder.pointBuilder().latitude(37.40297396667302).longitude((-5.970452763140202)).build()));
        getDs().save(sevilla);
        Route newYork = new Route("New York", GeoJson.lineString(PointBuilder.pointBuilder().latitude(40.75981395319104).longitude((-73.98302106186748)).build(), PointBuilder.pointBuilder().latitude(40.7636824529618).longitude((-73.98049869574606)).build(), PointBuilder.pointBuilder().latitude(40.76962974853814).longitude((-73.97964206524193)).build()));
        getDs().save(newYork);
        Route london = new Route("London", GeoJson.lineString(PointBuilder.pointBuilder().latitude(51.507780365645885).longitude((-0.21786745637655258)).build(), PointBuilder.pointBuilder().latitude(51.50802478194237).longitude((-0.21474729292094707)).build(), PointBuilder.pointBuilder().latitude(51.5086863655597).longitude((-0.20895397290587425)).build()));
        getDs().save(london);
        Route londonToParis = new Route("London To Paris", GeoJson.lineString(PointBuilder.pointBuilder().latitude(51.5286416).longitude((-0.1015987)).build(), PointBuilder.pointBuilder().latitude(48.858859).longitude(2.3470599).build()));
        getDs().save(londonToParis);
        getDs().ensureIndexes();
        // when
        List<Route> routesInTheUK = TestBase.toList(getDs().find(Route.class).field("route").within(uk).find());
        // then
        Assert.assertThat(routesInTheUK.size(), CoreMatchers.is(1));
        Assert.assertThat(routesInTheUK.get(0), CoreMatchers.is(london));
    }
}

