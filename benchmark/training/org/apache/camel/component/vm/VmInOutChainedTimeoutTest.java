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
package org.apache.camel.component.vm;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.TestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Assert;
import org.junit.Test;


public class VmInOutChainedTimeoutTest extends AbstractVmTestSupport {
    @Test
    public void testVmInOutChainedTimeout() throws Exception {
        StopWatch watch = new StopWatch();
        try {
            template2.requestBody("vm:a?timeout=1000", "Hello World");
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            // the chained vm caused the timeout
            ExchangeTimedOutException cause = TestSupport.assertIsInstanceOf(ExchangeTimedOutException.class, e.getCause());
            Assert.assertEquals(200, cause.getTimeout());
        }
        long delta = watch.taken();
        Assert.assertTrue(("Should be faster than 1 sec, was: " + delta), (delta < 1100));
    }
}

