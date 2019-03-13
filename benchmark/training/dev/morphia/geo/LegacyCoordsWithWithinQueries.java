package dev.morphia.geo;


import dev.morphia.TestBase;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Shape;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Although this tests the old legacy coordinate system of storing location, this set of tests shows the functionality that's available
 * with
 * these coordinates in later versions of the server that also support GeoJSON.  In order to get full geo querying functionality, you
 * should
 * use GeoJSON for storing your location not legacy co-ordinates.
 * <p/>
 * This test requires server version 2.4 or above as it uses $geoWithin.
 */
public class LegacyCoordsWithWithinQueries extends TestBase {
    @Test
    public void shouldNotReturnAnyPointsIfNothingInsideCircle() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords point = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(point);
        getDs().ensureIndexes();
        // when - search with circle that does not cover the only point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.center(new Shape.Point(2, 2), 0.5)).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldNotReturnAnyValuesWhenTheQueryBoxDoesNotContainAnyPoints() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords point = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(point);
        getDs().ensureIndexes();
        // when - search with a box that does not cover the point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(0.5, 0.5))).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldNotReturnAnyValuesWhenTheQueryPolygonDoesNotContainAnyPoints() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords point = new PlaceWithLegacyCoords(new double[]{ 7.3, 9.2 }, "place");
        getDs().save(point);
        getDs().ensureIndexes();
        // when - search with polygon that's nowhere near the given point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.polygon(new Shape.Point(0, 0), new Shape.Point(0, 5), new Shape.Point(2, 3), new Shape.Point(1, 0))).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnAPointThatIsFullyWithinQueryPolygon() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords expectedPoint = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(expectedPoint);
        getDs().ensureIndexes();
        // when - search with polygon that contains expected point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.polygon(new Shape.Point(0, 0), new Shape.Point(0, 5), new Shape.Point(2, 3), new Shape.Point(1, 0))).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(expectedPoint));
    }

    @Test
    public void shouldReturnOnlyThePointsWithinTheGivenCircle() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords expectedPoint = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Near point");
        getDs().save(expectedPoint);
        final PlaceWithLegacyCoords otherPoint = new PlaceWithLegacyCoords(new double[]{ 3.1, 5.2 }, "Further point");
        getDs().save(otherPoint);
        getDs().ensureIndexes();
        // when
        final List<PlaceWithLegacyCoords> found = TestBase.toList(getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.center(new Shape.Point(1, 2), 1.1)).find());
        // then
        Assert.assertThat(found.size(), CoreMatchers.is(1));
        Assert.assertThat(found.get(0), CoreMatchers.is(expectedPoint));
    }

    @Test
    public void shouldReturnPointOnBoundaryOfQueryCircle() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords expectedPoint = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(expectedPoint);
        getDs().ensureIndexes();
        // when - search with circle with an edge that exactly covers the point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.center(new Shape.Point(0, 1), 1)).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(expectedPoint));
    }

    @Test
    public void shouldReturnPointOnBoundaryOfQueryCircleWithSphericalGeometry() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords expectedPoint = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(expectedPoint);
        getDs().ensureIndexes();
        // when - search with circle with an edge that exactly covers the point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.centerSphere(new Shape.Point(0, 1), 1)).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(expectedPoint));
    }

    @Test
    public void shouldReturnPointThatIsFullyInsideTheQueryBox() {
        // given
        checkMinServerVersion(2.4);
        final PlaceWithLegacyCoords expectedPoint = new PlaceWithLegacyCoords(new double[]{ 1, 1 }, "place");
        getDs().save(expectedPoint);
        getDs().ensureIndexes();
        // when - search with a box that covers the whole point
        final PlaceWithLegacyCoords found = getDs().find(PlaceWithLegacyCoords.class).field("location").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(2, 2))).find(new FindOptions().limit(1)).tryNext();
        // then
        Assert.assertThat(found, CoreMatchers.is(expectedPoint));
    }
}

