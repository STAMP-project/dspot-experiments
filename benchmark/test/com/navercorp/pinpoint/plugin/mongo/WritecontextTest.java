/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.mongo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonJavaScript;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonSymbol;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.BsonValue;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 * @author Roy Kim
 */
public class WritecontextTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void parseBsonNestedClass() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("specialchar", new BasicDBObject("$gt", "1"));
        logger.debug("query:{}", query);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        Map<String, ?> query2Map = ((Map<String, ?>) (query1Map.get("specialchar")));
        checkValue(query2Map);
    }

    @Test
    public void parseArray() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("stringArray", new String[]{ "\"a", "b", "c", "\"\"", "" });
        logger.debug("query:{}", query);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("stringArray")));
        checkValue(objectArray);
    }

    @Test
    public void parseBsonArrayWithValues() throws IOException {
        BsonValue a = new BsonString("stest");
        BsonValue b = new BsonDouble(111);
        BsonValue c = new BsonBoolean(true);
        BsonDocument document = new BsonDocument().append("int32", new BsonInt32(12)).append("int64", new BsonInt64(77L)).append("bo\"olean", new BsonBoolean(true)).append("date", new BsonDateTime(new Date().getTime())).append("double", new BsonDouble(12.3)).append("string", new BsonString("pinpoint")).append("objectId", new org.bson.BsonObjectId(new ObjectId())).append("code", new BsonJavaScript("int i = 10;")).append("codeWithScope", new BsonJavaScriptWithScope("int x = y", new BsonDocument("y", new BsonInt32(1)))).append("regex", new BsonRegularExpression("^test.*regex.*xyz$", "big")).append("symbol", new BsonSymbol("wow")).append("timestamp", new BsonTimestamp(305419896, 5)).append("undefined", new BsonUndefined()).append("binary1", new BsonBinary(new byte[]{ ((byte) (224)), 79, ((byte) (208)), 32 })).append("oldBinary", new BsonBinary(BsonBinarySubType.OLD_BINARY, new byte[]{ 1, 1, 1, 1, 1 })).append("arrayInt", new org.bson.BsonArray(Arrays.asList(a, b, c, new BsonInt32(7)))).append("document", new BsonDocument("a", new BsonInt32(77))).append("dbPointer", new org.bson.BsonDbPointer("db.coll", new ObjectId())).append("null", new BsonNull()).append("decimal128", new org.bson.BsonDecimal128(new Decimal128(55)));
        BasicDBObject query = new BasicDBObject();
        query.put("ComplexBson", document);
        logger.debug("document:{}", document);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("val:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        checkValue(query1Map);
    }

    @Test
    public void parsePrimitiveIntArray() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("intArray", new int[]{ 1, 2, 3 });
        Object[] objArray = new Object[]{ query, query };
        logger.debug("objArray:{}", objArray);
        NormalizedBson stringStringValue = MongoUtil.parseBson(objArray, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), objArray.length);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("intArray")));
        checkValue(objectArray);
    }

    @Test
    public void parsePrimitiveDoubleArray() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("doubleArray", new double[]{ 1, 2, 3 });
        logger.debug("query:{}", query);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("doubleArray")));
        checkValue(objectArray);
    }

    @Test
    public void parseCollection() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("collection", Arrays.asList("naver", "apple"));
        logger.debug("query:{}", query);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("collection")));
        checkValue(objectArray);
    }

    @Test
    public void parseWithDoubleQuoteInKey() throws IOException {
        BasicDBObject query = new BasicDBObject();
        query.put("\"query", new BasicDBObject("\"$gt", "1"));
        logger.debug("query:{}", query);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        Map<String, ?> query2Map = ((Map<String, ?>) (query1Map.get("\"query")));
        checkValue(query2Map);
    }

    @Test
    public void parseTestAbbreviation_BsonValueArray() throws IOException {
        BsonInt32[] bsonInt32s = new BsonInt32[40];
        for (int i = 0; i < 40; i++) {
            bsonInt32s[i] = new BsonInt32((i + 1));
        }
        BsonDocument document = // .append("arrayInt", new BsonArray({1,1,1,1,1,1,1,1,1,1,1})
        new BsonDocument().append("double", new BsonDouble(12.3)).append("arrayInt", new org.bson.BsonArray(Arrays.asList(bsonInt32s))).append("binary1", new BsonBinary(new byte[]{ ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)), ((byte) (224)), 79, ((byte) (208)), 32, ((byte) (234)), 58, 105, 16, ((byte) (162)), ((byte) (216)), 8, 0, 43, 48, 48, ((byte) (157)) })).append("oldBinary", new BsonBinary(BsonBinarySubType.OLD_BINARY, new byte[]{ 1, 1, 1, 1, 1 }));
        BasicDBObject query = new BasicDBObject();
        query.put("ComplexBson", document);
        logger.debug("document:{}", document);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("val:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        checkValue(query1Map);
    }

    @Test
    public void parseTestAbbreviation_Collection() throws IOException {
        Integer[] integers = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 };
        List<Integer> arr = Arrays.asList(integers);
        BasicDBObject query = new BasicDBObject();
        query.put("intArray", arr);
        Object[] objArray = new Object[]{ query };
        logger.debug("objArray:{}", objArray);
        NormalizedBson stringStringValue = MongoUtil.parseBson(objArray, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), objArray.length);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("intArray")));
        checkValue(objectArray);
    }

    @Test
    public void parseTestAbbreviation_PrimitiveArray() throws IOException {
        int[] arr = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 };
        BasicDBObject query = new BasicDBObject();
        query.put("intArray", arr);
        Object[] objArray = new Object[]{ query };
        logger.debug("objArray:{}", objArray);
        NormalizedBson stringStringValue = MongoUtil.parseBson(objArray, true);
        logger.debug("parsedStringStringValue:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), objArray.length);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        ArrayList objectArray = ((ArrayList) (query1Map.get("intArray")));
        checkValue(objectArray);
    }

    @Test
    public void parseTestAbbreviation_Array() throws IOException {
        BsonValue c = new BsonBoolean(true);
        BsonDocument document = new BsonDocument().append("double", new BsonDouble(12.3)).append("arrayInt", new org.bson.BsonArray(Arrays.asList(c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c)));
        BasicDBObject query = new BasicDBObject();
        query.put("ComplexBson", document);
        logger.debug("document:{}", document);
        NormalizedBson stringStringValue = MongoUtil.parseBson(new Object[]{ query }, true);
        logger.debug("val:{}", stringStringValue);
        List list = objectMapper.readValue((("[" + (stringStringValue.getNormalizedBson())) + "]"), List.class);
        Assert.assertEquals(list.size(), 1);
        Map<String, ?> query1Map = ((Map<String, ?>) (list.get(0)));
        checkValue(query1Map);
    }
}

