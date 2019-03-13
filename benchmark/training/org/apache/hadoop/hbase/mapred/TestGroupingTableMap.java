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
package org.apache.hadoop.hbase.mapred;


import GroupingTableMap.GROUP_COLUMNS;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ MapReduceTests.class, SmallTests.class })
public class TestGroupingTableMap {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGroupingTableMap.class);

    @Test
    @SuppressWarnings({ "deprecation", "unchecked" })
    public void shouldNotCallCollectonSinceFindUniqueKeyValueMoreThanOnes() throws Exception {
        GroupingTableMap gTableMap = null;
        try {
            Result result = Mockito.mock(Result.class);
            Reporter reporter = Mockito.mock(Reporter.class);
            gTableMap = new GroupingTableMap();
            Configuration cfg = new Configuration();
            cfg.set(GROUP_COLUMNS, "familyA:qualifierA familyB:qualifierB");
            JobConf jobConf = new JobConf(cfg);
            gTableMap.configure(jobConf);
            byte[] row = new byte[]{  };
            List<Cell> keyValues = ImmutableList.<Cell>of(new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyA"), Bytes.toBytes("qualifierA"), Bytes.toBytes("1111")), new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyA"), Bytes.toBytes("qualifierA"), Bytes.toBytes("2222")), new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyB"), Bytes.toBytes("qualifierB"), Bytes.toBytes("3333")));
            Mockito.when(result.listCells()).thenReturn(keyValues);
            OutputCollector<ImmutableBytesWritable, Result> outputCollectorMock = Mockito.mock(OutputCollector.class);
            gTableMap.map(null, result, outputCollectorMock, reporter);
            Mockito.verify(result).listCells();
            Mockito.verifyZeroInteractions(outputCollectorMock);
        } finally {
            if (gTableMap != null)
                gTableMap.close();

        }
    }

    @Test
    @SuppressWarnings({ "deprecation", "unchecked" })
    public void shouldCreateNewKeyAlthoughExtraKey() throws Exception {
        GroupingTableMap gTableMap = null;
        try {
            Result result = Mockito.mock(Result.class);
            Reporter reporter = Mockito.mock(Reporter.class);
            gTableMap = new GroupingTableMap();
            Configuration cfg = new Configuration();
            cfg.set(GROUP_COLUMNS, "familyA:qualifierA familyB:qualifierB");
            JobConf jobConf = new JobConf(cfg);
            gTableMap.configure(jobConf);
            byte[] row = new byte[]{  };
            List<Cell> keyValues = ImmutableList.<Cell>of(new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyA"), Bytes.toBytes("qualifierA"), Bytes.toBytes("1111")), new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyB"), Bytes.toBytes("qualifierB"), Bytes.toBytes("2222")), new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyC"), Bytes.toBytes("qualifierC"), Bytes.toBytes("3333")));
            Mockito.when(result.listCells()).thenReturn(keyValues);
            OutputCollector<ImmutableBytesWritable, Result> outputCollectorMock = Mockito.mock(OutputCollector.class);
            gTableMap.map(null, result, outputCollectorMock, reporter);
            Mockito.verify(result).listCells();
            Mockito.verify(outputCollectorMock, Mockito.times(1)).collect(ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verifyNoMoreInteractions(outputCollectorMock);
        } finally {
            if (gTableMap != null)
                gTableMap.close();

        }
    }

    @Test
    @SuppressWarnings({ "deprecation" })
    public void shouldCreateNewKey() throws Exception {
        GroupingTableMap gTableMap = null;
        try {
            Result result = Mockito.mock(Result.class);
            Reporter reporter = Mockito.mock(Reporter.class);
            final byte[] bSeparator = Bytes.toBytes(" ");
            gTableMap = new GroupingTableMap();
            Configuration cfg = new Configuration();
            cfg.set(GROUP_COLUMNS, "familyA:qualifierA familyB:qualifierB");
            JobConf jobConf = new JobConf(cfg);
            gTableMap.configure(jobConf);
            final byte[] firstPartKeyValue = Bytes.toBytes("34879512738945");
            final byte[] secondPartKeyValue = Bytes.toBytes("35245142671437");
            byte[] row = new byte[]{  };
            List<Cell> cells = ImmutableList.<Cell>of(new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyA"), Bytes.toBytes("qualifierA"), firstPartKeyValue), new org.apache.hadoop.hbase.KeyValue(row, Bytes.toBytes("familyB"), Bytes.toBytes("qualifierB"), secondPartKeyValue));
            Mockito.when(result.listCells()).thenReturn(cells);
            final AtomicBoolean outputCollected = new AtomicBoolean();
            OutputCollector<ImmutableBytesWritable, Result> outputCollector = new OutputCollector<ImmutableBytesWritable, Result>() {
                @Override
                public void collect(ImmutableBytesWritable arg, Result result) throws IOException {
                    Assert.assertArrayEquals(org.apache.hbase.thirdparty.com.google.common.primitives.Bytes.concat(firstPartKeyValue, bSeparator, secondPartKeyValue), arg.copyBytes());
                    outputCollected.set(true);
                }
            };
            gTableMap.map(null, result, outputCollector, reporter);
            Mockito.verify(result).listCells();
            Assert.assertTrue("Output not received", outputCollected.get());
            final byte[] firstPartValue = Bytes.toBytes("238947928");
            final byte[] secondPartValue = Bytes.toBytes("4678456942345");
            byte[][] data = new byte[][]{ firstPartValue, secondPartValue };
            ImmutableBytesWritable byteWritable = gTableMap.createGroupKey(data);
            Assert.assertArrayEquals(org.apache.hbase.thirdparty.com.google.common.primitives.Bytes.concat(firstPartValue, bSeparator, secondPartValue), byteWritable.get());
        } finally {
            if (gTableMap != null)
                gTableMap.close();

        }
    }

    @Test
    @SuppressWarnings({ "deprecation" })
    public void shouldReturnNullFromCreateGroupKey() throws Exception {
        GroupingTableMap gTableMap = null;
        try {
            gTableMap = new GroupingTableMap();
            Assert.assertNull(gTableMap.createGroupKey(null));
        } finally {
            if (gTableMap != null)
                gTableMap.close();

        }
    }
}

