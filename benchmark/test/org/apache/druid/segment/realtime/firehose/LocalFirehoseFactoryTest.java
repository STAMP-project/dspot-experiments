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
package org.apache.druid.segment.realtime.firehose;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.Row;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class LocalFirehoseFactoryTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private LocalFirehoseFactory factory;

    @Test
    public void testConnect() throws IOException {
        try (final Firehose firehose = factory.connect(new StringInputRowParser(new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("timestamp", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("timestamp", "a")), new ArrayList(), new ArrayList()), ",", Arrays.asList("timestamp", "a"), false, 0), StandardCharsets.UTF_8.name()), null)) {
            final List<Row> rows = new ArrayList<>();
            while (firehose.hasMore()) {
                rows.add(firehose.nextRow());
            } 
            Assert.assertEquals(5, rows.size());
            rows.sort(Comparator.comparing(Row::getTimestamp));
            for (int i = 0; i < 5; i++) {
                final List<String> dimVals = rows.get(i).getDimension("a");
                Assert.assertEquals(1, dimVals.size());
                Assert.assertEquals((i + "th test file"), dimVals.get(0));
            }
        }
    }
}

