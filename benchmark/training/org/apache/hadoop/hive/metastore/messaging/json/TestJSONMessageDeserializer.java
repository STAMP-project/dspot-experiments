/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.messaging.json;


import JSONMessageDeserializer.mapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skyscreamer.jsonassert.JSONAssert;


@Category(MetastoreUnitTest.class)
public class TestJSONMessageDeserializer {
    public static class MyClass {
        @JsonProperty
        private int a;

        @JsonProperty
        private Map<String, String> map;

        private long l;

        private String shouldNotSerialize = "shouldNotSerialize";

        // for jackson to instantiate
        MyClass() {
        }

        MyClass(int a, Map<String, String> map, long l) {
            this.a = a;
            this.map = map;
            this.l = l;
        }

        @JsonProperty
        long getL() {
            return l;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestJSONMessageDeserializer.MyClass myClass = ((TestJSONMessageDeserializer.MyClass) (o));
            if ((a) != (myClass.a)) {
                return false;
            }
            if ((l) != (myClass.l)) {
                return false;
            }
            if (!(map.equals(myClass.map))) {
                return false;
            }
            return shouldNotSerialize.equals(myClass.shouldNotSerialize);
        }

        @Override
        public int hashCode() {
            int result = a;
            result = (31 * result) + (map.hashCode());
            result = (31 * result) + ((int) ((l) ^ ((l) >>> 32)));
            result = (31 * result) + (shouldNotSerialize.hashCode());
            return result;
        }
    }

    @Test
    public void shouldNotSerializePropertiesNotAnnotated() throws IOException, JSONException {
        TestJSONMessageDeserializer.MyClass obj = new TestJSONMessageDeserializer.MyClass(Integer.MAX_VALUE, new HashMap<String, String>() {
            {
                put("a", "a");
                put("b", "b");
            }
        }, Long.MAX_VALUE);
        String json = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals("{\"a\":2147483647,\"map\":{\"b\":\"b\",\"a\":\"a\"},\"l\":9223372036854775807}", json, false);
    }

    @Test
    public void shouldDeserializeJsonStringToObject() throws IOException {
        String json = "{\"a\":47,\"map\":{\"a\":\"a\",\"b\":\"a value for b\"},\"l\":98}";
        TestJSONMessageDeserializer.MyClass actual = mapper.readValue(json, TestJSONMessageDeserializer.MyClass.class);
        TestJSONMessageDeserializer.MyClass expected = new TestJSONMessageDeserializer.MyClass(47, new HashMap<String, String>() {
            {
                put("a", "a");
                put("b", "a value for b");
            }
        }, 98L);
        Assert.assertEquals(expected, actual);
    }
}

