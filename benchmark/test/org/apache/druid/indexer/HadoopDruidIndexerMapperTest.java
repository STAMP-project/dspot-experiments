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
package org.apache.druid.indexer;


import HadoopDruidIndexerConfig.CONFIG_PROPERTY;
import HadoopDruidIndexerConfig.IndexJobCounters.ROWS_THROWN_AWAY_COUNTER;
import HadoopDruidIndexerConfig.JSON_MAPPER;
import JacksonUtils.TYPE_REFERENCE_MAP_STRING_OBJECT;
import Mapper.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.path.StaticPathSpec;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.parsers.JSONPathSpec;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.indexing.DataSchema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.easymock.EasyMock;
import org.junit.Test;


public class HadoopDruidIndexerMapperTest {
    private static final ObjectMapper JSON_MAPPER = TestHelper.makeJsonMapper();

    private static final DataSchema DATA_SCHEMA = new DataSchema("test_ds", HadoopDruidIndexerMapperTest.JSON_MAPPER.convertValue(new HadoopyStringInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec("t", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("dim1", "dim1t", "dim2")), null, null), new JSONPathSpec(true, ImmutableList.of()), ImmutableMap.of())), TYPE_REFERENCE_MAP_STRING_OBJECT), new AggregatorFactory[]{ new CountAggregatorFactory("rows") }, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.NONE, null), null, HadoopDruidIndexerMapperTest.JSON_MAPPER);

    private static final HadoopIOConfig IO_CONFIG = new HadoopIOConfig(HadoopDruidIndexerMapperTest.JSON_MAPPER.convertValue(new StaticPathSpec("dummyPath", null), TYPE_REFERENCE_MAP_STRING_OBJECT), null, "dummyOutputPath");

    private static final HadoopTuningConfig TUNING_CONFIG = HadoopTuningConfig.makeDefaultTuningConfig().withWorkingPath("dummyWorkingPath");

    @Test
    public void testHadoopyStringParser() throws Exception {
        final HadoopDruidIndexerConfig config = new HadoopDruidIndexerConfig(new HadoopIngestionSpec(HadoopDruidIndexerMapperTest.DATA_SCHEMA, HadoopDruidIndexerMapperTest.IO_CONFIG, HadoopDruidIndexerMapperTest.TUNING_CONFIG));
        final HadoopDruidIndexerMapperTest.MyMapper mapper = new HadoopDruidIndexerMapperTest.MyMapper();
        final Configuration hadoopConfig = new Configuration();
        hadoopConfig.set(CONFIG_PROPERTY, HadoopDruidIndexerConfig.JSON_MAPPER.writeValueAsString(config));
        final Mapper.Context mapContext = EasyMock.mock(Context.class);
        EasyMock.expect(mapContext.getConfiguration()).andReturn(hadoopConfig).once();
        EasyMock.replay(mapContext);
        mapper.setup(mapContext);
        final List<Map<String, Object>> rows = ImmutableList.of(ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "x", "m1", 1.0), ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim2", "y", "m1", 1.0));
        for (Map<String, Object> row : rows) {
            mapper.map(NullWritable.get(), new Text(HadoopDruidIndexerMapperTest.JSON_MAPPER.writeValueAsString(row)), mapContext);
        }
        HadoopDruidIndexerMapperTest.assertRowListEquals(rows, mapper.getRows());
    }

    @Test
    public void testHadoopyStringParserWithTransformSpec() throws Exception {
        final HadoopDruidIndexerConfig config = new HadoopDruidIndexerConfig(new HadoopIngestionSpec(HadoopDruidIndexerMapperTest.DATA_SCHEMA.withTransformSpec(new org.apache.druid.segment.transform.TransformSpec(new SelectorDimFilter("dim1", "foo", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dim1t", "concat(dim1,dim1)", ExprMacroTable.nil())))), HadoopDruidIndexerMapperTest.IO_CONFIG, HadoopDruidIndexerMapperTest.TUNING_CONFIG));
        final HadoopDruidIndexerMapperTest.MyMapper mapper = new HadoopDruidIndexerMapperTest.MyMapper();
        final Configuration hadoopConfig = new Configuration();
        hadoopConfig.set(CONFIG_PROPERTY, HadoopDruidIndexerConfig.JSON_MAPPER.writeValueAsString(config));
        final Mapper.Context mapContext = EasyMock.mock(Context.class);
        EasyMock.expect(mapContext.getConfiguration()).andReturn(hadoopConfig).once();
        EasyMock.expect(mapContext.getCounter(ROWS_THROWN_AWAY_COUNTER)).andReturn(HadoopDruidIndexerMapperTest.getTestCounter());
        EasyMock.replay(mapContext);
        mapper.setup(mapContext);
        final List<Map<String, Object>> rows = ImmutableList.of(ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "foo", "dim2", "x", "m1", 1.0), ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "bar", "dim2", "y", "m1", 1.0), ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "foo", "dim2", "z", "m1", 1.0));
        for (Map<String, Object> row : rows) {
            mapper.map(NullWritable.get(), new Text(HadoopDruidIndexerMapperTest.JSON_MAPPER.writeValueAsString(row)), mapContext);
        }
        HadoopDruidIndexerMapperTest.assertRowListEquals(ImmutableList.of(ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "foo", "dim1t", "foofoo", "dim2", "x", "m1", 1.0), ImmutableMap.of("t", "2000-01-01T00:00:00.000Z", "dim1", "foo", "dim1t", "foofoo", "dim2", "z", "m1", 1.0)), mapper.getRows());
    }

    public static class MyMapper extends HadoopDruidIndexerMapper {
        private final List<InputRow> rows = new ArrayList<>();

        @Override
        protected void innerMap(final InputRow inputRow, final Context context) {
            rows.add(inputRow);
        }

        public List<InputRow> getRows() {
            return rows;
        }
    }
}

