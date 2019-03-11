/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.vector;


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.INT;
import MinorType.VARCHAR;
import NullableVarCharVector.Mutator;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.exec.vector.RepeatedVarCharVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(VectorTest.class)
public class TestFillEmpties extends SubOperatorTest {
    static final Logger logger = LoggerFactory.getLogger(TestFillEmpties.class);

    @Test
    public void testNullableVarChar() {
        @SuppressWarnings("resource")
        NullableVarCharVector vector = new NullableVarCharVector(SchemaBuilder.columnSchema("a", VARCHAR, OPTIONAL), SubOperatorTest.fixture.allocator());
        vector.allocateNew();
        // Create "foo", null, "bar", but omit the null.
        NullableVarCharVector.Mutator mutator = vector.getMutator();
        byte[] value = makeValue("foo");
        mutator.setSafe(0, value, 0, value.length);
        value = makeValue("bar");
        mutator.setSafe(2, value, 0, value.length);
        visualize(vector, 3);
        verifyOffsets(vector.getValuesVector().getOffsetVector(), new int[]{ 0, 3, 3, 6 });
        vector.close();
    }

    @Test
    public void testVarChar() {
        @SuppressWarnings("resource")
        VarCharVector vector = new VarCharVector(SchemaBuilder.columnSchema("a", VARCHAR, REQUIRED), SubOperatorTest.fixture.allocator());
        vector.allocateNew();
        // Create "foo", null, "bar", but omit the null.
        VarCharVector.Mutator mutator = vector.getMutator();
        byte[] value = makeValue("foo");
        mutator.setSafe(0, value, 0, value.length);
        // Work around: test fails without this. But, only the new column writers
        // call this method.
        mutator.fillEmpties(0, 2);
        value = makeValue("bar");
        mutator.setSafe(2, value, 0, value.length);
        visualize(vector, 3);
        verifyOffsets(vector.getOffsetVector(), new int[]{ 0, 3, 3, 6 });
        vector.close();
    }

    @Test
    public void testInt() {
        @SuppressWarnings("resource")
        IntVector vector = new IntVector(SchemaBuilder.columnSchema("a", INT, REQUIRED), SubOperatorTest.fixture.allocator());
        vector.allocateNew();
        // Create 1, 0, 2, but omit the 0.
        IntVector.Mutator mutator = vector.getMutator();
        mutator.setSafe(0, 1);
        mutator.setSafe(2, 3);
        visualize(vector, 3);
        vector.close();
    }

    @Test
    public void testRepeatedVarChar() {
        @SuppressWarnings("resource")
        RepeatedVarCharVector vector = new RepeatedVarCharVector(SchemaBuilder.columnSchema("a", VARCHAR, REPEATED), SubOperatorTest.fixture.allocator());
        vector.allocateNew();
        // Create "foo", null, "bar", but omit the null.
        RepeatedVarCharVector.Mutator mutator = vector.getMutator();
        mutator.startNewValue(0);
        byte[] value = makeValue("a");
        mutator.addSafe(0, value, 0, value.length);
        value = makeValue("b");
        mutator.addSafe(0, value, 0, value.length);
        // Work around: test fails without this. But, only the new column writers
        // call this method.
        mutator.fillEmpties(0, 2);
        mutator.startNewValue(2);
        value = makeValue("c");
        mutator.addSafe(2, value, 0, value.length);
        value = makeValue("d");
        mutator.addSafe(2, value, 0, value.length);
        visualize(vector, 3);
        verifyOffsets(vector.getOffsetVector(), new int[]{ 0, 2, 2, 4 });
        verifyOffsets(vector.getDataVector().getOffsetVector(), new int[]{ 0, 1, 2, 3, 4 });
        vector.close();
    }
}

