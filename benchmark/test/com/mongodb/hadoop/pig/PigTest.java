package com.mongodb.hadoop.pig;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.pig.tools.parameters.ParseException;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class PigTest extends BaseHadoopTest {
    private static final MongoClientURI URI = new MongoClientURI("mongodb://localhost:27017/mongo_hadoop.pigtests");

    private MongoClient mongoClient;

    private DB db;

    @Test
    public void mongoUpdateStorage() throws IOException, ParseException {
        runMongoUpdateStorageTest("/pig/update_simple_mus.pig", new String[]{ "(Daniel,Alabi,([car#a],[car#b],[car#c],[car#a],[car#b],[car#c]))", "(Tolu,Alabi,([car#d],[car#e],[car#f],[car#d],[car#e],[car#f]))", "(Tinuke,Dada,([car#g],[car#g]))" });
    }

    @Test
    public void mongoUpdateStorageMulti() throws IOException, ParseException {
        runMongoUpdateStorageTest("/pig/update_age_alabis_mus.pig", new String[]{ "(Daniel,Alabi,22.0)", "(Tolu,Alabi,24.0)", "(Tinuke,Dada,53.0)" });
    }

    @Test
    public void testPigUUID() throws IOException, ParseException {
        UUID uuid = UUID.randomUUID();
        BasicDBObject doc = new BasicDBObject("uuid", uuid);
        db.getCollection("uuid_test").insert(doc);
        org.apache.pig.pigunit.PigTest test = new org.apache.pig.pigunit.PigTest(getClass().getResource("/pig/pig_uuid.pig").getPath());
        test.assertOutput(new String[]{ ("(" + (uuid.toString())) + ")" });
    }

    @Test
    public void testDates() throws IOException, ParseException {
        mongoClient.getDatabase(PigTest.URI.getDatabase()).getCollection(PigTest.URI.getCollection()).insertOne(new Document("today", new Date()));
        MongoCollection<Document> outputCollection = mongoClient.getDatabase("mongo_hadoop").getCollection("datetests");
        PigTest.runScript("/pig/datestest.pig");
        for (Document doc : outputCollection.find()) {
            Object today = doc.get("today");
            Assert.assertTrue(("Expected a Date, but got a " + (today.getClass().getName())), (today instanceof Date));
        }
    }

    @Test
    public void testPigProjection() throws IOException, ParseException {
        DBCollection collection = mongoClient.getDB("mongo_hadoop").getCollection("projection_test");
        String[] expected = new String[100];
        for (int i = 0; i < (expected.length); ++i) {
            String letter = String.valueOf(((char) ('a' + (i % 26))));
            // {"_id": ObjectId(...), "i": <int>,
            // "d": {"s": <string>, "j": <int>, "k": <int>}}
            collection.insert(new BasicDBObjectBuilder().add("i", i).push("d").add("s", letter).add("j", (i + 1)).add("k", (i % 5)).pop().get());
            expected[i] = ((((("(" + i) + ",") + letter) + ",") + (i % 5)) + ")";
        }
        org.apache.pig.pigunit.PigTest test = new org.apache.pig.pigunit.PigTest(getClass().getResource("/pig/projection.pig").getPath());
        test.assertOutput(expected);
    }

    @Test
    public void testPigBSONOutput() throws IOException, ParseException {
        runMongoUpdateStorageTest("/pig/bson_test.pig", new String[]{ "(Daniel,Alabi,19.0)", "(Tolu,Alabi,21.0)", "(Tinuke,Dada,50.0)" }, "persons_read");
    }

    @Test
    public void testPigSchemaless() throws IOException, ParseException {
        // Seed data used by "schemaless.pig"
        MongoDatabase db = mongoClient.getDatabase("mongo_hadoop");
        List<Document> documents = new ArrayList<Document>(1000);
        for (int i = 0; i < 1000; ++i) {
            documents.add(new Document("_id", i));
        }
        db.getCollection("pig.schemaless").insertMany(documents);
        PigTest.runScript("/pig/schemaless.pig");
        Assert.assertEquals(1000, db.getCollection("pig.schemaless.out").count());
        Assert.assertNotNull(db.getCollection("pig.schemaless.out").find(new Document("_id", 999)).first());
    }

    @Test
    public void testPigSchemalessFromBSON() throws IOException, ParseException {
        runMongoUpdateStorageTest("/pig/bson_schemaless.pig", new String[]{ "(Daniel,Alabi,19.0)", "(Tolu,Alabi,21.0)", "(Tinuke,Dada,50.0)" });
    }

    @Test
    public void testMongoStorageEnsureIndex() throws IOException, ParseException {
        PigTest.runScript("/pig/ensure_index.pig");
        MongoClient client = new MongoClient("localhost:27017");
        // There should be an index on the "last" field, ascending.
        MongoCollection<Document> coll = client.getDatabase("mongo_hadoop").getCollection("ensure_indexes");
        Assert.assertTrue("Should have the index \"last_1\"", indexExists(coll, "last_1"));
        // Drop the index.
        coll.dropIndex("last_1");
        // Run the second pig script, which ensures a different index.
        PigTest.runScript("/pig/ensure_index_2.pig");
        Assert.assertTrue("Should have the index \"first_1\"", indexExists(coll, "first_1"));
        Assert.assertFalse("Should not have the index \"last_1\"", indexExists(coll, "last_1"));
    }

    @Test
    public void testPigUpdateReplace() throws IOException, ParseException {
        DBCollection replaceCollection = db.getCollection("replace_test");
        for (int i = 0; i < 10; ++i) {
            replaceCollection.insert(new BasicDBObject("i", i));
        }
        PigTest.runScript("/pig/replace_mus.pig");
        DBCursor cursor = replaceCollection.find().sort(new BasicDBObject("i", 1));
        for (int i = 1; i <= 10; ++i) {
            Assert.assertEquals(i, cursor.next().get("i"));
        }
    }
}

