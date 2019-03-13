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
package org.apache.camel.test.blueprint;


import CamelBlueprintTestSupport.SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT;
import org.junit.Assert;
import org.junit.Test;


public class ContextCreationTimeoutTest extends Assert {
    @Test
    public void testDefault() {
        System.clearProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT);
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        Assert.assertNull(ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testSystemPropertyNormal() {
        final Long someValue = 60000L;
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, someValue.toString());
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        Assert.assertEquals(someValue, ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testSystemPropertyMaxVal() {
        final Long someValue = Long.MAX_VALUE;
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, someValue.toString());
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        Assert.assertEquals(someValue, ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testSystemPropertyZero() {
        final Long zeroValue = 0L;
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, zeroValue.toString());
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        Assert.assertEquals(zeroValue, ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testSystemPropertyNegative() {
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, "-100");
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        try {
            ts.getCamelContextCreationTimeout();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertNull(e.getCause());
        }
    }

    @Test
    public void testSystemPropertyWrongFormat() {
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, "NaN");
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.DefaultTestSupport();
        try {
            ts.getCamelContextCreationTimeout();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(((e.getCause()) instanceof NumberFormatException));
        }
    }

    @Test
    public void testOverrideNormal() {
        final Long someValue = 60000L;
        System.clearProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT);
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.OverridingTestSupport(someValue);
        Assert.assertEquals(someValue, ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testOverrideSystemPropNormal() {
        final Long someValue = 60000L;
        final Long syspropValue = someValue + 60000L;
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, syspropValue.toString());
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.OverridingTestSupport(someValue);
        Assert.assertEquals(someValue, ts.getCamelContextCreationTimeout());
    }

    @Test
    public void testOverrideSystemPropNegative() {
        final Long someValue = 60000L;
        final Long syspropValue = ((Math.abs(someValue)) + 10) * (-1);
        System.setProperty(SPROP_CAMEL_CONTEXT_CREATION_TIMEOUT, syspropValue.toString());
        CamelBlueprintTestSupport ts = new ContextCreationTimeoutTest.OverridingTestSupport(someValue);
        Assert.assertEquals(someValue, ts.getCamelContextCreationTimeout());
    }

    private static class DefaultTestSupport extends CamelBlueprintTestSupport {}

    private static class OverridingTestSupport extends CamelBlueprintTestSupport {
        private final Long timeout;

        OverridingTestSupport(Long timeout) {
            this.timeout = timeout;
        }

        @Override
        protected Long getCamelContextCreationTimeout() {
            return timeout;
        }
    }
}

