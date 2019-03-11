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
package org.apache.druid.indexer.hadoop;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DatasourceRecordReaderTest {
    @Test
    public void testSanity() throws Exception {
        final DataSegment segment = new DataSegment("testds", Intervals.of("2014-10-22T00:00:00.000Z/2014-10-23T00:00:00.000Z"), "2015-07-15T22:02:40.171Z", ImmutableMap.of("type", "local", "path", this.getClass().getClassLoader().getResource("test-segment/index.zip").getPath()), ImmutableList.of("host"), ImmutableList.of("visited_sum", "unique_hosts"), new NumberedShardSpec(0, 1), 9, 4096);
        InputSplit split = new DatasourceInputSplit(Collections.singletonList(WindowedDataSegment.of(segment)), null);
        Configuration config = new Configuration();
        DatasourceInputFormat.addDataSource(config, new DatasourceIngestionSpec(segment.getDataSource(), segment.getInterval(), null, null, null, segment.getDimensions(), segment.getMetrics(), false, null), Collections.emptyList(), 0);
        TaskAttemptContext context = EasyMock.createNiceMock(TaskAttemptContext.class);
        EasyMock.expect(context.getConfiguration()).andReturn(config).anyTimes();
        EasyMock.replay(context);
        DatasourceRecordReader rr = new DatasourceRecordReader();
        rr.initialize(split, context);
        Assert.assertEquals(0, rr.getProgress(), 1.0E-4);
        List<InputRow> rows = new ArrayList<>();
        while (rr.nextKeyValue()) {
            rows.add(rr.getCurrentValue());
        } 
        verifyRows(rows);
        Assert.assertEquals(1, rr.getProgress(), 1.0E-4);
        rr.close();
    }
}

