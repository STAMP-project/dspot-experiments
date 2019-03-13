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
package org.apache.drill.exec.physical.impl.agg;


import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.physical.impl.SimpleRootExec;
import org.apache.drill.exec.vector.BigIntVector;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.NullableBigIntVector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestAgg extends ExecTest {
    private final DrillConfig c = DrillConfig.create();

    @Test
    public void oneKeyAgg() throws Throwable {
        final SimpleRootExec exec = doTest("/agg/test1.json");
        while (exec.next()) {
            final BigIntVector cnt = exec.getValueVectorById(SchemaPath.getSimplePath("cnt"), BigIntVector.class);
            final IntVector key = exec.getValueVectorById(SchemaPath.getSimplePath("blue"), IntVector.class);
            final long[] cntArr = new long[]{ 10001, 9999 };
            final int[] keyArr = new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE };
            for (int i = 0; i < (exec.getRecordCount()); i++) {
                Assert.assertEquals(((Long) (cntArr[i])), cnt.getAccessor().getObject(i));
                Assert.assertEquals(((Integer) (keyArr[i])), key.getAccessor().getObject(i));
            }
        } 
        if ((exec.getContext().getExecutorState().getFailureCause()) != null) {
            throw exec.getContext().getExecutorState().getFailureCause();
        }
        Assert.assertTrue((!(exec.getContext().getExecutorState().isFailed())));
    }

    @Test
    public void twoKeyAgg() throws Throwable {
        SimpleRootExec exec = doTest("/agg/twokey.json");
        while (exec.next()) {
            final IntVector key1 = exec.getValueVectorById(SchemaPath.getSimplePath("key1"), IntVector.class);
            final BigIntVector key2 = exec.getValueVectorById(SchemaPath.getSimplePath("key2"), BigIntVector.class);
            final BigIntVector cnt = exec.getValueVectorById(SchemaPath.getSimplePath("cnt"), BigIntVector.class);
            final NullableBigIntVector total = exec.getValueVectorById(SchemaPath.getSimplePath("total"), NullableBigIntVector.class);
            final Integer[] keyArr1 = new Integer[]{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE };
            final long[] keyArr2 = new long[]{ 0, 1, 2, 0, 1, 2 };
            final long[] cntArr = new long[]{ 34, 34, 34, 34, 34, 34 };
            final long[] totalArr = new long[]{ 0, 34, 68, 0, 34, 68 };
            for (int i = 0; i < (exec.getRecordCount()); i++) {
                Assert.assertEquals(((Long) (cntArr[i])), cnt.getAccessor().getObject(i));
                Assert.assertEquals(keyArr1[i], key1.getAccessor().getObject(i));
                Assert.assertEquals(((Long) (keyArr2[i])), key2.getAccessor().getObject(i));
                Assert.assertEquals(((Long) (totalArr[i])), total.getAccessor().getObject(i));
            }
        } 
        if ((exec.getContext().getExecutorState().getFailureCause()) != null) {
            throw exec.getContext().getExecutorState().getFailureCause();
        }
        Assert.assertTrue((!(exec.getContext().getExecutorState().isFailed())));
    }
}

