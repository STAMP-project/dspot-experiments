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
package org.apache.hadoop.hbase.regionserver.wal;


import WALEdit.METAFAMILY;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestFSWALEntry {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFSWALEntry.class);

    @Test
    public void testCollectFamilies() {
        byte[] family0 = Bytes.toBytes("family0");
        byte[] family1 = Bytes.toBytes("family1");
        byte[] family2 = Bytes.toBytes("family2");
        List<Cell> cells = new ArrayList<>();
        Assert.assertEquals(0, FSWALEntry.collectFamilies(cells).size());
        cells.add(CellUtil.createCell(family0, family0, family0));
        Assert.assertEquals(1, FSWALEntry.collectFamilies(cells).size());
        cells.add(CellUtil.createCell(family1, family1, family1));
        Assert.assertEquals(2, FSWALEntry.collectFamilies(cells).size());
        cells.add(CellUtil.createCell(family0, family0, family0));
        cells.add(CellUtil.createCell(family1, family1, family1));
        Assert.assertEquals(2, FSWALEntry.collectFamilies(cells).size());
        cells.add(CellUtil.createCell(family2, family2, family2));
        Assert.assertEquals(3, FSWALEntry.collectFamilies(cells).size());
        cells.add(CellUtil.createCell(METAFAMILY, METAFAMILY, METAFAMILY));
        Assert.assertEquals(3, FSWALEntry.collectFamilies(cells).size());
    }
}

