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
package org.apache.hadoop.hbase.ipc;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScannable;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, SmallTests.class })
public class TestHBaseRpcControllerImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseRpcControllerImpl.class);

    @Test
    public void testListOfCellScannerables() throws IOException {
        final int count = 10;
        List<CellScannable> cells = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            cells.add(TestHBaseRpcControllerImpl.createCell(i));
        }
        HBaseRpcController controller = new HBaseRpcControllerImpl(cells);
        CellScanner cellScanner = controller.cellScanner();
        int index = 0;
        for (; cellScanner.advance(); index++) {
            Cell cell = cellScanner.current();
            byte[] indexBytes = Bytes.toBytes(index);
            Assert.assertTrue(("" + index), Bytes.equals(indexBytes, 0, indexBytes.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
        Assert.assertEquals(count, index);
    }
}

