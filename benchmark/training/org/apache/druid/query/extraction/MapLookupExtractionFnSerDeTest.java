/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.extraction;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.druid.java.util.common.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MapLookupExtractionFnSerDeTest {
    private static ObjectMapper mapper;

    private static final Map<String, String> renames = ImmutableMap.of("foo", "bar", "bar", "baz");

    @Test
    public void testDeserialization() throws IOException {
        final DimExtractionFn fn = MapLookupExtractionFnSerDeTest.mapper.readerFor(DimExtractionFn.class).readValue(StringUtils.format("{\"type\":\"lookup\",\"lookup\":{\"type\":\"map\", \"map\":%s}}", MapLookupExtractionFnSerDeTest.mapper.writeValueAsString(MapLookupExtractionFnSerDeTest.renames)));
        for (String key : MapLookupExtractionFnSerDeTest.renames.keySet()) {
            Assert.assertEquals(MapLookupExtractionFnSerDeTest.renames.get(key), fn.apply(key));
        }
        final String crazyString = UUID.randomUUID().toString();
        Assert.assertEquals(null, fn.apply(crazyString));
        Assert.assertEquals(crazyString, MapLookupExtractionFnSerDeTest.mapper.readerFor(DimExtractionFn.class).<DimExtractionFn>readValue(StringUtils.format("{\"type\":\"lookup\",\"lookup\":{\"type\":\"map\", \"map\":%s}, \"retainMissingValue\":true}", MapLookupExtractionFnSerDeTest.mapper.writeValueAsString(MapLookupExtractionFnSerDeTest.renames))).apply(crazyString));
    }
}

