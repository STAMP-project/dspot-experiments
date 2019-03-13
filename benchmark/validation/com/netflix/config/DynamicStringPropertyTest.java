/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for DynamicStringProperty
 *
 * @author hyuan
 */
public class DynamicStringPropertyTest {
    private static final String NOCALLBACK = "no call back";

    private static final String AFTERCALLBACK = "after call back";

    private Runnable callback = new Runnable() {
        public void run() {
            if (DynamicStringPropertyTest.AFTERCALLBACK.equals(DynamicStringPropertyTest.callbackFlag)) {
                DynamicStringPropertyTest.callbackFlag = DynamicStringPropertyTest.NOCALLBACK;
            } else {
                DynamicStringPropertyTest.callbackFlag = DynamicStringPropertyTest.AFTERCALLBACK;
            }
        }
    };

    private static String callbackFlag = DynamicStringPropertyTest.NOCALLBACK;

    @Test
    public void testCallbacksAddUnsubscribe() {
        DynamicStringProperty dp = new DynamicStringProperty("testProperty", null);
        dp.addCallback(callback);
        // trigger callback
        ConfigurationManager.getConfigInstance().setProperty("testProperty", "cde");
        Assert.assertEquals(DynamicStringPropertyTest.AFTERCALLBACK, DynamicStringPropertyTest.callbackFlag);
        dp.removeAllCallbacks();
        // trigger callback again
        ConfigurationManager.getConfigInstance().setProperty("testProperty", "def");
        Assert.assertEquals(DynamicStringPropertyTest.AFTERCALLBACK, DynamicStringPropertyTest.callbackFlag);
        dp.addCallback(callback);
        ConfigurationManager.getConfigInstance().setProperty("testProperty", "efg");
        Assert.assertEquals(DynamicStringPropertyTest.NOCALLBACK, DynamicStringPropertyTest.callbackFlag);
    }
}

