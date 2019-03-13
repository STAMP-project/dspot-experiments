/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import BackOffExecution.STOP;
import FixedBackOff.DEFAULT_INTERVAL;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.FixedBackOff;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class FixedBackOffTests {
    @Test
    public void defaultInstance() {
        FixedBackOff backOff = new FixedBackOff();
        BackOffExecution execution = backOff.start();
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(DEFAULT_INTERVAL, execution.nextBackOff());
        }
    }

    @Test
    public void noAttemptAtAll() {
        FixedBackOff backOff = new FixedBackOff(100L, 0L);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(STOP, execution.nextBackOff());
    }

    @Test
    public void maxAttemptsReached() {
        FixedBackOff backOff = new FixedBackOff(200L, 2);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(200L, execution.nextBackOff());
        Assert.assertEquals(200L, execution.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());
    }

    @Test
    public void startReturnDifferentInstances() {
        FixedBackOff backOff = new FixedBackOff(100L, 1);
        BackOffExecution execution = backOff.start();
        BackOffExecution execution2 = backOff.start();
        Assert.assertEquals(100L, execution.nextBackOff());
        Assert.assertEquals(100L, execution2.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());
        Assert.assertEquals(STOP, execution2.nextBackOff());
    }

    @Test
    public void liveUpdate() {
        FixedBackOff backOff = new FixedBackOff(100L, 1);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(100L, execution.nextBackOff());
        backOff.setInterval(200L);
        backOff.setMaxAttempts(2);
        Assert.assertEquals(200L, execution.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());
    }

    @Test
    public void toStringContent() {
        FixedBackOff backOff = new FixedBackOff(200L, 10);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals("FixedBackOff{interval=200, currentAttempts=0, maxAttempts=10}", execution.toString());
        execution.nextBackOff();
        Assert.assertEquals("FixedBackOff{interval=200, currentAttempts=1, maxAttempts=10}", execution.toString());
        execution.nextBackOff();
        Assert.assertEquals("FixedBackOff{interval=200, currentAttempts=2, maxAttempts=10}", execution.toString());
    }
}

