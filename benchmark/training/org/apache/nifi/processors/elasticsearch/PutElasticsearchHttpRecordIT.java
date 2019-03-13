/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.elasticsearch;


import FetchElasticsearchHttp.REL_SUCCESS;
import PutElasticsearchHttpRecord.ALWAYS_SUPPRESS;
import PutElasticsearchHttpRecord.INDEX;
import PutElasticsearchHttpRecord.REL_FAILURE;
import PutElasticsearchHttpRecord.REL_RETRY;
import PutElasticsearchHttpRecord.SUPPRESS_MISSING;
import PutElasticsearchHttpRecord.SUPPRESS_NULLS;
import PutElasticsearchHttpRecord.TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class PutElasticsearchHttpRecordIT {
    protected TestRunner runner;

    private MockRecordParser recordReader;

    static RecordSchema personSchema;

    static TestRunner FETCH_RUNNER;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testNoNullSuppresion() throws Exception {
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        List<Map<String, String>> attrs = new ArrayList<>();
        Map<String, String> attr = new HashMap<>();
        attr.put("doc_id", "1");
        attrs.add(attr);
        setupPut();
        testFetch(attrs);
        byte[] raw = PutElasticsearchHttpRecordIT.FETCH_RUNNER.getContentAsByteArray(PutElasticsearchHttpRecordIT.FETCH_RUNNER.getFlowFilesForRelationship(REL_SUCCESS).get(0));
        String val = new String(raw);
        Map<String, Object> parsed = mapper.readValue(val, Map.class);
        Assert.assertNotNull(parsed);
        Map<String, Object> person = ((Map) (parsed.get("person")));
        Assert.assertNotNull(person);
        Assert.assertTrue(person.containsKey("sport"));
        Assert.assertNull(person.get("sport"));
    }

    @Test
    public void testMissingRecord() throws Exception {
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
            }
        }));
        recordReader.addRecord(2, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        runner.setProperty(SUPPRESS_NULLS, SUPPRESS_MISSING);
        sharedSuppressTest(( p1, p2) -> {
            Assert.assertFalse(p1.containsKey("sport"));
            Assert.assertTrue(p2.containsKey("sport"));
            Assert.assertNull(p2.get("sport"));
        });
    }

    @Test
    public void testAlwaysSuppress() throws Exception {
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
            }
        }));
        recordReader.addRecord(2, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        runner.setProperty(SUPPRESS_NULLS, ALWAYS_SUPPRESS);
        sharedSuppressTest(( p1, p2) -> {
            Assert.assertFalse(p1.containsKey("sport"));
            Assert.assertFalse(p2.containsKey("sport"));
        });
    }

    @Test
    public void testIllegalIndexName() throws Exception {
        // Undo some stuff from setup()
        runner.setProperty(INDEX, "people\"test");
        runner.setProperty(TYPE, "person");
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        List<Map<String, String>> attrs = new ArrayList<>();
        Map<String, String> attr = new HashMap<>();
        attr.put("doc_id", "1");
        attrs.add(attr);
        runner.enqueue("");
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(PutElasticsearchHttpRecord.REL_SUCCESS, 0);
    }

    @Test
    public void testIndexNameWithJsonChar() throws Exception {
        // Undo some stuff from setup()
        runner.setProperty(INDEX, "people}test");
        runner.setProperty(TYPE, "person");
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        List<Map<String, String>> attrs = new ArrayList<>();
        Map<String, String> attr = new HashMap<>();
        attr.put("doc_id", "1");
        attrs.add(attr);
        runner.enqueue("");
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(PutElasticsearchHttpRecord.REL_SUCCESS, 1);
    }

    @Test
    public void testTypeNameWithSpecialChars() throws Exception {
        // Undo some stuff from setup()
        runner.setProperty(INDEX, "people_test2");
        runner.setProperty(TYPE, "per\"son");
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(PutElasticsearchHttpRecordIT.personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", null);
            }
        }));
        List<Map<String, String>> attrs = new ArrayList<>();
        Map<String, String> attr = new HashMap<>();
        attr.put("doc_id", "1");
        attrs.add(attr);
        setupPut();
    }

    private interface SharedPostTest {
        void run(Map<String, Object> p1, Map<String, Object> p2);
    }
}

