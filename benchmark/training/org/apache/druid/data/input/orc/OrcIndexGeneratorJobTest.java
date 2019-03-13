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
package org.apache.druid.data.input.orc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.HadoopDruidIndexerConfig;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.joda.time.Interval;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class OrcIndexGeneratorJobTest {
    private static final AggregatorFactory[] aggs = new AggregatorFactory[]{ new LongSumAggregatorFactory("visited_num", "visited_num"), new HyperUniquesAggregatorFactory("unique_hosts", "host") };

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ObjectMapper mapper;

    private HadoopDruidIndexerConfig config;

    private final String dataSourceName = "website";

    private final List<String> data = ImmutableList.of("2014102200,a.example.com,100", "2014102200,b.exmaple.com,50", "2014102200,c.example.com,200", "2014102200,d.example.com,250", "2014102200,e.example.com,123", "2014102200,f.example.com,567", "2014102200,g.example.com,11", "2014102200,h.example.com,251", "2014102200,i.example.com,963", "2014102200,j.example.com,333", "2014102212,a.example.com,100", "2014102212,b.exmaple.com,50", "2014102212,c.example.com,200", "2014102212,d.example.com,250", "2014102212,e.example.com,123", "2014102212,f.example.com,567", "2014102212,g.example.com,11", "2014102212,h.example.com,251", "2014102212,i.example.com,963", "2014102212,j.example.com,333");

    private final Interval interval = Intervals.of("2014-10-22T00:00:00Z/P1D");

    private File dataRoot;

    private File outputRoot;

    private Integer[][][] shardInfoForEachSegment = new Integer[][][]{ new Integer[][]{ new Integer[]{ 0, 4 }, new Integer[]{ 1, 4 }, new Integer[]{ 2, 4 }, new Integer[]{ 3, 4 } } };

    private final InputRowParser inputRowParser = new OrcHadoopInputRowParser(new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec("timestamp", "yyyyMMddHH", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("host")), null, null)), "struct<timestamp:string,host:string,visited_num:int>", null);

    @Test
    public void testIndexGeneratorJob() throws IOException {
        verifyJob(new org.apache.druid.indexer.IndexGeneratorJob(config));
    }
}

