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
package org.apache.drill.exec.physical.impl.xsort.managed;


import Level.WARN;
import io.netty.buffer.DrillBuf;
import org.apache.drill.exec.exception.OutOfMemoryException;
import org.apache.drill.exec.memory.Accountant;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.util.AssertionUtil;
import org.apache.drill.test.LogFixture;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of temporary allocator feature to allow a grace margin
 * for error in allocations from operators that make a good-faith
 * effort to stay within their budgets, but are sometimes undone
 * by unexpected power-of-two buffer sizes and vector doubling.
 */
public class TestLenientAllocation extends SubOperatorTest {
    /**
     * Use a test-time hack to force the allocator to be lenient,
     * regardless of whether we are in debug mode or not.
     */
    @Test
    public void testLenient() {
        LogFixture.LogFixtureBuilder logBuilder = LogFixture.builder().logger(Accountant.class, WARN);
        try (LogFixture logFixture = logBuilder.build()) {
            // Test can't run without assertions
            Assert.assertTrue(AssertionUtil.isAssertionsEnabled());
            // Create a child allocator
            BufferAllocator allocator = SubOperatorTest.fixture.allocator().newChildAllocator("test", (10 * 1024), (128 * 1024));
            forceLenient();
            // Allocate most of the available memory
            DrillBuf buf1 = allocator.buffer((64 * 1024));
            // Oops, we did our math wrong; allocate too large a buffer.
            DrillBuf buf2 = allocator.buffer((128 * 1024));
            Assert.assertEquals((192 * 1024), allocator.getAllocatedMemory());
            // We keep making mistakes.
            DrillBuf buf3 = allocator.buffer((32 * 1024));
            // Right up to the hard limit
            DrillBuf buf4 = allocator.buffer((32 * 1024));
            Assert.assertEquals((256 * 1024), allocator.getAllocatedMemory());
            // Enough of this; we're abusing the system. Next
            // allocation fails.
            try {
                allocator.buffer(8);
                Assert.fail();
            } catch (OutOfMemoryException e) {
                // Expected
            }
            // Recover from our excesses
            buf2.close();
            buf3.close();
            buf4.close();
            Assert.assertEquals((64 * 1024), allocator.getAllocatedMemory());
            // We're back in the good graces of the allocator,
            // can allocate more.
            DrillBuf buf5 = allocator.buffer(8);
            // Clean up
            buf1.close();
            buf5.close();
            allocator.close();
        }
    }

    /**
     * Test that the allocator is normally strict in debug mode.
     */
    @Test
    public void testStrict() {
        LogFixture.LogFixtureBuilder logBuilder = LogFixture.builder().logger(Accountant.class, WARN);
        try (LogFixture logFixture = logBuilder.build()) {
            // Test can't run without assertions
            Assert.assertTrue(AssertionUtil.isAssertionsEnabled());
            // Create a child allocator
            BufferAllocator allocator = SubOperatorTest.fixture.allocator().newChildAllocator("test", (10 * 1024), (128 * 1024));
            // Allocate most of the available memory
            DrillBuf buf1 = allocator.buffer((64 * 1024));
            // Oops, we did our math wrong; allocate too large a buffer.
            try {
                allocator.buffer((128 * 1024));
                Assert.fail();
            } catch (OutOfMemoryException e) {
                // Expected
            }
            // Clean up
            buf1.close();
            allocator.close();
        }
    }

    public static final int ONE_MEG = 1024 * 1024;

    @Test
    public void testLenientLimit() {
        LogFixture.LogFixtureBuilder logBuilder = LogFixture.builder().logger(Accountant.class, WARN);
        try (LogFixture logFixture = logBuilder.build()) {
            // Test can't run without assertions
            Assert.assertTrue(AssertionUtil.isAssertionsEnabled());
            // Create a child allocator
            BufferAllocator allocator = SubOperatorTest.fixture.allocator().newChildAllocator("test", (10 * (TestLenientAllocation.ONE_MEG)), (128 * (TestLenientAllocation.ONE_MEG)));
            forceLenient();
            // Allocate most of the available memory
            DrillBuf buf1 = allocator.buffer((64 * (TestLenientAllocation.ONE_MEG)));
            // Oops, we did our math wrong; allocate too large a buffer.
            DrillBuf buf2 = allocator.buffer((128 * (TestLenientAllocation.ONE_MEG)));
            // Can't go the full 2x over limit, errors capped at 100 MB.
            try {
                allocator.buffer((64 * (TestLenientAllocation.ONE_MEG)));
                Assert.fail();
            } catch (OutOfMemoryException e) {
                // Expected
            }
            // Clean up
            buf1.close();
            buf2.close();
            allocator.close();
        }
    }
}

