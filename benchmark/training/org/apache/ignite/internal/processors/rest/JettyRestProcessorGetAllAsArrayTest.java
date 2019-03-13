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
package org.apache.ignite.internal.processors.rest;


import GridRestCommand.CACHE_GET_ALL;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 *
 */
public class JettyRestProcessorGetAllAsArrayTest extends JettyRestProcessorCommonSelfTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetAll() throws Exception {
        final Map<String, String> entries = F.asMap("getKey1", "getVal1", "getKey2", "getVal2");
        jcache().putAll(entries);
        String ret = content(DEFAULT_CACHE_NAME, CACHE_GET_ALL, "k1", "getKey1", "k2", "getKey2");
        info(("Get all command result: " + ret));
        assertNotNull(ret);
        assertFalse(ret.isEmpty());
        JsonNode node = JettyRestProcessorCommonSelfTest.JSON_MAPPER.readTree(ret);
        assertEquals(GridRestResponse.STATUS_SUCCESS, node.get("successStatus").asInt());
        assertTrue(node.get("error").isNull());
        JsonNode res = node.get("response");
        assertTrue(res.isArray());
        Set<Map<String, String>> returnValue = new HashSet<>();
        returnValue.add(F.asMap("key", "getKey1", "value", "getVal1"));
        returnValue.add(F.asMap("key", "getKey2", "value", "getVal2"));
        assertEquals(returnValue, JettyRestProcessorCommonSelfTest.JSON_MAPPER.treeToValue(res, Set.class));
    }
}

