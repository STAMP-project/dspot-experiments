package com.mongodb.hadoop.io;


import com.mongodb.BasicDBObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class MongoUpdateWritableTest {
    @Test
    public void testSerialization() throws Exception {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId());
        BasicDBObject modifiers = new BasicDBObject("$set", new BasicDBObject("foo", "bar"));
        MongoUpdateWritable writable = new MongoUpdateWritable(query, modifiers, true, false, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        writable.write(out);
        baos.flush();
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
        DataInputStream in = new DataInputStream(bais);
        MongoUpdateWritable blank = new MongoUpdateWritable();
        blank.readFields(in);
        Assert.assertEquals(writable, blank);
    }

    @Test
    public void testEquals() {
        BasicDBObject query1 = new BasicDBObject("_id", new ObjectId());
        BasicDBObject query2 = new BasicDBObject("_id", new ObjectId());
        BasicDBObject modifiers1 = new BasicDBObject("$set", new BasicDBObject("foo", "bar"));
        BasicDBObject modifiers2 = new BasicDBObject("$set", new BasicDBObject("bar", "baz"));
        MongoUpdateWritable writable = new MongoUpdateWritable(query1, modifiers1, true, false, false);
        // Not equal because queries differ.
        MongoUpdateWritable diffQuery = new MongoUpdateWritable(query2, modifiers1, true, false, false);
        Assert.assertNotEquals(writable, diffQuery);
        // Not equal because modifiers differ.
        MongoUpdateWritable diffModifier = new MongoUpdateWritable(query1, modifiers2, true, false, false);
        Assert.assertNotEquals(writable, diffModifier);
        // Not equal because upsert flag differs.
        MongoUpdateWritable diffUpsert = new MongoUpdateWritable(query1, modifiers1, false, false, false);
        Assert.assertNotEquals(writable, diffUpsert);
        // Not equal because multi flag differs.
        MongoUpdateWritable diffMulti = new MongoUpdateWritable(query1, modifiers1, true, true, false);
        Assert.assertNotEquals(writable, diffMulti);
        // Not equal because replace flag differs.
        MongoUpdateWritable diffReplace = new MongoUpdateWritable(query1, modifiers1, true, false, true);
        Assert.assertNotEquals(writable, diffReplace);
        MongoUpdateWritable same = new MongoUpdateWritable(query1, modifiers1, true, false, false);
        Assert.assertEquals(writable, same);
        // Test defaults for simple constructor.
        MongoUpdateWritable simpleConstructor = new MongoUpdateWritable(query1, modifiers1);
        Assert.assertEquals(writable, simpleConstructor);
    }
}

