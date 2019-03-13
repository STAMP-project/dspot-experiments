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
import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.zip.ZipFile;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.query.groupby.GroupByQueryConfig;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TimestampGroupByAggregationTest {
    private AggregationTestHelper helper;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ColumnSelectorFactory selectorFactory;

    private TestObjectColumnSelector selector;

    private Timestamp[] values = new Timestamp[10];

    private final String aggType;

    private final String aggField;

    private final String groupByField;

    private final DateTime expected;

    private final GroupByQueryConfig config;

    public TimestampGroupByAggregationTest(String aggType, String aggField, String groupByField, DateTime expected, GroupByQueryConfig config) {
        this.aggType = aggType;
        this.aggField = aggField;
        this.groupByField = groupByField;
        this.expected = expected;
        this.config = config;
    }

    @Test
    public void testSimpleDataIngestionAndGroupByTest() throws Exception {
        String recordParser = "{\n" + ((((((((((((((((((((((("  \"type\": \"string\",\n" + "  \"parseSpec\": {\n") + "    \"format\": \"tsv\",\n") + "    \"timestampSpec\": {\n") + "      \"column\": \"timestamp\",\n") + "      \"format\": \"auto\"\n") + "    },\n") + "    \"dimensionsSpec\": {\n") + "      \"dimensions\": [\n") + "        \"product\"\n") + "      ],\n") + "      \"dimensionExclusions\": [],\n") + "      \"spatialDimensions\": []\n") + "    },\n") + "    \"columns\": [\n") + "      \"timestamp\",\n") + "      \"cat\",\n") + "      \"product\",\n") + "      \"prefer\",\n") + "      \"prefer2\",\n") + "      \"pty_country\"\n") + "    ]\n") + "  }\n") + "}");
        String aggregator = (((((((("[\n" + ("  {\n" + "    \"type\": \"")) + (aggType)) + "\",\n") + "    \"name\": \"") + (aggField)) + "\",\n") + "    \"fieldName\": \"timestamp\"\n") + "  }\n") + "]";
        String groupBy = (((((((((((((("{\n" + (((((("  \"queryType\": \"groupBy\",\n" + "  \"dataSource\": \"test_datasource\",\n") + "  \"granularity\": \"MONTH\",\n") + "  \"dimensions\": [\"product\"],\n") + "  \"aggregations\": [\n") + "    {\n") + "      \"type\": \"")) + (aggType)) + "\",\n") + "      \"name\": \"") + (groupByField)) + "\",\n") + "      \"fieldName\": \"") + (aggField)) + "\"\n") + "    }\n") + "  ],\n") + "  \"intervals\": [\n") + "    \"2011-01-01T00:00:00.000Z/2011-05-01T00:00:00.000Z\"\n") + "  ]\n") + "}";
        ZipFile zip = new ZipFile(new File(this.getClass().getClassLoader().getResource("druid.sample.tsv.zip").toURI()));
        Sequence<Row> seq = helper.createIndexAndRunQueryOnSegment(zip.getInputStream(zip.getEntry("druid.sample.tsv")), recordParser, aggregator, 0, MONTH, 100, groupBy);
        List<Row> results = seq.toList();
        Assert.assertEquals(36, results.size());
        Assert.assertEquals(expected, getEvent().get(groupByField));
    }
}

