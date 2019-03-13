/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pinot.thirdeye.hadoop.aggregation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Properties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.apache.pinot.thirdeye.hadoop.config.ThirdEyeConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * This tests mapper of Aggregation phase, to check conversion of time column to bucket time
 * This also tests reducer to check aggregation using new time values
 */
public class AggregationPhaseTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String HADOOP_IO_SERIALIZATION = "io.serializations";

    private static final String AVRO_SCHEMA = "schema.avsc";

    private String outputPath;

    private Schema inputSchema;

    private ThirdEyeConfig thirdeyeConfig;

    private AggregationPhaseConfig aggPhaseConfig;

    Properties props = new Properties();

    private MapDriver<AvroKey<GenericRecord>, NullWritable, BytesWritable, BytesWritable> mapDriver;

    private ReduceDriver<BytesWritable, BytesWritable, AvroKey<GenericRecord>, NullWritable> reduceDriver;

    @Test
    public void testAggregationPhase() throws Exception {
        int recordCount = 0;
        List<GenericRecord> inputRecords = generateTestMapperData();
        for (GenericRecord record : inputRecords) {
            AvroKey<GenericRecord> inKey = new AvroKey<GenericRecord>();
            inKey.datum(record);
            mapDriver.addInput(new Pair<AvroKey<GenericRecord>, NullWritable>(inKey, NullWritable.get()));
            recordCount++;
        }
        List<Pair<BytesWritable, BytesWritable>> mapResult = mapDriver.run();
        Assert.assertEquals("Incorrect number of records emitted by mapper", recordCount, mapResult.size());
        AggregationPhaseMapOutputKey keyWrapper = AggregationPhaseMapOutputKey.fromBytes(mapResult.get(0).getFirst().getBytes(), aggPhaseConfig.getDimensionTypes());
        Assert.assertEquals(406058, keyWrapper.getTime());
        keyWrapper = AggregationPhaseMapOutputKey.fromBytes(mapResult.get(1).getFirst().getBytes(), aggPhaseConfig.getDimensionTypes());
        Assert.assertEquals(406058, keyWrapper.getTime());
        keyWrapper = AggregationPhaseMapOutputKey.fromBytes(mapResult.get(2).getFirst().getBytes(), aggPhaseConfig.getDimensionTypes());
        Assert.assertEquals(406059, keyWrapper.getTime());
        List<Pair<BytesWritable, List<BytesWritable>>> reduceInput = generateTestReduceData(mapResult);
        reduceDriver.addAll(reduceInput);
        List<Pair<AvroKey<GenericRecord>, NullWritable>> reduceResult = reduceDriver.run();
        Assert.assertEquals("Incorrect number of records returned by aggregation reducer", 2, reduceResult.size());
        GenericRecord record = reduceResult.get(0).getFirst().datum();
        List<Object> dimensionsExpected = Lists.newArrayList();
        dimensionsExpected.add("abc1");
        dimensionsExpected.add(501L);
        dimensionsExpected.add("xyz1");
        List<Object> dimensionsActual = getDimensionsFromRecord(record);
        Assert.assertEquals(dimensionsExpected, dimensionsActual);
        List<Integer> metricsExpected = Lists.newArrayList(200, 40);
        List<Integer> metricsActual = getMetricsFromRecord(record);
        Assert.assertEquals(metricsExpected, metricsActual);
        Assert.assertEquals(406058, ((long) (record.get("hoursSinceEpoch"))));
        record = reduceResult.get(1).getFirst().datum();
        dimensionsExpected = Lists.newArrayList();
        dimensionsExpected.add("abc2");
        dimensionsExpected.add(502L);
        dimensionsExpected.add("xyz2");
        dimensionsActual = getDimensionsFromRecord(record);
        Assert.assertEquals(dimensionsExpected, dimensionsActual);
        metricsExpected = Lists.newArrayList(10, 2);
        metricsActual = getMetricsFromRecord(record);
        Assert.assertEquals(metricsExpected, metricsActual);
        Assert.assertEquals(406059, ((long) (record.get("hoursSinceEpoch"))));
    }
}

