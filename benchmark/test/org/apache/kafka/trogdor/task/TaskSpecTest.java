/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.task;


import JsonUtil.JSON_SERDE;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.apache.kafka.trogdor.common.JsonUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TaskSpecTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testTaskSpecSerialization() throws Exception {
        try {
            JSON_SERDE.readValue("{\"startMs\":123,\"durationMs\":456,\"exitMs\":1000,\"error\":\"foo\"}", SampleTaskSpec.class);
            Assert.fail("Expected InvalidTypeIdException because type id is missing.");
        } catch (InvalidTypeIdException e) {
        }
        String inputJson = "{\"class\":\"org.apache.kafka.trogdor.task.SampleTaskSpec\"," + "\"startMs\":123,\"durationMs\":456,\"nodeToExitMs\":{\"node01\":1000},\"error\":\"foo\"}";
        SampleTaskSpec spec = JSON_SERDE.readValue(inputJson, SampleTaskSpec.class);
        Assert.assertEquals(123, startMs());
        Assert.assertEquals(456, durationMs());
        Assert.assertEquals(Long.valueOf(1000), spec.nodeToExitMs().get("node01"));
        Assert.assertEquals("foo", spec.error());
        String outputJson = JsonUtil.toJsonString(spec);
        Assert.assertEquals(inputJson, outputJson);
    }
}

