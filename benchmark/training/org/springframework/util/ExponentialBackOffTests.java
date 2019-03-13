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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class ExponentialBackOffTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultInstance() {
        ExponentialBackOff backOff = new ExponentialBackOff();
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(2000L, execution.nextBackOff());
        Assert.assertEquals(3000L, execution.nextBackOff());
        Assert.assertEquals(4500L, execution.nextBackOff());
    }

    @Test
    public void simpleIncrease() {
        ExponentialBackOff backOff = new ExponentialBackOff(100L, 2.0);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(100L, execution.nextBackOff());
        Assert.assertEquals(200L, execution.nextBackOff());
        Assert.assertEquals(400L, execution.nextBackOff());
        Assert.assertEquals(800L, execution.nextBackOff());
    }

    @Test
    public void fixedIncrease() {
        ExponentialBackOff backOff = new ExponentialBackOff(100L, 1.0);
        backOff.setMaxElapsedTime(300L);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(100L, execution.nextBackOff());
        Assert.assertEquals(100L, execution.nextBackOff());
        Assert.assertEquals(100L, execution.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());
    }

    @Test
    public void maxIntervalReached() {
        ExponentialBackOff backOff = new ExponentialBackOff(2000L, 2.0);
        backOff.setMaxInterval(4000L);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(2000L, execution.nextBackOff());
        Assert.assertEquals(4000L, execution.nextBackOff());
        Assert.assertEquals(4000L, execution.nextBackOff());// max reached

        Assert.assertEquals(4000L, execution.nextBackOff());
    }

    @Test
    public void maxAttemptsReached() {
        ExponentialBackOff backOff = new ExponentialBackOff(2000L, 2.0);
        backOff.setMaxElapsedTime(4000L);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(2000L, execution.nextBackOff());
        Assert.assertEquals(4000L, execution.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());// > 4 sec wait in total

    }

    @Test
    public void startReturnDifferentInstances() {
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxElapsedTime(4000L);
        BackOffExecution execution = backOff.start();
        BackOffExecution execution2 = backOff.start();
        Assert.assertEquals(2000L, execution.nextBackOff());
        Assert.assertEquals(2000L, execution2.nextBackOff());
        Assert.assertEquals(4000L, execution.nextBackOff());
        Assert.assertEquals(4000L, execution2.nextBackOff());
        Assert.assertEquals(STOP, execution.nextBackOff());
        Assert.assertEquals(STOP, execution2.nextBackOff());
    }

    @Test
    public void invalidInterval() {
        ExponentialBackOff backOff = new ExponentialBackOff();
        thrown.expect(IllegalArgumentException.class);
        backOff.setMultiplier(0.9);
    }

    @Test
    public void maxIntervalReachedImmediately() {
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxInterval(50L);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals(50L, execution.nextBackOff());
        Assert.assertEquals(50L, execution.nextBackOff());
    }

    @Test
    public void toStringContent() {
        ExponentialBackOff backOff = new ExponentialBackOff(2000L, 2.0);
        BackOffExecution execution = backOff.start();
        Assert.assertEquals("ExponentialBackOff{currentInterval=n/a, multiplier=2.0}", execution.toString());
        execution.nextBackOff();
        Assert.assertEquals("ExponentialBackOff{currentInterval=2000ms, multiplier=2.0}", execution.toString());
        execution.nextBackOff();
        Assert.assertEquals("ExponentialBackOff{currentInterval=4000ms, multiplier=2.0}", execution.toString());
    }
}

