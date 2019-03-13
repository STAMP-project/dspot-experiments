package io.mycat.backend.jdbc.mongodb;


import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import java.util.Date;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author liuxinsi
 * @unknown akalxs@gmail.com
 */
public class MongoEmbeddedObjectProcessorTest {
    @Test
    public void testValueMapperWithObjectId() {
        String id = "5978776b8d69f75e091067ed";
        Object obj = MongoEmbeddedObjectProcessor.valueMapper("_id", id, ObjectId.class);
        if (!(obj instanceof ObjectId)) {
            Assert.fail("not objectId");
        }
    }

    @Test
    public void testValueMapperWithEmbeddedObject() {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("str", "t1");
        dbObj.put("inte", 1);
        dbObj.put("date", new Date());
        dbObj.put("lon", 100L);
        dbObj.put("bool", true);
        dbObj.put("strs", new String[]{ "a", "b", "c" });
        dbObj.put("intes", new Integer[]{ 1, 2, 3 });
        dbObj.put("bytes", "ttt".getBytes());
        dbObj.put("b", "a".getBytes()[0]);
        Object o = MongoEmbeddedObjectProcessor.valueMapper("embObj", dbObj, TestObject.class);
        if (!(o instanceof TestObject)) {
            Assert.fail("not emb obj");
        }
    }

    @Test
    public void testValueMapperWithDeepEmbeddedObject() {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("str", "t1");
        dbObj.put("inte", 1);
        dbObj.put("date", new Date());
        dbObj.put("lon", 100L);
        dbObj.put("bool", true);
        dbObj.put("strs", new String[]{ "a", "b", "c" });
        dbObj.put("intes", new Integer[]{ 1, 2, 3 });
        dbObj.put("bytes", "ttt".getBytes());
        dbObj.put("b", "a".getBytes()[0]);
        BasicDBObject embedObj = new BasicDBObject();
        embedObj.put("embeddedStr", "e1");
        BasicDBObject deepEmbedObj1 = new BasicDBObject();
        deepEmbedObj1.put("str", "aaa");
        BasicDBObject deepEmbedObj2 = new BasicDBObject();
        deepEmbedObj2.put("str", "bbb");
        embedObj.put("testObjectList", Lists.newArrayList(deepEmbedObj1, deepEmbedObj2));
        dbObj.put("embeddedObject", embedObj);
        Object o = MongoEmbeddedObjectProcessor.valueMapper("embObj", dbObj, TestObject.class);
        if (!(o instanceof TestObject)) {
            Assert.fail("not emb obj");
        }
        System.out.println(o);
    }
}

