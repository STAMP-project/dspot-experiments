/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.mapreduce;


import GroupingTableMapper.GROUP_COLUMNS;
import Mapper.Context;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ MapReduceTests.class, SmallTests.class })
public class TestGroupingTableMapper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGroupingTableMapper.class);

    /**
     * Test GroupingTableMapper class
     */
    @Test
    public void testGroupingTableMapper() throws Exception {
        GroupingTableMapper mapper = new GroupingTableMapper();
        Configuration configuration = new Configuration();
        configuration.set(GROUP_COLUMNS, "family1:clm family2:clm");
        mapper.setConf(configuration);
        Result result = Mockito.mock(Result.class);
        @SuppressWarnings("unchecked")
        Context context = Mockito.mock(Context.class);
        context.write(ArgumentMatchers.any(), ArgumentMatchers.any());
        List<Cell> keyValue = new ArrayList<>();
        byte[] row = new byte[]{  };
        keyValue.add(new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("family2"), Bytes.toBytes("clm"), Bytes.toBytes("value1")));
        keyValue.add(new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("family1"), Bytes.toBytes("clm"), Bytes.toBytes("value2")));
        Mockito.when(result.listCells()).thenReturn(keyValue);
        mapper.map(null, result, context);
        // template data
        byte[][] data = new byte[][]{ Bytes.toBytes("value1"), Bytes.toBytes("value2") };
        ImmutableBytesWritable ibw = mapper.createGroupKey(data);
        Mockito.verify(context).write(ibw, result);
    }
}

