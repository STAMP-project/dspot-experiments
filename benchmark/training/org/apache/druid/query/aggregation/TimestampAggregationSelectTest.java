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
package org.apache.druid.query.aggregation;


import Granularities.MONTH;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.zip.ZipFile;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.query.Result;
import org.apache.druid.query.select.SelectResultValue;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TimestampAggregationSelectTest {
    private AggregationTestHelper helper;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ColumnSelectorFactory selectorFactory;

    private TestObjectColumnSelector selector;

    private Timestamp[] values = new Timestamp[10];

    private String aggType;

    private String aggField;

    private Long expected;

    public TimestampAggregationSelectTest(String aggType, String aggField, Long expected) {
        this.aggType = aggType;
        this.aggField = aggField;
        this.expected = expected;
    }

    @Test
    public void testSimpleDataIngestionAndSelectTest() throws Exception {
        String recordParser = "{\n" + ((((((((((((((((((((((("  \"type\": \"string\",\n" + "  \"parseSpec\": {\n") + "    \"format\": \"tsv\",\n") + "    \"timestampSpec\": {\n") + "      \"column\": \"timestamp\",\n") + "      \"format\": \"auto\"\n") + "    },\n") + "    \"dimensionsSpec\": {\n") + "      \"dimensions\": [\n") + "        \"product\"\n") + "      ],\n") + "      \"dimensionExclusions\": [],\n") + "      \"spatialDimensions\": []\n") + "    },\n") + "    \"columns\": [\n") + "      \"timestamp\",\n") + "      \"cat\",\n") + "      \"product\",\n") + "      \"prefer\",\n") + "      \"prefer2\",\n") + "      \"pty_country\"\n") + "    ]\n") + "  }\n") + "}");
        String aggregator = (((((((("[\n" + ("  {\n" + "    \"type\": \"")) + (aggType)) + "\",\n") + "    \"name\": \"") + (aggField)) + "\",\n") + "    \"fieldName\": \"timestamp\"\n") + "  }\n") + "]";
        ZipFile zip = new ZipFile(new File(this.getClass().getClassLoader().getResource("druid.sample.tsv.zip").toURI()));
        Sequence<?> seq = helper.createIndexAndRunQueryOnSegment(zip.getInputStream(zip.getEntry("druid.sample.tsv")), recordParser, aggregator, 0, MONTH, 100, Resources.toString(Resources.getResource("select.json"), StandardCharsets.UTF_8));
        Result<SelectResultValue> result = ((Result<SelectResultValue>) (Iterables.getOnlyElement(seq.toList())));
        Assert.assertEquals(36, result.getValue().getEvents().size());
        Assert.assertEquals(expected, result.getValue().getEvents().get(0).getEvent().get(aggField));
    }
}

