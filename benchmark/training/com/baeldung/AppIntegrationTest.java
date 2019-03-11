package com.baeldung;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import org.junit.Assert;
import org.junit.Test;


public class AppIntegrationTest {
    private static final String DB_NAME = "myMongoDb";

    private MongodExecutable mongodExe;

    private MongodProcess mongod;

    private Mongo mongo;

    private DB db;

    private DBCollection collection;

    @Test
    public void testAddressPersistance() {
        BasicDBObject contact = new BasicDBObject();
        contact.put("name", "John");
        contact.put("company", "Baeldung");
        // Inserting document
        collection.insert(contact);
        DBCursor cursorDoc = collection.find();
        BasicDBObject contact1 = new BasicDBObject();
        while (cursorDoc.hasNext()) {
            contact1 = ((BasicDBObject) (cursorDoc.next()));
            System.out.println(contact1);
        } 
        Assert.assertEquals(contact1.get("name"), "John");
    }
}

