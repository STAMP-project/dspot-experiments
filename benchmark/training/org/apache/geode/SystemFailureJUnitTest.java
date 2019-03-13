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
package org.apache.geode;


import java.util.concurrent.TimeUnit;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MembershipTest.class })
public class SystemFailureJUnitTest {
    private static final int LONG_WAIT = 30000;

    private int oldWaitTime;

    @Test
    public void testStopThreads() {
        SystemFailure.signalCacheCreate();
        SystemFailure.startThreads();
        long start = System.nanoTime();
        Thread watchDog = SystemFailure.getWatchDogForTest();
        Thread proctor = SystemFailure.getProctorForTest();
        await().until(() -> watchDog.isAlive());
        await().until(() -> proctor.isAlive());
        SystemFailure.stopThreads();
        long elapsed = (System.nanoTime()) - start;
        Assert.assertTrue(("Waited too long to shutdown: " + elapsed), (elapsed < (TimeUnit.MILLISECONDS.toNanos(SystemFailureJUnitTest.LONG_WAIT))));
        Assert.assertFalse(watchDog.isAlive());
        Assert.assertFalse(proctor.isAlive());
    }
}

