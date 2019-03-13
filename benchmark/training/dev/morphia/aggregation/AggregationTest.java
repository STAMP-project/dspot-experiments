/**
 * Copyright (c) 2008 - 2013 MongoDB, Inc. <http://mongodb.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.aggregation;


import CollationStrength.SECONDARY;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import dev.morphia.TestBase;
import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Validation;
import dev.morphia.geo.City;
import dev.morphia.geo.GeoJson;
import dev.morphia.geo.PlaceWithLegacyCoords;
import dev.morphia.geo.Point;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class AggregationTest extends TestBase {
    @Test
    public void testCollation() {
        checkMinServerVersion(3.4);
        getDs().save(Arrays.asList(new AggregationTest.User("john doe", new Date()), new AggregationTest.User("John Doe", new Date())));
        Query query = getDs().find(AggregationTest.User.class).field("name").equal("john doe");
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.User.class).match(query);
        Assert.assertEquals(1, count(pipeline.aggregate(AggregationTest.User.class)));
        Assert.assertEquals(2, count(pipeline.aggregate(AggregationTest.User.class, AggregationOptions.builder().collation(Collation.builder().locale("en").collationStrength(SECONDARY).build()).build())));
    }

    @Test
    public void testBypassDocumentValidation() {
        checkMinServerVersion(3.2);
        getDs().save(Arrays.asList(new AggregationTest.User("john doe", new Date()), new AggregationTest.User("John Doe", new Date())));
        MongoDatabase database = getMongoClient().getDatabase(TestBase.TEST_DB_NAME);
        database.getCollection("out_users").drop();
        database.createCollection("out_users", new CreateCollectionOptions().validationOptions(new ValidationOptions().validator(Document.parse("{ \"age\" : { \"gte\" : 13 } }"))));
        try {
            getDs().createAggregation(AggregationTest.User.class).match(getDs().find(AggregationTest.User.class).field("name").equal("john doe")).out("out_users", AggregationTest.User.class);
            Assert.fail("Document validation should have complained.");
        } catch (MongoCommandException e) {
            // expected
        }
        getDs().createAggregation(AggregationTest.User.class).match(getDs().find(AggregationTest.User.class).field("name").equal("john doe")).out("out_users", AggregationTest.User.class, AggregationOptions.builder().bypassDocumentValidation(true).build());
        Assert.assertEquals(1, getAds().find("out_users", AggregationTest.User.class).count());
    }

    @Test
    public void testDateAggregation() {
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.User.class).group(Group.id(Group.grouping("month", Accumulator.accumulator("$month", "date")), Group.grouping("year", Accumulator.accumulator("$year", "date"))), Group.grouping("count", Accumulator.accumulator("$sum", 1)));
        final DBObject group = getStages().get(0);
        final DBObject id = getDBObject(group, "$group", "_id");
        Assert.assertEquals(new BasicDBObject("$month", "$date"), id.get("month"));
        Assert.assertEquals(new BasicDBObject("$year", "$date"), id.get("year"));
        pipeline.aggregate(AggregationTest.User.class);
    }

    @Test
    public void testSampleStage() {
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.User.class).sample(1);
        final DBObject sample = getStages().get(0);
        Assert.assertEquals(new BasicDBObject("size", 1), sample.get("$sample"));
    }

    @Test
    public void testNullGroupId() {
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.User.class).group(Group.grouping("count", Accumulator.accumulator("$sum", 1)));
        final DBObject group = getStages().get(0);
        Assert.assertNull(group.get("_id"));
        pipeline.aggregate(AggregationTest.User.class);
    }

    @Test
    public void testDateToString() throws ParseException {
        checkMinServerVersion(3.0);
        Date joined = new SimpleDateFormat("yyyy-MM-dd z").parse("2016-05-01 UTC");
        getDs().save(new AggregationTest.User("John Doe", joined));
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.User.class).project(Projection.projection("string", Projection.expression("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date", "$joined"))));
        Iterator<AggregationTest.StringDates> aggregate = pipeline.aggregate(AggregationTest.StringDates.class, AggregationOptions.builder().build());
        while (aggregate.hasNext()) {
            AggregationTest.StringDates next = aggregate.next();
            Assert.assertEquals("2016-05-01", next.string);
        } 
    }

    @Test
    public void testGenericAccumulatorUsage() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.CountResult> aggregation = getDs().createAggregation(AggregationTest.Book.class).group("author", Group.grouping("count", Accumulator.accumulator("$sum", 1))).sort(Sort.ascending("_id")).aggregate(AggregationTest.CountResult.class);
        AggregationTest.CountResult result1 = aggregation.next();
        AggregationTest.CountResult result2 = aggregation.next();
        Assert.assertFalse("Expecting two results", aggregation.hasNext());
        Assert.assertEquals("Dante", result1.getAuthor());
        Assert.assertEquals(3, result1.getCount());
        Assert.assertEquals("Homer", result2.getAuthor());
        Assert.assertEquals(2, result2.getCount());
    }

    @Test
    public void testGeoNearWithGeoJson() {
        // given
        Point londonPoint = GeoJson.point(51.5286416, (-0.1015987));
        City london = new City("London", londonPoint);
        getDs().save(london);
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        getDs().save(manchester);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        getDs().save(sevilla);
        getDs().ensureIndexes();
        // when
        Iterator<City> citiesOrderedByDistanceFromLondon = getDs().createAggregation(City.class).geoNear(GeoNear.builder("distance").setNear(londonPoint).setSpherical(true).build()).aggregate(City.class);
        // then
        Assert.assertTrue(citiesOrderedByDistanceFromLondon.hasNext());
        Assert.assertEquals(london, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(manchester, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(sevilla, citiesOrderedByDistanceFromLondon.next());
        Assert.assertFalse(citiesOrderedByDistanceFromLondon.hasNext());
    }

    @Test
    public void testGeoNearWithLegacyCoords() {
        // given
        double latitude = 51.5286416;
        double longitude = -0.1015987;
        PlaceWithLegacyCoords london = new PlaceWithLegacyCoords(new double[]{ longitude, latitude }, "London");
        getDs().save(london);
        PlaceWithLegacyCoords manchester = new PlaceWithLegacyCoords(new double[]{ -2.2235922, 53.4722454 }, "Manchester");
        getDs().save(manchester);
        PlaceWithLegacyCoords sevilla = new PlaceWithLegacyCoords(new double[]{ -5.9550582, 37.3753708 }, "Sevilla");
        getDs().save(sevilla);
        getDs().ensureIndexes();
        // when
        Iterator<PlaceWithLegacyCoords> citiesOrderedByDistanceFromLondon = getDs().createAggregation(PlaceWithLegacyCoords.class).geoNear(GeoNear.builder("distance").setNear(latitude, longitude).setSpherical(false).build()).aggregate(PlaceWithLegacyCoords.class);
        // then
        Assert.assertTrue(citiesOrderedByDistanceFromLondon.hasNext());
        Assert.assertEquals(london, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(manchester, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(sevilla, citiesOrderedByDistanceFromLondon.next());
        Assert.assertFalse(citiesOrderedByDistanceFromLondon.hasNext());
    }

    @Test
    public void testGeoNearWithSphericalGeometry() {
        // given
        double latitude = 51.5286416;
        double longitude = -0.1015987;
        City london = new City("London", GeoJson.point(latitude, longitude));
        getDs().save(london);
        City manchester = new City("Manchester", GeoJson.point(53.4722454, (-2.2235922)));
        getDs().save(manchester);
        City sevilla = new City("Sevilla", GeoJson.point(37.3753708, (-5.9550582)));
        getDs().save(sevilla);
        getDs().ensureIndexes();
        // when
        Iterator<City> citiesOrderedByDistanceFromLondon = getDs().createAggregation(City.class).geoNear(GeoNear.builder("distance").setNear(latitude, longitude).setSpherical(true).build()).aggregate(City.class);
        // then
        Assert.assertTrue(citiesOrderedByDistanceFromLondon.hasNext());
        Assert.assertEquals(london, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(manchester, citiesOrderedByDistanceFromLondon.next());
        Assert.assertEquals(sevilla, citiesOrderedByDistanceFromLondon.next());
        Assert.assertFalse(citiesOrderedByDistanceFromLondon.hasNext());
    }

    @Test
    public void testLimit() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.Book> aggregate = getDs().createAggregation(AggregationTest.Book.class).limit(2).aggregate(AggregationTest.Book.class);
        int count = 0;
        while (aggregate.hasNext()) {
            aggregate.next();
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    /**
     * Test data pulled from https://docs.mongodb.com/v3.2/reference/operator/aggregation/lookup/
     */
    @Test
    public void testLookup() {
        checkMinServerVersion(3.2);
        getDs().save(Arrays.asList(new AggregationTest.Order(1, "abc", 12, 2), new AggregationTest.Order(2, "jkl", 20, 1), new AggregationTest.Order(3)));
        List<AggregationTest.Inventory> inventories = Arrays.asList(new AggregationTest.Inventory(1, "abc", "product 1", 120), new AggregationTest.Inventory(2, "def", "product 2", 80), new AggregationTest.Inventory(3, "ijk", "product 3", 60), new AggregationTest.Inventory(4, "jkl", "product 4", 70), new AggregationTest.Inventory(5, null, "Incomplete"), new AggregationTest.Inventory(6));
        getDs().save(inventories);
        getDs().createAggregation(AggregationTest.Order.class).lookup("inventory", "item", "sku", "inventoryDocs").out("lookups", AggregationTest.Order.class);
        List<AggregationTest.Order> lookups = TestBase.toList(getAds().createQuery("lookups", AggregationTest.Order.class).order("_id").find());
        Assert.assertEquals(inventories.get(0), lookups.get(0).inventoryDocs.get(0));
        Assert.assertEquals(inventories.get(3), lookups.get(1).inventoryDocs.get(0));
        Assert.assertEquals(inventories.get(4), lookups.get(2).inventoryDocs.get(0));
        Assert.assertEquals(inventories.get(5), lookups.get(2).inventoryDocs.get(1));
    }

    @Test
    public void testOut() {
        checkMinServerVersion(2.6);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        AggregationOptions options = AggregationOptions.builder().build();
        Iterator<AggregationTest.Author> aggregate = getDs().createAggregation(AggregationTest.Book.class).group("author", Group.grouping("books", Group.push("title"))).out(AggregationTest.Author.class, options);
        Assert.assertEquals(2, getDs().getCollection(AggregationTest.Author.class).count());
        AggregationTest.Author author = aggregate.next();
        Assert.assertEquals("Homer", author.name);
        Assert.assertEquals(Arrays.asList("The Odyssey", "Iliad"), author.books);
        getDs().createAggregation(AggregationTest.Book.class).group("author", Group.grouping("books", Group.push("title"))).out("different", AggregationTest.Author.class);
        Assert.assertEquals(2, getDb().getCollection("different").count());
    }

    @Test
    public void testOutNamedCollection() {
        checkMinServerVersion(2.6);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2, "Italian", "Sophomore Slump"), new AggregationTest.Book("Divine Comedy", "Dante", 1, "Not Very Funny", "I mean for a 'comedy'", "Ironic"), new AggregationTest.Book("Eclogues", "Dante", 2, "Italian", ""), new AggregationTest.Book("The Odyssey", "Homer", 10, "Classic", "Mythology", "Sequel"), new AggregationTest.Book("Iliad", "Homer", 10, "Mythology", "Trojan War", "No Sequel")));
        getDs().createAggregation(AggregationTest.Book.class).match(getDs().getQueryFactory().createQuery(getDs()).field("author").equal("Homer")).group("author", Group.grouping("copies", Group.sum("copies"))).out("testAverage", AggregationTest.Author.class);
        DBCursor testAverage = getDb().getCollection("testAverage").find();
        Assert.assertNotNull(testAverage);
        try {
            Assert.assertEquals(20, testAverage.next().get("copies"));
        } finally {
            testAverage.close();
        }
    }

    @Test
    public void testProjection() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        final AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.Book.class).group("author", Group.grouping("copies", Group.sum("copies"))).project(Projection.projection("_id").suppress(), Projection.projection("author", "_id"), Projection.projection("copies", Projection.divide(Projection.projection("copies"), 5))).sort(Sort.ascending("author"));
        Iterator<AggregationTest.Book> aggregate = pipeline.aggregate(AggregationTest.Book.class);
        AggregationTest.Book book = aggregate.next();
        Assert.assertEquals("Dante", book.author);
        Assert.assertEquals(1, book.copies.intValue());
        final List<DBObject> stages = ((AggregationPipelineImpl) (pipeline)).getStages();
        Assert.assertEquals(stages.get(0), obj("$group", obj("_id", "$author").append("copies", obj("$sum", "$copies"))));
        Assert.assertEquals(stages.get(1), obj("$project", obj("_id", 0).append("author", "$_id").append("copies", obj("$divide", Arrays.<Object>asList("$copies", 5)))));
    }

    @Test
    public void testSizeProjection() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        final AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.Book.class).group("author", Group.grouping("titles", Group.addToSet("title"))).project(Projection.projection("_id").suppress(), Projection.projection("author", "_id"), Projection.projection("copies", Projection.size(Projection.projection("titles")))).sort(Sort.ascending("author"));
        Iterator<AggregationTest.Book> aggregate = pipeline.aggregate(AggregationTest.Book.class);
        AggregationTest.Book book = aggregate.next();
        Assert.assertEquals("Dante", book.author);
        Assert.assertEquals(3, book.copies.intValue());
        final List<DBObject> stages = ((AggregationPipelineImpl) (pipeline)).getStages();
        Assert.assertEquals(stages.get(0), obj("$group", obj("_id", "$author").append("titles", obj("$addToSet", "$title"))));
        Assert.assertEquals(stages.get(1), obj("$project", obj("_id", 0).append("author", "$_id").append("copies", obj("$size", "$titles"))));
    }

    @Test
    public void testSkip() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        AggregationTest.Book book = getDs().createAggregation(AggregationTest.Book.class).skip(2).aggregate(AggregationTest.Book.class).next();
        Assert.assertEquals("Eclogues", book.title);
        Assert.assertEquals("Dante", book.author);
        Assert.assertEquals(2, book.copies.intValue());
    }

    @Test
    public void testUnwind() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        getDs().save(Arrays.asList(new AggregationTest.User("jane", format.parse("2011-03-02"), "golf", "racquetball"), new AggregationTest.User("joe", format.parse("2012-07-02"), "tennis", "golf", "swimming"), new AggregationTest.User("john", format.parse("2012-07-02"))));
        Iterator<AggregationTest.User> aggregate = getDs().createAggregation(AggregationTest.User.class).project(Projection.projection("_id").suppress(), Projection.projection("name"), Projection.projection("joined"), Projection.projection("likes")).unwind("likes").aggregate(AggregationTest.User.class);
        int count = 0;
        while (aggregate.hasNext()) {
            AggregationTest.User user = aggregate.next();
            switch (count) {
                case 0 :
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("golf", user.likes.get(0));
                    break;
                case 1 :
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("racquetball", user.likes.get(0));
                    break;
                case 2 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("tennis", user.likes.get(0));
                    break;
                case 3 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("golf", user.likes.get(0));
                    break;
                case 4 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("swimming", user.likes.get(0));
                    break;
                default :
                    Assert.fail("Should only find 5 elements");
            }
            count++;
        } 
        aggregate = getDs().createAggregation(AggregationTest.User.class).project(Projection.projection("_id").suppress(), Projection.projection("name"), Projection.projection("joined"), Projection.projection("likes")).unwind("likes", new com.mongodb.client.model.UnwindOptions().preserveNullAndEmptyArrays(true)).aggregate(AggregationTest.User.class);
        count = 0;
        while (aggregate.hasNext()) {
            AggregationTest.User user = aggregate.next();
            switch (count) {
                case 0 :
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("golf", user.likes.get(0));
                    break;
                case 1 :
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("racquetball", user.likes.get(0));
                    break;
                case 2 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("tennis", user.likes.get(0));
                    break;
                case 3 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("golf", user.likes.get(0));
                    break;
                case 4 :
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("swimming", user.likes.get(0));
                    break;
                case 5 :
                    Assert.assertEquals("john", user.name);
                    Assert.assertNull(user.likes);
                    break;
                default :
                    Assert.fail("Should only find 6 elements");
            }
            count++;
        } 
    }

    @Test
    public void testSortByCount() {
        checkMinServerVersion(3.4);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.SortByCountResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).sortByCount("author").aggregate(AggregationTest.SortByCountResult.class);
        AggregationTest.SortByCountResult result1 = aggregate.next();
        Assert.assertEquals(result1.id, "Dante");
        Assert.assertEquals(result1.count, 3);
        AggregationTest.SortByCountResult result2 = aggregate.next();
        Assert.assertEquals(result2.id, "Homer");
        Assert.assertEquals(result2.count, 2);
    }

    @Test
    public void testBucketWithoutOptions() {
        checkMinServerVersion(3.4);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.BucketResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucket("copies", Arrays.asList(1, 5, 12)).aggregate(AggregationTest.BucketResult.class);
        AggregationTest.BucketResult result1 = aggregate.next();
        Assert.assertEquals(result1.id, "1");
        Assert.assertEquals(result1.count, 3);
        AggregationTest.BucketResult result2 = aggregate.next();
        Assert.assertEquals(result2.id, "5");
        Assert.assertEquals(result2.count, 2);
    }

    @Test
    public void testBucketWithOptions() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.BucketResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucket("copies", Arrays.asList(1, 5, 10), new dev.morphia.query.BucketOptions().defaultField("test").output("count").sum(1)).aggregate(AggregationTest.BucketResult.class);
        AggregationTest.BucketResult result1 = aggregate.next();
        Assert.assertEquals(result1.id, "1");
        Assert.assertEquals(result1.count, 3);
        AggregationTest.BucketResult result2 = aggregate.next();
        Assert.assertEquals(result2.id, "test");
        Assert.assertEquals(result2.count, 2);
    }

    @Test(expected = RuntimeException.class)
    public void testBucketWithUnsortedBoundaries() {
        checkMinServerVersion(3.4);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.BucketResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucket("copies", Arrays.asList(5, 1, 10), new dev.morphia.query.BucketOptions().defaultField("test").output("count").sum(1)).aggregate(AggregationTest.BucketResult.class);
    }

    @Test(expected = RuntimeException.class)
    public void testBucketWithBoundariesWithSizeLessThanTwo() {
        checkMinServerVersion(3.4);
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 2), new AggregationTest.Book("Divine Comedy", "Dante", 1), new AggregationTest.Book("Eclogues", "Dante", 2), new AggregationTest.Book("The Odyssey", "Homer", 10), new AggregationTest.Book("Iliad", "Homer", 10)));
        Iterator<AggregationTest.BucketResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucket("copies", Arrays.asList(10), new dev.morphia.query.BucketOptions().defaultField("test").output("count").sum(1)).aggregate(AggregationTest.BucketResult.class);
    }

    @Test
    public void testBucketAutoWithoutGranularity() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 5), new AggregationTest.Book("Divine Comedy", "Dante", 10), new AggregationTest.Book("Eclogues", "Dante", 40), new AggregationTest.Book("The Odyssey", "Homer", 21)));
        Iterator<AggregationTest.BucketAutoResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucketAuto("copies", 2).aggregate(AggregationTest.BucketAutoResult.class);
        AggregationTest.BucketAutoResult result1 = aggregate.next();
        Assert.assertEquals(result1.id.min, 5);
        Assert.assertEquals(result1.id.max, 21);
        Assert.assertEquals(result1.count, 2);
        result1 = aggregate.next();
        Assert.assertEquals(result1.id.min, 21);
        Assert.assertEquals(result1.id.max, 40);
        Assert.assertEquals(result1.count, 2);
        Assert.assertFalse(aggregate.hasNext());
    }

    @Test
    public void testBucketAutoWithGranularity() {
        getDs().save(Arrays.asList(new AggregationTest.Book("The Banquet", "Dante", 5), new AggregationTest.Book("Divine Comedy", "Dante", 7), new AggregationTest.Book("Eclogues", "Dante", 40), new AggregationTest.Book("The Odyssey", "Homer", 21)));
        Iterator<AggregationTest.BooksBucketResult> aggregate = getDs().createAggregation(AggregationTest.Book.class).bucketAuto("copies", 3, new dev.morphia.query.BucketAutoOptions().granularity(BucketAutoOptions.Granularity.POWERSOF2).output("authors").addToSet("author").output("count").sum(1)).aggregate(AggregationTest.BooksBucketResult.class);
        AggregationTest.BooksBucketResult result1 = aggregate.next();
        Assert.assertEquals(result1.getId().min, 4);
        Assert.assertEquals(result1.getId().max, 8);
        Assert.assertEquals(result1.getCount(), 2);
        Assert.assertEquals(result1.authors, Collections.singleton("Dante"));
        result1 = aggregate.next();
        Assert.assertEquals(result1.getId().min, 8);
        Assert.assertEquals(result1.getId().max, 32);
        Assert.assertEquals(result1.getCount(), 1);
        Assert.assertEquals(result1.authors, Collections.singleton("Homer"));
        result1 = aggregate.next();
        Assert.assertEquals(result1.getId().min, 32);
        Assert.assertEquals(result1.getId().max, 64);
        Assert.assertEquals(result1.getCount(), 1);
        Assert.assertEquals(result1.authors, Collections.singleton("Dante"));
        Assert.assertFalse(aggregate.hasNext());
    }

    @Test
    public void testUserPreferencesPipeline() {
        final AggregationPipeline pipeline = /* the class is irrelevant for this test */
        getDs().createAggregation(AggregationTest.Book.class).group("state", Group.grouping("total_pop", Group.sum("pop"))).match(getDs().find(AggregationTest.Book.class).disableValidation().field("total_pop").greaterThanOrEq(10000000));
        DBObject group = obj("$group", obj("_id", "$state").append("total_pop", obj("$sum", "$pop")));
        DBObject match = obj("$match", obj("total_pop", obj("$gte", 10000000)));
        final List<DBObject> stages = ((AggregationPipelineImpl) (pipeline)).getStages();
        Assert.assertEquals(stages.get(0), group);
        Assert.assertEquals(stages.get(1), match);
    }

    @Test
    public void testGroupWithProjection() {
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.Author.class).group("subjectHash", Group.grouping("authors", Group.addToSet("fromAddress.address")), Group.grouping("messageDataSet", Group.grouping("$addToSet", Projection.projection("sentDate", "sentDate"), Projection.projection("messageId", "_id"))), Group.grouping("messageCount", Accumulator.accumulator("$sum", 1))).limit(10).skip(0);
        List<DBObject> stages = ((AggregationPipelineImpl) (pipeline)).getStages();
        DBObject group = stages.get(0);
        DBObject addToSet = getDBObject(group, "$group", "messageDataSet", "$addToSet");
        Assert.assertNotNull(addToSet);
        Assert.assertEquals(addToSet.get("sentDate"), "$sentDate");
        Assert.assertEquals(addToSet.get("messageId"), "$_id");
    }

    @Test
    public void testAdd() {
        AggregationPipeline pipeline = getDs().createAggregation(AggregationTest.Book.class).group(Group.grouping("summation", Accumulator.accumulator("$sum", Accumulator.accumulator("$add", Arrays.asList("$amountFromTBInDouble", "$amountFromParentPNLInDouble")))));
        DBObject group = ((DBObject) (getStages().get(0).get("$group")));
        DBObject summation = ((DBObject) (group.get("summation")));
        DBObject sum = ((DBObject) (summation.get("$sum")));
        List<?> add = ((List<?>) (sum.get("$add")));
        Assert.assertTrue(((add.get(0)) instanceof String));
        Assert.assertEquals("$amountFromTBInDouble", add.get(0));
        pipeline.aggregate(AggregationTest.User.class);
    }

    private static class StringDates {
        @Id
        private ObjectId id;

        private String string;
    }

    @Entity(value = "books", noClassnameStored = true)
    public static final class Book {
        @Id
        private ObjectId id;

        private String title;

        private String author;

        private Integer copies;

        private List<String> tags;

        private Book() {
        }

        public Book(final String title, final String author, final Integer copies, final String... tags) {
            this.title = title;
            this.author = author;
            this.copies = copies;
            this.tags = Arrays.asList(tags);
        }

        @Override
        public String toString() {
            return String.format("Book{title='%s', author='%s', copies=%d, tags=%s}", title, author, copies, tags);
        }
    }

    @Entity("authors")
    public static class Author {
        @Id
        private String name;

        private List<String> books;
    }

    public static class BucketResult {
        @Id
        private String id;

        private int count;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return (((("BucketResult{" + "id=") + (id)) + ", count=") + (count)) + '}';
        }
    }

    public static class BooksBucketResult extends AggregationTest.BucketAutoResult {
        private Set<String> authors;

        public Set<String> getAuthors() {
            return authors;
        }

        public void setAuthors(final Set<String> authors) {
            this.authors = authors;
        }
    }

    public static class BucketAutoResult {
        @Id
        private AggregationTest.BucketAutoResult.MinMax id;

        private int count;

        public AggregationTest.BucketAutoResult.MinMax getId() {
            return id;
        }

        public void setId(final AggregationTest.BucketAutoResult.MinMax id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return (((("BucketAutoResult{" + "id=") + (id)) + ", count=") + (count)) + '}';
        }

        public static class MinMax {
            private int min;

            private int max;

            public int getMin() {
                return min;
            }

            public void setMin(final int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(final int max) {
                this.max = max;
            }

            @Override
            public String toString() {
                return (((("MinMax{" + "min=") + (min)) + ", max=") + (max)) + '}';
            }
        }
    }

    public static class SortByCountResult {
        @Id
        private String id;

        private int count;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return (((("SortByCountResult{" + "id=") + (id)) + ", count=") + (count)) + '}';
        }
    }

    @Entity("users")
    @Validation("{ age : { $gte : 13 } }")
    private static final class User {
        @Id
        private ObjectId id;

        private String name;

        private Date joined;

        private List<String> likes;

        private int age;

        private User() {
        }

        private User(final String name, final Date joined, final String... likes) {
            this.name = name;
            this.joined = joined;
            this.likes = Arrays.asList(likes);
        }

        @Override
        public String toString() {
            return String.format("User{name='%s', joined=%s, likes=%s}", name, joined, likes);
        }
    }

    @Entity("counts")
    public static class CountResult {
        @Id
        private String author;

        @AlsoLoad("value")
        private int count;

        public String getAuthor() {
            return author;
        }

        public int getCount() {
            return count;
        }
    }

    @Entity("orders")
    private static class Order {
        @Id
        private int id;

        private String item;

        private int price;

        private int quantity;

        @Embedded
        private List<AggregationTest.Inventory> inventoryDocs;

        private Order() {
        }

        Order(final int id) {
            this.id = id;
        }

        Order(final int id, final String item, final int price, final int quantity) {
            this.id = id;
            this.item = item;
            this.price = price;
            this.quantity = quantity;
        }

        public List<AggregationTest.Inventory> getInventoryDocs() {
            return inventoryDocs;
        }

        public void setInventoryDocs(final List<AggregationTest.Inventory> inventoryDocs) {
            this.inventoryDocs = inventoryDocs;
        }

        public String getItem() {
            return item;
        }

        public void setItem(final String item) {
            this.item = item;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(final int price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(final int quantity) {
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = (31 * result) + ((item) != null ? item.hashCode() : 0);
            result = (31 * result) + (price);
            result = (31 * result) + (quantity);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AggregationTest.Order)) {
                return false;
            }
            final AggregationTest.Order order = ((AggregationTest.Order) (o));
            if ((id) != (order.id)) {
                return false;
            }
            if ((price) != (order.price)) {
                return false;
            }
            if ((quantity) != (order.quantity)) {
                return false;
            }
            return (item) != null ? item.equals(order.item) : (order.item) == null;
        }
    }

    @Entity(value = "inventory", noClassnameStored = true)
    public static class Inventory {
        @Id
        private int id;

        private String sku;

        private String description;

        private int instock;

        public Inventory() {
        }

        Inventory(final int id) {
            this.id = id;
        }

        Inventory(final int id, final String sku, final String description) {
            this.id = id;
            this.sku = sku;
            this.description = description;
        }

        Inventory(final int id, final String sku, final String description, final int instock) {
            this.id = id;
            this.sku = sku;
            this.description = description;
            this.instock = instock;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }

        public int getInstock() {
            return instock;
        }

        public void setInstock(final int instock) {
            this.instock = instock;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(final String sku) {
            this.sku = sku;
        }

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AggregationTest.Inventory)) {
                return false;
            }
            final AggregationTest.Inventory inventory = ((AggregationTest.Inventory) (o));
            if ((id) != (inventory.id)) {
                return false;
            }
            if ((instock) != (inventory.instock)) {
                return false;
            }
            if ((sku) != null ? !(sku.equals(inventory.sku)) : (inventory.sku) != null) {
                return false;
            }
            return (description) != null ? description.equals(inventory.description) : (inventory.description) == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = (31 * result) + ((sku) != null ? sku.hashCode() : 0);
            result = (31 * result) + ((description) != null ? description.hashCode() : 0);
            result = (31 * result) + (instock);
            return result;
        }
    }
}

