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
package org.apache.flink.table.runtime.sort;


import TableConfigOptions.SQL_EXEC_SORT_FILE_HANDLES_MAX_NUM;
import TableConfigOptions.SQL_EXEC_SPILL_COMPRESSION_ENABLED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.runtime.io.disk.SimpleCollectingOutputView;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsync;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.table.dataformat.BinaryRow;
import org.apache.flink.table.typeutils.BinaryRowSerializer;
import org.apache.flink.util.MutableObjectIterator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * UT for BufferedKVExternalSorter.
 */
@RunWith(Parameterized.class)
public class BufferedKVExternalSorterTest {
    private static final int PAGE_SIZE = MemoryManager.DEFAULT_PAGE_SIZE;

    private IOManager ioManager;

    private BinaryRowSerializer keySerializer;

    private BinaryRowSerializer valueSerializer;

    private NormalizedKeyComputer computer;

    private RecordComparator comparator;

    private int spillNumber;

    private int recordNumberPerFile;

    private Configuration conf;

    public BufferedKVExternalSorterTest(int spillNumber, int recordNumberPerFile, boolean spillCompress) {
        ioManager = new IOManagerAsync();
        conf = new Configuration();
        conf.setInteger(SQL_EXEC_SORT_FILE_HANDLES_MAX_NUM, 5);
        if (!spillCompress) {
            conf.setBoolean(SQL_EXEC_SPILL_COMPRESSION_ENABLED, false);
        }
        this.spillNumber = spillNumber;
        this.recordNumberPerFile = recordNumberPerFile;
    }

    @Test
    public void test() throws Exception {
        BufferedKVExternalSorter sorter = new BufferedKVExternalSorter(ioManager, keySerializer, valueSerializer, computer, comparator, BufferedKVExternalSorterTest.PAGE_SIZE, conf);
        TestMemorySegmentPool pool = new TestMemorySegmentPool(BufferedKVExternalSorterTest.PAGE_SIZE);
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < (spillNumber); i++) {
            ArrayList<MemorySegment> segments = new ArrayList<>();
            SimpleCollectingOutputView out = new SimpleCollectingOutputView(segments, pool, BufferedKVExternalSorterTest.PAGE_SIZE);
            writeKVToBuffer(keySerializer, valueSerializer, out, expected, recordNumberPerFile);
            sorter.sortAndSpill(segments, recordNumberPerFile, pool);
        }
        Collections.sort(expected);
        MutableObjectIterator<Tuple2<BinaryRow, BinaryRow>> iterator = sorter.getKVIterator();
        Tuple2<BinaryRow, BinaryRow> kv = new Tuple2(keySerializer.createInstance(), valueSerializer.createInstance());
        int count = 0;
        while ((kv = iterator.next(kv)) != null) {
            Assert.assertEquals(((int) (expected.get(count))), kv.f0.getInt(0));
            Assert.assertEquals((((expected.get(count)) * (-3)) + 177), kv.f1.getInt(0));
            count++;
        } 
        Assert.assertEquals(expected.size(), count);
        sorter.close();
    }
}

