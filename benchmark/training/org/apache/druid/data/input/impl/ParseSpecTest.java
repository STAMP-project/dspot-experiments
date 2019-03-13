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
package org.apache.druid.data.input.impl;


import JacksonUtils.TYPE_REFERENCE_MAP_STRING_OBJECT;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ParseSpecTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ObjectMapper mapper;

    @Test(expected = ParseException.class)
    public void testDuplicateNames() {
        // expected exception
        @SuppressWarnings("unused")
        final ParseSpec spec = new DelimitedParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("a", "b", "a")), new ArrayList(), new ArrayList()), ",", " ", Arrays.asList("a", "b"), false, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDimAndDimExcluOverlap() {
        // expected exception
        @SuppressWarnings("unused")
        final ParseSpec spec = new DelimitedParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("a", "B")), Collections.singletonList("B"), new ArrayList()), ",", null, Arrays.asList("a", "B"), false, 0);
    }

    @Test
    public void testDimExclusionDuplicate() {
        // expected exception
        @SuppressWarnings("unused")
        final ParseSpec spec = new DelimitedParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Collections.singletonList("a")), Lists.newArrayList("B", "B"), new ArrayList()), ",", null, Arrays.asList("a", "B"), false, 0);
    }

    @Test
    public void testDefaultTimestampSpec() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("parseSpec requires timestampSpec");
        // expected exception
        @SuppressWarnings("unused")
        final ParseSpec spec = new DelimitedParseSpec(null, new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Collections.singletonList("a")), Lists.newArrayList("B", "B"), new ArrayList()), ",", null, Arrays.asList("a", "B"), false, 0);
    }

    @Test
    public void testDimensionSpecRequired() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("parseSpec requires dimensionSpec");
        // expected exception
        @SuppressWarnings("unused")
        final ParseSpec spec = new DelimitedParseSpec(new TimestampSpec("timestamp", "auto", null), null, ",", null, Arrays.asList("a", "B"), false, 0);
    }

    @Test
    public void testSerde() throws IOException {
        final String json = "{" + ((("\"format\":\"timeAndDims\", " + "\"timestampSpec\": {\"column\":\"timestamp\"}, ") + "\"dimensionsSpec\":{}") + "}");
        final Object mapValue = mapper.readValue(json, TYPE_REFERENCE_MAP_STRING_OBJECT);
        final ParseSpec parseSpec = mapper.convertValue(mapValue, ParseSpec.class);
        Assert.assertEquals(TimeAndDimsParseSpec.class, parseSpec.getClass());
        Assert.assertEquals("timestamp", parseSpec.getTimestampSpec().getTimestampColumn());
        Assert.assertEquals(ImmutableList.of(), parseSpec.getDimensionsSpec().getDimensionNames());
        // Test round-trip.
        Assert.assertEquals(parseSpec, mapper.readValue(mapper.writeValueAsString(parseSpec), ParseSpec.class));
    }

    @Test
    public void testBadTypeSerde() throws IOException {
        final String json = "{" + ((("\"format\":\"foo\", " + "\"timestampSpec\": {\"column\":\"timestamp\"}, ") + "\"dimensionsSpec\":{}") + "}");
        final Object mapValue = mapper.readValue(json, TYPE_REFERENCE_MAP_STRING_OBJECT);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(JsonMappingException.class));
        expectedException.expectMessage("Could not resolve type id 'foo' into a subtype");
        mapper.convertValue(mapValue, ParseSpec.class);
    }
}

