/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processor.util.pattern;


import java.util.ArrayList;
import java.util.List;
import org.apache.nifi.processor.exception.ProcessException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRollbackOnFailure {
    private static final Logger logger = LoggerFactory.getLogger(TestRollbackOnFailure.class);

    @Test
    public void testContextDefaultBehavior() {
        // Disabling rollbackOnFailure would route Failure or Retry as they are.
        final RollbackOnFailure context = new RollbackOnFailure(false, false);
        Integer[][] inputs = new Integer[][]{ new Integer[]{ null, 2, 999 }, new Integer[]{ 4, 2, 2 }, new Integer[]{ 2, 0, 999 }, new Integer[]{ 10, 2, 999 }, new Integer[]{ 8, 2, 4 } };
        final List<Integer> results = new ArrayList<>();
        try {
            processInputs(context, inputs, results);
        } catch (ProcessException e) {
            Assert.fail("ProcessException should NOT be thrown");
        }
        Assert.assertEquals("Successful inputs", 2, context.getProcessedCount());
    }

    @Test
    public void testContextRollbackOnFailureNonTransactionalFirstFailure() {
        final RollbackOnFailure context = new RollbackOnFailure(true, false);
        // If the first execution fails without any succeeded inputs, it should throw a ProcessException.
        Integer[][] inputs = new Integer[][]{ new Integer[]{ null, 2, 999 }, new Integer[]{ 4, 2, 2 }, new Integer[]{ 2, 0, 999 }, new Integer[]{ 10, 2, 999 }, new Integer[]{ 8, 2, 4 } };
        final List<Integer> results = new ArrayList<>();
        try {
            processInputs(context, inputs, results);
            Assert.fail("ProcessException should be thrown");
        } catch (ProcessException e) {
            TestRollbackOnFailure.logger.info("Exception was thrown as expected.");
        }
        Assert.assertEquals("Successful inputs", 0, context.getProcessedCount());
    }

    @Test
    public void testContextRollbackOnFailureNonTransactionalAlreadySucceeded() {
        final RollbackOnFailure context = new RollbackOnFailure(true, false);
        // If an execution fails after succeeded inputs, it transfer the input to Failure instead of ProcessException,
        // and keep going. Because the external system does not support transaction.
        Integer[][] inputs = new Integer[][]{ new Integer[]{ 4, 2, 2 }, new Integer[]{ 2, 0, 999 }, new Integer[]{ null, 2, 999 }, new Integer[]{ 10, 2, 999 }, new Integer[]{ 8, 2, 4 } };
        final List<Integer> results = new ArrayList<>();
        try {
            processInputs(context, inputs, results);
        } catch (ProcessException e) {
            Assert.fail("ProcessException should NOT be thrown");
        }
        Assert.assertEquals("Successful inputs", 2, context.getProcessedCount());
    }

    @Test
    public void testContextRollbackOnFailureTransactionalAlreadySucceeded() {
        final RollbackOnFailure context = new RollbackOnFailure(true, true);
        // Even if an execution fails after succeeded inputs, it transfer the input to Failure,
        // because the external system supports transaction.
        Integer[][] inputs = new Integer[][]{ new Integer[]{ 4, 2, 2 }, new Integer[]{ 2, 0, 999 }, new Integer[]{ null, 2, 999 }, new Integer[]{ 10, 2, 999 }, new Integer[]{ 8, 2, 4 } };
        final List<Integer> results = new ArrayList<>();
        try {
            processInputs(context, inputs, results);
            Assert.fail("ProcessException should be thrown");
        } catch (ProcessException e) {
            TestRollbackOnFailure.logger.info("Exception was thrown as expected.");
        }
    }
}

