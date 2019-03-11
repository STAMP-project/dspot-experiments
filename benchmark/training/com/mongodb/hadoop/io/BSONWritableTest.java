package com.mongodb.hadoop.io;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Arrays;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.bson.BasicBSONObject;
import org.junit.Assert;
import org.junit.Test;


public class BSONWritableTest {
    @Test
    public void testToBSON() {
        Assert.assertEquals(null, BSONWritable.toBSON(null));
        Assert.assertEquals(null, BSONWritable.toBSON(NullWritable.get()));
        Assert.assertEquals("hello", BSONWritable.toBSON(new Text("hello")));
        DBObject obj = new BasicDBObject("hello", "world");
        Assert.assertEquals(obj, BSONWritable.toBSON(new BSONWritable(obj)));
        final BasicBSONObject bsonResult = new BasicBSONObject("one", 1);
        SortedMapWritable smw = new SortedMapWritable();
        smw.put(new Text("one"), new IntWritable(1));
        Assert.assertEquals(bsonResult, BSONWritable.toBSON(smw));
        MapWritable mw = new MapWritable();
        mw.put(new Text("one"), new IntWritable(1));
        Assert.assertEquals(bsonResult, BSONWritable.toBSON(mw));
        String[] expectedObjects = new String[]{ "one", "two" };
        Writable[] writableObjects = new Writable[]{ new Text("one"), new Text("two") };
        ArrayWritable aw = new ArrayWritable(Text.class, writableObjects);
        Object[] actual = ((Object[]) (BSONWritable.toBSON(aw)));
        Assert.assertTrue(Arrays.equals(expectedObjects, actual));
        Assert.assertEquals(false, BSONWritable.toBSON(new BooleanWritable(false)));
        byte[] bytes = new byte[]{ '0', '1', '2' };
        Assert.assertEquals(bytes, BSONWritable.toBSON(new BytesWritable(bytes)));
        byte b = ((byte) ('c'));
        Assert.assertEquals(b, BSONWritable.toBSON(new ByteWritable(b)));
        Assert.assertEquals(3.14159, BSONWritable.toBSON(new DoubleWritable(3.14159)));
        Assert.assertEquals(3.14159F, BSONWritable.toBSON(new FloatWritable(3.14159F)));
        Assert.assertEquals(42L, BSONWritable.toBSON(new LongWritable(42L)));
        Assert.assertEquals(42, BSONWritable.toBSON(new IntWritable(42)));
        // Catchall
        Assert.assertEquals("hi", BSONWritable.toBSON("hi"));
    }
}

