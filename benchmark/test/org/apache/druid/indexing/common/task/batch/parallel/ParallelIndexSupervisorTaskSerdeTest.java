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
package org.apache.druid.indexing.common.task.batch.parallel;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.segment.realtime.firehose.LocalFirehoseFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ParallelIndexSupervisorTaskSerdeTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final ParseSpec DEFAULT_PARSE_SPEC = new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("ts", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("ts", "dim")), new ArrayList(), new ArrayList()), null, Arrays.asList("ts", "dim", "val"), false, 0);

    private final TestUtils testUtils = new TestUtils();

    @Test
    public void testSerde() throws IOException {
        final ObjectMapper objectMapper = testUtils.getTestObjectMapper();
        objectMapper.registerSubtypes(new NamedType(LocalFirehoseFactory.class, "local"));
        final ParallelIndexSupervisorTask task = newTask(objectMapper, Intervals.of("2018/2019"));
        final String json = objectMapper.writeValueAsString(task);
        Assert.assertEquals(task, objectMapper.readValue(json, Task.class));
    }
}

