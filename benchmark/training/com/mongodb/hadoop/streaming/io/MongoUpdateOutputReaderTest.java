package com.mongodb.hadoop.streaming.io;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.io.MongoUpdateWritable;
import java.io.IOException;
import org.apache.hadoop.streaming.PipeMapRed;
import org.bson.BasicBSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MongoUpdateOutputReaderTest {
    private BSONWritable bsonWritable = new BSONWritable();

    @Test
    public void testNoUpdate() throws IOException {
        // Test document, does not describe an update.
        DBObject notAnUpdate = new BasicDBObject("_id", 42);
        PipeMapRed pipeMapRed = Mockito.mock(PipeMapRed.class);
        Mockito.when(pipeMapRed.getClientInput()).thenReturn(inputFromBSONObject(notAnUpdate));
        MongoUpdateOutputReader reader = new MongoUpdateOutputReader();
        reader.initialize(pipeMapRed);
        Assert.assertTrue(reader.readKeyValue());
        Assert.assertEquals(notAnUpdate, reader.getCurrentValue().getQuery());
    }

    @Test
    public void testUpdate() throws IOException {
        BasicBSONObject query = new BasicDBObject("i", 42);
        BasicBSONObject modifiers = new BasicDBObject("$set", new BasicDBObject("a", "b"));
        DBObject update = new BasicDBObjectBuilder().add("_id", query).add("modifiers", modifiers).push("options").add("multi", true).add("upsert", false).pop().get();
        MongoUpdateWritable muw = new MongoUpdateWritable(query, modifiers, false, true, false);
        PipeMapRed pipeMapRed = Mockito.mock(PipeMapRed.class);
        Mockito.when(pipeMapRed.getClientInput()).thenReturn(inputFromBSONObject(update));
        MongoUpdateOutputReader reader = new MongoUpdateOutputReader();
        reader.initialize(pipeMapRed);
        Assert.assertTrue(reader.readKeyValue());
        Assert.assertEquals(muw, reader.getCurrentValue());
    }
}

