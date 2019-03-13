package dev.morphia.query;


import com.mongodb.MongoQueryException;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestGeoQueries extends TestBase {
    @Test
    public void testGeoWithinBox() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(2, 2))).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testGeoWithinOutsideBox() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(0.4, 0.5))).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void testGeoWithinPolygon() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 0, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.polygon(new Shape.Point(0, 0), new Shape.Point(0, 5), new Shape.Point(2, 3), new Shape.Point(2, 0))).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testGeoWithinPolygon2() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 10, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.polygon(new Shape.Point(0, 0), new Shape.Point(0, 5), new Shape.Point(2, 3), new Shape.Point(2, 0))).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void testGeoWithinRadius() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.center(new Shape.Point(0, 1), 1.1)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testGeoWithinRadius2() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.center(new Shape.Point(0.5, 0.5), 0.77)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testGeoWithinRadiusSphere() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.centerSphere(new Shape.Point(0, 1), 1)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testNear() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").near(0, 0).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testNearMaxDistance() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").near(0, 0, 1.5).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
        final TestGeoQueries.Place notFound = getDs().find(TestGeoQueries.Place.class).field("loc").near(0, 0, 1).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(notFound);
    }

    @Test(expected = MongoQueryException.class)
    public void testNearNoIndex() {
        checkMinServerVersion(2.4);
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").near(0, 0).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void testWithinBox() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(2, 2))).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testWithinOutsideBox() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.box(new Shape.Point(0, 0), new Shape.Point(0.4, 0.5))).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void testWithinOutsideRadius() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.center(new Shape.Point(2, 2), 0.4)).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void testWithinRadius() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.center(new Shape.Point(0, 1), 1.1)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testWithinRadius2() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.center(new Shape.Point(0.5, 0.5), 0.77)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Test
    public void testWithinRadiusSphere() {
        checkMinServerVersion(2.4);
        getDs().ensureIndexes();
        final TestGeoQueries.Place place1 = new TestGeoQueries.Place("place1", new double[]{ 1, 1 });
        getDs().save(place1);
        final TestGeoQueries.Place found = getDs().find(TestGeoQueries.Place.class).field("loc").within(Shape.centerSphere(new Shape.Point(0, 1), 1)).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
    }

    @Entity
    private static class Place {
        @Id
        private ObjectId id;

        private String name;

        @Indexed(IndexDirection.GEO2D)
        private double[] loc;

        private Place() {
        }

        Place(final String name, final double[] loc) {
            this.name = name;
            this.loc = loc;
        }
    }
}

