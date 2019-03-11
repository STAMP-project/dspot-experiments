/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.monitoring;


import ThreadsMonitoring.Mode;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Contains simple tests for the {@link org.apache.geode.internal.monitoring.ThreadsMonitoring}.
 *
 * @since Geode 1.5
 */
public class ThreadsMonitoringJUnitTest {
    public enum ModeExpected {

        FunctionExecutor,
        PooledExecutor,
        SerialQueuedExecutor,
        OneTaskOnlyExecutor,
        ScheduledThreadExecutor,
        AGSExecutor;}

    public final int numberOfElements = 6;

    private static final Logger logger = LogService.getLogger();

    /**
     * Tests that number of elements in ThreadMonitoring.Mode is correct
     */
    @Test
    public void testVerifyNumberOfElements() {
        Assert.assertTrue(((Mode.values().length) == (numberOfElements)));
    }

    /**
     * Tests that type of elements in ThreadMonitoring.Mode is correct
     */
    @Test
    public void testVerifyTypeOfElements() {
        try {
            for (int i = 0; i < (Mode.values().length); i++) {
                Assert.assertTrue(Mode.values()[i].name().equals(ThreadsMonitoringJUnitTest.ModeExpected.values()[i].name()));
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            ThreadsMonitoringJUnitTest.logger.error("Please verify to update the test in case of changes in ThreadMonitoring.Mode enum\n", arrayIndexOutOfBoundsException);
            Assert.assertTrue(false);
        }
    }
}

