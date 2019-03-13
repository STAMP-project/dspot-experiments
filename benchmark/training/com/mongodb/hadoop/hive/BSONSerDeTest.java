package com.mongodb.hadoop.hive;


import com.mongodb.hadoop.io.BSONWritable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.bson.BasicBSONObject;
import org.bson.types.BSONTimestamp;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BSONSerDeTest {
    @Test
    public void testString() throws SerDeException {
        String columnNames = "s";
        String columnTypes = "string";
        String value = "value";
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(String.class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testDouble() throws SerDeException {
        String columnNames = "doub";
        String columnTypes = "double";
        Double value = 1.1;
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(Double.class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testNumericCasts() throws SerDeException {
        BSONSerDe serde = new BSONSerDe();
        String colName = "cast";
        Number[] nums = new Number[]{ 42.0, 42, ((short) (42)), 42.0F, 42L };
        Class[] numericClasses = new Class[]{ Double.class, Integer.class, Short.class, Float.class, Long.class };
        for (Number num : nums) {
            // Double
            Object result = helpDeserialize(serde, colName, "double", num);
            Assert.assertThat(num.doubleValue(), CoreMatchers.equalTo(result));
            // Int
            result = helpDeserialize(serde, colName, "int", num);
            Assert.assertThat(num.intValue(), CoreMatchers.equalTo(result));
            // Short
            result = helpDeserialize(serde, colName, "smallint", num);
            Assert.assertThat(num.shortValue(), CoreMatchers.equalTo(result));
            // Float
            result = helpDeserialize(serde, colName, "float", num);
            Assert.assertThat(num.floatValue(), CoreMatchers.equalTo(result));
            // Long
            result = helpDeserialize(serde, colName, "bigint", num);
            Assert.assertThat(num.longValue(), CoreMatchers.equalTo(result));
            for (Class klass : numericClasses) {
                ObjectInspector oi = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(klass);
                BasicBSONObject obj = new BasicBSONObject();
                Object serialized = helpSerialize(colName, oi, obj, num, serde);
                Assert.assertThat(new BSONWritable(obj), CoreMatchers.equalTo(serialized));
            }
        }
    }

    @Test
    public void testStringToTimestamp() throws SerDeException {
        String columnNames = "stringTs";
        String columnTypes = "timestamp";
        String value = "2015-08-06 07:32:30.062";
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertEquals(Timestamp.valueOf(value), result);
    }

    @Test
    public void testInt() throws SerDeException {
        String columnNames = "i";
        String columnTypes = "int";
        Integer value = 1234;
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(Integer.class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testBinary() throws SerDeException {
        String columnNames = "b";
        String columnTypes = "binary";
        byte[] value = new byte[2];
        value[0] = 'A';
        value[1] = '1';
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(byte[].class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testBoolean() throws SerDeException {
        String columnNames = "bool";
        String columnTypes = "boolean";
        Boolean value = false;
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(Boolean.class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testDates() throws SerDeException {
        String columnNames = "d";
        String columnTypes = "timestamp";
        Date d = new Date();
        Timestamp value = new Timestamp(d.getTime());
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        result = serde.deserializeField(d, serde.columnTypes.get(0), "");
        Assert.assertThat(value, CoreMatchers.equalTo(result));
        BSONTimestamp bts = new BSONTimestamp(((Long) ((d.getTime()) / 1000L)).intValue(), 1);
        result = serde.deserializeField(bts, serde.columnTypes.get(0), "");
        // BSONTimestamp only takes an int, so the long returned in the Timestamp won't be the same
        Assert.assertThat(((long) (bts.getTime())), CoreMatchers.equalTo(((((Timestamp) (result)).getTime()) / 1000L)));
        // Utilizes a timestampWritable because there's no native timestamp type in java for
        // object inspector class to relate to
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(TimestampWritable.class);
        BasicBSONObject bObject = new BasicBSONObject();
        BSONWritable serialized = ((BSONWritable) (helpSerialize(columnNames, innerInspector, bObject, new TimestampWritable(value), serde)));
        // The time going in to serialize is Timestamp but it comes out as BSONTimestamp
        BasicBSONObject bsonWithTimestamp = new BasicBSONObject();
        bsonWithTimestamp.put(columnNames, bts);
        Assert.assertThat(value.getTime(), CoreMatchers.equalTo(((Date) (serialized.getDoc().get(columnNames))).getTime()));
    }

    @Test
    public void testObjectID() throws SerDeException {
        String columnNames = "o";
        String columnTypes = "struct<oid:string,bsontype:int>";
        ObjectId value = new ObjectId();
        ArrayList<Object> returned = new ArrayList<Object>(2);
        returned.add(value.toString());
        returned.add(8);
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(returned, CoreMatchers.equalTo(result));
        // Since objectid is currently taken to be a string
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(String.class);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, innerInspector, bObject, value.toString(), serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testList() throws SerDeException {
        String columnNames = "a";
        String columnTypes = "array<string>";
        String inner = "inside";
        ArrayList<String> value = new ArrayList<String>();
        value.add(inner);
        BasicBSONList b = new BasicBSONList();
        b.add(inner);
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, b);
        Assert.assertThat(value.toArray(), CoreMatchers.equalTo(result));
        // Since objectid is currently taken to be a string
        ObjectInspector innerInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(String.class);
        ListObjectInspector listInspector = ObjectInspectorFactory.getStandardListObjectInspector(innerInspector);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, listInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testMap() throws SerDeException {
        String columnNames = "m";
        String columnTypes = "map<string,int>";
        BasicBSONObject value = new BasicBSONObject();
        String oneKey = "one";
        int oneValue = 10;
        value.put(oneKey, oneValue);
        String twoKey = "two";
        int twoValue = 20;
        value.put(twoKey, twoValue);
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value);
        Assert.assertThat(value.toMap(), CoreMatchers.equalTo(result));
        // Since objectid is currently taken to be a string
        ObjectInspector keyInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(String.class);
        ObjectInspector valueInspector = PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(Integer.class);
        MapObjectInspector mapInspector = ObjectInspectorFactory.getStandardMapObjectInspector(keyInspector, valueInspector);
        BasicBSONObject bObject = new BasicBSONObject();
        Object serialized = helpSerialize(columnNames, mapInspector, bObject, value, serde);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }

    @Test
    public void testStruct() throws SerDeException {
        String columnNames = "m";
        String columnTypes = "struct<one:int,two:string>";
        BasicBSONObject value = new BasicBSONObject();
        int oneValue = 10;
        String twoValue = "key";
        value.put("one", oneValue);
        value.put("two", twoValue);
        // Structs come back as arrays
        ArrayList<Object> returned = new ArrayList<Object>();
        returned.add(oneValue);
        returned.add(twoValue);
        BSONSerDe serde = new BSONSerDe();
        Object result = helpDeserialize(serde, columnNames, columnTypes, value, true);
        Assert.assertThat(returned, CoreMatchers.equalTo(result));
        // A struct must have an array or list of inner inspector types
        ArrayList<ObjectInspector> innerInspectorList = new ArrayList<ObjectInspector>();
        innerInspectorList.add(PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(Integer.class));
        innerInspectorList.add(PrimitiveObjectInspectorFactory.getPrimitiveObjectInspectorFromClass(String.class));
        // As well as a fields list
        ArrayList<String> innerFieldsList = new ArrayList<String>();
        innerFieldsList.add("one");
        innerFieldsList.add("two");
        // Then you get that inner struct's inspector
        StructObjectInspector structInspector = ObjectInspectorFactory.getStandardStructObjectInspector(innerFieldsList, innerInspectorList);
        // Which is used to get the overall struct inspector
        StructObjectInspector oi = createObjectInspector(columnNames, structInspector);
        // This should be how it turns out
        BasicBSONObject bObject = new BasicBSONObject();
        bObject.put(columnNames, value);
        // But structs are stored as array/list inside hive, so this is passed in
        ArrayList<Object> obj = new ArrayList<Object>();
        obj.add(returned);
        Object serialized = serde.serialize(obj, oi);
        Assert.assertThat(new BSONWritable(bObject), CoreMatchers.equalTo(serialized));
    }
}

