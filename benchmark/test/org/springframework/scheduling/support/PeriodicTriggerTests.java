/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.scheduling.support;


import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.TriggerContext;


/**
 *
 *
 * @author Mark Fisher
 * @since 3.0
 */
public class PeriodicTriggerTests {
    @Test
    public void fixedDelayFirstExecution() {
        Date now = new Date();
        PeriodicTrigger trigger = new PeriodicTrigger(5000);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertNegligibleDifference(now, next);
    }

    @Test
    public void fixedDelayWithInitialDelayFirstExecution() {
        Date now = new Date();
        long period = 5000;
        long initialDelay = 30000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertApproximateDifference(now, next, initialDelay);
    }

    @Test
    public void fixedDelayWithTimeUnitFirstExecution() {
        Date now = new Date();
        PeriodicTrigger trigger = new PeriodicTrigger(5, TimeUnit.SECONDS);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertNegligibleDifference(now, next);
    }

    @Test
    public void fixedDelayWithTimeUnitAndInitialDelayFirstExecution() {
        Date now = new Date();
        long period = 5;
        long initialDelay = 30;
        PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.SECONDS);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertApproximateDifference(now, next, (initialDelay * 1000));
    }

    @Test
    public void fixedDelaySubsequentExecution() {
        Date now = new Date();
        long period = 5000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, (period + 3000));
    }

    @Test
    public void fixedDelayWithInitialDelaySubsequentExecution() {
        Date now = new Date();
        long period = 5000;
        long initialDelay = 30000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, (period + 3000));
    }

    @Test
    public void fixedDelayWithTimeUnitSubsequentExecution() {
        Date now = new Date();
        long period = 5;
        PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.SECONDS);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, ((period * 1000) + 3000));
    }

    @Test
    public void fixedRateFirstExecution() {
        Date now = new Date();
        PeriodicTrigger trigger = new PeriodicTrigger(5000);
        trigger.setFixedRate(true);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertNegligibleDifference(now, next);
    }

    @Test
    public void fixedRateWithTimeUnitFirstExecution() {
        Date now = new Date();
        PeriodicTrigger trigger = new PeriodicTrigger(5, TimeUnit.SECONDS);
        trigger.setFixedRate(true);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertNegligibleDifference(now, next);
    }

    @Test
    public void fixedRateWithInitialDelayFirstExecution() {
        Date now = new Date();
        long period = 5000;
        long initialDelay = 30000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setFixedRate(true);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertApproximateDifference(now, next, initialDelay);
    }

    @Test
    public void fixedRateWithTimeUnitAndInitialDelayFirstExecution() {
        Date now = new Date();
        long period = 5;
        long initialDelay = 30;
        PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.MINUTES);
        trigger.setFixedRate(true);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(null, null, null));
        PeriodicTriggerTests.assertApproximateDifference(now, next, ((initialDelay * 60) * 1000));
    }

    @Test
    public void fixedRateSubsequentExecution() {
        Date now = new Date();
        long period = 5000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setFixedRate(true);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, period);
    }

    @Test
    public void fixedRateWithInitialDelaySubsequentExecution() {
        Date now = new Date();
        long period = 5000;
        long initialDelay = 30000;
        PeriodicTrigger trigger = new PeriodicTrigger(period);
        trigger.setFixedRate(true);
        trigger.setInitialDelay(initialDelay);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, period);
    }

    @Test
    public void fixedRateWithTimeUnitSubsequentExecution() {
        Date now = new Date();
        long period = 5;
        PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.HOURS);
        trigger.setFixedRate(true);
        Date next = trigger.nextExecutionTime(PeriodicTriggerTests.context(now, 500, 3000));
        PeriodicTriggerTests.assertApproximateDifference(now, next, (((period * 60) * 60) * 1000));
    }

    @Test
    public void equalsVerification() {
        PeriodicTrigger trigger1 = new PeriodicTrigger(3000);
        PeriodicTrigger trigger2 = new PeriodicTrigger(3000);
        Assert.assertFalse(trigger1.equals(new String("not a trigger")));
        Assert.assertFalse(trigger1.equals(null));
        Assert.assertEquals(trigger1, trigger1);
        Assert.assertEquals(trigger2, trigger2);
        Assert.assertEquals(trigger1, trigger2);
        trigger2.setInitialDelay(1234);
        Assert.assertFalse(trigger1.equals(trigger2));
        Assert.assertFalse(trigger2.equals(trigger1));
        trigger1.setInitialDelay(1234);
        Assert.assertEquals(trigger1, trigger2);
        trigger2.setFixedRate(true);
        Assert.assertFalse(trigger1.equals(trigger2));
        Assert.assertFalse(trigger2.equals(trigger1));
        trigger1.setFixedRate(true);
        Assert.assertEquals(trigger1, trigger2);
        PeriodicTrigger trigger3 = new PeriodicTrigger(3, TimeUnit.SECONDS);
        trigger3.setInitialDelay(7);
        trigger3.setFixedRate(true);
        Assert.assertFalse(trigger1.equals(trigger3));
        Assert.assertFalse(trigger3.equals(trigger1));
        trigger1.setInitialDelay(7000);
        Assert.assertEquals(trigger1, trigger3);
    }

    // helper class
    private static class TestTriggerContext implements TriggerContext {
        private final Date scheduled;

        private final Date actual;

        private final Date completion;

        TestTriggerContext(Date scheduled, Date actual, Date completion) {
            this.scheduled = scheduled;
            this.actual = actual;
            this.completion = completion;
        }

        @Override
        public Date lastActualExecutionTime() {
            return this.actual;
        }

        @Override
        public Date lastCompletionTime() {
            return this.completion;
        }

        @Override
        public Date lastScheduledExecutionTime() {
            return this.scheduled;
        }
    }
}

