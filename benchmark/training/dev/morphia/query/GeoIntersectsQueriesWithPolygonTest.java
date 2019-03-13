package dev.morphia.query;


import dev.morphia.TestBase;
import dev.morphia.geo.AllTheThings;
import dev.morphia.geo.Area;
import dev.morphia.geo.City;
import dev.morphia.geo.GeoJson;
import dev.morphia.geo.Regions;
import dev.morphia.geo.Route;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoIntersectsQueriesWithPolygonTest extends TestBase {
    @Test
    public void shouldFindAPointThatLiesInAQueryPolygon() {
        // given
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        getDs().save(manchester);
        City london = new City("London", GeoJson.point(51.5286416, (-0.1015987)));
        getDs().save(london);
        City sevilla = new City("Sevilla", GeoJson.point(37.4057731, (-5.966287)));
        getDs().save(sevilla);
        getDs().ensureIndexes();
        // when
        List<City> matchingCity = TestBase.toList(getDs().find(City.class).field("location").intersects(GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338)))).find());
        // then
        Assert.assertThat(matchingCity.size(), CoreMatchers.is(1));
        Assert.assertThat(matchingCity.get(0), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindAreasThatAPolygonIntersects() {
        // given
        Area sevilla = new Area("Spain", GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338))));
        getDs().save(sevilla);
        Area newYork = new Area("New York", GeoJson.polygon(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193)), GeoJson.point(40.75981395319104, (-73.98302106186748))));
        getDs().save(newYork);
        Area london = new Area("London", GeoJson.polygon(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425)), GeoJson.point(51.507780365645885, (-0.21786745637655258))));
        getDs().save(london);
        getDs().ensureIndexes();
        // when
        List<Area> areaContainingPoint = TestBase.toList(getDs().find(Area.class).field("area").intersects(GeoJson.polygon(GeoJson.point(37.4056048, (-5.9666089)), GeoJson.point(37.404497, (-5.9640557)), GeoJson.point(37.407239, (-5.962988)), GeoJson.point(37.4056048, (-5.9666089)))).find());
        // then
        Assert.assertThat(areaContainingPoint.size(), CoreMatchers.is(1));
        Assert.assertThat(areaContainingPoint.get(0), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindGeometryCollectionsWhereTheGivenPointIntersectsWithOneOfTheEntities() {
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
        List<AllTheThings> everythingInTheUK = TestBase.toList(getDs().find(AllTheThings.class).field("everything").intersects(GeoJson.polygon(GeoJson.point(37.4056048, (-5.9666089)), GeoJson.point(37.404497, (-5.9640557)), GeoJson.point(37.407239, (-5.962988)), GeoJson.point(37.4056048, (-5.9666089)))).find());
        // then
        Assert.assertThat(everythingInTheUK.size(), CoreMatchers.is(1));
        Assert.assertThat(everythingInTheUK.get(0), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindRegionsThatAPolygonCrosses() {
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
        List<Regions> regionsInTheUK = TestBase.toList(getDs().find(Regions.class).field("regions").intersects(GeoJson.polygon(GeoJson.point(37.4056048, (-5.9666089)), GeoJson.point(37.404497, (-5.9640557)), GeoJson.point(37.407239, (-5.962988)), GeoJson.point(37.4056048, (-5.9666089)))).find());
        // then
        Assert.assertThat(regionsInTheUK.size(), CoreMatchers.is(1));
        Assert.assertThat(regionsInTheUK.get(0), CoreMatchers.is(sevilla));
    }

    @Test
    public void shouldFindRoutesThatCrossAQueryPolygon() {
        // given
        Route sevilla = new Route("Spain", GeoJson.lineString(GeoJson.point(37.4056048, (-5.9666089)), GeoJson.point(37.404497, (-5.9640557))));
        getDs().save(sevilla);
        Route newYork = new Route("New York", GeoJson.lineString(GeoJson.point(40.75981395319104, (-73.98302106186748)), GeoJson.point(40.7636824529618, (-73.98049869574606)), GeoJson.point(40.76962974853814, (-73.97964206524193))));
        getDs().save(newYork);
        Route london = new Route("London", GeoJson.lineString(GeoJson.point(51.507780365645885, (-0.21786745637655258)), GeoJson.point(51.50802478194237, (-0.21474729292094707)), GeoJson.point(51.5086863655597, (-0.20895397290587425))));
        getDs().save(london);
        Route londonToParis = new Route("London To Paris", GeoJson.lineString(GeoJson.point(51.5286416, (-0.1015987)), GeoJson.point(48.858859, 2.3470599)));
        getDs().save(londonToParis);
        getDs().ensureIndexes();
        // when
        List<Route> routeContainingPoint = TestBase.toList(getDs().find(Route.class).field("route").intersects(GeoJson.polygon(GeoJson.point(37.40759155713022, (-5.964911067858338)), GeoJson.point(37.40341208875179, (-5.9643941558897495)), GeoJson.point(37.40297396667302, (-5.970452763140202)), GeoJson.point(37.40759155713022, (-5.964911067858338)))).find());
        // then
        Assert.assertThat(routeContainingPoint.size(), CoreMatchers.is(1));
        Assert.assertThat(routeContainingPoint.get(0), CoreMatchers.is(sevilla));
    }
}

