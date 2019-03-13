package dev.morphia.aggregation;


import com.mongodb.client.MongoCursor;
import dev.morphia.TestBase;
import dev.morphia.aggregation.zipcode.City;
import dev.morphia.aggregation.zipcode.Population;
import dev.morphia.aggregation.zipcode.State;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These tests recreate the example zip code data set aggregations as found in the official documentation.
 *
 * @unknown tutorial/aggregation-zip-code-data-set/ Aggregation with the Zip Code Data Set
 */
public class ZipCodeDataSetTest extends TestBase {
    public static final String MONGO_IMPORT;

    private static final Logger LOG = LoggerFactory.getLogger(ZipCodeDataSetTest.class);

    static {
        String property = System.getProperty("mongodb_server");
        String serverType = (property != null) ? property.replaceAll("-release", "") : "UNKNOWN";
        String path = String.format("/mnt/jenkins/mongodb/%s/%s/bin/mongoimport", serverType, property);
        if (new File(path).exists()) {
            MONGO_IMPORT = path;
        } else {
            MONGO_IMPORT = "/usr/local/bin/mongoimport";
        }
    }

    @Test
    public void averageCitySizeByState() {
        Assume.assumeTrue(new File(ZipCodeDataSetTest.MONGO_IMPORT).exists());
        installSampleData();
        AggregationPipeline pipeline = getDs().createAggregation(City.class).group(Group.id(Group.grouping("state"), Group.grouping("city")), Group.grouping("pop", Group.sum("pop"))).group("_id.state", Group.grouping("avgCityPop", Group.average("pop")));
        validate(((MongoCursor<Population>) (pipeline.aggregate(Population.class))), "MN", 5372);
    }

    @Test
    public void populationsAbove10M() {
        Assume.assumeTrue(new File(ZipCodeDataSetTest.MONGO_IMPORT).exists());
        installSampleData();
        Query<Object> query = getDs().getQueryFactory().createQuery(getDs());
        AggregationPipeline pipeline = getDs().createAggregation(City.class).group("state", Group.grouping("totalPop", Group.sum("pop"))).match(query.field("totalPop").greaterThanOrEq(10000000));
        validate(((MongoCursor<Population>) (pipeline.aggregate(Population.class))), "CA", 29754890);
        validate(((MongoCursor<Population>) (pipeline.aggregate(Population.class))), "OH", 10846517);
    }

    @Test
    public void smallestAndLargestCities() {
        Assume.assumeTrue(new File(ZipCodeDataSetTest.MONGO_IMPORT).exists());
        installSampleData();
        getMorphia().mapPackage(getClass().getPackage().getName());
        AggregationPipeline pipeline = getDs().createAggregation(City.class).group(Group.id(Group.grouping("state"), Group.grouping("city")), Group.grouping("pop", Group.sum("pop"))).sort(Sort.ascending("pop")).group("_id.state", Group.grouping("biggestCity", Group.last("_id.city")), Group.grouping("biggestPop", Group.last("pop")), Group.grouping("smallestCity", Group.first("_id.city")), Group.grouping("smallestPop", Group.first("pop"))).project(Projection.projection("_id").suppress(), Projection.projection("state", "_id"), Projection.projection("biggestCity", Projection.projection("name", "biggestCity"), Projection.projection("pop", "biggestPop")), Projection.projection("smallestCity", Projection.projection("name", "smallestCity"), Projection.projection("pop", "smallestPop")));
        MongoCursor<State> cursor = ((MongoCursor<State>) (pipeline.aggregate(State.class)));
        try {
            Map<String, State> states = new HashMap<String, State>();
            while (cursor.hasNext()) {
                State state = cursor.next();
                states.put(state.getState(), state);
            } 
            State state = states.get("SD");
            Assert.assertEquals("SIOUX FALLS", state.getBiggest().getName());
            Assert.assertEquals(102046, state.getBiggest().getPopulation().longValue());
            Assert.assertEquals("ZEONA", state.getSmallest().getName());
            Assert.assertEquals(8, state.getSmallest().getPopulation().longValue());
        } finally {
            cursor.close();
        }
    }
}

