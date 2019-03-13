/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.	See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.	You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.dataformat;


import org.junit.Test;


/**
 * Test for {@link BaseRow}s.
 */
public class BaseRowTest {
    private BinaryString str;

    private BinaryGeneric generic;

    private Decimal decimal1;

    private Decimal decimal2;

    private BinaryArray array;

    private BinaryMap map;

    private BinaryRow underRow;

    @Test
    public void testBinaryRow() {
        testAll(getBinaryRow());
    }

    @Test
    public void testNestedRow() {
        BinaryRow row = new BinaryRow(1);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        writer.writeRow(0, getBinaryRow(), null);
        writer.complete();
        testAll(row.getRow(0, 15));
    }

    @Test
    public void testGenericRow() {
        GenericRow row = new GenericRow(15);
        row.setField(0, true);
        row.setField(1, ((byte) (1)));
        row.setField(2, ((short) (2)));
        row.setField(3, 3);
        row.setField(4, ((long) (4)));
        row.setField(5, ((float) (5)));
        row.setField(6, ((double) (6)));
        row.setField(7, ((char) (7)));
        row.setField(8, str);
        row.setField(9, generic);
        row.setField(10, decimal1);
        row.setField(11, decimal2);
        row.setField(12, array);
        row.setField(13, map);
        row.setField(14, underRow);
        testAll(row);
    }

    @Test
    public void testBoxedWrapperRow() {
        BoxedWrapperRow row = new BoxedWrapperRow(15);
        row.setBoolean(0, true);
        row.setByte(1, ((byte) (1)));
        row.setShort(2, ((short) (2)));
        row.setInt(3, 3);
        row.setLong(4, ((long) (4)));
        row.setFloat(5, ((float) (5)));
        row.setDouble(6, ((double) (6)));
        row.setChar(7, ((char) (7)));
        row.setNonPrimitiveValue(8, str);
        row.setNonPrimitiveValue(9, generic);
        row.setNonPrimitiveValue(10, decimal1);
        row.setNonPrimitiveValue(11, decimal2);
        row.setNonPrimitiveValue(12, array);
        row.setNonPrimitiveValue(13, map);
        row.setNonPrimitiveValue(14, underRow);
        testAll(row);
    }

    @Test
    public void testJoinedRow() {
        GenericRow row1 = new GenericRow(5);
        row1.setField(0, true);
        row1.setField(1, ((byte) (1)));
        row1.setField(2, ((short) (2)));
        row1.setField(3, 3);
        row1.setField(4, ((long) (4)));
        GenericRow row2 = new GenericRow(10);
        row2.setField(0, ((float) (5)));
        row2.setField(1, ((double) (6)));
        row2.setField(2, ((char) (7)));
        row2.setField(3, str);
        row2.setField(4, generic);
        row2.setField(5, decimal1);
        row2.setField(6, decimal2);
        row2.setField(7, array);
        row2.setField(8, map);
        row2.setField(9, underRow);
        testAll(new JoinedRow(row1, row2));
    }
}

