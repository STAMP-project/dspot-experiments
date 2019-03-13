/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl.health;


import AbstractHealthCheck.CHECK_ENABLED;
import AbstractHealthCheck.FAILURE_COUNT;
import AbstractHealthCheck.INVOCATION_ATTEMPT_TIME;
import AbstractHealthCheck.INVOCATION_COUNT;
import AbstractHealthCheck.INVOCATION_TIME;
import HealthCheck.Result;
import HealthCheck.State;
import HealthCheck.State.DOWN;
import HealthCheck.State.UNKNOWN;
import HealthCheck.State.UP;
import java.time.Duration;
import java.util.Map;
import org.apache.camel.health.HealthCheck;
import org.apache.camel.health.HealthCheckResultBuilder;
import org.junit.Assert;
import org.junit.Test;


public class HealthCheckTest {
    @Test
    public void testCheck() throws Exception {
        HealthCheckTest.MyHealthCheck check = new HealthCheckTest.MyHealthCheck();
        check.setState(UP);
        HealthCheck.Result result;
        result = call();
        Assert.assertEquals(UNKNOWN, result.getState());
        Assert.assertTrue(result.getMessage().isPresent());
        Assert.assertEquals("Disabled", result.getMessage().get());
        Assert.assertEquals(false, result.getDetails().get(CHECK_ENABLED));
        getConfiguration().setEnabled(true);
        result = call();
        Assert.assertEquals(UP, result.getState());
        Assert.assertFalse(result.getMessage().isPresent());
        Assert.assertFalse(result.getDetails().containsKey(CHECK_ENABLED));
    }

    @Test
    public void testInterval() throws Exception {
        HealthCheckTest.MyHealthCheck check = new HealthCheckTest.MyHealthCheck();
        check.setState(UP);
        getConfiguration().setEnabled(true);
        getConfiguration().setInterval(Duration.ofMillis(1000));
        HealthCheck.Result result1 = check.call();
        Assert.assertEquals(UP, result1.getState());
        Thread.sleep(100);
        HealthCheck.Result result2 = check.call();
        Assert.assertEquals(UP, result2.getState());
        Assert.assertEquals(result1.getDetails().get(INVOCATION_TIME), result2.getDetails().get(INVOCATION_TIME));
        Assert.assertEquals(result1.getDetails().get(INVOCATION_COUNT), result2.getDetails().get(INVOCATION_COUNT));
        Assert.assertNotEquals(getMetaData().get(INVOCATION_ATTEMPT_TIME), result2.getDetails().get(INVOCATION_TIME));
        Thread.sleep(1250);
        HealthCheck.Result result3 = check.call();
        Assert.assertEquals(UP, result3.getState());
        Assert.assertNotEquals(result2.getDetails().get(INVOCATION_TIME), result3.getDetails().get(INVOCATION_TIME));
        Assert.assertNotEquals(result2.getDetails().get(INVOCATION_COUNT), result3.getDetails().get(INVOCATION_COUNT));
        Assert.assertEquals(getMetaData().get(INVOCATION_ATTEMPT_TIME), result3.getDetails().get(INVOCATION_TIME));
    }

    @Test
    public void testThreshold() throws Exception {
        HealthCheckTest.MyHealthCheck check = new HealthCheckTest.MyHealthCheck();
        check.setState(DOWN);
        getConfiguration().setEnabled(true);
        getConfiguration().setFailureThreshold(3);
        HealthCheck.Result result;
        for (int i = 0; i < (getConfiguration().getFailureThreshold()); i++) {
            result = call();
            Assert.assertEquals(UP, result.getState());
            Assert.assertEquals((i + 1), result.getDetails().get(INVOCATION_COUNT));
            Assert.assertEquals((i + 1), result.getDetails().get(FAILURE_COUNT));
        }
        Assert.assertEquals(DOWN, call().getState());
    }

    @Test
    public void testIntervalThreshold() throws Exception {
        HealthCheckTest.MyHealthCheck check = new HealthCheckTest.MyHealthCheck();
        check.setState(DOWN);
        getConfiguration().setEnabled(true);
        getConfiguration().setInterval(Duration.ofMillis(500));
        getConfiguration().setFailureThreshold(3);
        HealthCheck.Result result;
        int icount;
        int fcount;
        for (int i = 0; i < (getConfiguration().getFailureThreshold()); i++) {
            result = call();
            icount = ((int) (result.getDetails().get(INVOCATION_COUNT)));
            fcount = ((int) (result.getDetails().get(FAILURE_COUNT)));
            Assert.assertEquals(UP, result.getState());
            Assert.assertEquals((i + 1), icount);
            Assert.assertEquals((i + 1), fcount);
            result = call();
            Assert.assertEquals(UP, result.getState());
            Assert.assertEquals(icount, result.getDetails().get(INVOCATION_COUNT));
            Assert.assertEquals(fcount, result.getDetails().get(FAILURE_COUNT));
            Thread.sleep(550);
        }
        Assert.assertEquals(DOWN, call().getState());
    }

    // ********************************
    // 
    // ********************************
    private class MyHealthCheck extends AbstractHealthCheck {
        private State state;

        MyHealthCheck() {
            super("my");
            this.state = State.UP;
        }

        public void setState(State state) {
            this.state = state;
        }

        public State getState() {
            return state;
        }

        @Override
        public void doCall(HealthCheckResultBuilder builder, Map<String, Object> options) {
            builder.state(state);
        }
    }
}

