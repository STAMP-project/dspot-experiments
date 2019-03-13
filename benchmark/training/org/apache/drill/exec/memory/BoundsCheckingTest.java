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
package org.apache.drill.exec.memory;


import io.netty.buffer.DrillBuf;
import io.netty.util.IllegalReferenceCountException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BoundsCheckingTest {
    private static final Logger logger = LoggerFactory.getLogger(BoundsCheckingTest.class);

    private static boolean old;

    private RootAllocator allocator;

    @Test
    public void testLengthCheck() {
        try {
            BoundsChecking.lengthCheck(null, 0, 0);
            Assert.fail("expecting NullPointerException");
        } catch (NullPointerException e) {
            BoundsCheckingTest.logger.debug("", e);
        }
        try (DrillBuf buffer = allocator.buffer(1)) {
            try {
                BoundsChecking.lengthCheck(buffer, 0, (-1));
                Assert.fail("expecting IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                BoundsCheckingTest.logger.debug("", e);
            }
            BoundsChecking.lengthCheck(buffer, 0, 0);
            BoundsChecking.lengthCheck(buffer, 0, 1);
            try {
                BoundsChecking.lengthCheck(buffer, 0, 2);
                Assert.fail("expecting IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException e) {
                BoundsCheckingTest.logger.debug("", e);
            }
            try {
                BoundsChecking.lengthCheck(buffer, 2, 0);
                Assert.fail("expecting IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException e) {
                BoundsCheckingTest.logger.debug("", e);
            }
            try {
                BoundsChecking.lengthCheck(buffer, (-1), 0);
                Assert.fail("expecting IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException e) {
                BoundsCheckingTest.logger.debug("", e);
            }
        }
        DrillBuf buffer = allocator.buffer(1);
        buffer.release();
        try {
            BoundsChecking.lengthCheck(buffer, 0, 0);
            Assert.fail("expecting IllegalReferenceCountException");
        } catch (IllegalReferenceCountException e) {
            BoundsCheckingTest.logger.debug("", e);
        }
    }
}

