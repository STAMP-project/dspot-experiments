package dev.morphia.geo;


import com.mongodb.DBObject;
import com.mongodb.MongoException;
import dev.morphia.TestBase;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.testutil.IndexMatcher;
import dev.morphia.testutil.JSONMatcher;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test shows how to define an entity that uses the legacy co-ordinate pairs standard, which works with MongoDB server versions 2.2
 * and
 * earlier.  If you are using a server version higher than 2.2 (i.e. 2.4 and onwards) you should store location information as <a
 * href="http://docs.mongodb.org/manual/reference/glossary/#term-geojson">GeoJSON</a> and consult the documentation for indexes and queries
 * that work on this format.  Storing the location as GeoJSON gives you access to a wider range of queries.
 * <p/>
 * This set of tests should run on all server versions.
 */
public class LegacyCoordsTest extends TestBase {
    @Test
    public void shouldCreateA2dIndexOnAnEntityWithArrayOfCoordinates() {
        // given
        PlaceWithLegacyCoords pointA = new PlaceWithLegacyCoords(new double[]{ 3.1, 5.2 }, "Point A");
        getDs().save(pointA);
        // when
        getDs().ensureIndexes();
        // then
        List<DBObject> indexes = getDs().getCollection(PlaceWithLegacyCoords.class).getIndexInfo();
        Assert.assertThat(indexes, IndexMatcher.hasIndexNamed("location_2d"));
    }

    @Test
    public void shouldFindPointWithExactMatch() {
        // given
        final PlaceWithLegacyCoords nearbyPlace = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Nearby Place");
        getDs().save(nearbyPlace);
        getDs().ensureIndexes();
        // when
        List<PlaceWithLegacyCoords> found = TestBase.toList(getDs().find(PlaceWithLegacyCoords.class).field("location").equal(new double[]{ 1.1, 2.3 }).find());
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(found.size(), CoreMatchers.is(1));
        Assert.assertThat(found.get(0), CoreMatchers.is(nearbyPlace));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldGenerateCorrectQueryForNearSphereWithRadius() {
        // when
        Query<PlaceWithLegacyCoords> query = getDs().find(PlaceWithLegacyCoords.class).field("location").near(42.08563, (-87.99822), 2, true);
        // then
        Assert.assertThat(query.getQueryObject().toString(), JSONMatcher.jsonEqual(("{ \"location\" : " + ("{ \"$nearSphere\" : [ 42.08563 , -87.99822] , " + "\"$maxDistance\" : 2.0}}"))));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldGenerateCorrectQueryForNearWithMaxDistance() {
        // when
        Query<PlaceWithLegacyCoords> query = getDs().find(PlaceWithLegacyCoords.class).field("location").near(42.08563, (-87.99822), 2);
        // then
        Assert.assertThat(query.getQueryObject().toString(), JSONMatcher.jsonEqual(("{ \"location\" : " + ("{ \"$near\" : [ 42.08563 , -87.99822] , " + "\"$maxDistance\" : 2.0}}"))));
    }

    @Test
    public void shouldNotReturnAnyResultsIfNoLocationsWithinGivenRadius() {
        // given
        final PlaceWithLegacyCoords nearbyPlace = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Nearby Place");
        getDs().save(nearbyPlace);
        getDs().ensureIndexes();
        // when
        Query<PlaceWithLegacyCoords> locationQuery = getDs().find(PlaceWithLegacyCoords.class).field("location").near(1.0, 2.0, 0.1);
        // then
        Assert.assertThat(locationQuery.count(), CoreMatchers.is(0L));
        Assert.assertThat(locationQuery.find(new FindOptions().limit(1)).tryNext(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnAllLocationsOrderedByDistanceFromQueryLocationWhenPerformingNearQuery() {
        // given
        final PlaceWithLegacyCoords nearbyPlace = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Nearby Place");
        getDs().save(nearbyPlace);
        final PlaceWithLegacyCoords furtherAwayPlace = new PlaceWithLegacyCoords(new double[]{ 10.1, 12.3 }, "Further Away Place");
        getDs().save(furtherAwayPlace);
        getDs().ensureIndexes();
        // when
        final List<PlaceWithLegacyCoords> found = TestBase.toList(getDs().find(PlaceWithLegacyCoords.class).field("location").near(1.0, 2.0).find());
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(found.size(), CoreMatchers.is(2));
        Assert.assertThat(found.get(0), CoreMatchers.is(nearbyPlace));
        Assert.assertThat(found.get(1), CoreMatchers.is(furtherAwayPlace));
    }

    @Test
    public void shouldReturnOnlyThosePlacesWithinTheGivenRadius() {
        // given
        final PlaceWithLegacyCoords nearbyPlace = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Nearby Place");
        getDs().save(nearbyPlace);
        final PlaceWithLegacyCoords furtherAwayPlace = new PlaceWithLegacyCoords(new double[]{ 10.1, 12.3 }, "Further Away Place");
        getDs().save(furtherAwayPlace);
        getDs().ensureIndexes();
        // when
        final List<PlaceWithLegacyCoords> found = TestBase.toList(getDs().find(PlaceWithLegacyCoords.class).field("location").near(1.0, 2.0, 1.5).find());
        // then
        Assert.assertThat(found, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(found.size(), CoreMatchers.is(1));
        Assert.assertThat(found.get(0), CoreMatchers.is(nearbyPlace));
    }

    @Test(expected = MongoException.class)
    public void shouldThrowAnExceptionIfQueryingWithoutA2dIndex() {
        // given
        final PlaceWithLegacyCoords nearbyPlace = new PlaceWithLegacyCoords(new double[]{ 1.1, 2.3 }, "Nearby Place");
        getDs().save(nearbyPlace);
        List<DBObject> indexes = getDs().getCollection(PlaceWithLegacyCoords.class).getIndexInfo();
        Assert.assertThat(indexes, IndexMatcher.doesNotHaveIndexNamed("location_2d"));
        // when
        getDs().find(PlaceWithLegacyCoords.class).field("location").near(0, 0).find(new FindOptions().limit(1)).tryNext();
        // then expect the Exception
    }
}

