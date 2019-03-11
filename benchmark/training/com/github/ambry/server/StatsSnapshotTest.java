/**
 * Copyright 2018 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.server;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Serialization/deserialization unit tests of {@link StatsSnapshot}.
 */
public class StatsSnapshotTest {
    /**
     * Serialization unit test of {@link StatsSnapshot}.
     * Test if subMap and null fields are removed from serialized result.
     */
    @Test
    public void serializeStatsSnapshotTest() throws IOException {
        Long val = 100L;
        Map<String, StatsSnapshot> subMap = new HashMap<>();
        subMap.put("first", new StatsSnapshot(40L, null));
        subMap.put("second", new StatsSnapshot(60L, null));
        StatsSnapshot snapshot = new StatsSnapshot(val, subMap);
        String result = new ObjectMapper().writeValueAsString(snapshot);
        Assert.assertThat("Result should contain \"first\" keyword and associated entry", result, CoreMatchers.containsString("first"));
        Assert.assertThat("Result should contain \"second\" keyword and associated entry", result, CoreMatchers.containsString("second"));
        Assert.assertThat("Result should not contain \"subMap\" keyword", result, CoreMatchers.not(CoreMatchers.containsString("subMap")));
        Assert.assertThat("Result should ignore any null fields", result, CoreMatchers.not(CoreMatchers.containsString("null")));
    }

    /**
     * Deserialization unit test of {@link StatsSnapshot}.
     * Test if {@link StatsSnapshot} can be reconstructed from json string in the form of a directory or tree (consist of
     * value and subMap fields).
     */
    @Test
    public void deserializeStatsSnapshotTest() throws IOException {
        String jsonAsString = "{\"value\":100,\"first\":{\"value\":40},\"second\":{\"value\":60}}";
        StatsSnapshot snapshot = new ObjectMapper().readValue(jsonAsString, StatsSnapshot.class);
        Assert.assertEquals("Mismatch in total aggregated value for StatsSnapshot", 100L, snapshot.getValue());
        Assert.assertEquals("Mismatch in aggregated value for first account", 40L, snapshot.getSubMap().get("first").getValue());
        Assert.assertEquals("Mismatch in aggregated value for second account", 60L, snapshot.getSubMap().get("second").getValue());
        Assert.assertEquals("The subMap in first account should be null", null, snapshot.getSubMap().get("first").getSubMap());
        Assert.assertEquals("The subMap in second account should be null", null, snapshot.getSubMap().get("second").getSubMap());
    }
}

