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
package org.apache.flink.table.typeutils;


import org.apache.flink.api.common.typeutils.SerializerTestInstance;
import org.apache.flink.table.dataformat.BaseRow;
import org.apache.flink.testutils.DeeplyEqualsChecker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test for {@link BaseRowSerializer}.
 */
@RunWith(Parameterized.class)
public class BaseRowSerializerTest extends SerializerTestInstance<BaseRow> {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final BaseRowSerializer serializer;

    private final BaseRow[] testData;

    public BaseRowSerializerTest(BaseRowSerializer serializer, BaseRow[] testData) {
        super(new DeeplyEqualsChecker().withCustomCheck(( o1, o2) -> (o1 instanceof BaseRow) && (o2 instanceof BaseRow), ( o1, o2, checker) -> deepEqualsBaseRow(((BaseRow) (o1)), ((BaseRow) (o2)), serializer)), serializer, BaseRow.class, (-1), testData);
        this.serializer = serializer;
        this.testData = testData;
    }

    @Test
    public void testCopy() {
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(row));
        }
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(row, new org.apache.flink.table.dataformat.GenericRow(row.getArity())));
        }
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(serializer.baseRowToBinary(row), new org.apache.flink.table.dataformat.GenericRow(row.getArity())));
        }
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(serializer.baseRowToBinary(row)));
        }
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(serializer.baseRowToBinary(row), new org.apache.flink.table.dataformat.BinaryRow(row.getArity())));
        }
    }

    @Test
    public void testWrongCopy() {
        thrown.expect(IllegalArgumentException.class);
        serializer.copy(new org.apache.flink.table.dataformat.GenericRow(((serializer.getArity()) + 1)));
    }

    @Test
    public void testWrongCopyReuse() {
        thrown.expect(IllegalArgumentException.class);
        for (BaseRow row : testData) {
            deepEquals(row, serializer.copy(row, new org.apache.flink.table.dataformat.GenericRow(((row.getArity()) + 1))));
        }
    }
}

