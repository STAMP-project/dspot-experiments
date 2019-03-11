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
package org.apache.druid.storage.hdfs;


import DataSegment.PruneLoadSpecHolder;
import DataSegment.PruneLoadSpecHolder.DEFAULT;
import JobHelper.INDEX_ZIP;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.druid.indexer.Bucket;
import org.apache.druid.indexer.HadoopDruidIndexerConfig;
import org.apache.druid.indexer.HadoopIngestionSpec;
import org.apache.druid.indexer.JobHelper;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.jackson.GranularityModule;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.segment.loading.LocalDataSegmentPusherConfig;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.mapreduce.TaskType;
import org.joda.time.Interval;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 *
 */
public class HdfsDataSegmentPusherTest {
    static HdfsDataSegmentPusherTest.TestObjectMapper objectMapper;

    static {
        HdfsDataSegmentPusherTest.objectMapper = new HdfsDataSegmentPusherTest.TestObjectMapper();
        InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(ObjectMapper.class, HdfsDataSegmentPusherTest.objectMapper);
        injectableValues.addValue(PruneLoadSpecHolder.class, DEFAULT);
        HdfsDataSegmentPusherTest.objectMapper.setInjectableValues(injectableValues);
    }

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private HdfsDataSegmentPusher hdfsDataSegmentPusher;

    @Test
    public void testPushWithScheme() throws Exception {
        testUsingScheme("file");
    }

    @Test
    public void testPushWithBadScheme() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("No FileSystem for scheme");
        testUsingScheme("xyzzy");
        // Not reached
        Assert.assertTrue(false);
    }

    @Test
    public void testPushWithoutScheme() throws Exception {
        testUsingScheme(null);
    }

    @Test
    public void testPushWithMultipleSegments() throws Exception {
        testUsingSchemeForMultipleSegments("file", 3);
    }

    @Test
    public void testUsingUniqueFilePath() throws Exception {
        Configuration conf = new Configuration(true);
        // Create a mock segment on disk
        File segmentDir = tempFolder.newFolder();
        File tmp = new File(segmentDir, "version.bin");
        final byte[] data = new byte[]{ 0, 0, 0, 1 };
        Files.write(data, tmp);
        final long size = data.length;
        HdfsDataSegmentPusherConfig config = new HdfsDataSegmentPusherConfig();
        final File storageDirectory = tempFolder.newFolder();
        config.setStorageDirectory(StringUtils.format("file://%s", storageDirectory.getAbsolutePath()));
        HdfsDataSegmentPusher pusher = new HdfsDataSegmentPusher(config, conf, new DefaultObjectMapper());
        DataSegment segmentToPush = new DataSegment("foo", Intervals.of("2015/2016"), "0", new HashMap(), new ArrayList(), new ArrayList(), NoneShardSpec.instance(), 0, size);
        DataSegment segment = pusher.push(segmentDir, segmentToPush, true);
        Pattern pattern = Pattern.compile(".*/foo/20150101T000000\\.000Z_20160101T000000\\.000Z/0/0_[A-Za-z0-9-]{36}_index\\.zip");
        Assert.assertTrue(segment.getLoadSpec().get("path").toString(), pattern.matcher(segment.getLoadSpec().get("path").toString()).matches());
    }

    public static class TestObjectMapper extends ObjectMapper {
        public TestObjectMapper() {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            configure(MapperFeature.AUTO_DETECT_GETTERS, false);
            configure(MapperFeature.AUTO_DETECT_FIELDS, false);
            configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
            configure(MapperFeature.AUTO_DETECT_SETTERS, false);
            configure(SerializationFeature.INDENT_OUTPUT, false);
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            registerModule(new HdfsDataSegmentPusherTest.TestObjectMapper.TestModule().registerSubtypes(new NamedType(NumberedShardSpec.class, "NumberedShardSpec")));
            registerModule(new GranularityModule());
        }

        public static class TestModule extends SimpleModule {
            TestModule() {
                addSerializer(Interval.class, ToStringSerializer.instance);
                addSerializer(NumberedShardSpec.class, ToStringSerializer.instance);
                addDeserializer(Interval.class, new StdDeserializer<Interval>(Interval.class) {
                    @Override
                    public Interval deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        return Intervals.of(jsonParser.getText());
                    }
                });
            }
        }
    }

    @Test
    public void shouldNotHaveColonsInHdfsStorageDir() {
        Interval interval = Intervals.of("2011-10-01/2011-10-02");
        ImmutableMap<String, Object> loadSpec = ImmutableMap.of("something", "or_other");
        DataSegment segment = new DataSegment("something", interval, "brand:new:version", loadSpec, Arrays.asList("dim1", "dim2"), Arrays.asList("met1", "met2"), NoneShardSpec.instance(), null, 1);
        String storageDir = hdfsDataSegmentPusher.getStorageDir(segment, false);
        Assert.assertEquals("something/20111001T000000.000Z_20111002T000000.000Z/brand_new_version", storageDir);
    }

    @Test
    public void shouldMakeHDFSCompliantSegmentOutputPath() {
        HadoopIngestionSpec schema;
        try {
            schema = HdfsDataSegmentPusherTest.objectMapper.readValue(("{\n" + ((((((((((((("    \"dataSchema\": {\n" + "        \"dataSource\": \"source\",\n") + "        \"metricsSpec\": [],\n") + "        \"granularitySpec\": {\n") + "            \"type\": \"uniform\",\n") + "            \"segmentGranularity\": \"hour\",\n") + "            \"intervals\": [\"2012-07-10/P1D\"]\n") + "        }\n") + "    },\n") + "    \"ioConfig\": {\n") + "        \"type\": \"hadoop\",\n") + "        \"segmentOutputPath\": \"hdfs://server:9100/tmp/druid/datatest\"\n") + "    }\n") + "}")), HadoopIngestionSpec.class);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        // DataSchema dataSchema = new DataSchema("dataSource", null, null, Gra)
        // schema = new HadoopIngestionSpec(dataSchema, ioConfig, HadoopTuningConfig.makeDefaultTuningConfig());
        HadoopDruidIndexerConfig cfg = new HadoopDruidIndexerConfig(schema.withTuningConfig(schema.getTuningConfig().withVersion("some:brand:new:version")));
        Bucket bucket = new Bucket(4711, new org.joda.time.DateTime(2012, 7, 10, 5, 30, ISOChronology.getInstanceUTC()), 4712);
        Path path = JobHelper.makeFileNamePath(new Path(cfg.getSchema().getIOConfig().getSegmentOutputPath()), new DistributedFileSystem(), new DataSegment(cfg.getSchema().getDataSchema().getDataSource(), cfg.getSchema().getDataSchema().getGranularitySpec().bucketInterval(bucket.time).get(), cfg.getSchema().getTuningConfig().getVersion(), null, null, null, new NumberedShardSpec(bucket.partitionNum, 5000), (-1), (-1)), INDEX_ZIP, hdfsDataSegmentPusher);
        Assert.assertEquals(("hdfs://server:9100/tmp/druid/datatest/source/20120710T050000.000Z_20120710T060000.000Z/some_brand_new_version" + "/4712_index.zip"), path.toString());
        path = JobHelper.makeTmpPath(new Path(cfg.getSchema().getIOConfig().getSegmentOutputPath()), new DistributedFileSystem(), new DataSegment(cfg.getSchema().getDataSchema().getDataSource(), cfg.getSchema().getDataSchema().getGranularitySpec().bucketInterval(bucket.time).get(), cfg.getSchema().getTuningConfig().getVersion(), null, null, null, new NumberedShardSpec(bucket.partitionNum, 5000), (-1), (-1)), new org.apache.hadoop.mapreduce.TaskAttemptID("abc", 123, TaskType.REDUCE, 1, 0), hdfsDataSegmentPusher);
        Assert.assertEquals(("hdfs://server:9100/tmp/druid/datatest/source/20120710T050000.000Z_20120710T060000.000Z/some_brand_new_version" + "/4712_index.zip.0"), path.toString());
    }

    @Test
    public void shouldMakeDefaultSegmentOutputPathIfNotHDFS() {
        final HadoopIngestionSpec schema;
        try {
            schema = HdfsDataSegmentPusherTest.objectMapper.readValue(("{\n" + ((((((((((((("    \"dataSchema\": {\n" + "        \"dataSource\": \"the:data:source\",\n") + "        \"metricsSpec\": [],\n") + "        \"granularitySpec\": {\n") + "            \"type\": \"uniform\",\n") + "            \"segmentGranularity\": \"hour\",\n") + "            \"intervals\": [\"2012-07-10/P1D\"]\n") + "        }\n") + "    },\n") + "    \"ioConfig\": {\n") + "        \"type\": \"hadoop\",\n") + "        \"segmentOutputPath\": \"/tmp/dru:id/data:test\"\n") + "    }\n") + "}")), HadoopIngestionSpec.class);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        HadoopDruidIndexerConfig cfg = new HadoopDruidIndexerConfig(schema.withTuningConfig(schema.getTuningConfig().withVersion("some:brand:new:version")));
        Bucket bucket = new Bucket(4711, new org.joda.time.DateTime(2012, 7, 10, 5, 30, ISOChronology.getInstanceUTC()), 4712);
        Path path = JobHelper.makeFileNamePath(new Path(cfg.getSchema().getIOConfig().getSegmentOutputPath()), new LocalFileSystem(), new DataSegment(cfg.getSchema().getDataSchema().getDataSource(), cfg.getSchema().getDataSchema().getGranularitySpec().bucketInterval(bucket.time).get(), cfg.getSchema().getTuningConfig().getVersion(), null, null, null, new NumberedShardSpec(bucket.partitionNum, 5000), (-1), (-1)), INDEX_ZIP, new org.apache.druid.segment.loading.LocalDataSegmentPusher(new LocalDataSegmentPusherConfig()));
        Assert.assertEquals(("file:/tmp/dru:id/data:test/the:data:source/2012-07-10T05:00:00.000Z_2012-07-10T06:00:00.000Z/some:brand:new:" + "version/4712/index.zip"), path.toString());
        path = JobHelper.makeTmpPath(new Path(cfg.getSchema().getIOConfig().getSegmentOutputPath()), new LocalFileSystem(), new DataSegment(cfg.getSchema().getDataSchema().getDataSource(), cfg.getSchema().getDataSchema().getGranularitySpec().bucketInterval(bucket.time).get(), cfg.getSchema().getTuningConfig().getVersion(), null, null, null, new NumberedShardSpec(bucket.partitionNum, 5000), (-1), (-1)), new org.apache.hadoop.mapreduce.TaskAttemptID("abc", 123, TaskType.REDUCE, 1, 0), new org.apache.druid.segment.loading.LocalDataSegmentPusher(new LocalDataSegmentPusherConfig()));
        Assert.assertEquals(("file:/tmp/dru:id/data:test/the:data:source/2012-07-10T05:00:00.000Z_2012-07-10T06:00:00.000Z/some:brand:new:" + "version/4712/index.zip.0"), path.toString());
    }
}

