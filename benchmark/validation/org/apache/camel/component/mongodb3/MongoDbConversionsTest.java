/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mongodb3;


import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.converter.IOConverter;
import org.bson.Document;
import org.junit.Test;


public class MongoDbConversionsTest extends AbstractMongoDbTest {
    @Test
    public void testInsertMap() throws InterruptedException {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        Map<String, Object> m1 = new HashMap<>();
        Map<String, String> m1Nested = new HashMap<>();
        m1Nested.put("nested1", "nestedValue1");
        m1Nested.put("nested2", "nestedValue2");
        m1.put("field1", "value1");
        m1.put("field2", "value2");
        m1.put("nestedField", m1Nested);
        m1.put(MongoDbConstants.MONGO_ID, "testInsertMap");
        // Object result =
        template.requestBody("direct:insertMap", m1);
        Document b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertMap")).first();
        assertNotNull("No record with 'testInsertMap' _id", b);
    }

    @Test
    public void testInsertPojo() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        // Object result =
        template.requestBody("direct:insertPojo", new MongoDbConversionsTest.MyPojoTest());
        Document b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertPojo")).first();
        assertNotNull("No record with 'testInsertPojo' _id", b);
    }

    @Test
    public void testInsertJsonString() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        // Object result =
        template.requestBody("direct:insertJsonString", "{\"fruits\": [\"apple\", \"banana\", \"papaya\"], \"veggie\": \"broccoli\", \"_id\": \"testInsertJsonString\"}");
        // assertTrue(result instanceof WriteResult);
        Document b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertJsonString")).first();
        assertNotNull("No record with 'testInsertJsonString' _id", b);
    }

    @Test
    public void testInsertJsonInputStream() throws Exception {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        // Object result =
        template.requestBody("direct:insertJsonString", IOConverter.toInputStream("{\"fruits\": [\"apple\", \"banana\"], \"veggie\": \"broccoli\", \"_id\": \"testInsertJsonString\"}\n", null));
        Document b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertJsonString")).first();
        assertNotNull("No record with 'testInsertJsonString' _id", b);
    }

    @Test
    public void testInsertBsonInputStream() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        Document document = new Document(MongoDbConstants.MONGO_ID, "testInsertBsonString");
        // Object result =
        template.requestBody("direct:insertJsonString", new ByteArrayInputStream(document.toJson().getBytes()));
        Document b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertBsonString")).first();
        assertNotNull("No record with 'testInsertBsonString' _id", b);
    }

    // CHECKSTYLE:ON
    @SuppressWarnings("unused")
    private class MyPojoTest {
        public int number = 123;

        public String text = "hello";

        public String[] array = new String[]{ "daVinci", "copernico", "einstein" };

        // CHECKSTYLE:OFF
        public String _id = "testInsertPojo";
    }
}

