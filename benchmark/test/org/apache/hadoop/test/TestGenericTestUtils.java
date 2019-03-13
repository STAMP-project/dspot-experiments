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
package org.apache.hadoop.test;


import com.google.common.base.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.apache.hadoop.test.GenericTestUtils.LogCapturer.captureLogs;


public class TestGenericTestUtils extends GenericTestUtils {
    @Test
    public void testAssertExceptionContainsNullEx() throws Throwable {
        try {
            GenericTestUtils.assertExceptionContains("", null);
        } catch (AssertionError e) {
            if (!(e.toString().contains(GenericTestUtils.E_NULL_THROWABLE))) {
                throw e;
            }
        }
    }

    @Test
    public void testAssertExceptionContainsNullString() throws Throwable {
        try {
            GenericTestUtils.assertExceptionContains("", new TestGenericTestUtils.BrokenException());
        } catch (AssertionError e) {
            if (!(e.toString().contains(GenericTestUtils.E_NULL_THROWABLE_STRING))) {
                throw e;
            }
        }
    }

    @Test
    public void testAssertExceptionContainsWrongText() throws Throwable {
        try {
            GenericTestUtils.assertExceptionContains("Expected", new Exception("(actual)"));
        } catch (AssertionError e) {
            String s = e.toString();
            if ((!(s.contains(GenericTestUtils.E_UNEXPECTED_EXCEPTION))) || (!(s.contains("(actual)")))) {
                throw e;
            }
            if ((e.getCause()) == null) {
                throw new AssertionError("No nested cause in assertion", e);
            }
        }
    }

    @Test
    public void testAssertExceptionContainsWorking() throws Throwable {
        GenericTestUtils.assertExceptionContains("Expected", new Exception("Expected"));
    }

    private static class BrokenException extends Exception {
        public BrokenException() {
        }

        @Override
        public String toString() {
            return null;
        }
    }

    @Test(timeout = 10000)
    public void testLogCapturer() {
        final Logger log = LoggerFactory.getLogger(TestGenericTestUtils.class);
        GenericTestUtils.LogCapturer logCapturer = captureLogs(log);
        final String infoMessage = "info message";
        // test get output message
        log.info(infoMessage);
        Assert.assertTrue(logCapturer.getOutput().endsWith(String.format((infoMessage + "%n"))));
        // test clear output
        logCapturer.clearOutput();
        Assert.assertTrue(logCapturer.getOutput().isEmpty());
        // test stop capturing
        logCapturer.stopCapturing();
        log.info(infoMessage);
        Assert.assertTrue(logCapturer.getOutput().isEmpty());
    }

    @Test(timeout = 10000)
    public void testLogCapturerSlf4jLogger() {
        final Logger logger = LoggerFactory.getLogger(TestGenericTestUtils.class);
        GenericTestUtils.LogCapturer logCapturer = captureLogs(logger);
        final String infoMessage = "info message";
        // test get output message
        logger.info(infoMessage);
        Assert.assertTrue(logCapturer.getOutput().endsWith(String.format((infoMessage + "%n"))));
        // test clear output
        logCapturer.clearOutput();
        Assert.assertTrue(logCapturer.getOutput().isEmpty());
        // test stop capturing
        logCapturer.stopCapturing();
        logger.info(infoMessage);
        Assert.assertTrue(logCapturer.getOutput().isEmpty());
    }

    @Test
    public void testWaitingForConditionWithInvalidParams() throws Throwable {
        // test waitFor method with null supplier interface
        try {
            GenericTestUtils.waitFor(null, 0, 0);
        } catch (NullPointerException e) {
            GenericTestUtils.assertExceptionContains(GenericTestUtils.ERROR_MISSING_ARGUMENT, e);
        }
        Supplier<Boolean> simpleSupplier = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return true;
            }
        };
        // test waitFor method with waitForMillis greater than checkEveryMillis
        GenericTestUtils.waitFor(simpleSupplier, 5, 10);
        try {
            // test waitFor method with waitForMillis smaller than checkEveryMillis
            GenericTestUtils.waitFor(simpleSupplier, 10, 5);
            Assert.fail(("Excepted a failure when the param value of" + " waitForMillis is smaller than checkEveryMillis."));
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(GenericTestUtils.ERROR_INVALID_ARGUMENT, e);
        }
    }

    @Test
    public void testToLevel() throws Throwable {
        Assert.assertEquals(Level.INFO, GenericTestUtils.toLevel("INFO"));
        Assert.assertEquals(Level.DEBUG, GenericTestUtils.toLevel("NonExistLevel"));
        Assert.assertEquals(Level.INFO, GenericTestUtils.toLevel("INFO", Level.TRACE));
        Assert.assertEquals(Level.TRACE, GenericTestUtils.toLevel("NonExistLevel", Level.TRACE));
    }
}

