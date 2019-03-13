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
package org.apache.drill.exec.fn.impl;


import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.expr.fn.impl.ByteFunctionHelpers;
import org.apache.drill.exec.expr.holders.VarCharHolder;
import org.apache.drill.exec.memory.BufferAllocator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ UnlikelyTest.class, VectorTest.class })
public class TestByteComparisonFunctions extends ExecTest {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestByteComparisonFunctions.class);
    private static BufferAllocator allocator;

    private static VarCharHolder hello;

    private static VarCharHolder goodbye;

    private static VarCharHolder helloLong;

    private static VarCharHolder goodbyeLong;

    @Test
    public void testAfter() {
        final VarCharHolder left = TestByteComparisonFunctions.hello;
        final VarCharHolder right = TestByteComparisonFunctions.goodbye;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 1));
    }

    @Test
    public void testBefore() {
        final VarCharHolder left = TestByteComparisonFunctions.goodbye;
        final VarCharHolder right = TestByteComparisonFunctions.hello;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == (-1)));
    }

    @Test
    public void testEqualCompare() {
        final VarCharHolder left = TestByteComparisonFunctions.hello;
        final VarCharHolder right = TestByteComparisonFunctions.hello;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 0));
    }

    @Test
    public void testEqual() {
        final VarCharHolder left = TestByteComparisonFunctions.hello;
        final VarCharHolder right = TestByteComparisonFunctions.hello;
        Assert.assertTrue(((ByteFunctionHelpers.equal(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 1));
    }

    @Test
    public void testNotEqual() {
        final VarCharHolder left = TestByteComparisonFunctions.hello;
        final VarCharHolder right = TestByteComparisonFunctions.goodbye;
        Assert.assertTrue(((ByteFunctionHelpers.equal(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 0));
    }

    @Test
    public void testAfterLong() {
        final VarCharHolder left = TestByteComparisonFunctions.helloLong;
        final VarCharHolder right = TestByteComparisonFunctions.goodbyeLong;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 1));
    }

    @Test
    public void testBeforeLong() {
        final VarCharHolder left = TestByteComparisonFunctions.goodbyeLong;
        final VarCharHolder right = TestByteComparisonFunctions.helloLong;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == (-1)));
    }

    @Test
    public void testEqualCompareLong() {
        final VarCharHolder left = TestByteComparisonFunctions.helloLong;
        final VarCharHolder right = TestByteComparisonFunctions.helloLong;
        Assert.assertTrue(((ByteFunctionHelpers.compare(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 0));
    }

    @Test
    public void testEqualLong() {
        final VarCharHolder left = TestByteComparisonFunctions.helloLong;
        final VarCharHolder right = TestByteComparisonFunctions.helloLong;
        Assert.assertTrue(((ByteFunctionHelpers.equal(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 1));
    }

    @Test
    public void testNotEqualLong() {
        final VarCharHolder left = TestByteComparisonFunctions.helloLong;
        final VarCharHolder right = TestByteComparisonFunctions.goodbyeLong;
        Assert.assertTrue(((ByteFunctionHelpers.equal(left.buffer, left.start, left.end, right.buffer, right.start, right.end)) == 0));
    }
}

