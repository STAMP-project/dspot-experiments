/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.protocol.protobuf.v1.serialization.codec;


import JSONFormatter.JSON_CLASSNAME;
import java.util.Arrays;
import java.util.List;
import org.apache.geode.cache.Cache;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.JsonPdxConverter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class JsonPdxConverterJUnitTest {
    private String complexJSONString = "{\n" + ((((((((((((((((((((((((((((((((((((((((((("    \"_id\": \"599c7d885df276ac3e0bf10a\",\n" + "    \"index\": 0,\n") + "    \"guid\": \"395902d8-36ed-4178-ad70-2f720c557c55\",\n") + "    \"isActive\": true,\n") + "    \"balance\": \"$3,152.82\",\n") + "    \"picture\": \"http://placehold.it/32x32\",\n") + "    \"age\": 27,\n") + "    \"eyeColor\": \"blue\",\n") + "    \"name\": \"Kristina Norman\",\n") + "    \"gender\": \"female\",\n") + "    \"company\": \"ORBALIX\",\n") + "    \"email\": \"kristinanorman@orbalix.com\",\n") + "    \"phone\": \"+1 (983) 526-3433\",\n") + "    \"address\": \"400 Vermont Court, Denio, Wyoming, 7142\",\n") + "    \"about\": \"Mollit nostrud irure excepteur veniam aliqua. Non id tempor magna nisi ipsum minim. Culpa velit tempor culpa mollit cillum deserunt nisi culpa irure ut nostrud enim consectetur voluptate. Elit veniam velit enim minim. Sunt nostrud ea duis enim sit cillum.\",\n") + "    \"registered\": \"2015-03-11T02:22:45 +07:00\",\n") + "    \"latitude\": -0.853065,\n") + "    \"longitude\": -29.749358,\n") + "    \"tags\": [\n") + "      \"laboris\",\n") + "      \"velit\",\n") + "      \"non\",\n") + "      \"est\",\n") + "      \"anim\",\n") + "      \"amet\",\n") + "      \"cupidatat\"\n") + "    ],\n") + "    \"friends\": [\n") + "      {\n") + "        \"id\": 0,\n") + "        \"name\": \"Roseann Roy\"\n") + "      },\n") + "      {\n") + "        \"id\": 1,\n") + "        \"name\": \"Adriana Perry\"\n") + "      },\n") + "      {\n") + "        \"id\": 2,\n") + "        \"name\": \"Tyler Mccarthy\"\n") + "      }\n") + "    ],\n") + "    \"greeting\": \"Hello, Kristina Norman! You have 8 unread messages.\",\n") + "    \"favoriteFruit\": \"apple\"\n") + "  }");

    private Cache cache;

    @Test
    public void testSimpleJSONEncode() throws Exception {
        PdxInstanceFactory pdxInstanceFactory = ((GemFireCacheImpl) (cache)).createPdxInstanceFactory(JSON_CLASSNAME, false);
        pdxInstanceFactory.writeString("string", "someString");
        pdxInstanceFactory.writeBoolean("boolean", true);
        PdxInstance pdxInstance = pdxInstanceFactory.create();
        String encodedJSON = new JsonPdxConverter().encode(pdxInstance);
        String lineSeparator = System.lineSeparator();
        String expectedJsonString = ((((((("{" + lineSeparator) + "") + "  \"string\" : \"someString\",") + lineSeparator) + "") + "  \"boolean\" : true") + lineSeparator) + "}";
        Assert.assertEquals(expectedJsonString, encodedJSON);
    }

    @Test
    public void testComplexJSONEncode() throws Exception {
        PdxInstance pdxInstanceForComplexJSONString = createPDXInstanceForComplexJSONString();
        PdxInstance decodedJSONPdxInstance = new JsonPdxConverter().decode(complexJSONString);
        pdxInstanceEquals(pdxInstanceForComplexJSONString, decodedJSONPdxInstance);
    }

    @Test
    public void testJSONDecode() throws Exception {
        PdxInstance pdxInstance = new JsonPdxConverter().decode(complexJSONString);
        Assert.assertNotNull(pdxInstance);
        List<String> fieldNames = Arrays.asList("_id", "index", "guid", "isActive", "balance", "picture", "age", "eyeColor", "name", "gender", "company", "email", "phone", "address", "about", "registered", "latitude", "longitude", "tags", "friends", "greeting", "favoriteFruit");
        Assert.assertEquals(fieldNames, pdxInstance.getFieldNames());
    }
}

