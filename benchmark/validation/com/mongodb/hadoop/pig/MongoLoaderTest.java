package com.mongodb.hadoop.pig;


import LoadPushDown.RequiredField;
import LoadPushDown.RequiredFieldList;
import LoadPushDown.RequiredFieldResponse;
import MongoConfigUtil.INPUT_FIELDS;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.input.MongoRecordReader;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.pig.LoadPushDown;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.util.UDFContext;
import org.bson.types.Binary;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MongoLoaderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testBinaryNoSchema() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3 };
        BasicDBObject obj = new BasicDBObject("bytes", new Binary(data));
        MongoRecordReader rr = Mockito.mock(MongoRecordReader.class);
        Mockito.when(rr.nextKeyValue()).thenReturn(true);
        Mockito.when(rr.getCurrentValue()).thenReturn(obj);
        // No explicit schema.
        MongoLoader ml = new MongoLoader();
        ml.prepareToRead(rr, null);
        Tuple result = ml.getNext();
        // Tuple just contains a Map.
        Map<String, Object> tupleContents;
        tupleContents = ((Map<String, Object>) (result.get(0)));
        // Map contains DataByteArray with binary data.
        Assert.assertArrayEquals(data, get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testByteArrayNoSchema() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3 };
        BasicDBObject obj = new BasicDBObject("bytes", data);
        MongoRecordReader rr = Mockito.mock(MongoRecordReader.class);
        Mockito.when(rr.nextKeyValue()).thenReturn(true);
        Mockito.when(rr.getCurrentValue()).thenReturn(obj);
        // No explicit schema.
        MongoLoader ml = new MongoLoader();
        ml.prepareToRead(rr, null);
        Tuple result = ml.getNext();
        // Tuple just contains a Map.
        Map<String, Object> tupleContents;
        tupleContents = ((Map<String, Object>) (result.get(0)));
        // Map contains DataByteArray with binary data.
        Assert.assertArrayEquals(data, get());
    }

    @Test
    public void testSimpleBytearray() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3 };
        String userSchema = "d:bytearray";
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(data, ml.getFields()[0]);
        Assert.assertArrayEquals(data, get());
    }

    @Test
    public void testSimpleBinary() throws IOException {
        byte[] data = new byte[]{ 1, 2, 3 };
        String userSchema = "d:bytearray";
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(new Binary(data), ml.getFields()[0]);
        Assert.assertArrayEquals(data, get());
    }

    @Test
    public void testSimpleChararray() throws IOException {
        String userSchema = "d:chararray";
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField("value", ml.getFields()[0]);
        Assert.assertEquals("value", result);
    }

    @Test
    public void testSimpleFloat() throws IOException {
        String userSchema = "d:float";
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(1.1F, ml.getFields()[0]);
        Assert.assertEquals(1.1F, result);
    }

    @Test
    public void testSimpleFloatAsDouble() throws IOException {
        String userSchema = "d:float";
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(1.1, ml.getFields()[0]);
        Assert.assertEquals(1.1F, result);
    }

    @Test
    public void testSimpleDate() throws IOException {
        String userSchema = "d:datetime";
        MongoLoader ml = new MongoLoader(userSchema);
        Calendar calendar = Calendar.getInstance();
        Date in = calendar.getTime();
        DateTime out = new DateTime(in);
        Object result = BSONLoader.readField(in, ml.getFields()[0]);
        Assert.assertEquals(out, result);
    }

    @Test
    public void testSimpleTuple() throws IOException {
        String userSchema = "t:tuple(t1:chararray, t2:chararray)";
        Object val = new BasicDBObject().append("t1", "t1_value").append("t2", "t2_value");
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(val, ml.getFields()[0]);
        Tuple t = ((Tuple) (result));
        Assert.assertEquals(2, t.size());
        Assert.assertEquals("t1_value", t.get(0));
        Assert.assertEquals("t2_value", t.get(1));
    }

    @Test
    public void testSimpleTupleMissingField() throws IOException {
        String userSchema = "t:tuple(t1:chararray, t2:chararray, t3:chararray)";
        Object val = new BasicDBObject().append("t1", "t1_value").append("t2", "t2_value");
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(val, ml.getFields()[0]);
        Tuple t = ((Tuple) (result));
        Assert.assertEquals(3, t.size());
        Assert.assertEquals("t1_value", t.get(0));
        Assert.assertEquals("t2_value", t.get(1));
        Assert.assertNull(t.get(2));
    }

    @Test
    public void testSimpleTupleIncorrectFieldType() throws IOException {
        String userSchema = "t:tuple(t1:chararray, t2:float)";
        Object val = new BasicDBObject().append("t1", "t1_value").append("t2", "t2_value");
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(val, ml.getFields()[0]);
        Tuple t = ((Tuple) (result));
        Assert.assertEquals(2, t.size());
        Assert.assertEquals("t1_value", t.get(0));
        Assert.assertNull(t.get(1));
    }

    @Test
    public void testSimpleBag() throws IOException {
        String userSchema = "b:{t:tuple(t1:chararray, t2:chararray)}";
        BasicDBList bag = new BasicDBList();
        bag.add(new BasicDBObject().append("t1", "t11_value").append("t2", "t12_value"));
        bag.add(new BasicDBObject().append("t1", "t21_value").append("t2", "t22_value"));
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(bag, ml.getFields()[0]);
        DataBag b = ((DataBag) (result));
        Iterator<Tuple> bit = b.iterator();
        Tuple firstInnerT = bit.next();
        Assert.assertEquals(2, firstInnerT.size());
        Assert.assertEquals("t11_value", firstInnerT.get(0));
        Assert.assertEquals("t12_value", firstInnerT.get(1));
        Tuple secondInnerT = bit.next();
        Assert.assertEquals(2, secondInnerT.size());
        Assert.assertEquals("t21_value", secondInnerT.get(0));
        Assert.assertEquals("t22_value", secondInnerT.get(1));
        Assert.assertFalse(bit.hasNext());
    }

    @Test
    public void testBagThatIsNotABag() throws IOException {
        String userSchema = "b:{t:tuple(t1:chararray, t2:chararray)}";
        BasicDBObject notABag = new BasicDBObject();
        notABag.append("f1", new BasicDBObject().append("t1", "t11_value").append("t2", "t12_value"));
        notABag.append("f2", new BasicDBObject().append("t1", "t21_value").append("t2", "t22_value"));
        MongoLoader ml = new MongoLoader(userSchema);
        Object result = BSONLoader.readField(notABag, ml.getFields()[0]);
        Assert.assertNull(result);
    }

    @Test
    public void testDeepness() throws IOException {
        String userSchema = "b:{t:tuple(t1:chararray, b:{t:tuple(i1:int, i2:int)})}";
        BasicDBList innerBag = new BasicDBList();
        innerBag.add(new BasicDBObject().append("i1", 1).append("i2", 2));
        innerBag.add(new BasicDBObject().append("i1", 3).append("i2", 4));
        BasicDBList bag = new BasicDBList();
        bag.add(new BasicDBObject().append("t1", "t1_value").append("b", innerBag));
        MongoLoader ml = new MongoLoader(userSchema);
        DataBag result = ((DataBag) (BSONLoader.readField(bag, ml.getFields()[0])));
        Assert.assertEquals(1, result.size());
        Iterator<Tuple> bit = result.iterator();
        Tuple t = bit.next();
        Assert.assertEquals(2, t.size());
        DataBag innerBagResult = ((DataBag) (t.get(1)));
        Assert.assertEquals(2, innerBagResult.size());
        Iterator<Tuple> innerBit = innerBagResult.iterator();
        Tuple innerT = innerBit.next();
        Assert.assertEquals(2, innerT.get(1));
    }

    @Test
    public void testSimpleMap() throws Exception {
        // String userSchema = "m:[int]";
        // Note: before pig 0.9, explicitly setting the type for
        // map keys was not allowed, so can't test that here :(
        String userSchema = "m:[]";
        BasicDBObject obj = new BasicDBObject().append("k1", 1).append("k2", 2);
        MongoLoader ml = new MongoLoader(userSchema);
        Map m = ((Map) (BSONLoader.readField(obj, ml.getFields()[0])));
        Assert.assertEquals(2, m.size());
        Assert.assertEquals(1, m.get("k1"));
        Assert.assertEquals(2, m.get("k2"));
    }

    @Test
    public void testMapWithTuple() throws Exception {
        // String userSchema = "m:[(t1:chararray, t2:int)]";
        // Note: before pig 0.9, explicitly setting the type for
        // map keys was not allowed, so can't test that here :(
        String userSchema = "m:[]";
        BasicDBObject v1 = new BasicDBObject().append("t1", "t11 value").append("t2", 12);
        BasicDBObject v2 = new BasicDBObject().append("t1", "t21 value").append("t2", 22);
        BasicDBObject obj = new BasicDBObject().append("v1", v1).append("v2", v2);
        MongoLoader ml = new MongoLoader(userSchema);
        Map m = ((Map) (BSONLoader.readField(obj, ml.getFields()[0])));
        Assert.assertEquals(2, m.size());
        /* We can't safely cast to Tuple here 
        because pig < 0.9 doesn't allow setting types.
        Skip for now.

        Tuple t1 = (Tuple) m.get("v1");
        assertEquals("t11 value", t1.get(0));
        assertEquals(12, t1.get(1));

        Tuple t2 = (Tuple) m.get("v2");
        assertEquals("t21 value", t2.get(0));
         */
    }

    @Test
    public void testPushProjection() throws FrontendException {
        String userSchema = "a:int, m:[]";
        MongoLoader ml = new MongoLoader(userSchema);
        ml.setUDFContextSignature("signature");
        LoadPushDown.RequiredField aField = new LoadPushDown.RequiredField("a", 0, null, DataType.INTEGER);
        List<LoadPushDown.RequiredField> mSubFields = Collections.singletonList(new LoadPushDown.RequiredField("x", 0, null, DataType.INTEGER));
        LoadPushDown.RequiredField mField = new LoadPushDown.RequiredField("m", 1, mSubFields, DataType.MAP);
        LoadPushDown.RequiredFieldList requiredFields = new LoadPushDown.RequiredFieldList(Arrays.asList(aField, mField));
        LoadPushDown.RequiredFieldResponse response = ml.pushProjection(requiredFields);
        Assert.assertTrue(response.getRequiredFieldResponse());
        Properties props = UDFContext.getUDFContext().getUDFProperties(MongoLoader.class, new String[]{ "signature" });
        Assert.assertEquals(get(), JSON.parse(props.getProperty(INPUT_FIELDS)));
    }
}

